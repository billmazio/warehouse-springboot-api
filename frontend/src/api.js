import axios from 'axios';

const API_BASE_URL = '/api';

export const getDashboardData = async () => {
    try {
        const response = await axios.get('/api/dashboard');
        return response.data;
    } catch (error) {
        console.error('Error fetching dashboard data', error);
        throw error;
    }
};


export const login = async (credentials) => {
    try {
        const response = await axios.post(`${API_BASE_URL}/auth/login`, credentials);
        return response.data;
    } catch (error) {
        console.error('Error during login', error);
        throw error;
    }
};
