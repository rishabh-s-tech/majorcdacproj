import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { FaShoppingCart } from "react-icons/fa";
import API from "../api/axiosConfig";
import { useAuth } from "../context/AuthContext";
import { useCart } from "../context/CartContext";

const currency = new Intl.NumberFormat("en-IN", {
  style: "currency",
  currency: "INR",
  maximumFractionDigits: 0
});

function Plants() {
  const navigate = useNavigate();
  const { user } = useAuth();
  const { refreshCount } = useCart();

  const [plants, setPlants] = useState([]);
  const [message, setMessage] = useState("");
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    API.get("/plants")
      .then((response) => {
        setPlants(response.data);
      })
      .catch((error) => {
        console.log(error);
        setMessage("Unable to load plants right now");
      })
      .finally(() => {
        setIsLoading(false);
      });
  }, []);

  const addToCart = (plantId) => {
    if (!user) {
      navigate("/login");
      return;
    }

    API.post("/cart/add", {
      plantId: plantId,
      quantity: 1
    })
      .then(() => {
        setMessage("Plant added to cart");
        refreshCount();
      })
      .catch((error) => {
        setMessage(error.response?.data?.message || "Unable to add this plant to your cart");
      });
  };

  const availableCount = useMemo(() => {
    return plants.filter((plant) => Number(plant.stockQuantity) > 0).length;
  }, [plants]);

  return (
    <main className="page-shell">
      <div className="toolbar">
        <div>
          <p className="section-kicker">Shop the collection</p>
          <h1 className="page-title">Plants for every corner</h1>
          <p className="page-subtitle mt-3 mb-0">
            Browse curated indoor and outdoor greens with simple pricing and quick cart actions.
          </p>
        </div>
        <span className="stock-chip">{availableCount} in stock</span>
      </div>

      {message && (
        <div className={`alert ${message.includes("added") ? "alert-success" : "alert-warning"} text-center`}>
          {message}
        </div>
      )}

      {isLoading ? (
        <div className="empty-state">Loading fresh inventory...</div>
      ) : plants.length === 0 ? (
        <div className="empty-state">No plants are available yet.</div>
      ) : (
        <div className="product-grid">
          {plants.map((plant) => (
            <article className="card product-card" key={plant.plantId}>
              <img
                src={`/images/${plant.imageUrl}`}
                className="product-media"
                alt={plant.plantName}
              />

              <div className="card-body">
                <div>
                  <span className="stock-chip">
                    {Number(plant.stockQuantity) > 0 ? `${plant.stockQuantity} available` : "Out of stock"}
                  </span>
                  <h3 className="mt-3">{plant.plantName}</h3>
                  <p className="product-description mb-0">
                    {plant.description}
                  </p>
                </div>

                <div className="price-row">
                  <span className="price">{currency.format(plant.price || 0)}</span>
                  <button
                    className="btn btn-success px-3"
                    onClick={() => addToCart(plant.plantId)}
                    disabled={Number(plant.stockQuantity) <= 0}
                    type="button"
                  >
                    <FaShoppingCart aria-hidden="true" /> Add
                  </button>
                </div>
              </div>
            </article>
          ))}
        </div>
      )}
    </main>
  );
}

export default Plants;
