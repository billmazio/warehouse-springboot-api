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
        // Log the error
        console.error("Error in Axios response interceptor:", error.response || error.message);

        // Optional: Redirect to login if unauthorized (401)
        if (error.response?.status === 401) {
            console.warn("Unauthorized! Redirecting to login...");
            window.location.href = "/login"; // Redirect to login page
        }

        // Optional: Handle token expiration (403 or custom logic)
        if (error.response?.status === 403) {
            console.warn("Token expired or insufficient permissions.");
            // Additional token refresh logic can go here if implemented
        }

        return Promise.reject(error); // Reject the error to handle it in the component
    }
);

// Example reusable API function to fetch dashboard data
export const fetchDashboardData = async () => {
    try {
        const response = await api.get("/api/dashboard"); // Make API call
        return response.data; // Return the data from the response
    } catch (err) {
        console.error("Error in fetchDashboardData:", err.response || err.message);
        throw err; // Re-throw the error to be handled in the component
    }
};

// Example reusable API function to fetch user details
export const fetchUserDetails = async () => {
    try {
        const response = await api.get("/api/user/details"); // Example endpoint
        return response.data; // Return the data
    } catch (err) {
        console.error("Error in fetchUserDetails:", err.response || err.message);
        throw err; // Re-throw the error
    }
};

// Example reusable API function for logout
export const logout = async () => {
    try {
        const response = await api.post("/auth/logout"); // Call logout API
        localStorage.removeItem("token"); // Remove token from localStorage
        return response.data;
    } catch (err) {
        console.error("Error in logout:", err.response || err.message);
        throw err; // Re-throw the error
    }
};

// Export the configured Axios instance for direct use if needed
export default api;
