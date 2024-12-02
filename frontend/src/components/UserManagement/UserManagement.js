import React, { useEffect, useState } from "react";
import {
    fetchUsers,
    fetchUserDetails,
    createUser,
    deleteUser,
    fetchStores,
} from "../../services/api";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "./UserManagement.css";

const UserManagement = () => {
    const [users, setUsers] = useState([]);
    const [stores, setStores] = useState([]);
    const [loggedInUserRole, setLoggedInUserRole] = useState("");
    const [error, setError] = useState("");
    const [showConfirmation, setShowConfirmation] = useState(false);
    const [userToDelete, setUserToDelete] = useState(null);
    const [newUser, setNewUser] = useState({
        username: "",
        password: "",
        role: "LOCAL_ADMIN",
        enable: 1,
        storeId: "",
    });

    useEffect(() => {
        const loadData = async () => {
            try {
                const [userData, loggedInUser, storeData] = await Promise.all([
                    fetchUsers(),
                    fetchUserDetails(),
                    fetchStores(),
                ]);

                setUsers(userData);
                setStores(storeData);
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

    const openConfirmationDialog = (user) => {
        setUserToDelete(user);
        setShowConfirmation(true);
    };

    const closeConfirmationDialog = () => {
        setShowConfirmation(false);
        setUserToDelete(null);
    };

    const confirmDelete = async () => {
        if (!userToDelete) return;

        try {
            await deleteUser(userToDelete.id);
            setUsers(users.filter((user) => user.id !== userToDelete.id));
            toast.success(`User "${userToDelete.username}" deleted successfully.`);
        } catch (err) {
            console.error("Error deleting user:", err);
            toast.error("Failed to delete user.");
        }

        closeConfirmationDialog();
    };

    const handleCreate = async () => {
        if (loggedInUserRole !== "SUPER_ADMIN") {
            toast.warning("You are not authorized to create users.");
            return;
        }

        if (!newUser.username.trim() || !newUser.password.trim() || !newUser.storeId) {
            toast.warning("Username, password, and store selection are required.");
            return;
        }

        try {
            const createdUser = await createUser(newUser);
            setUsers([...users, createdUser]);
            setNewUser({ username: "", password: "", role: "LOCAL_ADMIN", enable: 1, storeId: "" });
            toast.success("User created successfully.");
        } catch (err) {
            console.error("Error creating user:", err);
            toast.error("Failed to create user.");
        }
    };

    return (
        <div className="user-management-container">
            <ToastContainer />
            <h2>Διαχείριση Χρηστών</h2>
            {error && <p className="error-message">{error}</p>}

            {loggedInUserRole === "SUPER_ADMIN" && (
                <div className="create-user-form">
                    <input
                        type="text"
                        placeholder="Enter username"
                        value={newUser.username}
                        onChange={(e) => setNewUser({ ...newUser, username: e.target.value })}
                    />
                    <input
                        type="password"
                        placeholder="Enter password"
                        value={newUser.password}
                        onChange={(e) => setNewUser({ ...newUser, password: e.target.value })}
                    />
                    <select
                        value={newUser.role}
                        onChange={(e) => setNewUser({ ...newUser, role: e.target.value })}
                    >
                        <option value="SUPER_ADMIN">Super Admin</option>
                        <option value="LOCAL_ADMIN">Local Admin</option>
                    </select>
                    <label>
                        Enable:
                        <input
                            type="checkbox"
                            checked={newUser.enable === 1}
                            onChange={(e) =>
                                setNewUser({ ...newUser, enable: e.target.checked ? 1 : 0 })
                            }
                        />
                    </label>
                    <select
                        value={newUser.storeId}
                        onChange={(e) => setNewUser({ ...newUser, storeId: e.target.value })}
                    >
                        <option value="" disabled>
                            Select Store
                        </option>
                        {stores.map((store) => (
                            <option key={store.id} value={store.id}>
                                {store.title}
                            </option>
                        ))}
                    </select>

                    <button className="create-button" onClick={handleCreate}>
                        Create User
                    </button>
                </div>
            )}

            <table className="user-table">
                <thead>
                <tr>
                    <th>Username</th>
                    <th>Role</th>
                    <th>Status</th>
                    <th>Store</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {users.map((user) => (
                    <tr key={user.id}>
                        <td>{user.username}</td>
                        <td>{user.roles?.[0]?.name || "N/A"}</td>
                        <td>{user.enable === 1 ? "Active" : "Inactive"}</td>
                        <td>{user.store?.title || "N/A"}</td>
                        <td>
                            <button
                                className="delete-button"
                                onClick={() => openConfirmationDialog(user)}
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
                            Are you sure you want to delete user{" "}
                            <strong>{userToDelete?.username}</strong>?
                        </p>
                        <div className="confirmation-actions">
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

export default UserManagement;
