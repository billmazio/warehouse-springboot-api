import axios from "axios";

// Set the base URL for your API
const API_BASE_URL = process.env.REACT_APP_API_URL || "http://localhost:8080";

// Create an Axios instance
const api = axios.create({
    baseURL: API_BASE_URL, // Set the base URL for all API calls
    timeout: 10000, // Optional: Set a timeout for requests (10 seconds)
});

// Add a request interceptor to include the token in headers
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem("token"); // Retrieve token from localStorage
        if (token) {
            config.headers.Authorization = `Bearer ${token}`; // Add token to Authorization header
        } else {
            console.warn("No token found in localStorage! Some requests may fail.");
        }
        return config; // Return the modified config
    },
    (error) => {
        console.error("Error in Axios request interceptor:", error);
        return Promise.reject(error);
    }
);

// Add a response interceptor for global error handling
api.interceptors.response.use(
    (response) => response, // Simply return the response if no error
    (error) => {
        console.error("Error in Axios response interceptor:", error.response || error.message);

        if (error.response?.status === 401) {
            console.warn("Unauthorized! Redirecting to login...");
            window.location.href = "/login"; // Redirect to login page
        }

        if (error.response?.status === 403) {
            console.warn("Token expired or insufficient permissions.");
        }

        return Promise.reject(error);
    }
);

// Add reusable API functions

// Fetch all users
export const fetchUsers = async () => {
    try {
        const response = await api.get("/api/users"); // API call to fetch users
        return response.data;
    } catch (err) {
        console.error("Error in fetchUsers:", err.response || err.message);
        throw err;
    }
};

// Delete a user
export const deleteUser = async (userId) => {
    try {
        const response = await api.delete(`/api/users/${userId}`); // API call to delete user
        return response.data;
    } catch (err) {
        console.error("Error in deleteUser:", err.response || err.message);
        throw err;
    }
};

// Fetch dashboard data
export const fetchDashboardData = async () => {
    try {
        const response = await api.get("/api/dashboard");
        return response.data;
    } catch (err) {
        console.error("Error in fetchDashboardData:", err.response || err.message);
        throw err;
    }
};

// Fetch user details
export const fetchUserDetails = async () => {
    try {
        const response = await api.get("/api/user/details");
        return response.data;
    } catch (err) {
        console.error("Error in fetchUserDetails:", err.response || err.message);
        throw err;
    }
};

// Logout
export const logout = async () => {
    try {
        const response = await api.post("/auth/logout");
        localStorage.removeItem("token");
        return response.data;
    } catch (err) {
        console.error("Error in logout:", err.response || err.message);
        throw err;
    }
};

// Export the Axios instance
export default api;
