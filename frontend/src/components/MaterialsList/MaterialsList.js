import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { fetchMaterialsByStoreId } from "../../services/api";
import "./MaterialsList.css";

const MaterialsList = () => {
    const { storeId } = useParams(); // Get the store ID from the URL
    const navigate = useNavigate(); // To navigate back to stores
    const [materials, setMaterials] = useState([]);
    const [filteredMaterials, setFilteredMaterials] = useState([]);
    const [filterText, setFilterText] = useState("");
    const [filterSize, setFilterSize] = useState("");
    const [error, setError] = useState("");

    useEffect(() => {
        const loadMaterials = async () => {
            try {
                const materialsData = await fetchMaterialsByStoreId(storeId);
                setMaterials(materialsData);
                setFilteredMaterials(materialsData); // Initialize with full materials list
            } catch (err) {
                setError("Failed to fetch materials.");
                console.error("Error:", err);
            }
        };

        loadMaterials();
    }, [storeId]);

    const handleFilter = () => {
        const filtered = materials.filter((material) => {
            const matchesText = filterText
                ? material.text.toLowerCase().includes(filterText.toLowerCase())
                : true;
            const matchesSize = filterSize ? material.sizeId === parseInt(filterSize, 10) : true;
            return matchesText && matchesSize;
        });
        setFilteredMaterials(filtered);
    };

    return (
        <div className="materials-list-container">
            <button onClick={() => navigate("/manage-stores")} className="back-button">
                Πίσω στις Αποθήκες
            </button>

            <h2>Υλικά για Αποθήκη ID: {storeId}</h2>
            {error && <p className="error-message">{error}</p>}

            {/* Filters */}
            <div className="filters">
                <input
                    type="text"
                    placeholder="Φίλτρο ανά κείμενο (π.χ. μπλούζες)"
                    value={filterText}
                    onChange={(e) => setFilterText(e.target.value)}
                />
                <input
                    type="number"
                    placeholder="Φίλτρο ανά μέγεθος ID"
                    value={filterSize}
                    onChange={(e) => setFilterSize(e.target.value)}
                />
                <button onClick={handleFilter}>Φιλτράρισμα</button>
            </div>

            {/* Materials List */}
            {filteredMaterials.length > 0 ? (
                <ul>
                    {filteredMaterials.map((material) => (
                        <li key={material.id}>
                            <strong>{material.text}</strong> - Μέγεθος: {material.sizeId}, Ποσότητα: {material.quantity}
                        </li>
                    ))}
                </ul>
            ) : (
                <p>Δεν υπάρχουν υλικά για αυτήν την αποθήκη.</p>
            )}
        </div>
    );
};

export default MaterialsList;
