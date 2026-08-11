// AI vulnerability triage — summarizes Trivy's CRITICAL/HIGH findings for a build
// into a short, plain-English risk assessment, written straight into the GitHub
// Actions run summary. This step is advisory only: it never affects whether the
// build passes or fails (that's still entirely Trivy's job, in the Gate step
// above it in trivy.yml). It runs with `if: always()`, so it fires even when the
// CRITICAL gate has just failed — that's exactly when a fast, readable summary
// is most useful, before anyone has to open the raw CVE table.
//
// Uses the Google Gemini API (free tier via Google AI Studio — no billing
// required, https://aistudio.google.com/apikey) rather than a paid provider,
// so this step costs nothing to run.
//
// Usage: node .github/scripts/ai-triage.mjs <trivy-results.json>
// Requires: GEMINI_API_KEY env var (GitHub Actions secret)

import fs from "fs";

const inputPath = process.argv[2];

if (!inputPath || !fs.existsSync(inputPath)) {
  console.log("## 🤖 AI Vulnerability Triage\n");
  console.log("_No Trivy JSON results file found — skipping AI summary._");
  process.exit(0);
}

const raw = JSON.parse(fs.readFileSync(inputPath, "utf8"));

const cves = (raw.Results || [])
  .flatMap((r) => r.Vulnerabilities || [])
  .map((v) => ({
    id: v.VulnerabilityID,
    pkg: v.PkgName,
    installed: v.InstalledVersion,
    fixed: v.FixedVersion || "none",
    severity: v.Severity,
    title: v.Title,
  }));

if (cves.length === 0) {
  console.log("## 🤖 AI Vulnerability Triage\n");
  console.log("No CRITICAL or HIGH findings in this scan — nothing to triage.");
  process.exit(0);
}

if (!process.env.GEMINI_API_KEY) {
  console.log("## 🤖 AI Vulnerability Triage\n");
  console.log(
    `_Skipped: GEMINI_API_KEY secret is not configured. ${cves.length} CRITICAL/HIGH finding(s) present — see the Trivy report step above for details._`
  );
  process.exit(0);
}

const prompt = `You are a security engineer triaging a container vulnerability scan for a small dev team.
Given this JSON list of CVEs found in a Docker image, write:
1. One short paragraph: the overall risk level and whether this build looks safe to ship.
2. One line per CVE: what it affects, why it matters in plain English, and whether a fix is available.
Keep the whole response under 200 words, no fluff, markdown format, no headings (the caller adds its own heading).

CVEs:
${JSON.stringify(cves, null, 2)}`;

const MODEL = "gemini-3.6-flash";
const url = `https://generativelanguage.googleapis.com/v1beta/models/${MODEL}:generateContent?key=${process.env.GEMINI_API_KEY}`;

try {
  const res = await fetch(url, {
    method: "POST",
    headers: { "content-type": "application/json" },
    body: JSON.stringify({
      contents: [{ parts: [{ text: prompt }] }],
      generationConfig: { maxOutputTokens: 500 },
    }),
  });

  if (!res.ok) {
    const errText = await res.text();
    console.log("## 🤖 AI Vulnerability Triage\n");
    console.log(`_AI summary unavailable (API error ${res.status}). ${cves.length} CRITICAL/HIGH finding(s) present — see the Trivy report step above._`);
    console.error("Gemini API error:", errText);
    process.exit(0); // advisory step — never fail the job because of this
  }

  const data = await res.json();
  const summary = data.candidates?.[0]?.content?.parts?.[0]?.text ?? "_No summary returned._";

  console.log("## 🤖 AI Vulnerability Triage\n");
  console.log(summary);
} catch (err) {
  console.log("## 🤖 AI Vulnerability Triage\n");
  console.log(`_AI summary unavailable (${err.message}). ${cves.length} CRITICAL/HIGH finding(s) present — see the Trivy report step above._`);
  process.exit(0);
}
