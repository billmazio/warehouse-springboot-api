import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { createOrder, fetchUsers, fetchStores, fetchMaterials, fetchSizes, fetchOrders, editOrder, deleteOrder } from "../../services/api"; // Added editOrder and deleteOrder
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "./OrderManagement.css";

const OrderManagement = () => {
    const navigate = useNavigate();
    const [stores, setStores] = useState([]);
    const [users, setUsers] = useState([]);
    const [materials, setMaterials] = useState([]);
    const [sizes, setSizes] = useState([]);
    const [orders, setOrders] = useState([]);
    const [newOrder, setNewOrder] = useState({
        quantity: 0,
        dateOfOrder: "",
        status: 1,
        stock: 0,
        sold: 0,
        materialText: "",
        materialStoreId: "",
        sizeName: "",
        storeTitle: "",
        userName: "",
    });

    const [editingOrder, setEditingOrder] = useState(null); // Track which order is being edited

    useEffect(() => {
        const loadData = async () => {
            try {
                const [storeData, userData, materialData, sizeData, orderData] = await Promise.all([
                    fetchStores(),
                    fetchUsers(),
                    fetchMaterials(),
                    fetchSizes(),
                    fetchOrders(),
                ]);

                setStores(storeData);
                setUsers(userData);
                setMaterials(materialData);
                setSizes(sizeData);
                setOrders(orderData);
            } catch (err) {
                console.error("Error fetching data:", err);
                toast.error("Αποτυχία φόρτωσης δεδομένων.");
            }
        };

        loadData();
    }, []);

    const handleCreate = async () => {
        const requiredFields = ["quantity", "dateOfOrder", "userName", "storeTitle", "materialText", "materialStoreId", "sizeName", "status", "sold", "stock"];
        const missingFields = requiredFields.filter(field => !newOrder[field]);

        if (missingFields.length > 0) {
            toast.warning(`Missing required fields: ${missingFields.join(", ")}`);
            return;
        }

        try {
            const createdOrder = await createOrder(newOrder);
            setOrders([...orders, createdOrder]);
            setNewOrder({
                quantity: 0,
                dateOfOrder: "",
                status: 1,
                stock: 0,
                sold: 0,
                materialText: "",
                materialStoreId: "",
                sizeName: "",
                storeTitle: "",
                userName: "",
            });
            toast.success("Η παραγγελία δημιουργήθηκε με επιτυχία.");
        } catch (err) {
            toast.error("Αποτυχία δημιουργίας παραγγελίας.");
        }
    };

    // Handle edit button click
    const handleEdit = async () => {
        if (!editingOrder) return;

        const updatedOrder = { ...editingOrder, ...newOrder };

        try {
            const updated = await editOrder(updatedOrder.id, updatedOrder);
            setOrders(orders.map(order => order.id === updated.id ? updated : order));
            toast.success("Η παραγγελία ενημερώθηκε με επιτυχία.");
            setEditingOrder(null); // Close the edit form after successful update
            setNewOrder({ ...updated }); // Pre-fill new order form with updated data
        } catch (err) {
            toast.error("Αποτυχία ενημέρωσης παραγγελίας.");
        }
    };

    // Handle delete button click
    const handleDelete = async (orderId) => {
        try {
            await deleteOrder(orderId);
            setOrders(orders.filter(order => order.id !== orderId));
            toast.success("Η παραγγελία διαγράφηκε επιτυχώς.");
        } catch (err) {
            toast.error("Αποτυχία διαγραφής παραγγελίας.");
        }
    };

    // Filter materials and sizes based on the selected store
    const filteredMaterials = materials.filter(material => material.storeTitle === newOrder.storeTitle);
    const filteredSizes = sizes.filter(size => filteredMaterials.some(material => material.sizeId === size.id));

    const uniqueMaterials = filteredMaterials.reduce((acc, material) => {
        if (!acc.some((item) => item.text === material.text)) {
            acc.push(material);
        }
        return acc;
    }, []);



    return (
        <div className="order-management-container">
            <ToastContainer />
            <button onClick={() => navigate("/dashboard")} className="back-button">
                Πίσω στην Κεντρική Διαχείριση
            </button>

            <h2>Δημιουργία Παραγγελίας</h2>
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
                <input
                    type="number"
                    placeholder="Απόθεμα"
                    value={newOrder.stock}
                    onChange={(e) => setNewOrder({...newOrder, stock: e.target.value})}
                />
                <input
                    type="number"
                    placeholder="Πωλήσεις"
                    value={newOrder.sold}
                    onChange={(e) => setNewOrder({...newOrder, sold: e.target.value})}
                />
                <select
                    value={newOrder.storeTitle}
                    onChange={(e) => setNewOrder({...newOrder, storeTitle: e.target.value})}
                >
                    <option value="" disabled>Επιλογή Αποθήκης</option>
                    {stores.map((store) => (
                        <option key={store.id} value={store.title}>
                            {store.title}
                        </option>
                    ))}
                </select>
                <select
                    value={newOrder.materialText}
                    onChange={(e) => {
                        const selectedMaterial = uniqueMaterials.find((material) => material.text === e.target.value);
                        setNewOrder({
                            ...newOrder,
                            materialText: e.target.value,
                            materialStoreId: selectedMaterial ? selectedMaterial.id : "",
                        });
                    }}
                >
                    <option value="" disabled>Επιλογή Υλικού</option>
                    {uniqueMaterials.map((material) => (
                        <option key={material.id} value={material.text}>
                            {material.text}
                        </option>
                    ))}
                </select>


                <select
                    value={newOrder.sizeName}
                    onChange={(e) => setNewOrder({...newOrder, sizeName: e.target.value})}
                >
                    <option value="" disabled>Επιλογή Μεγέθους</option>
                    {filteredSizes.map((size) => (
                        <option key={size.id} value={size.name}>
                            {size.name}
                        </option>
                    ))}
                </select>
                <select
                    value={newOrder.userName}
                    onChange={(e) => setNewOrder({...newOrder, userName: e.target.value})}
                >
                    <option value="" disabled>Επιλογή Χρήστη</option>
                    {users.map((user) => (
                        <option key={user.id} value={user.username}>
                            {user.username}
                        </option>
                    ))}
                </select>
                <select
                    value={newOrder.status}
                    onChange={(e) => setNewOrder({...newOrder, status: e.target.value})}
                >
                    <option value={1}>Σε Εκκρεμότητα</option>
                    <option value={2}>Ολοκληρωμένη</option>
                    <option value={3}>Ακυρωμένη</option>
                </select>

                <button className="create-button" onClick={handleCreate}>
                    Δημιουργία Παραγγελίας
                </button>
                {editingOrder && (
                    <button className="edit-button" onClick={handleEdit}>
                        Ενημέρωση Παραγγελίας
                    </button>
                )}
            </div>

            <h2>Λίστα Παραγγελιών</h2>
            <table className="order-table">
                <thead>
                <tr>
                    <th>Ημερομηνία</th>
                    <th>Ποσότητα</th>
                    <th>Απόθεμα</th>
                    <th>Πωλήσεις</th>
                    <th>Υλικό</th>
                    <th>Μέγεθος</th>
                    <th>Αποθήκη</th>
                    <th>Χρήστης</th>
                    <th>Κατάσταση</th>
                    <th>Ενέργειες</th>
                </tr>
                </thead>
                <tbody>
                {orders.map((order) => (
                    <tr key={order.id}>
                        <td>{order.dateOfOrder}</td>
                        <td>{order.quantity}</td>
                        <td>{order.stock}</td>
                        <td>{order.sold}</td>
                        <td>{order.materialText}</td>
                        <td>{order.sizeName}</td>
                        <td>{order.storeTitle}</td>
                        <td>{order.userName}</td>
                        <td>{order.status === 1 ? "Σε Εκκρεμότητα" : order.status === 2 ? "Ολοκληρωμένη" : "Ακυρωμένη"}</td>
                        <td>
                            <button onClick={() => setEditingOrder(order)}>Επεξεργασία</button>
                            <button onClick={() => handleDelete(order.id)}>Διαγραφή</button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default OrderManagement;
