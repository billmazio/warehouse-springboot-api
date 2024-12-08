import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { fetchMaterialsByStoreId } from "../../services/api";
import "./MaterialsList.css";

const MaterialsList = () => {
    const { storeId } = useParams(); // Get the store ID from the URL
    const [materials, setMaterials] = useState([]);
    const [error, setError] = useState("");

    useEffect(() => {
        const loadMaterials = async () => {
            try {
                const materialsData = await fetchMaterialsByStoreId(storeId);
                setMaterials(materialsData);
            } catch (err) {
                setError("Failed to fetch materials.");
                console.error("Error:", err);
            }
        };

        loadMaterials();
    }, [storeId]);

    return (
        <div className="materials-list-container">
            <h2>Υλικά για Αποθήκη ID: {storeId}</h2>
            {error && <p className="error-message">{error}</p>}
            {materials.length > 0 ? (
                <ul>
                    {materials.map((material) => (
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
