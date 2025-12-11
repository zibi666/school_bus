<template>
  <div class="page-container">
    <div class="header-row">
      <div>
        <h2 class="page-title">我的订单</h2>
        <p class="subhead">查看您的包车申请记录与审核状态。</p>
      </div>
      <button class="btn-primary btn-new" @click="$router.push('/student/charter')">
        <span class="icon-plus">+</span> 新申请
      </button>
    </div>

    <div v-if="orders.length === 0" class="empty-state">
      <div class="empty-icon">📂</div>
      <p>暂无申请记录</p>
      <button class="btn-apply" @click="$router.push('/student/charter')">
        <span class="icon-go">🚌</span> 去申请
      </button>
    </div>

    <div v-else class="orders-grid">
      <div 
        v-for="order in orders" 
        :key="order.id" 
        class="order-card"
        :class="statusClass(order.status)"
      >
        <div class="card-top">
          <div class="status-badge">
            <span class="dot"></span>
            {{ order.status }}
          </div>
          <span class="date">{{ formatDate(order.startTime) }}</span>
        </div>
        
        <div class="card-body">
          <h3 class="destination">{{ order.destination }}</h3>
          
          <div class="info-row">
            <span class="label">时间</span>
            <span class="value">{{ formatTimeRange(order.startTime, order.endTime) }}</span>
          </div>
          <div class="info-row">
            <span class="label">车型</span>
            <span class="value">{{ order.requestedCarType }}</span>
          </div>
          <div v-if="order.price" class="info-row">
            <span class="label">总价</span>
            <span class="value highlight-price">¥{{ order.price }}</span>
          </div>
          <div v-if="order.invitationCode && order.isApplicant && order.status === '已通过'" class="info-row">
            <span class="label">邀请码</span>
            <span class="value invitation-code" @click="copyInvitationCode(order.invitationCode)">
              {{ order.invitationCode }}
            </span>
          </div>
          <div v-if="order.status === '审核中'" class="info-row">
            <span class="label">支付状态</span>
            <span class="value" :class="order.isPaid ? 'paid' : 'unpaid'">
              {{ order.isPaid ? '✓ 已支付' : '待支付' }}
            </span>
          </div>
          
          <div v-if="order.status === '已通过' && order.busInfo" class="approved-box">
            <div class="detail-row">
              <span class="d-label">车牌</span>
              <span class="d-value highlight">{{ order.busInfo.plateNumber }}</span>
            </div>
            <div class="detail-row">
              <span class="d-label">司机</span>
              <span class="d-value">{{ order.busInfo.driverName }}</span>
            </div>
            <div class="detail-row">
              <span class="d-label">电话</span>
              <span class="d-value">{{ order.busInfo.number }}</span>
            </div>
          </div>

          <div v-if="order.status === '已拒绝'" class="reject-box">
            <p class="reject-reason">拒绝理由：{{ order.rejectReason }}</p>
          </div>
        </div>

        <div class="card-footer" v-if="order.status === '审核中'">
          <div style="display:flex; justify-content:space-between; align-items:center; gap:12px; width: 100%;">
            <button v-if="!order.isPaid" class="btn-pay-small" @click="openPay(order)">
              立即支付
            </button>
            <span v-else></span> <button class="btn-danger-ghost" @click="handleCancelOrder(order.orderId)">
              取消申请
            </button>
          </div>
        </div>

        <div class="card-footer" v-if="order.status === '已通过' && order.isApplicant">
          <button class="btn-danger-ghost" @click="handleRefundOrder(order.orderId)">
            申请退票
          </button>
        </div>

        <div class="card-footer" v-if="order.status === '已拒绝'">
          <button class="btn-danger-ghost" @click="handleDeleteOrder(order.orderId)">
            删除订单
          </button>
        </div>

        <div class="card-footer" v-if="order.status === '已退票'">
          <p class="refund-status">✓ 已退票</p>
        </div>
      </div>
    </div>

    <div v-if="showPaymentModal" class="payment-modal" @click.self="cancelPayment">
      <div class="payment-content">
        <div class="payment-header">
          <h3>确认支付</h3>
          <button type="button" class="close-btn" @click="cancelPayment">×</button>
        </div>
        
        <div class="payment-body">
          <div class="payment-info">
            <div class="info-row">
              <span class="label">目的地</span>
              <span class="value">{{ currentPaymentOrder && currentPaymentOrder.destination }}</span>
            </div>
            <div class="info-row">
              <span class="label">用车时间</span>
              <span class="value">{{ currentPaymentOrder ? formatTimeRange(currentPaymentOrder.startTime, currentPaymentOrder.endTime) : '' }}</span>
            </div>
            <div class="info-row">
              <span class="label">车型</span>
              <span class="value">{{ currentPaymentOrder && currentPaymentOrder.requestedCarType }}</span>
            </div>
            <div class="info-row">
              <span class="label">用车时长</span>
              <span class="value">{{ currentPaymentOrder ? formatDuration(currentPaymentOrder.startTime, currentPaymentOrder.endTime) : '' }}</span>
            </div>
            <div class="info-row price-row">
              <span class="label">应付金额</span>
              <span class="price-value">¥{{ currentPaymentOrder && currentPaymentOrder.price }}</span>
            </div>
          </div>
          
          <div class="payment-notice">
            <p>⚠️ 支付成功后，订单将提交至管理员审核</p>
            <p>💡 审核通过后可在"我的订单"查看车辆信息</p>
          </div>
        </div>
        
        <div class="payment-footer">
          <button type="button" class="btn-cancel-pay" @click="cancelPayment">取消支付</button>
          <button type="button" class="btn-pay" @click="confirmPay">确认支付</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
