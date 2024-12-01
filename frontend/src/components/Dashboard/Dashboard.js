import React, { useEffect, useState } from "react";
import { fetchDashboardData } from "../../services/api"; // Use the function
import "./Dashboard.css";

const Dashboard = () => {
    const [dashboardData, setDashboardData] = useState({ user: 0 });
    const [error, setError] = useState("");

    useEffect(() => {
        const fetchData = async () => {
            try {
                const data = await fetchDashboardData(); // Call API
                setDashboardData({
                    user: data.user,
                    materials: data.materials,
                    sizes: data.sizes,
                    orders: data.orders,
                    stores: data.stores,
                });
            } catch (err) {
                console.error("Error fetching dashboard data:", err);
                setError("Failed to load dashboard data.");
            }
        };

        fetchData();
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
                <div className="card user-management">
                    <h3>User Management</h3>
                    <p>Active Users: {dashboardData.user}</p>
                </div>
                <div className="card materials-management">
                    <h3>Materials</h3>
                    <p>Total Materials: {dashboardData.materials}</p>
                </div>
                <div className="card sizes-management">
                    <h3>Sizes</h3>
                    <p>Total Sizes: {dashboardData.sizes}</p>
                </div>
                <div className="card orders-management">
                    <h3>Orders</h3>
                    <p>Total Orders: {dashboardData.orders}</p>
                </div>
                <div className="card stores-management">
                    <h3>Stores</h3>
                    <p>Total Stores: {dashboardData.stores}</p>
                </div>
            </div>

        </div>
    );
};

export default Dashboard;
