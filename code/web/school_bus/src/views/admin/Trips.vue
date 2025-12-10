<template>
  <div class="page-container">
    <div class="header-row">
      <div>
        <h2 class="page-title">订单审核</h2>
        <p class="subhead">处理学生包车申请，调度车辆资源。</p>
      </div>
    </div>
    
    <div class="table-wrapper">
      <table class="glass-table">
        <thead>
          <tr>
            <th>订单号</th>
            <th>申请人</th>
            <th>目的地</th>
            <th>时间段</th>
            <th>需求车型</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in orders" :key="order.orderId">
            <td class="font-mono">#{{ order.orderId }}</td>
            <td>{{ order.studentId }}</td>
            <td>{{ order.destination }}</td>
            <td>{{ formatTimeRange(order.startTime, order.endTime) }}</td>
            <td><span class="car-tag">{{ order.requestedCarType }}</span></td>
            <td>
              <span :class="['status-pill', statusClass(order.status)]">
                <span class="dot"></span>
                {{ order.status }}
              </span>
            </td>
            <td>
              <div v-if="order.status === '审核中'" class="actions">
                <button class="btn-icon-approve" @click="openApprove(order)" title="通过">
                  ✓
                </button>
                <button class="btn-icon-reject" @click="openReject(order)" title="拒绝">
                  ✕
                </button>
              </div>
              <div v-else-if="order.status === '已通过'" class="actions">
                <button class="btn-icon-revoke" @click="openRevoke(order)" title="撤销">
                  ↶
                </button>
              </div>
              <span v-else class="text-gray">-</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Approve Modal -->
    <div v-if="showApproveModal" class="modal-overlay" @click.self="showApproveModal = false">
      <div class="modal glass-modal approve-modal">
        <div class="modal-header">
          <div>
            <h3>✓ 审核通过</h3>
            <p class="modal-subtitle">为订单分配车辆</p>
          </div>
          <button class="close-btn" @click="showApproveModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>选择车辆</label>
            <select v-model="approveForm.busId" class="glass-input">
              <option value="" disabled>请选择车辆</option>
              <option v-for="bus in availableBuses" :key="bus.busId" :value="bus.busId">
                {{ bus.plateNumber }} ({{ bus.driverName }}) - {{ bus.carType }}
              </option>
            </select>
          </div>
        </div>
        <div class="modal-actions">
          <button class="btn-ghost" @click="showApproveModal = false">取消</button>
          <button class="btn-primary-admin" @click="confirmApprove">确认分配</button>
        </div>
      </div>
    </div>

    <!-- Reject Modal -->
    <div v-if="showRejectModal" class="modal-overlay" @click.self="showRejectModal = false">
      <div class="modal glass-modal reject-modal">
        <div class="modal-header">
          <div>
            <h3>✕ 拒绝申请</h3>
            <p class="modal-subtitle">请说明拒绝原因</p>
          </div>
          <button class="close-btn" @click="showRejectModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>拒绝理由</label>
            <textarea 
              v-model="rejectForm.reason" 
              placeholder="请输入拒绝原因..." 
              class="glass-input"
              rows="4"
            ></textarea>
          </div>
        </div>
        <div class="modal-actions">
          <button class="btn-ghost" @click="showRejectModal = false">取消</button>
          <button class="btn-danger" @click="confirmReject">确认拒绝</button>
        </div>
      </div>
    </div>

    <!-- Revoke Modal -->
    <div v-if="showRevokeModal" class="modal-overlay" @click.self="showRevokeModal = false">
      <div class="modal glass-modal revoke-modal">
        <div class="modal-header">
          <div>
            <h3>🔄 撤销订单</h3>
            <p class="modal-subtitle">订单将返回待审核状态</p>
          </div>
          <button class="close-btn" @click="showRevokeModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>撤销理由</label>
            <textarea 
              v-model="revokeForm.reason" 
              placeholder="请输入撤销原因..." 
              class="glass-input"
              rows="4"
            ></textarea>
          </div>
          <div class="warning-box">
            ⚠️ 撤销后该订单状态将变为"已拒绝"，分配的车辆将被释放。
          </div>
        </div>
        <div class="modal-actions">
          <button class="btn-ghost" @click="showRevokeModal = false">取消</button>
          <button class="btn-revoke" @click="confirmRevoke">确认撤销</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAllOrders, getAllBuses, approveOrder, rejectOrder, revokeOrder } from '../../api'

const orders = ref([])
const allBuses = ref([])
const availableBuses = ref([])
const showApproveModal = ref(false)
const showRejectModal = ref(false)
const showRevokeModal = ref(false)
const currentOrder = ref(null)

const approveForm = reactive({ busId: '' })
const rejectForm = reactive({ reason: '' })
const revokeForm = reactive({ reason: '' })

const fetchData = async () => {
    try {
        const res = await getAllOrders()
        if (res.code === 200) {
            orders.value = res.data.reverse()
        }
        
        const busRes = await getAllBuses()
        if (busRes.code === 200) {
            allBuses.value = busRes.data
        }
    } catch (e) {
        console.error(e)
    }
}