// 请确保这里的 api 引用路径是正确的
import { getMyOrders, cancelOrder, deleteOrder, getBus, refundOrder, payOrder } from '../../api'

const route = useRoute()
const orders = ref([])
const showPaymentModal = ref(false)
const currentPaymentOrder = ref(null)

const fetchOrders = async () => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  if (!userInfo.studentId) return
  
  try {
    const res = await getMyOrders(userInfo.studentId)
    if (res.code === 200) {
      const list = res.data
      // Fetch bus info for approved orders
      for (let order of list) {
        if (order.status === '已通过' && order.busId) {
            try {
                const busRes = await getBus(order.busId)
                if (busRes.code === 200) {
                    order.busInfo = busRes.data
                }
            } catch (e) {
                console.error('Failed to fetch bus info', e)
            }
        }
      }
      orders.value = list.reverse() // Show newest first
      // ensure boolean flags are normalized
      orders.value.forEach(o => { o.isPaid = !!o.isPaid })
    }
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  fetchOrders()
})

const openPay = (order) => {
  currentPaymentOrder.value = order
  showPaymentModal.value = true
}

const confirmPay = async () => {
  if (!currentPaymentOrder.value) return
  try {
    const res = await payOrder(currentPaymentOrder.value.orderId)
    if (res.code === 200) {
      // 支付成功关闭弹窗并提示
      showPaymentModal.value = false
      alert('支付成功')
      // 更新本地状态并刷新列表
      currentPaymentOrder.value.isPaid = true
      fetchOrders()
    } else {
      alert(res.message || '支付失败')
    }
  } catch (e) {
    console.error(e)
    alert((e && e.message) || '支付异常')
  }
}

// 监听路由查询参数，如果有 refresh 参数则重新加载订单
watch(() => route.query.refresh, (newVal) => {
  if (newVal) {
    fetchOrders()
  }
})

const statusClass = (status) => {
  if (status === '已通过') return 'status-approved'
  if (status === '已拒绝') return 'status-rejected'
  if (status === '已退票') return 'status-refunded'
  return 'status-pending'
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString()
}

const formatTimeRange = (startTime, endTime) => {
  if (!startTime || !endTime) return ''
  
  try {
    const start = new Date(startTime)
    const end = new Date(endTime)
    
    const month = start.getMonth() + 1
    const day = start.getDate()
    const startHour = String(start.getHours()).padStart(2, '0')
    const startMinute = String(start.getMinutes()).padStart(2, '0')
    const endHour = String(end.getHours()).padStart(2, '0')
    const endMinute = String(end.getMinutes()).padStart(2, '0')
    
    return `${month}月${day}日 ${startHour}:${startMinute}-${endHour}:${endMinute}`
  } catch (e) {
    return ''
  }
}

const copyInvitationCode = (code) => {
  navigator.clipboard.writeText(code).then(() => {
    alert('邀请码已复制到剪贴板')
  }).catch(() => {
    alert('复制失败，请手动复制')
  })
}

const handleCancelOrder = async (id) => {
  if(confirm('确定要取消吗？')) {
    try {
        const res = await cancelOrder(id)
        if (res.code === 200) {
            fetchOrders()
        } else {
            alert(res.message)
        }
    } catch (e) {
        alert('取消失败')
    }
  }
}

