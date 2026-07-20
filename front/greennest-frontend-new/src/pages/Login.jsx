import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import API from "../api/axiosConfig";
import { useAuth } from "../context/AuthContext";

function Login() {
  const navigate = useNavigate();
  const { login } = useAuth();

  const [formData, setFormData] = useState({
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

    API.post("/users/login", formData)
      .then((response) => {
        login(response.data);
        navigate("/plants");
      })
      .catch((error) => {
        setMessage(error.response?.data?.message || "Invalid email or password");
      })
      .finally(() => {
        setIsSubmitting(false);
      });
  };

  return (
    <main className="auth-layout">
      <aside className="auth-aside">
        <p className="section-kicker text-white">Welcome back</p>
        <h1>Sign in to continue your GreenNest order.</h1>
        <p>Manage your saved cart, view orders, and check delivery status from one calm workspace.</p>
      </aside>

      <section className="auth-card">
        <p className="section-kicker">Customer login</p>
        <h2 className="page-title fs-1 mb-2">Login</h2>
        <p className="muted-copy mb-4">Use the email and password connected to your account.</p>

        {message && (
          <div className="alert alert-danger">
            {message}
          </div>
        )}

        <form onSubmit={handleSubmit}>
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
              autoComplete="current-password"
              required
            />
          </div>

          <button className="btn btn-success w-100 py-2" type="submit" disabled={isSubmitting}>
            {isSubmitting ? "Signing in..." : "Login"}
          </button>
        </form>

        <p className="muted-copy mt-4 mb-0">
          New to GreenNest? <Link className="fw-bold" to="/register">Create an account</Link>
        </p>
      </section>
    </main>
  );
}

export default Login;
