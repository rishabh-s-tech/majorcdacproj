import { Link, NavLink, useNavigate } from "react-router-dom";
import { FaLeaf, FaShoppingBag, FaShoppingCart, FaSignOutAlt, FaUser } from "react-icons/fa";
import { useAuth } from "../context/AuthContext";
import { useCart } from "../context/CartContext";

function Navbar() {
  const navigate = useNavigate();
  const { user, logout: authLogout } = useAuth();
  const { count } = useCart();

  const logout = () => {
    authLogout();
    navigate("/login");
  };

  return (
    <header className="site-navbar">
      <div className="navbar-inner">
        <Link className="brand-link" to="/">
          <span className="brand-mark" aria-hidden="true">
            <FaLeaf />
          </span>
          <span>
            GreenNest
            <small>Indoor plants and garden essentials</small>
          </span>
        </Link>

        <nav className="nav-links" aria-label="Primary navigation">
          <NavLink className="nav-link-pill" to="/plants">
            <FaLeaf aria-hidden="true" /> Plants
          </NavLink>

          {user && (
            <>
              <NavLink className="nav-link-pill" to="/cart">
                <FaShoppingCart aria-hidden="true" /> Cart
                {count > 0 && <span className="cart-badge">{count}</span>}
              </NavLink>

              <NavLink className="nav-link-pill" to="/orders">
                <FaShoppingBag aria-hidden="true" /> Orders
              </NavLink>
            </>
          )}

          {user?.role === "ADMIN" && (
            <>
              <NavLink className="nav-link-pill" to="/admin/plants">
                Inventory
              </NavLink>
              <NavLink className="nav-link-pill" to="/admin/orders">
                Fulfilment
              </NavLink>
            </>
          )}

          {!user ? (
            <>
              <NavLink className="nav-link-pill" to="/register">
                Register
              </NavLink>
              <NavLink className="nav-action primary" to="/login">
                <FaUser aria-hidden="true" /> Login
              </NavLink>
            </>
          ) : (
            <button className="nav-action primary" type="button" onClick={logout}>
              <FaSignOutAlt aria-hidden="true" /> Logout
            </button>
          )}
        </nav>
      </div>
    </header>
  );
}

export default Navbar;
