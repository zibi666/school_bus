/**
 * API 请求模块
 * 
 * 功能说明：
 * 1. 创建 axios 实例并配置基础 URL、超时时间
 * 2. 添加响应拦截器，统一处理 API 响应格式
 * 3. 导出所有后端 API 的请求函数，供各页面组件调用
 * 
 * API 响应格式：
 * {
 *   "code": 200,           // 业务状态码
 *   "message": "success",  // 业务消息
 *   "data": {...}          // 响应数据
 * }
 * 
 * 错误响应格式（400/401/403/404/500）：
 * {
 *   "code": 400,
 *   "message": "错误消息",
 *   "data": null
 * }
 */

import axios from 'axios'

/**
 * 创建 axios 实例
 * baseURL: API 服务器地址（支持环境变量配置，默认为本地开发服务器）
 * timeout: 请求超时时间 5 秒
 */
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://118.195.133.25:9090/api',
  timeout: 5000
})

/**
 * 响应拦截器
 * 
 * 功能：
 * - 从响应中提取 data 字段（ApiResponse 的响应体）
 * - 捕获 HTTP 错误，并返回后端定义的错误响应
 * 
 * 这样前端可以统一处理所有 API 响应，无需手动解析 response.data
 */
api.interceptors.response.use(response => {
  // 成功响应：返回后端返回的 ApiResponse 对象
  // 前端得到的是 { code, message, data } 的结构
  return response.data
}, error => {
  console.error('API Error:', error)
  
  // HTTP 错误（如 4xx, 5xx）处理
  // 返回后端返回的错误响应体（包含 code、message 等）
  if (error.response && error.response.data) {
    return Promise.reject(error.response.data)
  }
  
  // 网络错误、超时等其他错误
  return Promise.reject(error)
})

// ==================== 认证接口 ====================

/**
 * 学生登录
 * @param {Object} data - { studentId, password }
 * @returns {Promise} ApiResponse<Student>
 */
export const loginStudent = (data) => api.post('/auth/student/login', data)

/**
 * 学生注册
 * @param {Object} data - { studentId, name, password, location }
 * @returns {Promise} ApiResponse<Student>
 */
export const registerStudent = (data) => api.post('/auth/student/register', data)

/**
 * 管理员登录
 * @param {Object} data - { account, password }
 * @returns {Promise} ApiResponse<Admin>
 */
export const loginAdmin = (data) => api.post('/auth/admin/login', data)

// ==================== 学生业务接口 ====================

/**
 * 获取学生个人信息
 * @param {String} studentId - 学号
 * @returns {Promise} ApiResponse<Student>
 */
export const getStudentProfile = (studentId) => api.get(`/student/profile/${studentId}`)

/**
 * 更新学生个人信息
 * @param {String} studentId - 学号
 * @param {Object} data - { name, location, password } 
 * @returns {Promise} ApiResponse<void>
 */
export const updateStudentProfile = (studentId, data) => api.put(`/student/profile/${studentId}`, data)

/**
 * 创建新订单（包车申请）
 * @param {Object} data - { studentId, destination, startTime, endTime, requestedCarType }
 * @returns {Promise} ApiResponse<Order>
 */
export const createOrder = (data) => api.post('/student/order', data)

/**
 * 获取学生的所有订单（包括自己创建的和加入的）
 * @param {String} studentId - 学号
 * @returns {Promise} ApiResponse<Array<Order>>
 */
export const getMyOrders = (studentId) => api.get(`/student/orders/${studentId}`)

/**
 * 取消订单（仅"审核中"状态可调用）
 * @param {Integer} orderId - 订单 ID
 * @returns {Promise} ApiResponse<void>
 */
export const cancelOrder = (orderId) => api.post(`/student/order/cancel/${orderId}`)

/**
 * 删除订单
 * @param {Integer} orderId - 订单 ID
 * @returns {Promise} ApiResponse<void>
 */
export const deleteOrder = (orderId) => api.delete(`/student/order/${orderId}`)

/**
 * 获取车辆详情
 * @param {Integer} busId - 车辆 ID
 * @returns {Promise} ApiResponse<Bus>
 */
export const getBus = (busId) => api.get(`/student/bus/${busId}`)

/**
 * 计算订单价格（前端价格计算器）
 * 在用户选择时间和车型后，获取价格预览
 * @param {Object} data - { requestedCarType, startTime, endTime }
 * @returns {Promise} ApiResponse<{ hours, formattedHours, price, formattedTimeRange }>
 */
