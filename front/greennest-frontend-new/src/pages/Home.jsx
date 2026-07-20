import { Link } from "react-router-dom";
import { FaLeaf, FaShippingFast, FaShieldAlt } from "react-icons/fa";
import heroImage from "../assets/hero.png";

function Home() {
  return (
    <>
      <main className="hero-section">
        <section>
          <p className="section-kicker">GreenNest plant studio</p>
          <h1 className="hero-title">Bring home plants that feel thoughtfully chosen.</h1>
          <p className="hero-copy">
            Shop easy-care indoor plants, balcony greens, and premium garden picks with a clean
            ordering experience built for modern plant lovers.
          </p>

          <div className="hero-actions">
            <Link className="btn btn-success btn-lg px-4" to="/plants">
              Shop plants
            </Link>
            <Link className="btn btn-outline-secondary btn-lg px-4" to="/register">
              Create account
            </Link>
          </div>

          <div className="hero-metrics">
            <div className="metric">
              <strong>120+</strong>
              <span>curated varieties</span>
            </div>
            <div className="metric">
              <strong>48h</strong>
              <span>fresh dispatch</span>
            </div>
            <div className="metric">
              <strong>4.8</strong>
              <span>customer rating</span>
            </div>
          </div>
        </section>

        <aside className="hero-visual" aria-label="GreenNest featured plants">
          <img src={heroImage} alt="A styled indoor plant collection" />
          <div className="hero-note">
            <div>
              <strong>Seasonal edit</strong>
              <div>Air-purifying greens for work desks and living rooms.</div>
            </div>
            <span className="stock-chip">Fresh stock</span>
          </div>
        </aside>
      </main>

      <section className="feature-band" aria-label="Store benefits">
        <article className="feature-item">
          <FaLeaf size={24} aria-hidden="true" />
          <h3>Healthy plants</h3>
          <p className="muted-copy">Each plant is selected for condition, shape, and home readiness.</p>
        </article>
        <article className="feature-item">
          <FaShippingFast size={26} aria-hidden="true" />
          <h3>Careful delivery</h3>
          <p className="muted-copy">Packaging keeps leaves, stems, and soil stable in transit.</p>
        </article>
        <article className="feature-item">
          <FaShieldAlt size={24} aria-hidden="true" />
          <h3>Secure account</h3>
          <p className="muted-copy">Track orders and manage your cart through a protected checkout flow.</p>
        </article>
      </section>
    </>
  );
}

export default Home;
