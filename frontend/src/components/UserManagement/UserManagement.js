import React, { useEffect, useState } from "react";
import {
    fetchUsers,
    fetchUserDetails,
    createUser,
    deleteUser,
    updateUserRoles,
    fetchStores,
} from "../../services/api";
import "./UserManagement.css";

const UserManagement = () => {
    const [users, setUsers] = useState([]); // State for storing users
    const [stores, setStores] = useState([]); // State for storing stores
    const [loggedInUserRole, setLoggedInUserRole] = useState(""); // State for the logged-in user's role
    const [error, setError] = useState("");
    const [newUser, setNewUser] = useState({
        username: "",
        password: "",
        role: "LOCAL_ADMIN",
        enable: true,
        storeId: "",
    }); // New user state

    useEffect(() => {
        const loadData = async () => {
            try {
                const [userData, loggedInUser, storeData] = await Promise.all([
                    fetchUsers(), // Fetch all users
                    fetchUserDetails(), // Fetch logged-in user's details
                    fetchStores(), // Fetch stores from the backend
                ]);

                setUsers(userData); // Set the users data
                setStores(storeData); // Set the stores data
                const roles = loggedInUser.roles.map((role) => role.name); // Extract roles
                if (roles.includes("SUPER_ADMIN")) {
                    setLoggedInUserRole("SUPER_ADMIN"); // Set logged-in user's role
                }
            } catch (err) {
                setError("Failed to fetch data.");
                console.error("Error:", err);
            }
        };

        loadData();
    }, []);

    const handleDelete = async (userId) => {
        if (loggedInUserRole !== "SUPER_ADMIN") {
            alert("You are not authorized to delete users.");
            return;
        }

        try {
            await deleteUser(userId); // API call to delete the user
            setUsers(users.filter((user) => user.id !== userId)); // Update the state to remove the deleted user
            alert("User deleted successfully.");
        } catch (err) {
            console.error("Error deleting user:", err);
            alert("Failed to delete user.");
        }
    };

    const handleCreate = async () => {
        if (loggedInUserRole !== "SUPER_ADMIN") {
            alert("You are not authorized to create users.");
            return;
        }

        if (!newUser.username.trim() || !newUser.password.trim() || !newUser.storeId) {
            alert("Username, password, and store selection are required.");
            return;
        }

        try {
            const createdUser = await createUser(newUser); // API call to create a user
            setUsers([...users, createdUser]); // Add new user to the state
            setNewUser({ username: "", password: "", role: "LOCAL_ADMIN", enable: true, storeId: "" }); // Reset new user state
            alert("User created successfully.");
        } catch (err) {
            console.error("Error creating user:", err);
            alert("Failed to create user.");
        }
    };

    const handleRoleChange = async (userId, newRole) => {
        if (loggedInUserRole !== "SUPER_ADMIN") {
            alert("You are not authorized to change roles.");
            return;
        }

        try {
            await updateUserRoles(userId, newRole); // API call to update the role
            setUsers(users.map((user) =>
                user.id === userId
                    ? { ...user, roles: [{ name: newRole }] } // Update the user's role locally
                    : user
            ));
            alert("Role updated successfully.");
        } catch (err) {
            console.error("Error updating user role:", err);
            alert("Failed to update role.");
        }
    };

    return (
        <div className="user-management-container">
            <h2>Διαχείριση Χρηστών</h2>
            {error && <p className="error-message">{error}</p>}
            {loggedInUserRole === "SUPER_ADMIN" && (
                <div className="create-user-form">
                    <input
                        type="text"
                        placeholder="Enter username"
                        value={newUser.username}
                        onChange={(e) => setNewUser({...newUser, username: e.target.value})}
                    />
                    <input
                        type="password"
                        placeholder="Enter password"
                        value={newUser.password}
                        onChange={(e) => setNewUser({...newUser, password: e.target.value})}
                    />
                    <select
                        value={newUser.role}
                        onChange={(e) => setNewUser({...newUser, role: e.target.value})}
                    >
                        <option value="SUPER_ADMIN">Super Admin</option>
                        <option value="LOCAL_ADMIN">Local Admin</option>
                    </select>
                    <label>
                        Enable:
                        <input
                            type="checkbox"
                            checked={newUser.enable}
                            onChange={(e) => setNewUser({...newUser, enable: e.target.checked})}
                        />
                    </label>
                    <select
                        value={newUser.storeId}
                        onChange={(e) => setNewUser({...newUser, storeId: e.target.value})}
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
                        <td>
                            {loggedInUserRole === "SUPER_ADMIN" ? (
                                <select
                                    value={user.roles?.[0]?.name || ""} // Safely access roles[0].name
                                    onChange={(e) => handleRoleChange(user.id, e.target.value)}
                                >
                                    <option value="SUPER_ADMIN">Super Admin</option>
                                    <option value="LOCAL_ADMIN">Local Admin</option>
                                </select>
                            ) : (
                                (user.roles || []).map((role) => role.name).join(", ") // Default to empty array if roles is undefined
                            )}
                        </td>
                        <td>{user.enable ? "Active" : "Inactive"}</td>
                        <td>{user.store?.name || "N/A"}</td>
                        <td>
                            {loggedInUserRole === "SUPER_ADMIN" && (
                                <button
                                    className="delete-button"
                                    onClick={() => handleDelete(user.id)}
                                >
                                    Delete
                                </button>
                            )}
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default UserManagement;
