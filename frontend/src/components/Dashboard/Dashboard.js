import React, { useEffect, useState } from "react";
import { fetchDashboardData } from "../../services/api";
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";
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
    const [calendarDate, setCalendarDate] = useState(new Date()); // State for calendar

    useEffect(() => {
        const fetchData = async () => {
            try {
                const data = await fetchDashboardData();
                setDashboardData({
                    user: data.user || 0,
                    materials: data.materials || 0,
                    sizes: data.sizes || 0,
                    orders: data.orders || 0,
                    stores: data.stores || 0,
                });
            } catch (err) {
                console.error("Error fetching dashboard data:", err);
                setError("Failed to load dashboard data.");
            }
        };

        fetchData();
    }, []);

    const handleRedirect = (path) => {
        window.location.href = path;
    };

    return (
        <div className="dashboard-container">
            {/* Sidebar */}
            <aside className="sidebar">
                <div className="sidebar-header">
                    <img src="/img/user.png" alt="User" />
                    <h4>Admin Panel</h4>
                    <span>[SuperAdmin]</span>
                </div>
                <ul className="menu">
                    <li onClick={() => handleRedirect("/home")}>
                        <i className="fa fa-palette"></i> Αρχική
                    </li>
                    <li onClick={() => handleRedirect("/storage/stores")}>
                        <i className="fa fa-landmark"></i> Αποθήκες
                    </li>
                    <li onClick={() => handleRedirect("/storage/materials")}>
                        <i className="fa fa-child"></i> Ενδύματα
                    </li>
                    <li onClick={() => handleRedirect("/storage/orders")}>
                        <i className="fa fa-truck"></i> Παραγγελίες
                    </li>
                    <li onClick={() => handleRedirect("/storage/users")}>
                        <i className="fa fa-book"></i> Διαχείριση Χρηστών
                    </li>
                    <li onClick={() => handleRedirect("/change-password")}>
                        <i className="fa fa-lock"></i> Αλλαγή Κωδικού
                    </li>
                </ul>
            </aside>

            {/* Main Content */}
            <main className="main-content">
                <header className="header">
                    <h1>Κεντρική Αποθήκη</h1>
                    <button
                        className="logout-btn"
                        onClick={() => handleRedirect("/logout")}
                    >
                        Αποσύνδεση
                    </button>
                </header>

                {/* Error Message */}
                {error && <p className="error-message">{error}</p>}

                {/* Cards */}
                <section className="dashboard-cards">
                    <div className="card card-users" onClick={() => handleRedirect("/storage/users")}>
                        <i className="fa fa-users card-icon"></i>
                        <h3>Διαχείριση Χρηστών</h3>
                        <p>Ενεργοί Χρήστες: {dashboardData.user}</p>
                    </div>
                    <div className="card card-materials" onClick={() => handleRedirect("/storage/materials")}>
                        <i className="fa fa-tshirt card-icon"></i>
                        <h3>Διαχείριση Ενδυμάτων</h3>
                        <p>Καταχωρημένα: {dashboardData.materials}</p>
                    </div>
                    <div className="card card-sizes" onClick={() => handleRedirect("/storage/sizes")}>
                        <i className="fa fa-ruler card-icon"></i>
                        <h3>Διαχείριση Μεγεθών</h3>
                        <p>Συνολικά Μεγέθη: {dashboardData.sizes}</p>
                    </div>
                    <div className="card card-orders" onClick={() => handleRedirect("/storage/orders")}>
                        <i className="fa fa-shopping-cart card-icon"></i>
                        <h3>Παραγγελίες</h3>
                        <p>Συνολικές Παραγγελίες: {dashboardData.orders}</p>
                    </div>
                    <div className="card card-stores" onClick={() => handleRedirect("/storage/stores")}>
                        <i className="fa fa-warehouse card-icon"></i>
                        <h3>Διαχείριση Αποθηκών</h3>
                        <p>Ενεργές Αποθήκες: {dashboardData.stores}</p>
                    </div>
                </section>

                {/* Calendar */}
                <section className="calendar-container">
                    <h3>Ημερολόγιο</h3>
                    <Calendar onChange={setCalendarDate} value={calendarDate} />
                </section>

                {/* Footer */}
                <footer className="footer">
                    <p>&copy; 2024 Storage Management. All Rights Reserved.</p>
                </footer>
            </main>
        </div>
    );
};

export default Dashboard;
