import React, { useEffect, useState } from "react";
import { format, isValid } from "date-fns";
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
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(true);  // Loading state for async data
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
            setLoading(true);  // Set loading true at the start of fetching
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
            } finally {
                setLoading(false);  // Set loading false once data is fetched
            }
        };

        loadData();
    }, []);

    const formatDate = (date) => {
        if (!date || !isValid(new Date(date))) return '';  // Return empty string if date is invalid
        return format(new Date(date), 'MM/dd/yyyy');
    };

    const handleDateChange = (e) => {
        const date = e.target.value;
        setNewOrder({...newOrder, dateOfOrder: date});
    };

    const handleCreate = async () => {
        const requiredFields = ["quantity", "dateOfOrder", "userId", "storeId", "materialId", "sizeId"];
        const missingFields = requiredFields.filter(field => !newOrder[field]);

        if (missingFields.length > 0) {
            toast.warning(`Missing required fields: ${missingFields.join(", ")}`);
            console.log("Missing fields:", missingFields);
            return;
        }

        console.log("New Order Data:", newOrder);

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
            toast.success("Order created successfully.");
        } catch (err) {
            console.error("Error creating order:", err);
            toast.error("Failed to create order.");
        }
    };


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

    return (
        <div className="order-management-container">
            <ToastContainer />
            <button onClick={() => navigate("/dashboard")} className="back-button">
                Πίσω στην Κεντρική Διαχείριση
            </button>

            <h2>Διαχείριση Παραγγελιών</h2>
            {error && <p className="error-message">{error}</p>}

            {/* Loading Spinner */}
            {loading && <p>Φόρτωση δεδομένων...</p>}

            {!loading && (
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
                        onChange={handleDateChange}
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
                            .filter((store) => store.id !== centralStoreId) // Exclude the central store
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
                        {sizes.map(size => (
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
            )}

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
                        <td>{formatDate(order.dateOfOrder)}</td>
                        <td>{order.user?.username || 'Unknown User'}</td>
                        <td>{order.stores?.title || 'Unknown Store'}</td>
                        <td>{order.materials?.text || 'Unknown Material'}</td>
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
