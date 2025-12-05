<template>
  <div class="page-container">
    <h2 class="page-title">订单审核</h2>
    
    <div class="table-container">
      <table>
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
            <td><span class="tag">{{ order.requestedCarType }}</span></td>
            <td>
              <span :class="['status-tag', statusClass(order.status)]">{{ order.status }}</span>
            </td>
            <td>
              <div v-if="order.status === '审核中'" class="actions">
                <button class="btn-approve" @click="openApprove(order)">通过</button>
                <button class="btn-reject" @click="openReject(order)">拒绝</button>
              </div>
              <span v-else class="text-gray">-</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Approve Modal -->
    <div v-if="showApproveModal" class="modal-overlay">
      <div class="modal">
        <h3>审核通过 - 分配车辆</h3>
        <div class="form-group">
          <label>选择车辆</label>
          <select v-model="approveForm.busId">
            <option v-for="bus in availableBuses" :key="bus.busId" :value="bus.busId">
              {{ bus.plateNumber }} ({{ bus.driverName }})
            </option>
          </select>
        </div>
        <div class="modal-actions">
          <button @click="showApproveModal = false">取消</button>
          <button class="btn-primary" @click="confirmApprove">确认</button>
        </div>
      </div>
    </div>

    <!-- Reject Modal -->
    <div v-if="showRejectModal" class="modal-overlay">
      <div class="modal">
        <h3>拒绝申请</h3>
        <div class="form-group">
          <label>拒绝理由</label>
          <textarea v-model="rejectForm.reason" placeholder="请输入拒绝原因..."></textarea>
        </div>
        <div class="modal-actions">
          <button @click="showRejectModal = false">取消</button>
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
            orders.value = res.data
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

const statusClass = (s) => {
  if(s === '已通过') return 'success'
  if(s === '已拒绝') return 'danger'
  return 'warning'
}

const openApprove = (order) => {
  currentOrder.value = order
  showApproveModal.value = true
}

const openReject = (order) => {
  currentOrder.value = order
  showRejectModal.value = true
}

const confirmApprove = async () => {
    if (!approveForm.busId) return alert('请选择车辆')
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
</script>

<style scoped>
.page-container { padding: 2rem; }
.page-title { margin-bottom: 2rem; color: #1f2937; }

.table-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0,0,0,0.05);
  overflow: hidden;
}

table { width: 100%; border-collapse: collapse; }
th, td { padding: 1rem; text-align: left; border-bottom: 1px solid #f3f4f6; }
th { background: #f9fafb; font-weight: 600; color: #4b5563; }
tr:hover { background: #f9fafb; }

.tag { background: #e0e7ff; color: #4338ca; padding: 2px 8px; border-radius: 4px; font-size: 0.85rem; }
.status-tag { padding: 2px 8px; border-radius: 12px; font-size: 0.85rem; font-weight: bold; }
.status-tag.warning { background: #fef3c7; color: #d97706; }
.status-tag.success { background: #d1fae5; color: #059669; }
.status-tag.danger { background: #fee2e2; color: #dc2626; }

.actions { display: flex; gap: 0.5rem; }
.btn-approve { background: #10b981; color: white; border: none; padding: 4px 12px; border-radius: 4px; cursor: pointer; }
.btn-reject { background: #ef4444; color: white; border: none; padding: 4px 12px; border-radius: 4px; cursor: pointer; }

.modal-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.5);
  display: flex; align-items: center; justify-content: center;
}
.modal { background: white; padding: 2rem; border-radius: 12px; width: 400px; }
.form-group { margin-bottom: 1rem; }
.form-group label { display: block; margin-bottom: 0.5rem; color: #4b5563; }
.form-group input, .form-group select, .form-group textarea {
  width: 100%; padding: 8px; border: 1px solid #d1d5db; border-radius: 6px;
}
.modal-actions { display: flex; justify-content: flex-end; gap: 1rem; margin-top: 1.5rem; }
.btn-primary { background: #8b5cf6; color: white; border: none; padding: 8px 16px; border-radius: 6px; cursor: pointer; }
.btn-danger { background: #ef4444; color: white; border: none; padding: 8px 16px; border-radius: 6px; cursor: pointer; }
</style>
