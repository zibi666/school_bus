<template>
  <div class="page-container">
    <div class="header-row">
      <h2>我的订单</h2>
      <button class="new-btn" @click="$router.push('/student/charter')">+ 新申请</button>
    </div>

    <div class="orders-grid">
      <div v-for="order in orders" :key="order.id" class="order-card" :class="statusClass(order.status)">
        <div class="card-header">
          <span class="status-badge">{{ order.status }}</span>
          <span class="date">{{ order.createTime }}</span>
        </div>
        
        <div class="card-body">
          <h3>{{ order.destination }}</h3>
          <p class="info-row"><i class="icon"></i> {{ order.usageTime }}</p>
          <p class="info-row"><i class="icon"></i> {{ order.requestedCarType }}</p>
          
          <!-- Details for Approved -->
          <div v-if="order.status === '已通过' && order.busInfo" class="approved-details">
            <div class="detail-item">
              <span class="label">车牌号</span>
              <span class="value">{{ order.busInfo.plateNumber }}</span>
            </div>
            <div class="detail-item">
              <span class="label">司机</span>
              <span class="value">{{ order.busInfo.driverName }}</span>
            </div>
          </div>

          <!-- Reason for Rejected -->
          <div v-if="order.status === '已拒绝'" class="reject-reason">
            拒绝理由：{{ order.rejectReason }}
          </div>
        </div>

        <div class="card-footer" v-if="order.status === '审核中'">
          <button class="cancel-btn" @click="handleCancelOrder(order.orderId)">取消申请</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyOrders, cancelOrder, getBus } from '../../api'

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
      orders.value = list
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
</script>

<style scoped>
.page-container {
  padding: 2rem;
  max-width: 1000px;
  margin: 0 auto;
}

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.new-btn {
  background: #8b5cf6;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: bold;
}

.orders-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.order-card {
  background: white;
  border-radius: 16px;
  padding: 1.5rem;
  box-shadow: 0 4px 6px rgba(0,0,0,0.05);
  border: 1px solid #f0f0f0;
  transition: transform 0.2s;
  display: flex;
  flex-direction: column;
}

.order-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 15px rgba(0,0,0,0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 1rem;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: bold;
}

.status-pending .status-badge { background: #fef3c7; color: #d97706; }
.status-approved .status-badge { background: #d1fae5; color: #059669; }
.status-rejected .status-badge { background: #fee2e2; color: #dc2626; }

.status-approved { border-left: 5px solid #059669; }
.status-rejected { border-left: 5px solid #dc2626; }
.status-pending { border-left: 5px solid #d97706; }

.card-body h3 {
  margin: 0 0 0.5rem 0;
  color: #1f2937;
}

.info-row {
  color: #6b7280;
  font-size: 0.9rem;
  margin: 0.25rem 0;
}

.approved-details {
  margin-top: 1rem;
  background: #f9fafb;
  padding: 0.8rem;
  border-radius: 8px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  font-size: 0.85rem;
  margin-bottom: 0.3rem;
}

.price-tag {
  text-align: right;
  font-size: 1.2rem;
  font-weight: bold;
  color: #8b5cf6;
  margin-top: 0.5rem;
}

.reject-reason {
  margin-top: 1rem;
  background: #fef2f2;
  color: #b91c1c;
  padding: 0.8rem;
  border-radius: 8px;
  font-size: 0.9rem;
}

.card-footer {
  margin-top: auto;
  padding-top: 1rem;
  text-align: right;
}

.cancel-btn {
  color: #ef4444;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 0.9rem;
}
.cancel-btn:hover { text-decoration: underline; }
</style>
