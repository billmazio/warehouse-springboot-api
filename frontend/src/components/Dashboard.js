import React, { useEffect, useState } from "react";
import axios from "axios";
import "./Dashboard.css";

const Dashboard = () => {
    const [dashboardData, setDashboardData] = useState({
        user: 0,
        // Commenting out other fields
        // storageCount: 0,
        // materialCount: 0,
        // newOrdersCount: 0,
        // ordersCount: 0,
        // storeTitle: "",
    });
    const [error, setError] = useState("");

    useEffect(() => {
        const fetchDashboardData = async () => {
            try {
                const response = await axios.get("http://localhost:8080/api/dashboard", {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem("token")}`,
                    },
                });
                console.log("Fetched User Count:", response.data.user); // Debugging log for "user" field
                setDashboardData({ user: response.data.user }); // Update only the "user" field
            } catch (err) {
                setError("Failed to load dashboard data.");
                console.error("API Error:", err.response?.data || err.message);
            }
        };

        fetchDashboardData();
    }, []);

    const handleRedirect = (path) => {
        window.location.href = path; // Redirect to the specified path
    };

    return (
        <div className="dashboard-container">
            <header className="dashboard-header">
                <h1>Dashboard</h1>
                <button
                    className="logout-btn"
                    onClick={() => handleRedirect("/logout")}
                >
                    Logout
                </button>
            </header>
            {error && <p className="error-message">{error}</p>}

            <div className="cards">
                {/* User Management */}
                <div
                    className="card user-management"
                    onClick={() => handleRedirect("/storage/users")}
                >
                    <h3>User Management</h3>
                    <p>Active Users: {dashboardData.user}</p>
                </div>

                {/* Commenting out other sections */}
                {/* <div className="card storage-management">
                    <h3>Storage Management</h3>
                    <p>Active Storages: {dashboardData.storageCount}</p>
                </div>

                <div className="card material-management">
                    <h3>Material Management</h3>
                    <p>Total Materials: {dashboardData.materialCount}</p>
                </div>

                <div className="card orders">
                    <h3>Orders</h3>
                    <p>New Orders: {dashboardData.newOrdersCount}</p>
                    <p>Total Orders: {dashboardData.ordersCount}</p>
                </div>

                <div className="card store-info">
                    <h3>Store</h3>
                    <p>{dashboardData.storeTitle}</p>
                </div> */}
            </div>
        </div>
    );
};

export default Dashboard;
