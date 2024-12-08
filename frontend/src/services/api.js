import axios from "axios";

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


export const createUser = async (userData) => {
    try {
        const response = await api.post("/api/users", {
            username: userData.username,
            password: userData.password,
            enable: userData.enable ? 1 : 0, // Convert boolean to integer for 'enable'
            store: { id: userData.storeId }, // Pass the store ID within a 'store' object
            roles: [{ name: userData.role }] // Pass roles
        });
        return response.data;
    } catch (err) {
        console.error("Error creating user:", err.response || err.message);
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
        const response = await api.get("/api/users/details");
        return response.data;
    } catch (err) {
        console.error("Error in fetchUserDetails:", err.response || err.message);
        throw err;
    }
};

export const fetchStores = async () => {
    try {
        const response = await api.get("/api/stores"); // Use the correct endpoint
        return response.data; // Ensure this returns an array of stores
    } catch (err) {
        console.error("Error fetching stores:", err.response || err.message);
        throw err;
    }
};

export const createStore = async (storeData) => {
    try {
        const response = await api.post("/api/stores", {
            title: storeData.title,
            address: storeData.address,
            enable: storeData.enable ? 1 : 0, // Convert boolean to integer for 'enable'

        });
        return response.data;
    } catch (err) {
        console.error("Error creating store:", err.response || err.message);
        throw err;
    }
};

export const deleteStore = async (id) => {
    try {
        const response = await api.delete(`/api/stores/${id}`);
        return response.data;
    } catch (err) {
        console.error("Error in deleteStore:", err.response || err.message);
        throw err;
    }
};

/*
export const fetchMaterials = async () => {
    try {
        const response = await api.get("/api/materials");
        return response.data;
    } catch (err) {
        console.error("Error fetching materials:", err.response || err.message);
        throw err;
    }
};
*/

export const fetchStoreDetails = async (storeId) => {
    try {
        const response = await api.get(`/api/stores/${storeId}`);
        return response.data;
    } catch (err) {
        console.error("Error fetching store details:", err);
        throw err;
    }
};


// New function to fetch materials by store ID
export const fetchMaterialsByStoreId = async (storeId) => {
    try {
        const response = await api.get(`/api/stores/${storeId}/materials`);
        return response.data;
    } catch (err) {
        console.error("Error fetching materials:", err.response || err.message);
        throw err;
    }
};

/*export const fetchMaterialsWithFilters = async (text, sizeId) => {
    try {
        const response = await api.get("/api/materials", {
            params: {
                text: text || null,
                sizeId: sizeId || null,
            },
        });
        return response.data;
    } catch (err) {
        console.error("Error fetching materials with filters:", err.response || err.message);
        throw err;
    }
};*/


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
