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
            <td>{{ order.usageTime }}</td>
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
              <span v-else class="text-gray">-</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Approve Modal -->
    <div v-if="showApproveModal" class="modal-overlay" @click.self="showApproveModal = false">
      <div class="modal glass-modal">
        <div class="modal-header">
          <h3>审核通过 - 分配车辆</h3>
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
      <div class="modal glass-modal">
        <div class="modal-header">
          <h3>拒绝申请</h3>
          <button class="close-btn" @click="showRejectModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>拒绝理由</label>
            <textarea 
              v-model="rejectForm.reason" 
              placeholder="请输入拒绝原因..." 
              class="glass-input"
              rows="3"
            ></textarea>
          </div>
        </div>
        <div class="modal-actions">
          <button class="btn-ghost" @click="showRejectModal = false">取消</button>
          <button class="btn-danger" @click="confirmReject">确认拒绝</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAllOrders, getAllBuses, approveOrder, rejectOrder } from '../../api'

const orders = ref([])
const availableBuses = ref([])
const showApproveModal = ref(false)
const showRejectModal = ref(false)
const currentOrder = ref(null)

const approveForm = reactive({ busId: '' })
const rejectForm = reactive({ reason: '' })

const fetchData = async () => {
    try {
        const res = await getAllOrders()
        if (res.code === 200) {
            orders.value = res.data.reverse()
        }
        
        const busRes = await getAllBuses()
        if (busRes.code === 200) {
            availableBuses.value = busRes.data.filter(b => b.isActive)
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

const openApprove = (order) => {
    currentOrder.value = order
    approveForm.busId = ''
    showApproveModal.value = true
}

const openReject = (order) => {
    currentOrder.value = order
    rejectForm.reason = ''
    showRejectModal.value = true
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
            rejectReason: rejectForm.reason
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
</script>

<style scoped>
.page-container {
  padding: 8px;
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
  background: rgba(15, 23, 42, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: var(--shadow-1);
}

.glass-table {
  width: 100%;
  border-collapse: collapse;
  color: #e2e8f0;
}

.glass-table th {
  text-align: left;
  padding: 16px 20px;
  background: rgba(255, 255, 255, 0.03);
  color: #f8fafc;
  font-weight: 700;
  font-size: 14px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
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
  background: rgba(255, 255, 255, 0.02);
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
  background: #1e293b;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  padding: 24px;
  width: 100%;
  max-width: 450px;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.5);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.modal-header h3 {
  margin: 0;
  color: #f8fafc;
}

.close-btn {
  background: transparent;
  border: none;
  color: #94a3b8;
  font-size: 24px;
  cursor: pointer;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 20px;
}

.form-group label {
  color: #cbd5e1;
  font-size: 14px;
}

.glass-input {
  background: rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  padding: 10px 12px;
  color: #f8fafc;
  outline: none;
  width: 100%;
}

.glass-input:focus {
  border-color: #f43f5e;
  box-shadow: 0 0 0 2px rgba(244, 63, 94, 0.1);
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.btn-ghost {
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: #cbd5e1;
  padding: 8px 16px;
  border-radius: 10px;
  cursor: pointer;
}

.btn-ghost:hover {
  background: rgba(255, 255, 255, 0.05);
}

.btn-primary-admin {
  background: linear-gradient(135deg, #f43f5e 0%, #e11d48 100%);
  color: white;
  border: none;
  padding: 8px 20px;
  border-radius: 10px;
  font-weight: 600;
  cursor: pointer;
}

.btn-danger {
  background: #ef4444;
  color: white;
  border: none;
  padding: 8px 20px;
  border-radius: 10px;
  font-weight: 600;
  cursor: pointer;
}
</style>
