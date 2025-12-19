<template>
  <div class="passengers-container">
    <div class="page-header">
      <h2 class="headline">我的发车</h2>
      <p class="subhead">查看您申请的车辆及乘客信息</p>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading-state">
      <div class="loader"></div>
      <p>正在加载车辆信息...</p>
    </div>

    <!-- Empty State -->
    <div v-else-if="myManagedTrips.length === 0" class="empty-state">
      <div class="empty-icon">🚌</div>
      <h3>暂无申请车辆</h3>
      <p>您还没有申请过任何车辆，或者您的申请尚未通过。</p>
      <button class="btn-primary" @click="$router.push('/student/charter')">
        去申请包车
      </button>
    </div>

    <!-- Content -->
    <div v-else class="trips-list">
      <div v-for="group in groupDefinitions" :key="group.key" class="status-group">
        <!-- Group Header -->
        <div class="group-header" @click="toggleGroup(group.key)" :class="{ active: activeGroups[group.key] }">
          <div class="group-title">
            <span class="group-icon">{{ group.icon }}</span>
            <span class="label">{{ group.label }}</span>
            <span class="count">({{ groupedOrders[group.key].length }})</span>
            <span class="desc" v-if="!activeGroups[group.key] || windowWidth > 640">{{ group.desc }}</span>
          </div>
          <div class="chevron" :class="{ rotated: activeGroups[group.key] }">▼</div>
        </div>

        <!-- Group Content -->
        <div v-show="activeGroups[group.key]" class="group-body">
          <div v-if="groupedOrders[group.key].length === 0" class="empty-group">
            暂无此状态的订单
          </div>
          
          <div v-else class="cards-wrapper">
            <div v-for="trip in groupedOrders[group.key]" :key="trip.id" class="trip-card glass-surface">
              <!-- Header: 车辆与路线信息 -->
              <div class="card-header">
                <div class="route-info">
                  <div class="destination">
                    <span class="icon">📍</span>
                    {{ trip.destination }}
                  </div>
                  <div class="time-badge">
                    {{ formatTime(trip.startTime) }} - {{ formatTime(trip.endTime) }}
                  </div>
                </div>
                <div class="status-badge" :class="getStatusClass(trip.status)">
                  {{ getStatusText(trip.status) }}
                </div>
              </div>

              <!-- Body: 车辆详情 -->
              <div class="card-body">
                <div class="info-grid">
                  <div class="info-item">
                    <label>邀请码</label>
                    <div class="value code">{{ trip.invitationCode }}</div>
                  </div>
                  <div class="info-item">
                    <label>车型</label>
                    <div class="value">{{ trip.requestedCarType }}</div>
                  </div>
                  <div class="info-item" v-if="trip.bus">
                    <label>车牌号</label>
                    <div class="value">{{ trip.bus.plateNumber }}</div>
                  </div>
                  <div class="info-item" v-if="trip.bus">
                    <label>司机</label>
                    <div class="value">{{ trip.bus.driverName }} ({{ trip.bus.number }})</div>
                  </div>
                </div>
              </div>

              <!-- Footer: 乘客列表 -->
              <div class="card-footer">
                <div class="passengers-section">
                  <h4>
                    <span class="icon">👥</span> 
                    乘客名单 
                    <span class="count">({{ getPassengers(trip).length }}人)</span>
                  </h4>
                  
                  <div class="passengers-table-wrapper">
                    <table class="passengers-table">
                      <thead>
                        <tr>
                          <th>姓名/学号</th>
                          <th>类型</th>
                          <th>加入时间</th>
                          <th>状态</th>
                        </tr>
                      </thead>
                      <tbody>
                        <tr v-for="p in getPassengers(trip)" :key="p.id">
                          <td>
                            <div class="user-cell">
                              <div class="avatar">{{ p.name[0] }}</div>
                              <div class="user-detail">
                                <span class="name">{{ p.name }}</span>
                                <span class="id">{{ p.studentId }}</span>
                              </div>
                            </div>
                          </td>
                          <td>
                            <span class="role-badge" :class="p.isApplicant ? 'applicant' : 'member'">
                              {{ p.isApplicant ? '申请人' : '邀请加入' }}
                            </span>
                          </td>
                          <td class="time-cell">{{ p.joinTime }}</td>
                          <td>
                            <span class="status-dot" :class="p.status === '已上车' ? 'active' : 'pending'"></span>
                            {{ p.status }}
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { getMyOrders, getBus, getTripPassengers } from '../../api'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(true)
const allOrders = ref([])
const userInfo = ref({})

// 获取当前用户信息
const loadUserInfo = () => {
  const info = localStorage.getItem('userInfo')
  if (info) {
    userInfo.value = JSON.parse(info)
  }
}

// 过滤出我申请的车辆 (我是申请人)
const myManagedTrips = computed(() => {
  return allOrders.value.filter(order => {
    // 必须是申请人 (isApplicant 为 true)
    return order.isApplicant === true
  })
})

