import React, { useEffect, useState } from "react";
import { fetchUsers, fetchUserDetails, updateUserRoles, deleteUser } from "../../services/api";
import "./UserManagement.css";

const UserManagement = () => {
    const [users, setUsers] = useState([]);
    const [loggedInUserRole, setLoggedInUserRole] = useState("");
    const [error, setError] = useState("");
    const [showConfirmation, setShowConfirmation] = useState(false);
    const [userToDelete, setUserToDelete] = useState(null);

    useEffect(() => {
        const loadData = async () => {
            try {
                const [userData, loggedInUser] = await Promise.all([
                    fetchUsers(),
                    fetchUserDetails(),
                ]);

                setUsers(userData);
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
            alert("User deleted successfully.");
        } catch (err) {
            console.error("Error deleting user:", err);
            alert("Failed to delete user.");
        }

        closeConfirmationDialog();
    };

    const handleRoleChange = async (userId, newRole) => {
        if (loggedInUserRole !== "SUPER_ADMIN") {
            alert("You are not authorized to change roles.");
            return;
        }

        try {
            await updateUserRoles(userId, newRole);
            setUsers(
                users.map((user) =>
                    user.id === userId ? { ...user, roles: [{ name: newRole }] } : user
                )
            );
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
                        <td>
                            {loggedInUserRole === "SUPER_ADMIN" ? (
                                <select
                                    value={user.roles[0]?.name || ""}
                                    onChange={(e) => handleRoleChange(user.id, e.target.value)}
                                >
                                    <option value="SUPER_ADMIN">Super Admin</option>
                                    <option value="LOCAL_ADMIN">Local Admin</option>
                                </select>
                            ) : (
                                user.roles.map((role) => role.name).join(", ")
                            )}
                        </td>
                        <td>{user.enable ? "Active" : "Inactive"}</td>
                        <td>
                            {loggedInUserRole === "SUPER_ADMIN" && (
                                <>
                                    <button
                                        className="edit-button"
                                        onClick={() => console.log("Edit user", user.id)}
                                    >
                                        Edit
                                    </button>
                                    <button
                                        className="delete-button"
                                        onClick={() => openConfirmationDialog(user)}
                                    >
                                        Delete
                                    </button>
                                </>
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
                            Are you sure you want to delete user{" "}
                            <strong>{userToDelete.username}</strong>?
                        </p>
                        <div className="confirmation-actions">
                            <button
                                className="cancel-button"
                                onClick={closeConfirmationDialog}
                            >
                                Cancel
                            </button>
                            <button
                                className="confirm-button"
                                onClick={confirmDelete}
                            >
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
