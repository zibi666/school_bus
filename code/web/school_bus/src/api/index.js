import axios from 'axios'

const api = axios.create({
  baseURL: '/api', // Assuming proxy or backend is at /api
  timeout: 5000
})

// Attach identity headers so backend can identify the caller
api.interceptors.request.use(config => {
  const rawUserInfo = localStorage.getItem('userInfo')
  if (rawUserInfo) {
    try {
      const userInfo = JSON.parse(rawUserInfo)
      config.headers = config.headers || {}
      if (userInfo.role === 'student' && userInfo.studentId) {
        config.headers['X-Student-Id'] = userInfo.studentId
      }
      if (userInfo.role === 'admin' && userInfo.adminId) {
        config.headers['X-Admin-Id'] = userInfo.adminId
      }
    } catch (error) {
      console.warn('Invalid userInfo in localStorage', error)
    }
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
export const getAvailableTrips = () => api.get('/student/trips/available')
export const bookTrip = (busId) => api.post(`/student/trips/${busId}/book`)
export const joinByInviteCode = (code) => api.post('/student/join', { code })
export const getMyTrips = () => api.get('/student/trips')
export const refundTicket = (plateNumber) => api.post(`/student/refund/${plateNumber}`)
export const getStudentProfile = () => api.get('/student/profile')

// Admin
export const createTrip = (data) => api.post('/admin/trips', data)
export const getAllTrips = () => api.get('/admin/trips')
export const getTripPassengers = (plateNumber) => api.get(`/admin/trips/${plateNumber}/passengers`)
export const updateTripDriver = (plateNumber, driverPhone) => api.put(`/admin/trips/${plateNumber}/driver`, { driverPhone })
export const getAllDrivers = () => api.get('/admin/drivers')
export const addDriver = (data) => api.post('/admin/drivers', data)

export default api
