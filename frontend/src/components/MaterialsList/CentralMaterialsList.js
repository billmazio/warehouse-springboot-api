import React, { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { fetchAllMaterialsPaginated, fetchSizes, deleteMaterial, editMaterial } from "../../services/api";

import "./MaterialsList.css";

const CentralMaterialsList = () => {
    const navigate = useNavigate();

    const [materials, setMaterials] = useState([]);
    const [sizes, setSizes] = useState([]);
    const [filterText, setFilterText] = useState("");
    const [filterSize, setFilterSize] = useState("");
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [editingMaterial, setEditingMaterial] = useState(null);
    const [editFormData, setEditFormData] = useState({ text: "", sizeId: "", quantity: "" });
    const [showConfirmation, setShowConfirmation] = useState(false);
    const [materialToDelete, setMaterialToDelete] = useState(null);
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

    // Open confirmation dialog for delete
    const openConfirmationDialog = (material) => {
        setMaterialToDelete(material);
        setShowConfirmation(true);
    };

    // Close confirmation dialog
    const closeConfirmationDialog = () => {
        setShowConfirmation(false);
        setMaterialToDelete(null);
    };

    const handleEditClick = (material) => {
        setEditingMaterial(material);
        setEditFormData({
            text: material.text,
            sizeId: material.sizeId,
            quantity: material.quantity,
        });
    };
    const handleSaveEdit = async () => {
        try {
            await editMaterial(editingMaterial.id, editFormData); // Send updated data to API
            setEditingMaterial(null); // Close the form
            loadMaterials(); // Refresh the list
        } catch (err) {
            console.error("Failed to update material", err);
            setError("Failed to update material.");
        }
    };


    // Confirm delete
    const confirmDelete = async () => {
        try {
            await deleteMaterial(materialToDelete.id);
            setMaterials(materials.filter((m) => m.id !== materialToDelete.id));
            closeConfirmationDialog();
        } catch (err) {
            console.error("Failed to delete material", err);
        }
    };

    const handleFilter = () => {
        setCurrentPage(0);
        loadMaterials();
    };

    return (
        <div className="store-management-container">
            <button onClick={() => navigate("/dashboard")} className="back-button">
                Πίσω στην Κεντρική Διαχείριση
            </button>

            <h2>Διαχείριση Ενδυμάτων</h2>

            <div className="store-create-form">
                <input
                    type="text"
                    placeholder="Φίλτρο ανά προϊόν"
                    value={filterText}
                    onChange={(e) => setFilterText(e.target.value)}
                />
                <select value={filterSize} onChange={(e) => setFilterSize(e.target.value)}>
                    <option  value="">Φίλτρο ανά μέγεθος</option>
                    {sizes.map((size) => (
                        <option key={size.id} value={size.id}>
                            {size.name}
                        </option>
                    ))}
                </select>
                <button className="create-button" onClick={handleFilter}>
                    Φιλτράρισμα
                </button>
            </div>

            <table className="store-table">
                <thead>
                <tr>
                    <th>Προϊόν</th>
                    <th>Μέγεθος</th>
                    <th>Ποσότητα</th>
                    <th>Ενέργειες</th>
                </tr>
                </thead>
                <tbody>
                {materials.length > 0 ? (
                    materials.map((material) => (
                        <tr key={material.id}>
                            <td>{material.text}</td>
                            <td>{material.sizeName}</td>
                            <td>{material.quantity}</td>
                            <td>
                                <button
                                    className="view-button"
                                    onClick={() => handleEditClick(material)}
                                >
                                    Επεξεργασία
                                </button>

                                <button
                                    className="delete-button"
                                    onClick={() => openConfirmationDialog(material)}
                                >
                                    Διαγραφή
                                </button>
                            </td>
                        </tr>
                    ))
                ) : (
                    <tr>
                        <td colSpan="4">Δεν υπάρχουν ενδύματα.</td>
                    </tr>
                )}
                </tbody>
            </table>

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
            {editingMaterial && (
                <div className="edit-modal">
                    <h3>Επεξεργασία Προϊόντος</h3>
                    <input
                        type="text"
                        placeholder="Προϊόν"
                        value={editFormData.text}
                        onChange={(e) => setEditFormData({ ...editFormData, text: e.target.value })}
                    />
                    <select
                        value={editFormData.sizeId}
                        onChange={(e) => setEditFormData({ ...editFormData, sizeId: e.target.value })}
                    >
                        <option value="">Επιλέξτε μέγεθος</option>
                        {sizes.map((size) => (
                            <option key={size.id} value={size.id}>
                                {size.name}
                            </option>
                        ))}
                    </select>
                    <input
                        type="number"
                        placeholder="Ποσότητα"
                        value={editFormData.quantity}
                        onChange={(e) => setEditFormData({ ...editFormData, quantity: e.target.value })}
                    />
                    <div className="materials-confirmation-actions">
                        <button className="materials-cancel-button" onClick={() => setEditingMaterial(null)}>
                            Ακύρωση
                        </button>
                        <button className="materials-confirm-button" onClick={handleSaveEdit}>
                            Αποθήκευση
                        </button>
                    </div>
                </div>
            )}


            {/* Confirmation Dialog */}
            {showConfirmation && (
                <div className="confirmation-dialog">
                    <div className="confirmation-content">
                        <p>
                            Είστε σίγουροι ότι θέλετε να διαγράψετε το προϊόν{" "}
                            <strong>{materialToDelete?.text}</strong>;
                        </p>
                        <div className="materials-confirmation-actions">
                            <button
                                className="materials-cancel-button"
                                onClick={closeConfirmationDialog}
                            >
                                Ακύρωση
                            </button>
                            <button
                                className="materials-confirm-button"
                                onClick={confirmDelete}
                            >
                                Επιβεβαίωση
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );

};

export default CentralMaterialsList;
