<template>
  <div class="page-container">
    <div class="header-row">
      <div>
        <h2 class="page-title">车辆资源管理</h2>
        <p class="subhead">管理校车车队与司机信息。</p>
      </div>
      <button class="btn-primary-admin" @click="showAddModal = true">
        <span class="icon-plus">+</span> 新增车辆
      </button>
    </div>

    <div class="grid-container">
      <div v-for="bus in buses" :key="bus.busId" class="bus-card">
        <div class="card-top">
          <div class="bus-icon-box">
            🚌
          </div>
          <div class="status-badge" :class="bus.isActive ? 'status-free' : 'status-busy'">
            <span class="dot"></span>
            {{ bus.isActive ? '空闲' : '使用中' }}
          </div>
        </div>
        
        <div class="bus-info">
          <h3 class="plate-number">{{ bus.plateNumber }}</h3>
          <p class="info-row">
            <span class="label">车型</span>
            <span class="value">{{ bus.carType }}</span>
          </p>
          <p class="info-row">
            <span class="label">司机</span>
            <span class="value">{{ bus.driverName }}</span>
          </p>
        </div>

        <button class="btn-delete-ghost" @click="deleteBus(bus.busId)">
          删除车辆
        </button>
      </div>
    </div>

    <!-- Add Modal -->
    <div v-if="showAddModal" class="modal-overlay" @click.self="showAddModal = false">
      <div class="modal glass-modal">
        <div class="modal-header">
          <h3>新增车辆</h3>
          <button class="close-btn" @click="showAddModal = false">×</button>
        </div>
        <form @submit.prevent="addBus" class="modal-form">
          <div class="form-group">
            <label>车牌号</label>
            <input v-model="form.plateNumber" required placeholder="例如：京A·88888" class="glass-input">
          </div>
          <div class="form-group">
            <label>车型</label>
            <select v-model="form.carType" required class="glass-input">
              <option value="" disabled>请选择车型</option>
              <option>大巴 (45座)</option>
              <option>中巴 (20座)</option>
              <option>商务车 (7座)</option>
            </select>
          </div>
          <div class="form-group">
            <label>司机姓名</label>
            <input v-model="form.driverName" required placeholder="请输入司机姓名" class="glass-input">
          </div>
          <div class="modal-actions">
            <button type="button" class="btn-ghost" @click="showAddModal = false">取消</button>
            <button type="submit" class="btn-primary-admin">保存车辆</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAllBuses, addBus as addBusApi, deleteBus as deleteBusApi } from '../../api'

const buses = ref([])
const showAddModal = ref(false)
const form = reactive({ plateNumber: '', carType: '', driverName: '' })

const fetchBuses = async () => {
    try {
        const res = await getAllBuses()
        if (res.code === 200) {
            buses.value = res.data
        }
    } catch (e) {
        console.error(e)
    }
}

onMounted(() => {
  fetchBuses()
})

const addBus = async () => {
    try {
        const res = await addBusApi({
            ...form,
            isActive: true
        })
        if (res.code === 200) {
            showAddModal.value = false
            form.plateNumber = ''
            form.carType = ''
            form.driverName = ''
            fetchBuses()
        } else {
            alert(res.message)
        }
    } catch (e) {
        alert('添加失败')
    }
}

const deleteBus = async (id) => {
    if(confirm('确定要删除吗？')) {
        try {
            const res = await deleteBusApi(id)
            if (res.code === 200) {
                fetchBuses()
            } else {
                alert(res.message)
            }
        } catch (e) {
            alert('删除失败')
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

.btn-primary-admin {
  background: linear-gradient(135deg, #f43f5e 0%, #e11d48 100%);
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(244, 63, 94, 0.3);
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn-primary-admin:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(244, 63, 94, 0.4);
}

.grid-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.bus-card {
  background: rgba(15, 23, 42, 0.8);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 18px;
  padding: 20px;
  transition: all 0.2s;
  position: relative;
  overflow: hidden;
}

.bus-card:hover {
  transform: translateY(-2px);
  border-color: rgba(244, 63, 94, 0.3);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.card-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.bus-icon-box {
  width: 48px;
  height: 48px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  font-weight: 600;
  padding: 4px 10px;
  border-radius: 20px;
}

.status-free {
  background: rgba(16, 185, 129, 0.1);
  color: #34d399;
}

.status-busy {
  background: rgba(244, 63, 94, 0.1);
  color: #f43f5e;
}

.dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
}

.plate-number {
  margin: 0 0 12px;
  color: #f8fafc;
  font-size: 20px;
  letter-spacing: 0.5px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.label {
  color: var(--text-secondary);
}

.value {
  color: #e2e8f0;
}

.btn-delete-ghost {
  width: 100%;
  margin-top: 16px;
  padding: 8px;
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: #94a3b8;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-delete-ghost:hover {
  border-color: #ef4444;
  color: #ef4444;
  background: rgba(239, 68, 68, 0.05);
}

/* Modal Styles */
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
  max-width: 400px;
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

.modal-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
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
}

.glass-input:focus {
  border-color: #f43f5e;
  box-shadow: 0 0 0 2px rgba(244, 63, 94, 0.1);
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 8px;
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
</style>
