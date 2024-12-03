import React, { useEffect, useState } from "react";
import { fetchDashboardData, fetchUserDetails } from "../../services/api";
import Calendar from "../Calendar/CalendarComponent";
import UserManagement from "../UserManagement/UserManagement";

import "./Dashboard.css";

const Dashboard = () => {
    const [dashboardData, setDashboardData] = useState({
        user: 0,
        materials: 0,
        sizes: 0,
        orders: 0,
        stores: 0,
    });
    const [error, setError] = useState("");
    const [calendarDate, setCalendarDate] = useState(new Date());
    const [activeSection, setActiveSection] = useState("dashboard"); // Manage active section
    const [userDetails, setUserDetails] = useState(null);
    useEffect(() => {
        const fetchData = async () => {
            try {
                // Fetch both dashboard data and user details
                const [dashboardResponse, userResponse] = await Promise.all([
                    fetchDashboardData(),
                    fetchUserDetails()
                ]);

                // Set dashboard data
                setDashboardData({
                    user: dashboardResponse.user || 0,
                    materials: dashboardResponse.materials || 0,
                    sizes: dashboardResponse.sizes || 0,
                    orders: dashboardResponse.orders || 0,
                    stores: dashboardResponse.stores || 0,
                });

                // Set user details
                setUserDetails(userResponse);

            } catch (err) {
                console.error("Error fetching data:", err);
                setError("Failed to load data.");
            }
        };

        fetchData();
    }, []);

    const handleSectionChange = (section) => {
        setActiveSection(section); // Update active section
    };

    const handleLogout = () => {
        localStorage.removeItem("token");
        window.location.href = "/login";
    };

    return (
        <div className="dashboard-container">
            {/* Sidebar */}
            <aside className="sidebar">
                <div className="sidebar-header">
                    <div className="user-avatar">
                        <span>{userDetails?.username?.charAt(0).toUpperCase() || "U"}</span> {/* First letter or default */}
                    </div>
                    <div className="user-info">
                        <h4 className="welcome-message">Welcome,</h4>
                        <h4 className="username">{userDetails?.username || "User"}</h4>
                    </div>
                </div>


                <ul className="menu">
                    <li
                        className={activeSection === "dashboard" ? "active" : ""}
                        onClick={() => handleSectionChange("dashboard")}
                    >
                        <i className="fa fa-palette"></i> Αρχική
                    </li>
                    <li
                        className={activeSection === "users" ? "active" : ""}
                        onClick={() => handleSectionChange("users")}
                    >
                        <i className="fa fa-users"></i> Διαχείριση Χρηστών
                    </li>
                    <li
                        className={activeSection === "stores" ? "active" : ""}
                        onClick={() => handleSectionChange("stores")}
                    >
                        <i className="fa fa-warehouse"></i> Αποθήκες
                    </li>
                    <li
                        className={activeSection === "materials" ? "active" : ""}
                        onClick={() => handleSectionChange("materials")}
                    >
                        <i className="fa fa-tshirt"></i> Ενδύματα
                    </li>
                    <li
                        className={activeSection === "orders" ? "active" : ""}
                        onClick={() => handleSectionChange("orders")}
                    >
                        <i className="fa fa-shopping-cart"></i> Παραγγελίες
                    </li>
                    <li
                        className={activeSection === "change-password" ? "active" : ""}
                        onClick={() => handleSectionChange("change-password")}
                    >
                        <i className="fa fa-lock"></i> Αλλαγή Κωδικού
                    </li>
                </ul>
            </aside>

            {/* Main Content */}
            <main className="main-content">
                <header className="header">
                    <h1>Κεντρική Αποθήκη</h1>
                    <button className="logout-btn" onClick={handleLogout}>
                        Αποσύνδεση
                    </button>
                </header>

                {/* Render Active Section */}
                {error && <p className="error-message">{error}</p>}
                {activeSection === "dashboard" && (
                    <section className="dashboard-cards">
                        <div className="card" onClick={() => handleSectionChange("users")}>
                            <i className="fa fa-users card-icon"></i>
                            <h3>Διαχείριση Χρηστών</h3>
                            <p>Ενεργοί Χρήστες: {dashboardData.user}</p>
                        </div>
                        <div className="card" onClick={() => handleSectionChange("materials")}>
                            <i className="fa fa-tshirt card-icon"></i>
                            <h3>Διαχείριση Ενδυμάτων</h3>
                            <p>Καταχωρημένα: {dashboardData.materials}</p>
                        </div>
                        <div className="card" onClick={() => handleSectionChange("sizes")}>
                            <i className="fa fa-ruler card-icon"></i>
                            <h3>Διαχείριση Μεγεθών</h3>
                            <p>Συνολικά Μεγέθη: {dashboardData.sizes}</p>
                        </div>
                        <div className="card" onClick={() => handleSectionChange("orders")}>
                            <i className="fa fa-shopping-cart card-icon"></i>
                            <h3>Παραγγελίες</h3>
                            <p>Συνολικές Παραγγελίες: {dashboardData.orders}</p>
                        </div>
                        <div className="card" onClick={() => handleSectionChange("stores")}>
                            <i className="fa fa-warehouse card-icon"></i>
                            <h3>Διαχείριση Αποθηκών</h3>
                            <p>Ενεργές Αποθήκες: {dashboardData.stores}</p>
                        </div>
                    </section>
                )}
                {activeSection === "users" && <UserManagement />}
                {activeSection === "calendar" && (
                    <section className="calendar-container">
                        <Calendar onChange={setCalendarDate} value={calendarDate} />
                    </section>
                )}

                <footer className="footer">
                    <p>&copy; 2024 Storage Management. All Rights Reserved.</p>
                </footer>
            </main>
        </div>
    );
};

export default Dashboard;