const handleDeleteOrder = async (id) => {
  if(confirm('确定要删除该已拒绝订单吗？')) {
    try {
        const res = await deleteOrder(id)
        if (res.code === 200) {
            fetchOrders()
        } else {
            alert(res.message)
        }
    } catch (e) {
        if (e && e.message) {
            alert(e.message)
        } else {
            alert('删除失败')
        }
    }
  }
}

const handleRefundOrder = async (id) => {
  if(confirm('确定要申请退票吗？此操作将退掉该邀请码下所有学生的订单')) {
    try {
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
        const res = await refundOrder(id, userInfo.studentId)
        if (res.code === 200) {
            alert('退票成功')
            fetchOrders()
        } else {
            alert(res.message || '退票失败')
        }
    } catch (e) {
        if (e && e.message) {
            alert(e.message)
        } else {
            alert('退票失败')
        }
    }
  }
}

const cancelPayment = () => {
  showPaymentModal.value = false
}

const formatDuration = (startTime, endTime) => {
  if (!startTime || !endTime) return ''
  try {
    const s = new Date(startTime)
    const e = new Date(endTime)
    const diff = e - s
    const minutes = Math.floor(diff / 60000)
    const h = Math.floor(minutes / 60)
    const m = minutes % 60
    if (m === 0) return `${h}小时`
    return `${h}小时${m}分钟`
  } catch (e) {
    return ''
  }
}
</script>

