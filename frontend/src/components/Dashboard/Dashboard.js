import React, { useEffect, useState } from "react";
import { fetchDashboardData } from "../../services/api"; // Use the function
import "./Dashboard.css";

const Dashboard = () => {
    const [dashboardData, setDashboardData] = useState({ user: 0 });
    const [error, setError] = useState("");

    useEffect(() => {
        const fetchData = async () => {
            try {
                const token = localStorage.getItem("token");
                if (!token) {
                    console.error("No token found in localStorage");
                    setError("Authentication error: Token missing.");
                    return;
                }

                // Use the imported fetchDashboardData function
                const data = await fetchDashboardData(token);
                console.log("Fetched Dashboard Data:", data);

                setDashboardData({ user: data.user }); // Update state
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
                <div
                    className="card user-management"
                    onClick={() => handleRedirect("/storage/users")}
                >
                    <h3>User Management</h3>
                    <p>Active Users: {dashboardData.user}</p>
                </div>
            </div>
        </div>
    );
};

export default Dashboard;
