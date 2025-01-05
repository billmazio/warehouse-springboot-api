import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { fetchOrders, createOrder, deleteOrder, fetchUsers, fetchMaterials, fetchStores, fetchSizes } from "../../services/api";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "./OrderManagement.css";

const centralStoreId = 1;

const OrderManagement = () => {
    const navigate = useNavigate();
    const [orders, setOrders] = useState([]);
    const [users, setUsers] = useState([]);
    const [stores, setStores] = useState([]);
    const [sizes, setSizes] = useState([]);
    const [uniqueMaterials, setUniqueMaterials] = useState([]);
    const [filteredSizes, setFilteredSizes] = useState([]);
    const [error, setError] = useState("");
    const [showConfirmation, setShowConfirmation] = useState(false);
    const [orderToDelete, setOrderToDelete] = useState(null);
    const [newOrder, setNewOrder] = useState({
        quantity: "",
        dateOfOrder: "",
        userId: "",
        storeId: "",
        materialId: "",
        status: "",
        sold: "",
        stock: "",
        sizeId: ""
    });
    const [distributionData, setDistributionData] = useState({
        receiverStoreId: "",
    });

    useEffect(() => {
        const loadData = async () => {
            try {
                const orderData = await fetchOrders();
                setOrders(orderData);

                const userData = await fetchUsers();
                setUsers(userData);

                const storeData = await fetchStores();
                setStores(storeData);

                const materialData = await fetchMaterials();
                const sizeData = await fetchSizes();
                setSizes(sizeData);

                // Extract unique materials
                const uniqueMaterialTypes = [...new Set(materialData.map(material => material.text))];
                setUniqueMaterials(uniqueMaterialTypes);
            } catch (err) {
                setError("Αποτυχία ανάκτησης δεδομένων.");
                console.error("Σφάλμα:", err);
            }
        };

        loadData();
    }, []);

    useEffect(() => {
        if (newOrder.materialId) {
            const materialSizes = sizes.filter(size => size.materialId === newOrder.materialId);
            setFilteredSizes(materialSizes);
        } else {
            setFilteredSizes([]);
        }
    }, [newOrder.materialId, sizes]);

    const openConfirmationDialog = (order) => {
        setOrderToDelete(order);
        setShowConfirmation(true);
    };

    const closeConfirmationDialog = () => {
        setShowConfirmation(false);
        setOrderToDelete(null);
    };

    const confirmDelete = async () => {
        if (!orderToDelete) return;
        try {
            await deleteOrder(orderToDelete.id);
            setOrders(orders.filter((order) => order.id !== orderToDelete.id));
            toast.success(`Η παραγγελία με ID "${orderToDelete.id}" διαγράφηκε με επιτυχία.`);
        } catch (err) {
            console.error("Σφάλμα κατά τη διαγραφή παραγγελίας:", err);
            toast.error("Αποτυχία διαγραφής παραγγελίας.");
        }
        closeConfirmationDialog();
    };

    const handleCreate = async () => {
        if (!newOrder.quantity || !newOrder.dateOfOrder || !newOrder.userId || !newOrder.storeId || !newOrder.materialId || !newOrder.sizeId) {
            toast.warning("Όλα τα πεδία είναι υποχρεωτικά.");
            return;
        }

        try {
            const createdOrder = await createOrder(newOrder);
            setOrders([...orders, createdOrder]);
            setNewOrder({
                quantity: "",
                dateOfOrder: "",
                userId: "",
                storeId: "",
                materialId: "",
                status: "",
                sold: "",
                stock: "",
                sizeId: ""
            });
            toast.success("Η παραγγελία δημιουργήθηκε με επιτυχία.");
        } catch (err) {
            console.error("Σφάλμα κατά τη δημιουργία παραγγελίας:", err);
            toast.error("Αποτυχία δημιουργίας παραγγελίας.");
        }
    };

    return (
        <div className="order-management-container">
            <ToastContainer/>
            <button onClick={() => navigate("/dashboard")} className="back-button">
                Πίσω στην Κεντρική Διαχείριση
            </button>

            <h2>Διαχείριση Παραγγελιών</h2>
            {error && <p className="error-message">{error}</p>}

            <div className="order-create-form">
                <input
                    type="number"
                    placeholder="Ποσότητα"
                    value={newOrder.quantity}
                    onChange={(e) => setNewOrder({...newOrder, quantity: e.target.value})}
                />
                <input
                    type="date"
                    placeholder="Ημερομηνία Παραγγελίας"
                    value={newOrder.dateOfOrder}
                    onChange={(e) => setNewOrder({...newOrder, dateOfOrder: e.target.value})}
                />
                <select
                    value={newOrder.userId}
                    onChange={(e) => setNewOrder({...newOrder, userId: e.target.value})}
                >
                    <option value="">Επιλέξτε Χρήστη</option>
                    {users.map(user => (
                        <option key={user.id} value={user.id}>
                            {user.username}
                        </option>
                    ))}
                </select>
                <select
                    value={distributionData.receiverStoreId}
                    onChange={(e) =>
                        setDistributionData({
                            ...distributionData,
                            receiverStoreId: e.target.value,
                        })
                    }
                >
                    <option value="">Επιλέξτε Αποθήκη Προορισμού</option>
                    {stores
                        .filter((store) => store.id !== centralStoreId)
                        .map((store) => (
                            <option key={store.id} value={store.id}>
                                {store.title}
                            </option>
                        ))}
                </select>
                <select
                    value={newOrder.materialId}
                    onChange={(e) => setNewOrder({...newOrder, materialId: e.target.value})}
                >
                    <option value="">Επιλέξτε Υλικό</option>
                    {uniqueMaterials.map((material, index) => (
                        <option key={index} value={material}>
                            {material}
                        </option>
                    ))}
                </select>
                <select
                    value={newOrder.sizeId}
                    onChange={(e) => setNewOrder({...newOrder, sizeId: e.target.value})}
                >
                    <option value="">Επιλέξτε Μέγεθος</option>
                    {filteredSizes.map(size => (
                        <option key={size.id} value={size.id}>
                            {size.name}
                        </option>
                    ))}
                </select>
                <input
                    type="text"
                    placeholder="Κατάσταση"
                    value={newOrder.status}
                    onChange={(e) => setNewOrder({...newOrder, status: e.target.value})}
                />
                <input
                    type="number"
                    placeholder="Πωλήσεις"
                    value={newOrder.sold}
                    onChange={(e) => setNewOrder({...newOrder, sold: e.target.value})}
                />
                <input
                    type="number"
                    placeholder="Απόθεμα"
                    value={newOrder.stock}
                    onChange={(e) => setNewOrder({...newOrder, stock: e.target.value})}
                />
                <button className="create-button" onClick={handleCreate}>
                    Δημιουργία Παραγγελίας
                </button>
            </div>

            <table className="order-table">
                <thead>
                <tr>
                    <th>ΠΟΣΟΤΗΤΑ</th>
                    <th>ΗΜΕΡΟΜΗΝΙΑ ΠΑΡΑΓΓΕΛΙΑΣ</th>
                    <th>ΧΡΗΣΤΗΣ</th>
                    <th>ΑΠΟΘΗΚΗ</th>
                    <th>ΥΛΙΚΟ</th>
                    <th>ΚΑΤΑΣΤΑΣΗ</th>
                    <th>ΠΟΥΛΗΜΕΝΟ</th>
                    <th>ΑΠΟΘΕΜΑ</th>
                    <th>ΕΝΕΡΓΕΙΕΣ</th>
                </tr>
                </thead>
                <tbody>
                {orders.map((order) => (
                    <tr key={order.id}>
                        <td>{order.quantity}</td>
                        <td>{order.dateOfOrder}</td>
                        <td>{order.user?.username}</td>
                        <td>{order.stores?.title}</td>
                        <td>{order.materials?.text}</td>
                        <td>{order.status}</td>
                        <td>{order.sold}</td>
                        <td>{order.stock}</td>
                        <td>
                            <button
                                className="delete-button"
                                onClick={() => openConfirmationDialog(order)}
                            >
                                Delete
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
                            Are you sure you want to delete Order ID <strong>{orderToDelete?.id}</strong>?
                        </p>
                        <div className="button-group">
                            <button className="cancel-button" onClick={closeConfirmationDialog}>
                                Cancel
                            </button>
                            <button className="confirm-button" onClick={confirmDelete}>
                                Confirm
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default OrderManagement;