<style scoped>
.page-container {
  padding: 24px; /* 增加内边距 */
  width: 100%;
  box-sizing: border-box;
}

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.page-title {
  margin: 0 0 8px;
  font-size: 32px;
  font-weight: 900;
  background: linear-gradient(135deg, #22d3ee 0%, #8b5cf6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.5px;
}

.subhead {
  color: #94a3b8;
  margin: 0;
  font-size: 14px;
}

/* --- 主要按钮样式 (New Application) --- */
.btn-primary {
  padding: 10px 20px;
  background: linear-gradient(135deg, #22d3ee 0%, #8b5cf6 100%);
  color: #ffffff;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 16px rgba(34, 211, 238, 0.3);
  letter-spacing: 0.5px;
  position: relative;
  overflow: hidden;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  text-decoration: none;
}

.btn-primary::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.5s ease;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(34, 211, 238, 0.4);
}

.btn-primary:hover::before {
  left: 100%;
}

.btn-primary:active {
  transform: translateY(0);
}

.icon-plus {
  font-size: 18px;
  line-height: 1;
}

/* --- 卡片内的支付小按钮 --- */
.btn-pay-small {
  padding: 8px 16px;
  background: linear-gradient(135deg, #f59e0b 0%, #f97316 100%); /* 橙色系，突出支付 */
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(249, 115, 22, 0.3);
  transition: all 0.2s ease;
}

.btn-pay-small:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(249, 115, 22, 0.4);
  filter: brightness(1.1);
}

/* --- 危险操作/幽灵按钮 --- */
.btn-danger-ghost {
  background: transparent;
  border: 1px solid rgba(239, 68, 68, 0.3);
  color: #f87171;
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-danger-ghost:hover {
  background: rgba(239, 68, 68, 0.1);
  border-color: #ef4444;
  color: #fca5a5;
}

/* --- 空状态 --- */
.empty-state {
  text-align: center;
  padding: 60px;
  background: rgba(255, 255, 255, 0.02);
  border-radius: 20px;
  border: 1px dashed rgba(255, 255, 255, 0.1);
  margin-top: 40px;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-state p {
  color: #94a3b8;
  margin-bottom: 20px;
}

.btn-apply {
  background: linear-gradient(135deg, #22d3ee 0%, #8b5cf6 100%);
  border: none;
  color: #ffffff;
  padding: 12px 28px;
  border-radius: 28px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 10px;
  box-shadow: 0 4px 16px rgba(34, 211, 238, 0.3);
}

.btn-apply:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(34, 211, 238, 0.4);
}

/* --- 订单网格 --- */
.orders-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.order-card {
  background: rgba(15, 23, 42, 0.8);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 18px;
  padding: 20px;
  transition: transform 0.2s, box-shadow 0.2s;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.order-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  border-color: rgba(255, 255, 255, 0.15);
}

/* 状态左边框颜色 */
.status-approved { border-left: 4px solid #10b981; }
.status-rejected { border-left: 4px solid #ef4444; }
.status-refunded { border-left: 4px solid #8b5cf6; }
.status-pending { border-left: 4px solid #f59e0b; }

.card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  font-weight: 700;
  padding: 4px 10px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.05);
}

.status-approved .status-badge { color: #34d399; background: rgba(16, 185, 129, 0.1); }
.status-rejected .status-badge { color: #f87171; background: rgba(239, 68, 68, 0.1); }
.status-refunded .status-badge { color: #a78bfa; background: rgba(139, 92, 246, 0.1); }
.status-pending .status-badge { color: #fbbf24; background: rgba(245, 158, 11, 0.1); }

.dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
}

.date {
  font-size: 12px;
  color: #94a3b8;
}

.card-body {
  flex: 1;
}

.destination {
  margin: 0 0 16px;
  color: #f8fafc;
  font-size: 20px;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
  align-items: center;
}

.label {
  color: #94a3b8;
}

.value {
  color: #e2e8f0;
  text-align: right;
  flex: 1;
  font-weight: 500;
}

.highlight-price {
  color: #22d3ee;
  font-weight: 700;
  font-size: 16px;
}

.value.paid { color: #34d399; }
.value.unpaid { color: #f87171; }

.invitation-code {
  color: #60a5fa;
  font-weight: 600;
  cursor: pointer;
  padding: 2px 6px;
  background: rgba(96, 165, 250, 0.1);
  border-radius: 4px;
  transition: all 0.2s ease;
}

.invitation-code:hover {
  background: rgba(96, 165, 250, 0.2);
  color: #93c5fd;
}

/* Approved Box */
.approved-box {
  margin-top: 16px;
  padding: 12px;
  background: rgba(16, 185, 129, 0.08);
  border-radius: 12px;
  border: 1px solid rgba(16, 185, 129, 0.15);
}

.detail-row {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  margin-bottom: 6px;
}
.detail-row:last-child { margin-bottom: 0; }

.d-label { color: #6ee7b7; }
.d-value { color: #ecfdf5; font-weight: 500; }
.highlight { font-weight: bold; color: #34d399; }

/* Reject Box */
.reject-box {
  margin-top: 16px;
  padding: 12px;
  background: rgba(239, 68, 68, 0.08);
  border-radius: 12px;
  border: 1px solid rgba(239, 68, 68, 0.15);
}

.reject-reason {
  margin: 0;
  font-size: 13px;
  color: #fca5a5;
  line-height: 1.4;
}

.card-footer {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  display: flex;
  justify-content: flex-end;
}

.refund-status {
  color: #a78bfa;
  font-weight: 600;
  font-size: 14px;
  margin: 0;
}

/* =========================================
   支付弹窗美化样式 (复用 Apply 页面风格)
   ========================================= */

.payment-modal {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.payment-content {
  background: linear-gradient(135deg, rgba(15, 23, 42, 0.95), rgba(30, 41, 59, 0.95));
  border: 1px solid rgba(34, 211, 238, 0.3);
  border-radius: 24px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
  animation: slideUp 0.3s ease;
  overflow: hidden;
}

@keyframes slideUp {
  from {
    transform: translateY(30px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.payment-header {
  padding: 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.payment-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #f8fafc;
}

.payment-header .close-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: rgba(255, 255, 255, 0.08);
  color: #cbd5e1;
  font-size: 24px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.payment-header .close-btn:hover {
  background: rgba(255, 255, 255, 0.12);
  color: #f8fafc;
}

.payment-body {
  padding: 24px;
}

.payment-info {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 20px;
}

.payment-info .info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  margin-bottom: 0; /* Override generic info-row */
}

.payment-info .info-row:last-child {
  border-bottom: none;
}

.price-row {
  padding-top: 16px !important;
  margin-top: 8px !important;
  border-top: 2px solid rgba(34, 211, 238, 0.3) !important;
}

.price-value {
  color: #22d3ee;
  font-size: 28px;
  font-weight: 800;
}

.payment-notice {
  background: rgba(34, 211, 238, 0.08);
  border: 1px solid rgba(34, 211, 238, 0.2);
  border-radius: 12px;
  padding: 16px;
}

.payment-notice p {
  margin: 0 0 8px 0;
  color: #94a3b8;
  font-size: 13px;
  line-height: 1.6;
}

.payment-notice p:last-child {
  margin-bottom: 0;
}

.payment-footer {
  padding: 20px 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.btn-cancel-pay,
.btn-pay {
  padding: 14px 20px;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-cancel-pay {
  background: rgba(255, 255, 255, 0.08);
  color: #e2e8f0;
}

.btn-cancel-pay:hover {
  background: rgba(255, 255, 255, 0.12);
}

.btn-pay {
  background: linear-gradient(135deg, #22d3ee 0%, #8b5cf6 100%);
  color: #ffffff;
  box-shadow: 0 4px 16px rgba(34, 211, 238, 0.4);
}

.btn-pay:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(34, 211, 238, 0.5);
}
</style>