const fetchData = async () => {
  loading.value = true
  try {
    if (!userInfo.value.studentId) return
    const res = await getMyOrders(userInfo.value.studentId)
    if (res.code === 200) {
      const list = res.data
      
      // 为已通过的订单获取车辆信息
      for (let order of list) {
        if (order.status === '已通过' && order.busId) {
          try {
            const busRes = await getBus(order.busId)
            if (busRes.code === 200) {
              order.bus = busRes.data
            }
          } catch (e) {
            console.error('获取车辆信息失败', e)
          }
        }
        
        // 获取同车乘客信息
        if (order.invitationCode) {
          try {
            const pRes = await getTripPassengers(order.invitationCode)
            if (pRes.code === 200) {
              order.passengersList = pRes.data
            }
          } catch (e) {
            console.error('获取乘客信息失败', e)
          }
        }
      }
      
      allOrders.value = list
    }
  } catch (error) {
    console.error('获取订单失败', error)
  } finally {
    loading.value = false
  }
}

// 分组定义
const groupDefinitions = [
  { key: 'reviewing', label: '审核中', icon: '⏳', desc: '已支付，等待管理员分配车辆' },
  { key: 'pending_pay', label: '待支付', icon: '💳', desc: '请尽快支付，未支付订单不予审核' },
  { key: 'completed', label: '已完成', icon: '✅', desc: '行程已结束或已安排车辆' },
  { key: 'refunded', label: '已退票', icon: '↩️', desc: '已取消、已拒绝或已退票的订单' }
]

// 折叠状态
const activeGroups = ref({
  reviewing: true,
  pending_pay: true,
  completed: false,
  refunded: false
})

const toggleGroup = (key) => {
  activeGroups.value[key] = !activeGroups.value[key]
}

// 分组计算属性
const groupedOrders = computed(() => {
  const groups = {
    reviewing: [],
    pending_pay: [],
    completed: [],
    refunded: []
  }
  
  myManagedTrips.value.forEach(trip => {
    if (trip.status === '审核中') {
      if (trip.isPaid) {
        groups.reviewing.push(trip)
      } else {
        groups.pending_pay.push(trip)
      }
    } else if (['已通过', '已完成'].includes(trip.status)) {
      groups.completed.push(trip)
    } else if (['已退票', '已拒绝', '已取消'].includes(trip.status)) {
      groups.refunded.push(trip)
    }
  })
  
  return groups
})