onMounted(() => {
  fetchData()
})

const statusClass = (status) => {
  if (status === '已通过') return 'status-approved'
  if (status === '已拒绝') return 'status-rejected'
  return 'status-pending'
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

const openApprove = (order) => {
    currentOrder.value = order
    approveForm.busId = ''
    
    // 筛选与订单车型匹配的所有车辆
    availableBuses.value = allBuses.value.filter(bus => {
        // 检查车型是否匹配（使用模糊匹配，因为数据库中可能是 "大巴 (45座)" 这样的格式）
        return bus.carType.includes(order.requestedCarType) || order.requestedCarType.includes(bus.carType)
    })
    
    showApproveModal.value = true
}

const openReject = (order) => {
    currentOrder.value = order
    rejectForm.reason = ''
    showRejectModal.value = true
}

const openRevoke = (order) => {
    currentOrder.value = order
    revokeForm.reason = ''
    showRevokeModal.value = true
}

const confirmApprove = async () => {
    if (!approveForm.busId) {
        alert('请选择车辆')
        return
    }
    try {
        const res = await approveOrder({
            orderId: currentOrder.value.orderId,
            busId: approveForm.busId
        })
        if (res.code === 200) {
            showApproveModal.value = false
            fetchData()
        } else {
            alert(res.message)
        }
    } catch (e) {
        alert('操作失败')
    }
}

const confirmReject = async () => {
    if (!rejectForm.reason) {
        alert('请输入拒绝理由')
        return
    }
    try {
        const res = await rejectOrder({
            orderId: currentOrder.value.orderId,
            reason: rejectForm.reason
        })
        if (res.code === 200) {
            showRejectModal.value = false
            fetchData()
        } else {
            alert(res.message)
        }
    } catch (e) {
        alert('操作失败')
    }
}

const confirmRevoke = async () => {
    if (!revokeForm.reason) {
        alert('请输入撤销理由')
        return
    }
    try {
        const res = await revokeOrder({
            orderId: currentOrder.value.orderId,
            reason: revokeForm.reason
        })
        if (res.code === 200) {
            showRevokeModal.value = false
            fetchData()
        } else {
            alert(res.message)
        }
    } catch (e) {
        alert('操作失败')
    }
}
</script>

<style scoped>
.page-container {
  padding: 24px;
}

.header-row {
  margin-bottom: 24px;
}

.page-title {
  margin: 0 0 4px;
  font-size: 32px;
  font-weight: 900;
  background: linear-gradient(135deg, #f43f5e 0%, #e11d48 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.5px;
}

.subhead {
  color: var(--text-secondary);
  margin: 0;
}

.table-wrapper {
  background: linear-gradient(135deg, rgba(15, 23, 42, 0.85) 0%, rgba(30, 41, 59, 0.8) 100%);
  border: 1px solid rgba(244, 63, 94, 0.15);
  border-radius: 18px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3), inset 0 1px 0 rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
}

.glass-table {
  width: 100%;
  border-collapse: collapse;
  color: #e2e8f0;
}

.glass-table th {
  text-align: left;
  padding: 18px 20px;
  background: linear-gradient(135deg, rgba(15, 23, 42, 0.95) 0%, rgba(30, 41, 59, 0.9) 100%);
  color: #e0e7ff;
  font-weight: 700;
  font-size: 13px;
  letter-spacing: 1px;
  text-transform: uppercase;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.05);
}

.glass-table td {
  padding: 16px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  font-size: 14px;
}

.glass-table tr:last-child td {
  border-bottom: none;
}

.glass-table tr:hover {
  background: rgba(34, 211, 238, 0.08);
}

.font-mono {
  font-family: monospace;
  color: #60a5fa;
  font-weight: 600;
}

.car-tag {
  display: inline-block;
  padding: 2px 8px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 6px;
  font-size: 12px;
  color: #cbd5e1;
}

.status-pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.status-approved { background: rgba(16, 185, 129, 0.1); color: #34d399; }
.status-rejected { background: rgba(244, 63, 94, 0.1); color: #f43f5e; }
.status-pending { background: rgba(245, 158, 11, 0.1); color: #fbbf24; }

.dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
}

.actions {
  display: flex;
  gap: 8px;
}

.btn-icon-approve,
.btn-icon-reject {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.btn-icon-approve {
  background: rgba(16, 185, 129, 0.1);
  color: #34d399;
}

.btn-icon-approve:hover {
  background: #10b981;
  color: white;
}

.btn-icon-reject {
  background: rgba(244, 63, 94, 0.1);
  color: #f43f5e;
}

.btn-icon-reject:hover {
  background: #f43f5e;
  color: white;
}

.btn-icon-revoke {
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.15) 0%, rgba(249, 115, 22, 0.1) 100%);
  color: #fbbf24;
  border: 1.5px solid rgba(245, 158, 11, 0.3);
  width: 36px;
  height: 36px;
  border-radius: 10px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  font-size: 16px;
  font-weight: 600;
  position: relative;
  overflow: hidden;
}

.btn-icon-revoke::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.4s ease;
}

