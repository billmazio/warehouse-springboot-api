import React, { useEffect, useState } from "react";
import {
    fetchStores,
    createStore,
    deleteStore
} from "../../services/api";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "./StoreManagement.css";

const StoreManagement = () => {
    const [stores, setStores] = useState([]);
    const [error, setError] = useState("");
    const [showConfirmation, setShowConfirmation] = useState(false);
    const [storeToDelete, setStoreToDelete] = useState(null);
    const [newStore, setNewStore] = useState({
        title: "",
        address: "",
    });

    useEffect(() => {
        const loadStores = async () => {
            try {
                const storeData = await fetchStores();
                setStores(storeData);
            } catch (err) {
                setError("Failed to fetch store data.");
                console.error("Error:", err);
            }
        };

        loadStores();
    }, []);

    const openConfirmationDialog = (store) => {
        setStoreToDelete(store);
        setShowConfirmation(true);
    };

    const closeConfirmationDialog = () => {
        setShowConfirmation(false);
        setStoreToDelete(null);
    };

    const confirmDelete = async () => {
        if (!storeToDelete) return;

        try {
            await deleteStore(storeToDelete.id);
            setStores(stores.filter((store) => store.id !== storeToDelete.id));
            toast.success(`Η αποθήκη "${storeToDelete.title}" διαγράφηκε επιτυχώς.`);
        } catch (err) {
            console.error("Error deleting store:", err);
            toast.error("Αποτυχία διαγραφής αποθήκης.");
        }

        closeConfirmationDialog();
    };

    const handleCreate = async () => {
        if (!newStore.title.trim() || !newStore.address.trim()) {
            toast.warning("Ο Τίτλος και η Διεύθυνση είναι απαραίτητα.");
            return;
        }

        try {
            const createdStore = await createStore(newStore);
            setStores([...stores, createdStore]);
            setNewStore({ title: "", address: "" });
            toast.success("Η αποθήκη δημιουργήθηκε επιτυχώς.");
        } catch (err) {
            console.error("Error creating store:", err);
            toast.error("Αποτυχία δημιουργίας αποθήκης.");
        }
    };

    return (
        <div className="store-management-container">
            <ToastContainer />
            <h2>Διαχείριση Αποθηκών</h2>
            {error && <p className="error-message">{error}</p>}

            <div className="create-store-form">
                <input
                    type="text"
                    placeholder="Εισάγετε τίτλο αποθήκης"
                    value={newStore.title}
                    onChange={(e) => setNewStore({...newStore, title: e.target.value})}
                />
                <input
                    type="text"
                    placeholder="Εισάγετε διεύθυνση αποθήκης"
                    value={newStore.address}
                    onChange={(e) => setNewStore({...newStore, address: e.target.value})}
                />
                <button className="create-button" onClick={handleCreate}>
                    Δημιουργία Αποθήκης
                </button>
                <button
                    className="cancel-button"
                    onClick={() => setNewStore({title: "", address: ""})}
                >
                    Ακύρωση
                </button>
            </div>

            <table className="store-table">
                <thead>
                <tr>
                    <th>ΤΙΤΛΟΣ</th>
                    <th>ΔΙΕΥΘΥΝΣΗ</th>
                    <th>ΕΝΕΡΓΕΙΕΣ</th>
                </tr>
                </thead>
                <tbody>
                {stores.map((store) => (
                    <tr key={store.id}>
                        <td>{store.title}</td>
                        <td>{store.address}</td>
                        <td>
                            <button
                                className="delete-button"
                                onClick={() => openConfirmationDialog(store)}
                            >
                                Διαγραφή
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            {showConfirmation && (
                <div className="confirmation-dialog">
                    <div className="confirmation-content">
                        <p>
                            Είστε σίγουροι ότι θέλετε να διαγράψετε την αποθήκη{" "}
                            <strong>{storeToDelete?.title}</strong>;
                        </p>
                        <div className="store-confirmation-actions">
                            <button className="store-cancel-button" onClick={closeConfirmationDialog}>
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
