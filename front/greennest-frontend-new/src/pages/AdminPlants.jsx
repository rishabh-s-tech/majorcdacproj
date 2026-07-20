import { useCallback, useEffect, useState } from "react";
import API from "../api/axiosConfig";

const emptyForm = {
  plantName: "",
  description: "",
  price: "",
  stockQuantity: "",
  imageUrl: "",
  categoryId: ""
};

const currency = new Intl.NumberFormat("en-IN", {
  style: "currency",
  currency: "INR",
  maximumFractionDigits: 0
});

function AdminPlants() {
  const [plants, setPlants] = useState([]);
  const [categories, setCategories] = useState([]);
  const [formData, setFormData] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);
  const [message, setMessage] = useState("");

  const loadData = useCallback(() => {
    API.get("/plants")
      .then((response) => {
        setPlants(response.data);
      })
      .catch(() => {
        setMessage("Unable to load plants");
      });

    API.get("/categories")
      .then((response) => {
        setCategories(response.data);
      })
      .catch(() => {
        setMessage("Unable to load categories");
      });
  }, []);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const handleChange = (event) => {
    setFormData({
      ...formData,
      [event.target.name]: event.target.value
    });
  };

  const resetForm = () => {
    setFormData(emptyForm);
    setEditingId(null);
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    setMessage("");

    const plantData = {
      plantName: formData.plantName,
      description: formData.description,
      price: Number(formData.price),
      stockQuantity: Number(formData.stockQuantity),
      imageUrl: formData.imageUrl,
      category: {
        categoryId: Number(formData.categoryId)
      }
    };

    const request = editingId
      ? API.put(`/plants/${editingId}`, plantData)
      : API.post(`/plants/${formData.categoryId}`, plantData);

    request
      .then(() => {
        setMessage(editingId ? "Plant updated" : "Plant added");
        resetForm();
        loadData();
      })
      .catch((error) => {
        setMessage(error.response?.data?.message || "Unable to save plant");
      });
  };

  const editPlant = (plant) => {
    setEditingId(plant.plantId);
    setFormData({
      plantName: plant.plantName || "",
      description: plant.description || "",
      price: plant.price || "",
      stockQuantity: plant.stockQuantity || "",
      imageUrl: plant.imageUrl || "",
      categoryId: plant.category?.categoryId || ""
    });
  };

  const deletePlant = (plantId) => {
    API.delete(`/plants/${plantId}`)
      .then(() => {
        setMessage("Plant deleted");
        loadData();
      })
      .catch(() => {
        setMessage("Unable to delete plant");
      });
  };

  return (
    <main className="page-shell">
      <div className="toolbar">
        <div>
          <p className="section-kicker">Admin inventory</p>
          <h1 className="page-title">Manage plants</h1>
          <p className="page-subtitle mt-3 mb-0">Create, edit, and review product availability.</p>
        </div>
        <span className="stock-chip">{plants.length} products</span>
      </div>

      {message && (
        <div className="alert alert-info text-center">
          {message}
        </div>
      )}

      <div className="admin-grid">
        <form className="form-card" onSubmit={handleSubmit}>
          <h2 className="panel-title mb-3">
            {editingId ? "Edit plant" : "Add plant"}
          </h2>

          <div className="mb-3">
            <label className="form-label" htmlFor="plantName">Plant name</label>
            <input
              id="plantName"
              className="form-control"
              name="plantName"
              value={formData.plantName}
              onChange={handleChange}
              required
            />
          </div>

          <div className="mb-3">
            <label className="form-label" htmlFor="description">Description</label>
            <textarea
              id="description"
              className="form-control"
              name="description"
              value={formData.description}
              onChange={handleChange}
              rows="4"
              required
            />
          </div>

          <div className="row">
            <div className="col-md-6 mb-3">
              <label className="form-label" htmlFor="price">Price</label>
              <input
                id="price"
                type="number"
                className="form-control"
                name="price"
                value={formData.price}
                onChange={handleChange}
                min="0"
                required
              />
            </div>

            <div className="col-md-6 mb-3">
              <label className="form-label" htmlFor="stockQuantity">Stock</label>
              <input
                id="stockQuantity"
                type="number"
                className="form-control"
                name="stockQuantity"
                value={formData.stockQuantity}
                onChange={handleChange}
                min="0"
                required
              />
            </div>
          </div>

          <div className="mb-3">
            <label className="form-label" htmlFor="imageUrl">Image file name</label>
            <input
              id="imageUrl"
              className="form-control"
              name="imageUrl"
              value={formData.imageUrl}
              onChange={handleChange}
              placeholder="rose.jpg"
              required
            />
          </div>

          <div className="mb-4">
            <label className="form-label" htmlFor="categoryId">Category</label>
            <select
              id="categoryId"
              className="form-select"
              name="categoryId"
              value={formData.categoryId}
              onChange={handleChange}
              required
            >
              <option value="">Select category</option>
              {categories.map((category) => (
                <option value={category.categoryId} key={category.categoryId}>
                  {category.categoryName}
                </option>
              ))}
            </select>
          </div>

          <button className="btn btn-success w-100 py-2" type="submit">
            {editingId ? "Update plant" : "Add plant"}
          </button>

          {editingId && (
            <button
              className="btn btn-outline-secondary w-100 mt-2"
              type="button"
              onClick={resetForm}
            >
              Cancel
            </button>
          )}
        </form>

        <div className="table-responsive table-card">
          <table className="table align-middle">
            <thead>
              <tr>
                <th>Plant</th>
                <th>Category</th>
                <th>Price</th>
                <th>Stock</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {plants.map((plant) => (
                <tr key={plant.plantId}>
                  <td>
                    <div className="d-flex align-items-center gap-3">
                      <img
                        src={`/images/${plant.imageUrl}`}
                        alt={plant.plantName}
                        className="table-thumb"
                      />
                      <strong>{plant.plantName}</strong>
                    </div>
                  </td>
                  <td>{plant.category?.categoryName}</td>
                  <td>{currency.format(plant.price || 0)}</td>
                  <td>{plant.stockQuantity}</td>
                  <td>
                    <div className="d-flex gap-2 flex-wrap">
                      <button
                        className="btn btn-outline-success btn-sm"
                        onClick={() => editPlant(plant)}
                        type="button"
                      >
                        Edit
                      </button>
                      <button
                        className="btn btn-outline-danger btn-sm"
                        onClick={() => deletePlant(plant.plantId)}
                        type="button"
                      >
                        Delete
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </main>
  );
}

export default AdminPlants;