.btn-icon-revoke:hover {
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.25) 0%, rgba(249, 115, 22, 0.2) 100%);
  border-color: rgba(245, 158, 11, 0.6);
  color: #fcd34d;
  box-shadow: 0 4px 16px rgba(245, 158, 11, 0.25);
  transform: translateY(-2px);
}

.btn-icon-revoke:hover::before {
  left: 100%;
}

.text-gray {
  color: #64748b;
}

/* Modal Styles (Shared) */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.glass-modal {
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
  border: 1.5px solid rgba(244, 63, 94, 0.15);
  border-radius: 20px;
  padding: 28px;
  width: 100%;
  max-width: 480px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.6), 0 0 30px rgba(244, 63, 94, 0.1);
  backdrop-filter: blur(10px);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  gap: 16px;
}

.modal-body {
  margin-bottom: 24px;
}

.modal-header h3 {
  margin: 0 0 4px;
  color: #f8fafc;
  font-size: 18px;
  font-weight: 700;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.reject-modal .modal-header h3 {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.revoke-modal {
  border-color: rgba(245, 158, 11, 0.15);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.6), 0 0 30px rgba(245, 158, 11, 0.1);
}

.warning-box {
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid rgba(239, 68, 68, 0.2);
  border-radius: 10px;
  padding: 12px 14px;
  color: #fca5a5;
  font-size: 13px;
  font-weight: 500;
  margin-top: 16px;
  line-height: 1.5;
}

.modal-subtitle {
  margin: 0;
  color: #94a3b8;
  font-size: 13px;
  font-weight: 500;
}

.close-btn {
  background: rgba(244, 63, 94, 0.08);
  border: 1px solid rgba(244, 63, 94, 0.15);
  color: #94a3b8;
  font-size: 24px;
  cursor: pointer;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.close-btn:hover {
  background: rgba(244, 63, 94, 0.15);
  border-color: rgba(244, 63, 94, 0.3);
  color: #f43f5e;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 24px;
}

.form-group label {
  color: #f8fafc;
  font-size: 13px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.glass-input {
  background: rgba(15, 23, 42, 0.8);
  border: 1.5px solid rgba(244, 63, 94, 0.15);
  border-radius: 12px;
  padding: 12px 16px;
  color: #f8fafc;
  outline: none;
  width: 100%;
  box-sizing: border-box;
  transition: all 0.3s ease;
  font-size: 14px;
  font-weight: 500;
  font-family: inherit;
}

.glass-input:focus {
  border-color: #f43f5e;
  box-shadow: 0 0 0 4px rgba(244, 63, 94, 0.2);
  background: rgba(15, 23, 42, 0.95);
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 28px;
  padding-top: 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
}

.btn-ghost {
  background: transparent;
  border: 1.5px solid rgba(255, 255, 255, 0.15);
  color: #cbd5e1;
  padding: 10px 20px;
  border-radius: 10px;
  cursor: pointer;
  font-weight: 600;
  font-size: 14px;
  transition: all 0.3s ease;
}

.btn-ghost:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.3);
  color: #f8fafc;
}

.btn-primary-admin {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  border: none;
  padding: 12px 28px;
  border-radius: 10px;
  font-weight: 700;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  box-shadow: 0 4px 16px rgba(16, 185, 129, 0.3), inset 0 1px 0 rgba(255, 255, 255, 0.15);
  position: relative;
  overflow: hidden;
  letter-spacing: 0.5px;
}

.btn-primary-admin::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.6s ease;
}

.btn-primary-admin:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(16, 185, 129, 0.4), inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.btn-primary-admin:hover::before {
  left: 100%;
}

.approve-modal {
  border-color: rgba(16, 185, 129, 0.15);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.6), 0 0 30px rgba(16, 185, 129, 0.1);
}

.reject-modal {
  border-color: rgba(239, 68, 68, 0.15);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.6), 0 0 30px rgba(239, 68, 68, 0.1);
}

.btn-danger {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: white;
  border: none;
  padding: 12px 28px;
  border-radius: 10px;
  font-weight: 700;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  box-shadow: 0 4px 16px rgba(239, 68, 68, 0.3), inset 0 1px 0 rgba(255, 255, 255, 0.15);
  position: relative;
  overflow: hidden;
  letter-spacing: 0.5px;
}

.btn-danger::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.6s ease;
}

.btn-danger:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(239, 68, 68, 0.4), inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.btn-danger:hover::before {
  left: 100%;
}

.btn-revoke {
  background: linear-gradient(135deg, #f59e0b 0%, #f97316 100%);
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 12px;
  font-weight: 700;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(245, 158, 11, 0.3), inset 0 1px 0 rgba(255, 255, 255, 0.15);
  letter-spacing: 0.5px;
}

.btn-revoke::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  transition: left 0.6s ease;
}

.btn-revoke:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 32px rgba(245, 158, 11, 0.4), inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.btn-revoke:hover::before {
  left: 100%;
}
</style>
