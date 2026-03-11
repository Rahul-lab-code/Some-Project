import axiosClient from './axiosClient';

// Auth
export const login = (data) => axiosClient.post('/auth/login', data);
export const register = (data) => axiosClient.post('/auth/register', data);

// Properties
export const getProperties = (params) => axiosClient.get('/properties', { params });
export const getPropertyById = (id) => axiosClient.get(`/properties/${id}`);
export const createProperty = (data) => axiosClient.post('/properties', data);
export const updateProperty = (id, data) => axiosClient.put(`/properties/${id}`, data);
export const deleteProperty = (id) => axiosClient.delete(`/properties/${id}`);

// Bookings
export const getBookings = () => axiosClient.get('/bookings');
export const getBookingById = (id) => axiosClient.get(`/bookings/${id}`);
export const createBooking = (data) => axiosClient.post('/bookings', data);

// Calendar
export const getCalendar = (propertyId) => axiosClient.get(`/calendar?propertyId=${propertyId}`);

// Reviews
export const getReviews = (propertyId) => axiosClient.get(`/reviews?propertyId=${propertyId}`);
export const createReview = (data) => axiosClient.post('/reviews', data);

// Payments
export const getPayments = () => axiosClient.get('/payments');
export const makePayment = (data) => axiosClient.post('/payments', data);

// Disputes
export const getDisputes = () => axiosClient.get('/disputes');
export const createDispute = (data) => axiosClient.post('/disputes', data);

// Maintenance
export const getMaintenance = () => axiosClient.get('/maintenance');
export const requestMaintenance = (data) => axiosClient.post('/maintenance', data);

// Users
export const getUsers = () => axiosClient.get('/users');
export const getUserById = (id) => axiosClient.get(`/users/${id}`);

// Admin Analytics
export const getAnalytics = () => axiosClient.get('/admin/analytics');

export default {
    login, register,
    getProperties, getPropertyById, createProperty, updateProperty, deleteProperty,
    getBookings, getBookingById, createBooking,
    getCalendar,
    getReviews, createReview,
    getPayments, makePayment,
    getDisputes, createDispute,
    getMaintenance, requestMaintenance,
    getUsers, getUserById,
    getAnalytics
};
