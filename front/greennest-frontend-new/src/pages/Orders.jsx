import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import API from "../api/axiosConfig";

const currency = new Intl.NumberFormat("en-IN", {
  style: "currency",
  currency: "INR",
  maximumFractionDigits: 0
});

function Orders() {
  const [orders, setOrders] = useState([]);
  const [message, setMessage] = useState("");
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    API.get("/orders/me")
      .then((response) => {
        setOrders(response.data);
      })
      .catch(() => {
        setMessage("Unable to load orders");
      })
      .finally(() => {
        setIsLoading(false);
      });
  }, []);

  const formatDate = (dateValue) => {
    if (!dateValue) {
      return "";
    }

    return new Date(dateValue).toLocaleString();
  };

  return (
    <main className="page-shell">
      <div className="toolbar">
        <div>
          <p className="section-kicker">Order history</p>
          <h1 className="page-title">My orders</h1>
          <p className="page-subtitle mt-3 mb-0">Track every GreenNest purchase from placement to delivery.</p>
        </div>
        <Link className="btn btn-outline-secondary" to="/plants">Shop again</Link>
      </div>

      {message && (
        <div className="alert alert-warning text-center">
          {message}
        </div>
      )}

      {isLoading ? (
        <div className="empty-state">Loading orders...</div>
      ) : orders.length === 0 ? (
        <div className="empty-state">
          <h3>No orders placed yet</h3>
          <p className="muted-copy mb-4">Your plant orders will appear here after checkout.</p>
          <Link className="btn btn-success" to="/plants">Browse plants</Link>
        </div>
      ) : (
        <div className="d-flex flex-column gap-4">
          {orders.map((order) => (
            <article className="order-card" key={order.orderId}>
              <div className="order-card-header">
                <div>
                  <p className="section-kicker mb-1">Order #{order.orderId}</p>
                  <strong>{formatDate(order.orderDate)}</strong>
                </div>
                <div className="text-lg-end">
                  <span className="status-chip">{order.status}</span>
                  <div className="price mt-2">{currency.format(order.totalAmount || 0)}</div>
                </div>
              </div>

              <div className="table-responsive p-3">
                <table className="table align-middle">
                  <thead>
                    <tr>
                      <th>Plant</th>
                      <th>Quantity</th>
                      <th>Amount</th>
                    </tr>
                  </thead>
                  <tbody>
                    {order.orderItems.map((item) => (
                      <tr key={item.orderItemId}>
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
                        <td>{item.quantity}</td>
                        <td>{currency.format(item.price || 0)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </article>
          ))}
        </div>
      )}
    </main>
  );
}

export default Orders;
