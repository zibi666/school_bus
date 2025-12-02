import axios from 'axios'

const api = axios.create({
  baseURL: '/api', // Assuming proxy or backend is at /api
  timeout: 5000
})

// Request interceptor to add token
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Response interceptor
api.interceptors.response.use(response => {
  return response.data
}, error => {
  return Promise.reject(error)
})

// Auth
export const loginStudent = (data) => api.post('/auth/student/login', data)
export const registerStudent = (data) => api.post('/auth/student/register', data)
export const loginAdmin = (data) => api.post('/auth/admin/login', data)
export const registerAdmin = (data) => api.post('/auth/admin/register', data)

// Student
export const charterBus = (data) => api.post('/student/charter', data)
export const joinByInviteCode = (code) => api.post('/student/join', { code })
export const getMyTrips = () => api.get('/student/trips')
export const refundTicket = (ticketId) => api.post(`/student/refund/${ticketId}`)
export const getStudentProfile = () => api.get('/student/profile')

// Admin
export const getAllTrips = () => api.get('/admin/trips')
export const getTripPassengers = (plateNumber) => api.get(`/admin/trips/${plateNumber}/passengers`)
export const updateTripDriver = (plateNumber, driverPhone) => api.put(`/admin/trips/${plateNumber}/driver`, { driverPhone })
export const getAllDrivers = () => api.get('/admin/drivers')
export const addDriver = (data) => api.post('/admin/drivers', data)

export default api
