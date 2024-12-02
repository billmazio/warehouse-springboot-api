import React, { useState, useEffect } from "react";
import { fetchUsers, deleteUser } from "../../services/api"; // Import API functions
import "./UserManagement.css";


const UserManagement = () => {
    const [users, setUsers] = useState([]); // State to store users
    const [error, setError] = useState(""); // State for error handling

    useEffect(() => {
        const fetchAllUsers = async () => {
            try {
                const data = await fetchUsers(); // Fetch users from the API
                setUsers(data); // Set users in state
            } catch (err) {
                console.error("Error fetching users:", err.response || err.message);
                setError("Failed to load users.");
            }
        };

        fetchAllUsers();
    }, []);

    const handleDelete = async (userId) => {
        try {
            await deleteUser(userId); // Call API to delete the user
            setUsers(users.filter((user) => user.id !== userId)); // Remove deleted user from state
        } catch (err) {
            console.error("Error deleting user:", err);
            setError("Failed to delete user.");
        }
    };

    return (
        <div className="user-management">
            <h2>Manage Users</h2>
            {error && <p className="error-message">{error}</p>}
            <table className="user-table">
                <thead>
                <tr>
                    <th>Username</th>
                    <th>Role</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {users.map((user) => (
                    <tr key={user.id}>
                        <td>{user.username}</td>
                        <td>{user.role}</td>
                        <td>{user.status === "active" ? "Active" : "Inactive"}</td>
                        <td>
                            <button onClick={() => handleDelete(user.id)}>Delete</button>
                            {/* Add edit functionality here */}
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default UserManagement;
