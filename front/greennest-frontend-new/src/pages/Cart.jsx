import { useCallback, useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { FaMinus, FaPlus, FaTrash } from "react-icons/fa";
import API from "../api/axiosConfig";
import { useCart } from "../context/CartContext";

const currency = new Intl.NumberFormat("en-IN", {
  style: "currency",
  currency: "INR",
  maximumFractionDigits: 0
});

function Cart() {
  const navigate = useNavigate();
  const { refreshCount } = useCart();

  const [cartItems, setCartItems] = useState([]);
  const [message, setMessage] = useState("");
  const [isLoading, setIsLoading] = useState(true);

  const loadCart = useCallback(() => {
    API.get("/cart")
      .then((response) => {
        setCartItems(response.data);
      })
      .catch(() => {
        setMessage("Unable to load cart");
      })
      .finally(() => {
        setIsLoading(false);
      });
  }, []);

  useEffect(() => {
    loadCart();
  }, [loadCart]);

  const updateQuantity = (cartId, quantity) => {
    if (quantity < 1) {
      return;
    }

    API.put(`/cart/${cartId}?quantity=${quantity}`)
      .then(() => {
        loadCart();
        refreshCount();
      })
      .catch((error) => {
        setMessage(error.response?.data?.message || "Unable to update quantity");
      });
  };

  const removeItem = (cartId) => {
    API.delete(`/cart/${cartId}`)
      .then(() => {
        loadCart();
        refreshCount();
      })
      .catch(() => {
        setMessage("Unable to remove item");
      });
  };

  const placeOrder = () => {
    API.post("/orders/place")
      .then(() => {
        setCartItems([]);
        refreshCount();
        navigate("/orders");
      })
      .catch((error) => {
        setMessage(error.response?.data?.message || "Unable to place order");
      });
  };

  const totalAmount = cartItems.reduce((total, item) => {
    return total + (item.plant.price * item.quantity);
  }, 0);

  return (
    <main className="page-shell">
      <div className="toolbar">
        <div>
          <p className="section-kicker">Checkout</p>
          <h1 className="page-title">Your cart</h1>
          <p className="page-subtitle mt-3 mb-0">Review plants, adjust quantities, and place your order.</p>
        </div>
        <Link className="btn btn-outline-secondary" to="/plants">Continue shopping</Link>
      </div>

      {message && (
        <div className="alert alert-warning text-center">
          {message}
        </div>
      )}

      {isLoading ? (
        <div className="empty-state">Loading your cart...</div>
      ) : cartItems.length === 0 ? (
        <div className="empty-state">
          <h3>Your cart is empty</h3>
          <p className="muted-copy mb-4">Add a few plants to build your GreenNest order.</p>
          <Link className="btn btn-success" to="/plants">Browse plants</Link>
        </div>
      ) : (
        <>
          <div className="table-responsive table-card">
            <table className="table align-middle">
              <thead>
                <tr>
                  <th>Plant</th>
                  <th>Price</th>
                  <th>Quantity</th>
                  <th>Total</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {cartItems.map((item) => (
                  <tr key={item.cartId}>
                    <td>
                      <div className="d-flex align-items-center gap-3">
                        <img
                          src={`/images/${item.plant.imageUrl}`}
                          alt={item.plant.plantName}
                          className="table-thumb"
                        />
                        <strong>{item.plant.plantName}</strong>
                      </div>
                    </td>
                    <td>{currency.format(item.plant.price || 0)}</td>
                    <td>
                      <div className="quantity-control">
                        <button
                          className="btn btn-outline-secondary btn-sm"
                          onClick={() => updateQuantity(item.cartId, item.quantity - 1)}
                          type="button"
                          aria-label={`Decrease ${item.plant.plantName} quantity`}
                        >
                          <FaMinus size={12} aria-hidden="true" />
                        </button>
                        <span>{item.quantity}</span>
                        <button
                          className="btn btn-outline-secondary btn-sm"
                          onClick={() => updateQuantity(item.cartId, item.quantity + 1)}
                          type="button"
                          aria-label={`Increase ${item.plant.plantName} quantity`}
                        >
                          <FaPlus size={12} aria-hidden="true" />
                        </button>
                      </div>
                    </td>
                    <td>{currency.format((item.plant.price || 0) * item.quantity)}</td>
                    <td>
                      <button
                        className="btn btn-outline-danger btn-sm"
                        onClick={() => removeItem(item.cartId)}
                        type="button"
                      >
                        <FaTrash aria-hidden="true" /> Remove
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          <aside className="summary-panel mt-4">
            <h2 className="panel-title">Order summary</h2>
            <div className="summary-row">
              <span>Items</span>
              <strong>{cartItems.length}</strong>
            </div>
            <div className="summary-row">
              <span>Delivery</span>
              <strong>Calculated later</strong>
            </div>
            <div className="summary-row total">
              <span>Total</span>
              <strong>{currency.format(totalAmount)}</strong>
            </div>
            <button className="btn btn-success w-100 mt-3 py-2" onClick={placeOrder} type="button">
              Place order
            </button>
          </aside>
        </>
      )}
    </main>
  );
}

export default Cart;