export const calculateOrderPrice = (data) => api.post('/student/order/calculate-price', data)

/**
 * 支付订单
 * 学生确认订单后调用此接口，将订单状态从"审核中"变为"已支付"
 * @param {Integer} orderId - 订单 ID
 * @returns {Promise} ApiResponse<void>
 */
export const payOrder = (orderId) => api.post(`/student/order/pay/${orderId}`)

/**
 * 通过邀请码查询订单（查看原申请人的订单信息）
 * 学生输入邀请码后，获取该邀请码对应的原始订单信息
 * @param {String} invitationCode - 邀请码（8位，如 ABC12345）
 * @returns {Promise} ApiResponse<Order>
 */
export const getOrderByInvitationCode = (invitationCode) => api.get(`/student/order/by-invitation-code/${invitationCode}`)

/**
 * 通过邀请码加入订单（拼车）
 * 学生输入邀请码后，创建一个新订单记录加入该邀请码的行程
 * @param {String} invitationCode - 邀请码（8位）
 * @param {String} studentId - 加入学生的学号
 * @returns {Promise} ApiResponse<Order>
 */
export const joinOrderByInvitationCode = (invitationCode, studentId) => api.post(`/student/order/join-by-invitation-code/${invitationCode}`, null, { params: { studentId } })

/**
 * 申请退票（级联退票）
 * 申请人退票时，同邀请码下所有订单都变为"已退票"
 * @param {Integer} orderId - 订单 ID（必须是申请人的订单）
 * @param {String} studentId - 学号（权限验证）
 * @returns {Promise} ApiResponse<void>
 */
export const refundOrder = (orderId, studentId) => api.post(`/student/order/refund/${orderId}`, null, { params: { studentId } })

/**
 * 离开订单（仅加入者可调用）
 * 加入者可以删除自己的订单记录，退出该拼车行程
 * @param {Integer} orderId - 订单 ID（必须是加入者的订单，isApplicant=false）
 * @param {String} studentId - 学号（权限验证）
 * @returns {Promise} ApiResponse<void>
 */
export const leaveOrder = (orderId, studentId) => api.post(`/student/order/leave/${orderId}`, null, { params: { studentId } })

// ==================== 管理员业务接口 ====================

/**
 * 获取所有订单（仅获取已支付的订单）
 * @returns {Promise} ApiResponse<Array<Order>>
 */
export const getAllOrders = () => api.get('/admin/orders')

/**
 * 审核通过订单
 * 管理员检查订单是否合理，选择合适的车辆进行分配
 * @param {Object} data - { orderId, busId }
 * @returns {Promise} ApiResponse<void>
 */
export const approveOrder = (data) => api.post('/admin/order/approve', data)

/**
 * 审核拒绝订单
 * 管理员因某些原因拒绝订单申请
 * @param {Object} data - { orderId, reason }
 * @returns {Promise} ApiResponse<void>
 */
export const rejectOrder = (data) => api.post('/admin/order/reject', data)

/**
 * 撤销订单（仅撤销已通过的订单）
 * 管理员可以撤销之前通过的订单，将其状态改为"已拒绝"
 * @param {Object} data - { orderId, reason }
 * @returns {Promise} ApiResponse<void>
 */
export const revokeOrder = (data) => api.post('/admin/order/revoke', data)

/**
 * 获取所有车辆
 * @returns {Promise} ApiResponse<Array<Bus>>
 */
export const getAllBuses = () => api.get('/admin/buses')

/**
 * 添加新车辆
 * @param {Object} data - { plateNumber, carType, driverName, number, price }
 * @returns {Promise} ApiResponse<Bus>
 */
export const addBus = (data) => api.post('/admin/bus', data)

/**
 * 删除车辆
 * @param {Integer} busId - 车辆 ID
 * @returns {Promise} ApiResponse<void>
 */
export const deleteBus = (busId) => api.delete(`/admin/bus/${busId}`)

/**
 * 检查车辆在指定时间段内的可用性
 * 查看车辆是否在给定时间范围内有冲突的已通过订单
 * @param {Object} data - { busId, startTime, endTime }
 * @returns {Promise} ApiResponse<{ available, conflictCount }>
 */
export const checkBusAvailability = (data) => api.post('/admin/bus/availability', data)

export default api
