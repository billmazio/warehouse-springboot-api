import React, { useState, useEffect, useCallback } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
    fetchMaterialsPaginated,
    fetchStoreDetails,
    fetchSizes,
} from "../../services/api";
import "./MaterialsList.css";

const MaterialsList = () => {
    const [storeTitle, setStoreTitle] = useState("");
    const [materials, setMaterials] = useState([]);
    const [sizes, setSizes] = useState([]);
    const [error, setError] = useState("");
    const [filterText, setFilterText] = useState("");
    const [filterSize, setFilterSize] = useState("");
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const { storeId } = useParams();
    const navigate = useNavigate();

    // Function to load materials with filters and pagination
    const loadMaterials = useCallback(async () => {
        try {
            const response = await fetchMaterialsPaginated(
                storeId,
                currentPage,
                5, // Page size
                filterText,
                filterSize
            );
            setMaterials(response.content);
            setTotalPages(response.totalPages);
        } catch (err) {
            setError("Failed to fetch materials.");
            console.error("Error fetching materials:", err);
        }
    }, [storeId, currentPage, filterText, filterSize]);

    // Load store details
    const loadStoreDetails = useCallback(async () => {
        try {
            const storeData = await fetchStoreDetails(storeId);
            setStoreTitle(storeData.title);
        } catch (err) {
            setError("Failed to fetch store details.");
            console.error("Error fetching store details:", err);
        }
    }, [storeId]);

    // Load available sizes
    const loadSizes = useCallback(async () => {
        try {
            const sizesData = await fetchSizes();
            setSizes(sizesData);
        } catch (err) {
            setError("Failed to fetch sizes.");
            console.error("Error fetching sizes:", err);
        }
    }, []);

    // Load materials, store details, and sizes on mount or when dependencies change
    useEffect(() => {
        loadMaterials();
        loadStoreDetails();
        loadSizes();
    }, [loadMaterials, loadStoreDetails, loadSizes]);

    // Filter handler
    const handleFilter = () => {
        setCurrentPage(0); // Reset to the first page when applying filters
        loadMaterials();
    };

    return (
        <div className="materials-list-container">
            {/* Back Button */}
            <button
                onClick={() => navigate("/dashboard/manage-stores")}
                className="back-button"
            >
                Πίσω στις Αποθήκες
            </button>

            <h2>Υλικά για Αποθήκη: {storeTitle || "Loading..."}</h2>
            {error && <p className="error-message">{error}</p>}

            {/* Filters */}
            <div className="filters">
                <input
                    type="text"
                    placeholder="Φίλτρο ανά προϊόν (π.χ. μπλούζες)"
                    value={filterText}
                    onChange={(e) => setFilterText(e.target.value)}
                />
                <select
                    value={filterSize}
                    onChange={(e) => setFilterSize(e.target.value)}
                >
                    <option value="">Φίλτρο ανά μέγεθος</option>
                    {sizes.map((size) => (
                        <option key={size.id} value={size.id}>
                            {size.name}
                        </option>
                    ))}
                </select>
                <button onClick={handleFilter}>Φιλτράρισμα</button>
            </div>

            {/* Materials List */}
            {materials.length > 0 ? (
                <ul>
                    {materials.map((material) => (
                        <li key={material.id}>
                            <strong>{material.text}</strong> - Μέγεθος:{" "}
                            {material.sizeName}, Ποσότητα: {material.quantity}
                        </li>
                    ))}
                </ul>
            ) : (
                <p>Δεν υπάρχουν υλικά για αυτήν την αποθήκη.</p>
            )}

            {/* Pagination Controls */}
            <div className="pagination">
                <button
                    onClick={() => setCurrentPage((prev) => Math.max(prev - 1, 0))}
                    disabled={currentPage === 0}
                >
                    Προηγούμενη
                </button>
                <span>
                    Σελίδα {currentPage + 1} από {totalPages}
                </span>
                <button
                    onClick={() =>
                        setCurrentPage((prev) => Math.min(prev + 1, totalPages - 1))
                    }
                    disabled={currentPage >= totalPages - 1}
                >
                    Επόμενη
                </button>
            </div>
        </div>
    );
};

export default MaterialsList;
