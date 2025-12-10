<template>
  <div class="page-container">
    <div class="header-row">
      <div>
        <h2 class="page-title">我的订单</h2>
        <p class="subhead">查看您的包车申请记录与审核状态。</p>
      </div>
      <button class="btn-primary" @click="$router.push('/student/charter')">
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
          <div v-if="order.invitationCode" class="info-row">
            <span class="label">邀请码</span>
            <span class="value invitation-code" @click="copyInvitationCode(order.invitationCode)">
              {{ order.invitationCode }} 📋
            </span>
          </div>
          <div v-if="order.status === '审核中'" class="info-row">
            <span class="label">支付状态</span>
            <span class="value" :class="order.isPaid ? 'paid' : 'unpaid'">
              {{ order.isPaid ? '✓ 已支付' : '未支付' }}
            </span>
          </div>
          
          <!-- Details for Approved -->
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

          <!-- Reason for Rejected -->
          <div v-if="order.status === '已拒绝'" class="reject-box">
            <p class="reject-reason">拒绝理由：{{ order.rejectReason }}</p>
          </div>
        </div>

        <div class="card-footer" v-if="order.status === '审核中'">
          <button class="btn-danger-ghost" @click="handleCancelOrder(order.orderId)">
            取消申请
          </button>
        </div>

        <div class="card-footer" v-if="order.status === '已拒绝'">
          <button class="btn-danger-ghost" @click="handleDeleteOrder(order.orderId)">
            删除订单
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyOrders, cancelOrder, deleteOrder, getBus } from '../../api'

const orders = ref([])

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
    }
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  fetchOrders()
})

const statusClass = (status) => {
  if (status === '已通过') return 'status-approved'
  if (status === '已拒绝') return 'status-rejected'
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
</script>

<style scoped>
.page-container {
  padding: 8px;
}

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  margin: 0 0 4px;
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
}

.icon-plus {
  margin-right: 6px;
  font-weight: bold;
}

.btn-primary {
  padding: 12px 24px;
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
  display: flex;
  align-items: center;
  gap: 6px;
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

.empty-state {
  text-align: center;
  padding: 60px;
  background: rgba(255, 255, 255, 0.02);
  border-radius: 20px;
  border: 1px dashed rgba(255, 255, 255, 0.1);
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-state p {
  color: var(--text-secondary);
  margin-bottom: 20px;
}

.orders-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.order-card {
  background: rgba(15, 23, 42, 0.8);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 16px;
  padding: 20px;
  transition: transform 0.2s, box-shadow 0.2s;
  position: relative;
  overflow: hidden;
}

.order-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-2);
  border-color: rgba(255, 255, 255, 0.15);
}

/* Status Colors */
.status-approved {
  border-left: 4px solid #10b981;
}
.status-rejected {
  border-left: 4px solid #ef4444;
}
.status-pending {
  border-left: 4px solid #f59e0b;
}

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
  font-size: 13px;
  font-weight: 600;
  padding: 4px 10px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.05);
}

.status-approved .status-badge { color: #34d399; background: rgba(16, 185, 129, 0.1); }
.status-rejected .status-badge { color: #f87171; background: rgba(239, 68, 68, 0.1); }
.status-pending .status-badge { color: #fbbf24; background: rgba(245, 158, 11, 0.1); }

.dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
}

.date {
  font-size: 12px;
  color: #ffffff;
}

.destination {
  margin: 0 0 12px;
  color: #ffffff;
  font-size: 18px;
  font-weight: 700;
}

.info-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.label {
  color: #ffffff;
}

.value {
  color: #ffffff;
}

.highlight-price {
  color: #fbbf24;
  font-weight: 700;
}

.value.paid {
  color: #34d399;
  font-weight: 600;
}

.value.unpaid {
  color: #f87171;
}

.invitation-code {
  color: #60a5fa;
  font-weight: 600;
  cursor: pointer;
  padding: 2px 6px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.invitation-code:hover {
  background: rgba(96, 165, 250, 0.1);
  color: #93c5fd;
}

.approved-box {
  margin-top: 12px;
  padding: 10px;
  background: rgba(16, 185, 129, 0.05);
  border-radius: 8px;
  border: 1px solid rgba(16, 185, 129, 0.1);
}

.detail-row {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  margin-bottom: 4px;
}

.detail-row:last-child {
  margin-bottom: 0;
}

.d-label {
  color: #a7f3d0;
}

.d-value {
  color: #ecfdf5;
}

.highlight {
  font-weight: bold;
  color: #34d399;
}

.reject-box {
  margin-top: 12px;
  padding: 10px;
  background: rgba(239, 68, 68, 0.05);
  border-radius: 8px;
  border: 1px solid rgba(239, 68, 68, 0.1);
}

.reject-reason {
  margin: 0;
  font-size: 13px;
  color: #fca5a5;
}

.card-footer {
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  text-align: right;
}

.btn-danger-ghost {
  background: transparent;
  border: 1px solid rgba(239, 68, 68, 0.3);
  color: #f87171;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-danger-ghost:hover {
  background: rgba(239, 68, 68, 0.1);
  border-color: #ef4444;
}

.btn-apply {
  background: linear-gradient(135deg, #22d3ee 0%, #8b5cf6 100%);
  border: none;
  color: #ffffff;
  padding: 14px 32px;
  border-radius: 28px;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 10px;
  box-shadow: 0 4px 16px rgba(34, 211, 238, 0.3);
  position: relative;
  overflow: hidden;
}

.btn-apply::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.2) 0%, transparent 100%);
  transition: left 0.3s ease;
  z-index: 1;
}

.btn-apply:hover::before {
  left: 100%;
}

.btn-apply:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(34, 211, 238, 0.4);
}

.btn-apply:active {
  transform: translateY(0);
}

.icon-go {
  font-size: 18px;
  display: inline-block;
}
</style>
