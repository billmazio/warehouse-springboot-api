import React, { useEffect, useState } from "react";
import { fetchUsers } from "../../services/api"; // Import the API function
import "./UserManagement.css";

const UserManagement = () => {
    const [users, setUsers] = useState([]); // State for storing users
    const [error, setError] = useState("");

    useEffect(() => {
        const loadUsers = async () => {
            try {
                const data = await fetchUsers(); // Fetch users from API
                setUsers(data); // Update state with user data
            } catch (err) {
                setError("Failed to fetch users.");
                console.error("Error:", err);
            }
        };

        loadUsers();
    }, []);

    const handleDelete = async (userId) => {
        // Implement delete functionality (if required)
        console.log("Delete user with ID:", userId);
    };

    return (
        <div className="user-management-container">
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
                        <td>{user.roles.map((role) => role.name).join(", ")}</td>
                        <td>{user.enable ? "Active" : "Inactive"}</td>
                        <td>
                            <button
                                className="delete-button"
                                onClick={() => handleDelete(user.id)}
                            >
                                Delete
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default UserManagement;
