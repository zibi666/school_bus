import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 5000
})

// Response interceptor
api.interceptors.response.use(response => {
  return response.data
}, error => {
  console.error('API Error:', error)
  return Promise.reject(error)
})

// Auth
export const loginStudent = (data) => api.post('/auth/student/login', data)
export const registerStudent = (data) => api.post('/auth/student/register', data)
export const loginAdmin = (data) => api.post('/auth/admin/login', data)

// Student
export const getStudentProfile = (studentId) => api.get(`/student/profile/${studentId}`)
export const createOrder = (data) => api.post('/student/order', data)
export const getMyOrders = (studentId) => api.get(`/student/orders/${studentId}`)
export const cancelOrder = (orderId) => api.post(`/student/order/cancel/${orderId}`)
export const getBus = (busId) => api.get(`/student/bus/${busId}`)

// Admin
export const getAllOrders = () => api.get('/admin/orders')
export const approveOrder = (data) => api.post('/admin/order/approve', data)
export const rejectOrder = (data) => api.post('/admin/order/reject', data)
export const getAllBuses = () => api.get('/admin/buses')
export const addBus = (data) => api.post('/admin/bus', data)
export const deleteBus = (busId) => api.delete(`/admin/bus/${busId}`)

export default api
