import React, { useEffect, useState } from "react";
import {
    fetchStores,
    fetchUserDetails,
    createStore,
    deleteStore,
} from "../../services/api";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "./StoreManagement.css";

const StoreManagement = () => {
    const [stores, setStores] = useState([]);
    const [loggedInUserRole, setLoggedInUserRole] = useState("");
    const [error, setError] = useState("");
    const [showConfirmation, setShowConfirmation] = useState(false);
    const [storeToDelete, setStoreToDelete] = useState(null);
    const [newStore, setNewStore] = useState({
        title: "",
        address: "",
        enable: 1, // Default to Active
    });

    // Fetch stores and user role on component mount
    useEffect(() => {
        const loadData = async () => {
            try {
                const [storeData, loggedInUser] = await Promise.all([
                    fetchStores(),
                    fetchUserDetails(),
                ]);
                setStores(storeData);

                // Determine logged-in user's role
                const roles = loggedInUser.roles.map((role) => role.name);
                if (roles.includes("SUPER_ADMIN")) {
                    setLoggedInUserRole("SUPER_ADMIN");
                }
            } catch (err) {
                setError("Failed to fetch data.");
                console.error("Error:", err);
            }
        };

        loadData();
    }, []);

    // Open confirmation dialog for deleting a store
    const openConfirmationDialog = (store) => {
        setStoreToDelete(store);
        setShowConfirmation(true);
    };

    // Close confirmation dialog
    const closeConfirmationDialog = () => {
        setShowConfirmation(false);
        setStoreToDelete(null);
    };

    // Handle deleting a store
    const confirmDelete = async () => {
        if (!storeToDelete) return;

        if (loggedInUserRole !== "SUPER_ADMIN") {
            toast.warning("You are not authorized to delete stores.");
            return;
        }

        try {
            await deleteStore(storeToDelete.id);
            setStores(stores.filter((store) => store.id !== storeToDelete.id));
            toast.success(`Η αποθήκη "${storeToDelete.title}" διαγράφηκε επιτυχώς.`);
        } catch (err) {
            console.error("Error deleting store:", err);
            toast.error("Αποτυχία διαγραφής αποθήκης. Ελέγξτε εάν υπάρχουν σχετικές εγγραφές.");
        }

        closeConfirmationDialog();
    };

    // Handle creating a store
    const handleCreate = async () => {
        if (loggedInUserRole !== "SUPER_ADMIN") {
            toast.warning("You are not authorized to create stores.");
            return;
        }

        if (!newStore.title.trim() || !newStore.address.trim()) {
            toast.warning("Ο Τίτλος και η Διεύθυνση είναι απαραίτητα.");
            return;
        }

        try {
            const createdStore = await createStore(newStore);
            setStores([...stores, createdStore]);
            setNewStore({ title: "", address: "", enable: 1 }); // Reset form
            toast.success("Η αποθήκη δημιουργήθηκε επιτυχώς.");
        } catch (err) {
            console.error("Error creating store:", err);
            toast.error("Αποτυχία δημιουργίας αποθήκης.");
        }
    };

    // Handle cancel action
    const handleCancel = () => {
        setNewStore({ title: "", address: "", enable: 1 }); // Reset form
    };

    return (
        <div className="store-management-container">
            <ToastContainer />
            <h2>Διαχείριση Αποθηκών</h2>
            {error && <p className="error-message">{error}</p>}

            {/* Create Store Form - Visible only to SUPER_ADMIN */}
            {loggedInUserRole === "SUPER_ADMIN" && (
                <div className="store-create-form">
                    <input
                        type="text"
                        placeholder="Εισάγετε τίτλο αποθήκης"
                        value={newStore.title}
                        onChange={(e) => setNewStore({ ...newStore, title: e.target.value })}
                    />
                    <input
                        type="text"
                        placeholder="Εισάγετε διεύθυνση αποθήκης"
                        value={newStore.address}
                        onChange={(e) => setNewStore({ ...newStore, address: e.target.value })}
                    />
                    <label>
                        Enable:
                        <input
                            type="checkbox"
                            checked={newStore.enable === 1}
                            onChange={(e) =>
                                setNewStore({ ...newStore, enable: e.target.checked ? 1 : 0 })
                            }
                        />
                    </label>
                    <button className="create-button" onClick={handleCreate}>
                        Δημιουργία Αποθήκης
                    </button>
                    <button className="cancel-button" onClick={handleCancel}>
                        Ακύρωση
                    </button>
                </div>
            )}

            {/* Store Table */}
            <table className="store-table">
                <thead>
                <tr>
                    <th>ΤΙΤΛΟΣ</th>
                    <th>ΔΙΕΥΘΥΝΣΗ</th>
                    <th>ΚΑΤΑΣΤΑΣΗ</th>
                    <th>ΕΝΕΡΓΕΙΕΣ</th>
                </tr>
                </thead>
                <tbody>
                {stores.map((store) => (
                    <tr key={store.id}>
                        <td>{store.title}</td>
                        <td>{store.address}</td>
                        <td>{store.enable === 1 ? "Active" : "Inactive"}</td>
                        <td>
                            {loggedInUserRole === "SUPER_ADMIN" && (
                                <button
                                    className="delete-button"
                                    onClick={() => openConfirmationDialog(store)}
                                >
                                    Διαγραφή
                                </button>
                            )}
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            {/* Confirmation Dialog */}
            {showConfirmation && (
                <div className="confirmation-dialog">
                    <div className="confirmation-content">
                        <p>
                            Είστε σίγουροι ότι θέλετε να διαγράψετε την αποθήκη{" "}
                            <strong>{storeToDelete?.title}</strong>;
                        </p>
                        <div className="store-confirmation-actions">
                            <button
                                className="store-cancel-button"
                                onClick={closeConfirmationDialog}
                            >
                                Ακύρωση
                            </button>
                            <button className="store-confirm-button" onClick={confirmDelete}>
                                Επιβεβαίωση
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default StoreManagement;
