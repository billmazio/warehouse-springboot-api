import React, { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { fetchAllMaterialsPaginated, fetchSizes } from "../../services/api";
import "./MaterialsList.css";

const CentralMaterialsList = () => {
    const navigate = useNavigate();

    const [materials, setMaterials] = useState([]);
    const [sizes, setSizes] = useState([]);
    const [filterText, setFilterText] = useState("");
    const [filterSize, setFilterSize] = useState("");
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [error, setError] = useState("");

    // Fetch all materials with filters and pagination
    const loadMaterials = useCallback(async () => {
        try {
            const response = await fetchAllMaterialsPaginated(currentPage, 5, filterText, filterSize);
            setMaterials(response.content || []);
            setTotalPages(response.totalPages || 0);
        } catch (err) {
            setError("Failed to fetch materials.");
            console.error(err);
        }
    }, [currentPage, filterText, filterSize]);

    // Fetch available sizes
    const loadSizes = useCallback(async () => {
        try {
            const sizesData = await fetchSizes();
            setSizes(sizesData);
        } catch (err) {
            setError("Failed to fetch sizes.");
            console.error(err);
        }
    }, []);

    useEffect(() => {
        loadMaterials();
        loadSizes();
    }, [loadMaterials, loadSizes]);

    const handleFilter = () => {
        setCurrentPage(0);
        loadMaterials();
    };

    return (
        <div className="materials-list-container">
            <button onClick={() => navigate("/dashboard")} className="back-button">
                Πίσω στην Κεντρική Διαχείριση
            </button>

            <h2>Ενδύματα</h2>
            {error && <p className="error-message">{error}</p>}

            <div className="filters">
                <input
                    type="text"
                    placeholder="Φίλτρο ανά προϊόν"
                    value={filterText}
                    onChange={(e) => setFilterText(e.target.value)}
                />
                <select value={filterSize} onChange={(e) => setFilterSize(e.target.value)}>
                    <option value="">Φίλτρο ανά μέγεθος</option>
                    {sizes.map((size) => (
                        <option key={size.id} value={size.id}>
                            {size.name}
                        </option>
                    ))}
                </select>
                <button onClick={handleFilter}>Φιλτράρισμα</button>
            </div>

            {materials.length > 0 ? (
                <ul>
                    {materials.map((material) => (
                        <li key={material.id}>
                            <strong>{material.text}</strong> - Μέγεθος: {material.sizeName}, Ποσότητα: {material.quantity}
                        </li>
                    ))}
                </ul>
            ) : (
                <p>Δεν υπάρχουν ενδύματα.</p>
            )}

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
                    onClick={() => setCurrentPage((prev) => Math.min(prev + 1, totalPages - 1))}
                    disabled={currentPage >= totalPages - 1}
                >
                    Επόμενη
                </button>
            </div>
        </div>
    );
};

export default CentralMaterialsList;
