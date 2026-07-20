import { FaLeaf } from "react-icons/fa";

function Footer() {
  return (
    <footer className="site-footer">
      <div className="footer-inner">
        <div className="d-flex align-items-center gap-2">
          <FaLeaf aria-hidden="true" />
          <strong>GreenNest</strong>
          <span>Curated greenery for calmer homes.</span>
        </div>
        <div>Secure checkout, healthy plants, careful delivery.</div>
      </div>
    </footer>
  );
}

export default Footer;
