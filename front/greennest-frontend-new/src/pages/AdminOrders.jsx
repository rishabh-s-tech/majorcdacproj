import { useCallback, useEffect, useState } from "react";
import API from "../api/axiosConfig";

const statuses = [
  "PLACED",
  "PACKED",
  "SHIPPED",
  "DELIVERED",
  "CANCELLED"
];

const currency = new Intl.NumberFormat("en-IN", {
  style: "currency",
  currency: "INR",
  maximumFractionDigits: 0
});

function AdminOrders() {
  const [orders, setOrders] = useState([]);
  const [message, setMessage] = useState("");
  const [isLoading, setIsLoading] = useState(true);

  const loadOrders = useCallback(() => {
    API.get("/orders")
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

  useEffect(() => {
    loadOrders();
  }, [loadOrders]);

  const updateStatus = (orderId, status) => {
    API.put(`/orders/${orderId}/status?status=${status}`)
      .then(() => {
        setMessage("Order status updated");
        loadOrders();
      })
      .catch(() => {
        setMessage("Unable to update order status");
      });
  };

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
          <p className="section-kicker">Admin fulfilment</p>
          <h1 className="page-title">Manage orders</h1>
          <p className="page-subtitle mt-3 mb-0">Review purchases and update fulfilment status.</p>
        </div>
        <span className="stock-chip">{orders.length} orders</span>
      </div>

      {message && (
        <div className="alert alert-info text-center">
          {message}
        </div>
      )}

      {isLoading ? (
        <div className="empty-state">Loading orders...</div>
      ) : orders.length === 0 ? (
        <div className="empty-state">No orders found.</div>
      ) : (
        <div className="d-flex flex-column gap-4">
          {orders.map((order) => (
            <article className="order-card" key={order.orderId}>
              <div className="order-card-header">
                <div>
                  <p className="section-kicker mb-1">Order #{order.orderId}</p>
                  <strong>{formatDate(order.orderDate)}</strong>
                  <div className="muted-copy small mt-1">
                    {order.user?.name} | {order.user?.email}
                  </div>
                </div>

                <div className="d-flex align-items-center gap-3 flex-wrap">
                  <div className="price">
                    {currency.format(order.totalAmount || 0)}
                  </div>
                  <select
                    className="form-select"
                    value={order.status}
                    onChange={(event) => updateStatus(order.orderId, event.target.value)}
                    aria-label={`Update order ${order.orderId} status`}
                    style={{ width: "170px" }}
                  >
                    {statuses.map((status) => (
                      <option value={status} key={status}>
                        {status}
                      </option>
                    ))}
                  </select>
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

export default AdminOrders;
