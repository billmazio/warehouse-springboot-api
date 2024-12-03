import React from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login/Login";
import Dashboard from "./components/Dashboard/Dashboard";
import PrivateRoute from "./components/PrivateRoute/PrivateRoute";
import UserManagement from "./components/UserManagement/UserManagement";
import StoreManagement from "./components/StoreManagement/StoreManagement"; // Import StoreManagement

const App = () => {
    return (
        <Router>
            <Routes>
                {/* Login Route */}
                <Route path="/login" element={<Login />} />

                {/* Dashboard Route */}
                <Route
                    path="/dashboard"
                    element={
                        <PrivateRoute>
                            <Dashboard />
                        </PrivateRoute>
                    }
                />

                {/* User Management Route */}
                <Route
                    path="/manage-users"
                    element={
                        <PrivateRoute>
                            <UserManagement />
                        </PrivateRoute>
                    }
                />

                {/* Store Management Route */}
                <Route
                    path="/manage-stores"
                    element={
                        <PrivateRoute>
                            <StoreManagement />
                        </PrivateRoute>
                    }
                />

                {/* Catch-All Route */}
                <Route path="*" element={<Navigate to="/login" replace />} />
            </Routes>
        </Router>
    );
};

export default App;
