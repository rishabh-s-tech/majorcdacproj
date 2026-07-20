import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import API from "../api/axiosConfig";

function Register() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: ""
  });

  const [message, setMessage] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleChange = (event) => {
    setFormData({
      ...formData,
      [event.target.name]: event.target.value
    });
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    setMessage("");
    setIsSubmitting(true);

    API.post("/users/register", formData)
      .then(() => {
        setMessage("Account created. Redirecting to login...");
        setTimeout(() => navigate("/login"), 700);
      })
      .catch((error) => {
        setMessage(error.response?.data?.message || "Unable to create account. Please try a different email.");
      })
      .finally(() => {
        setIsSubmitting(false);
      });
  };

  return (
    <main className="auth-layout">
      <aside className="auth-aside">
        <p className="section-kicker text-white">Join GreenNest</p>
        <h1>Create an account for faster plant shopping.</h1>
        <p>Save your cart, place orders securely, and keep your plant purchases organized.</p>
      </aside>

      <section className="auth-card">
        <p className="section-kicker">Customer account</p>
        <h2 className="page-title fs-1 mb-2">Register</h2>
        <p className="muted-copy mb-4">Set up your account with a name, email, and password.</p>

        {message && (
          <div className={`alert ${message.startsWith("Account") ? "alert-success" : "alert-danger"}`}>
            {message}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label className="form-label" htmlFor="name">Full name</label>
            <input
              id="name"
              name="name"
              className="form-control"
              value={formData.name}
              onChange={handleChange}
              autoComplete="name"
              required
            />
          </div>

          <div className="mb-3">
            <label className="form-label" htmlFor="email">Email</label>
            <input
              id="email"
              type="email"
              name="email"
              className="form-control"
              value={formData.email}
              onChange={handleChange}
              autoComplete="email"
              required
            />
          </div>

          <div className="mb-4">
            <label className="form-label" htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              name="password"
              className="form-control"
              value={formData.password}
              onChange={handleChange}
              autoComplete="new-password"
              minLength="6"
              required
            />
          </div>

          <button className="btn btn-success w-100 py-2" type="submit" disabled={isSubmitting}>
            {isSubmitting ? "Creating account..." : "Create account"}
          </button>
        </form>

        <p className="muted-copy mt-4 mb-0">
          Already have an account? <Link className="fw-bold" to="/login">Login</Link>
        </p>
      </section>
    </main>
  );
}

export default Register;
