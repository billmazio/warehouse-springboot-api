import { Navigate } from "react-router-dom";

const PrivateRoute = ({ children }) => {
    const token = localStorage.getItem("token");
    if (!token) {
        // Redirect to login if no token
        return <Navigate to="/login" />;
    }

    // Optionally, validate the token with the backend
    // to ensure it's still valid (can add additional logic here)

    return children;
};

export default PrivateRoute;
