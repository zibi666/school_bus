<template>
  <div class="page-container">
    <div class="header-row">
      <h2 class="page-title">车辆资源管理</h2>
      <button class="btn-add" @click="showAddModal = true">+ 新增车辆</button>
    </div>

    <div class="grid-container">
      <div v-for="bus in buses" :key="bus.busId" class="bus-card">
        <div class="bus-icon">车</div>
        <div class="bus-info">
          <h3>{{ bus.plateNumber }}</h3>
          <p class="type">{{ bus.carType }}</p>
          <p class="driver">司机：{{ bus.driverName }}</p>
        </div>
        <div class="bus-status">
          <span :class="['status-dot', bus.isActive ? 'free' : 'busy']"></span>
          {{ bus.isActive ? '空闲' : '使用中' }}
        </div>
        <button class="btn-delete" @click="deleteBus(bus.busId)">×</button>
      </div>
    </div>

    <!-- Add Modal -->
    <div v-if="showAddModal" class="modal-overlay">
      <div class="modal">
        <h3>新增车辆</h3>
        <form @submit.prevent="addBus">
          <div class="form-group">
            <label>车牌号</label>
            <input v-model="form.plateNumber" required placeholder="例如：京A·88888">
          </div>
          <div class="form-group">
            <label>车型</label>
            <select v-model="form.carType" required>
              <option>大巴 (45座)</option>
              <option>中巴 (20座)</option>
              <option>商务车 (7座)</option>
            </select>
          </div>
          <div class="form-group">
            <label>司机姓名</label>
            <input v-model="form.driverName" required placeholder="请输入司机姓名">
          </div>
          <div class="modal-actions">
            <button type="button" @click="showAddModal = false">取消</button>
            <button type="submit" class="btn-primary">保存</button>
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
.page-container { padding: 2rem; }
.header-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem; }
.page-title { color: #1f2937; margin: 0; }

.btn-add {
  background: #8b5cf6; color: white; border: none; padding: 10px 20px; border-radius: 8px; cursor: pointer; font-weight: bold;
}

.grid-container {
  display: grid; grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); gap: 1.5rem;
}

.bus-card {
  background: white; padding: 1.5rem; border-radius: 16px; box-shadow: 0 4px 6px rgba(0,0,0,0.05);
  display: flex; align-items: center; gap: 1rem; transition: transform 0.2s;
}
.bus-card:hover { transform: translateY(-5px); }

.bus-icon { font-size: 2.5rem; background: #f3f4f6; padding: 10px; border-radius: 12px; }
.bus-info h3 { margin: 0; color: #1f2937; }
.bus-info .type { color: #6b7280; font-size: 0.9rem; margin: 4px 0; }
.bus-info .driver { color: #9ca3af; font-size: 0.85rem; margin: 0; }

.bus-status { margin-left: auto; display: flex; flex-direction: column; align-items: center; font-size: 0.8rem; color: #6b7280; }
.status-dot { width: 10px; height: 10px; border-radius: 50%; margin-bottom: 4px; }
.status-dot.free { background: #10b981; box-shadow: 0 0 8px #10b981; }
.status-dot.busy { background: #ef4444; box-shadow: 0 0 8px #ef4444; }

.btn-delete {
    position: absolute; top: 10px; right: 10px; background: transparent; border: none; color: #9ca3af; cursor: pointer; font-size: 1.2rem;
}
.btn-delete:hover { color: #ef4444; }

.modal-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.5);
  display: flex; align-items: center; justify-content: center;
}
.modal { background: white; padding: 2rem; border-radius: 12px; width: 400px; }
.form-group { margin-bottom: 1rem; }
.form-group label { display: block; margin-bottom: 0.5rem; color: #4b5563; }
.form-group input, .form-group select {
  width: 100%; padding: 8px; border: 1px solid #d1d5db; border-radius: 6px;
}
.modal-actions { display: flex; justify-content: flex-end; gap: 1rem; margin-top: 1.5rem; }
.btn-primary { background: #8b5cf6; color: white; border: none; padding: 8px 16px; border-radius: 6px; cursor: pointer; }
</style>