const formatTime = (timeStr) => {
  if (!timeStr) return '--'
  const date = new Date(timeStr)
  return `${date.getMonth() + 1}/${date.getDate()} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const getStatusText = (status) => {
  const map = {
    '审核中': '审核中',
    '已通过': '已通过',
    '已支付': '已支付',
    '已拒绝': '已拒绝',
    '已取消': '已取消',
    '已退票': '已退票',
    '已完成': '已完成'
  }
  return map[status] || status
}

const getStatusClass = (status) => {
  const map = {
    '审核中': 'status-pending',
    '已通过': 'status-success',
    '已支付': 'status-success',
    '已拒绝': 'status-danger',
    '已取消': 'status-gray',
    '已退票': 'status-gray',
    '已完成': 'status-success'
  }
  return map[status] || ''
}

// 获取乘客列表
const getPassengers = (trip) => {
  if (trip.passengersList && trip.passengersList.length > 0) {
    return trip.passengersList.map(p => ({
      id: p.orderId,
      name: p.studentName || '未知用户',
      studentId: p.studentId,
      isApplicant: p.isApplicant,
      joinTime: p.startTime, // 暂用开始时间代替加入时间
      status: p.status
    }))
  }

  // Fallback (如果API失败，显示自己)
  return [
    {
      id: trip.studentId,
      name: userInfo.value.name || '我',
      studentId: trip.studentId,
      isApplicant: true,
      joinTime: trip.startTime,
      status: trip.status
    }
  ]
}

onMounted(() => {
  loadUserInfo()
  fetchData()
  window.addEventListener('resize', updateWidth)
})

const windowWidth = ref(window.innerWidth)
const updateWidth = () => {
  windowWidth.value = window.innerWidth
}

onUnmounted(() => {
  window.removeEventListener('resize', updateWidth)
})
</script>

<style scoped>
.passengers-container {
  padding: 24px;
  animation: fadeIn 0.5s ease;
  width: 100%;
  box-sizing: border-box;
}

.page-header {
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

.trips-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.trip-card {
  background: rgba(15, 23, 42, 0.8);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 18px;
  overflow: hidden;
  transition: transform 0.2s, box-shadow 0.2s;
}

.trip-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.4);
  border-color: rgba(255, 255, 255, 0.15);
}

.card-header {
  padding: 16px 24px;
  background: rgba(255, 255, 255, 0.02);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.route-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.destination {
  font-size: 18px;
  font-weight: 700;
  color: #f8fafc;
  display: flex;
  align-items: center;
  gap: 8px;
}

.time-badge {
  font-size: 13px;
  color: #94a3b8;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}
.status-pending { background: rgba(245, 158, 11, 0.15); color: #fbbf24; }
.status-success { background: rgba(16, 185, 129, 0.15); color: #34d399; }
.status-danger { background: rgba(239, 68, 68, 0.15); color: #f87171; }
.status-gray { background: rgba(148, 163, 184, 0.15); color: #94a3b8; }

.card-body {
  padding: 24px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.info-item label {
  display: block;
  font-size: 12px;
  color: #94a3b8;
  margin-bottom: 6px;
  font-weight: 500;
}

.info-item .value {
  font-size: 15px;
  color: #e2e8f0;
  font-weight: 500;
}

.info-item .value.code {
  font-family: monospace;
  color: #22d3ee;
  font-size: 16px;
  letter-spacing: 1px;
  background: rgba(34, 211, 238, 0.1);
  padding: 2px 8px;
  border-radius: 4px;
  display: inline-block;
}

.card-footer {
  background: rgba(0, 0, 0, 0.2);
  padding: 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
}

.passengers-section h4 {
  margin: 0 0 16px 0;
  color: #f8fafc;
  font-size: 15px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.passengers-section .count {
  color: #94a3b8;
  font-weight: normal;
  font-size: 13px;
}

.passengers-table-wrapper {
  overflow-x: auto;
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 12px;
}

.passengers-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.passengers-table th {
  text-align: left;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.03);
  color: #94a3b8;
  font-weight: 600;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  font-size: 13px;
}

.passengers-table td {
  padding: 12px 16px;
  color: #e2e8f0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.passengers-table tr:last-child td {
  border-bottom: none;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 14px;
  color: white;
  box-shadow: 0 2px 8px rgba(99, 102, 241, 0.3);
}

.user-detail {
  display: flex;
  flex-direction: column;
}

.user-detail .name {
  font-weight: 500;
  font-size: 14px;
}

.user-detail .id {
  font-size: 11px;
  color: #94a3b8;
}

.role-badge {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 500;
}

.role-badge.applicant {
  background: rgba(34, 211, 238, 0.15);
  color: #22d3ee;
  border: 1px solid rgba(34, 211, 238, 0.3);
}

.role-badge.member {
  background: rgba(148, 163, 184, 0.15);
  color: #94a3b8;
}

.status-dot {
  display: inline-block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #94a3b8;
  margin-right: 6px;
}

.status-dot.active {
  background: #34d399;
  box-shadow: 0 0 8px rgba(52, 211, 153, 0.4);
}

/* Loading & Empty States */
.loading-state, .empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
  color: #94a3b8;
}

.empty-state {
  background: rgba(255, 255, 255, 0.02);
  border-radius: 20px;
  border: 1px dashed rgba(255, 255, 255, 0.1);
}

.loader {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  border-top-color: #22d3ee;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.btn-primary {
  padding: 10px 24px;
  background: linear-gradient(135deg, #22d3ee 0%, #8b5cf6 100%);
  color: #ffffff;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 16px rgba(34, 211, 238, 0.3);
  margin-top: 16px;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(34, 211, 238, 0.4);
}

@keyframes spin { to { transform: rotate(360deg); } }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }

@media (max-width: 640px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
}

/* Grouping Styles */
.status-group {
  margin-bottom: 16px;
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(15, 23, 42, 0.4);
}

.group-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: rgba(255, 255, 255, 0.03);
  cursor: pointer;
  transition: all 0.2s ease;
  user-select: none;
}

.group-header:hover {
  background: rgba(255, 255, 255, 0.06);
}

.group-header.active {
  background: rgba(255, 255, 255, 0.08);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.group-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.group-icon {
  font-size: 18px;
}

.group-title .label {
  font-weight: 700;
  font-size: 16px;
  color: #f8fafc;
}

.group-title .count {
  background: rgba(255, 255, 255, 0.1);
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  color: #94a3b8;
}

.group-title .desc {
  font-size: 13px;
  color: #64748b;
  margin-left: 8px;
  font-weight: normal;
}

.chevron {
  color: #94a3b8;
  font-size: 12px;
  transition: transform 0.3s ease;
}

.chevron.rotated {
  transform: rotate(180deg);
}

.group-body {
  padding: 16px;
  animation: slideDown 0.3s ease-out;
}

.empty-group {
  text-align: center;
  padding: 32px;
  color: #64748b;
  font-size: 14px;
  border: 1px dashed rgba(255, 255, 255, 0.1);
  border-radius: 12px;
}

.cards-wrapper {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

@keyframes slideDown {
  from { opacity: 0; transform: translateY(-10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
