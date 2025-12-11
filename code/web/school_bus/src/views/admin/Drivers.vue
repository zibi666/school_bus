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

    <!-- Time Selection Panel -->
    <div class="time-selector-panel">
      <div class="time-selector-content">
        <div class="time-input-group">
          <label>查询日期与时间</label>
          <div class="time-inputs">
            <input 
              v-model="queryDate" 
              type="date" 
              class="glass-input"
              @change="onTimeChange"
            >
            <input 
              v-model="queryStartTime" 
              type="time" 
              class="glass-input"
              @change="onTimeChange"
            >
            <span class="separator">-</span>
            <input 
              v-model="queryEndTime" 
              type="time" 
              class="glass-input"
              @change="onTimeChange"
            >
          </div>
        </div>
        <div class="time-info">
          <p v-if="selectedTimeRange" class="time-range-text">
            📅 {{ formatTimeRange() }}
          </p>
        </div>
      </div>
    </div>

    <div class="grid-container">
      <div v-for="bus in buses" :key="bus.busId" class="bus-card">
        <div class="card-top">
          <div class="bus-icon-box">
            🚌
          </div>
          <div class="status-badge" :class="getBusStatusClass(bus)">
            <span class="dot"></span>
            {{ getBusStatusText(bus) }}
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
          <p class="info-row">
            <span class="label">单价</span>
            <span class="value">¥{{ bus.price }}/小时</span>
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
            <label for="plateNumber">车牌号</label>
            <input id="plateNumber" v-model="form.plateNumber" required placeholder="例如：京A·88888" class="glass-input">
          </div>
          <div class="form-group">
            <label for="carType">车型</label>
            <select id="carType" v-model="form.carType" required class="glass-input">
              <option value="" disabled>请选择车型</option>
              <option value="大巴">大巴 (45座)</option>
              <option value="中巴">中巴 (20座)</option>
              <option value="商务车">商务车 (7座)</option>
            </select>
          </div>
          <div class="form-group">
            <label for="driverName">司机姓名</label>
            <input id="driverName" v-model="form.driverName" required placeholder="请输入司机姓名" class="glass-input">
          </div>
          <div class="form-group">
            <label for="number">司机号码</label>
            <input id="number" v-model="form.number" required placeholder="例如：13800138000" class="glass-input">
          </div>
          <div class="form-group">
            <label>每小时单价（元）</label>
            <div class="price-auto-info">
              <span class="price-value">¥{{ getAutoPrice(form.carType) }}/小时</span>
              <span class="price-hint">（根据车型自动设置：大巴800、中巴600、商务车1000）</span>
            </div>
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
import { ref, reactive, onMounted, computed } from 'vue'
import { getAllBuses, addBus as addBusApi, deleteBus as deleteBusApi, checkBusAvailability } from '../../api'

const buses = ref([])
const showAddModal = ref(false)
const form = reactive({ plateNumber: '', carType: '', driverName: '', number: '' })

// 根据车型自动获取价格
const getAutoPrice = (carType) => {
  const priceMap = {
    '大巴': 800,
    '中巴': 600,
    '商务车': 1000
  }
  return priceMap[carType] || 0
}

// 时间选择相关
const queryDate = ref('')
const queryStartTime = ref('00:00')
const queryEndTime = ref('23:59')
const busAvailabilityMap = ref({}) // 存储每个车辆的可用性
const hasCheckedAvailability = ref(false) // 标记是否已进行过可用性检查

const selectedTimeRange = computed(() => {
  return queryDate.value && queryStartTime.value && queryEndTime.value
})

const formatTimeRange = () => {
  if (!selectedTimeRange.value) return ''
  const dateStr = new Date(queryDate.value).toLocaleDateString('zh-CN')
  return `${dateStr} ${queryStartTime.value}-${queryEndTime.value}`
}

const onTimeChange = async () => {
  if (!selectedTimeRange.value) return
  
  // 查询所有车辆在该时间段的可用性
  busAvailabilityMap.value = {}
  hasCheckedAvailability.value = false // 重置状态
  
  for (let bus of buses.value) {
    try {
      const startDateTime = `${queryDate.value}T${queryStartTime.value}:00`
      const endDateTime = `${queryDate.value}T${queryEndTime.value}:00`
      
      const res = await checkBusAvailability({
        busId: bus.busId.toString(),
        startTime: startDateTime,
        endTime: endDateTime
      })
      
      if (res.code === 200) {
        busAvailabilityMap.value[bus.busId] = res.data.isAvailable
      }
    } catch (e) {
      console.error('检查可用性失败:', e)
    }
  }
  
  // 所有查询完成后标记为已检查
  hasCheckedAvailability.value = true
}

const getBusStatusClass = (bus) => {
  if (!selectedTimeRange.value || !hasCheckedAvailability.value) {
    // 未选择时间段或未进行过检查时，不显示状态
    return 'status-unknown'
  }
  
  // 检查日期是否已过
  const selectedDate = new Date(queryDate.value)
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  selectedDate.setHours(0, 0, 0, 0)
  
  if (selectedDate < today) {
    return 'status-past'
  }
  
  // 已选择时间段且已检查时，显示该时间段的可用性
  const isAvailable = busAvailabilityMap.value[bus.busId]
  return isAvailable ? 'status-free' : 'status-busy'
}

const getBusStatusText = (bus) => {
  if (!selectedTimeRange.value || !hasCheckedAvailability.value) {
    return '检查中...'
  }
  
  // 检查日期是否已过
  const selectedDate = new Date(queryDate.value)
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  selectedDate.setHours(0, 0, 0, 0)
  
  if (selectedDate < today) {
    return '📅 已过日期'
  }
  
  const isAvailable = busAvailabilityMap.value[bus.busId]
  return isAvailable ? '✓ 可用' : '✗ 已占用'
}

const fetchBuses = async () => {
    try {
        const res = await getAllBuses()
        if (res.code === 200) {
            buses.value = res.data
            // 初始化时间为今天
            const today = new Date().toISOString().split('T')[0]
            queryDate.value = today
            // 页面加载完成后自动进行一次检查
            await onTimeChange()
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
          price: getAutoPrice(form.carType),
          isActive: true
        })
        if (res.code === 200) {
            showAddModal.value = false
            form.plateNumber = ''
            form.carType = ''
            form.driverName = ''
            form.number = ''
            fetchBuses()
        } else {
            // 显示后端返回的具体错误信息
            alert(res.message || '添加失败')
        }
    } catch (e) {
        // catch块中捕获的是axios拦截器返回的错误对象 {code, message, data}
        console.error('Add bus error:', e)
        if (e && e.message) {
            alert(e.message)
        } else {
            alert('添加失败')
        }
    }
}

const deleteBus = async (id) => {
    if(confirm('确定要删除吗？')) {
        try {
            const res = await deleteBusApi(id)
            if (res.code === 200) {
                fetchBuses()
            } else {
                alert(res.message || '删除失败')
            }
        } catch (e) {
            console.error('Delete bus error:', e)
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
  padding: 24px;
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
  color: #94a3b8;
  margin: 0;
  font-size: 15px;
}

.btn-primary-admin {
  background: linear-gradient(135deg, #f43f5e 0%, #e11d48 100%);
  color: white;
  border: none;
  padding: 12px 28px;
  border-radius: 12px;
  font-weight: 700;
  font-size: 15px;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(244, 63, 94, 0.3), inset 0 1px 0 rgba(255, 255, 255, 0.1);
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  display: flex;
  align-items: center;
  gap: 8px;
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
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  transition: left 0.6s ease;
}

.btn-primary-admin:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 32px rgba(244, 63, 94, 0.5), inset 0 1px 0 rgba(255, 255, 255, 0.15);
}

.btn-primary-admin:hover::before {
  left: 100%;
}

.grid-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.bus-card {
  background: linear-gradient(135deg, rgba(15, 23, 42, 0.95) 0%, rgba(30, 41, 59, 0.9) 100%);
  border: 1px solid rgba(244, 63, 94, 0.25);
  border-radius: 20px;
  padding: 24px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.25);
  backdrop-filter: blur(8px);
}

.bus-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(244, 63, 94, 0.3), transparent);
  pointer-events: none;
}

.bus-card:hover {
  transform: translateY(-6px);
  border-color: rgba(244, 63, 94, 0.4);
  box-shadow: 0 16px 40px rgba(244, 63, 94, 0.2);
}

.card-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.bus-icon-box {
  width: 52px;
  height: 52px;
  background: linear-gradient(135deg, rgba(244, 63, 94, 0.1) 0%, rgba(225, 29, 72, 0.05) 100%);
  border: 1px solid rgba(244, 63, 94, 0.2);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
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

.status-past {
  background: rgba(107, 114, 128, 0.1);
  color: #9ca3af;
}

.status-unknown {
  background: rgba(99, 102, 241, 0.1);
  color: #818cf8;
}

.dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
}

.plate-number {
  margin: 0 0 16px;
  color: #f8fafc;
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 1px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 14px;
  padding: 8px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.info-row:last-of-type {
  border-bottom: none;
}

.label {
  color: #f8fafc;
  font-weight: 700;
  text-transform: uppercase;
  font-size: 12px;
  letter-spacing: 0.5px;
}

.value {
  color: #f8fafc;
  font-weight: 500;
}

.btn-delete-ghost {
  width: 100%;
  margin-top: 16px;
  padding: 10px;
  background: transparent;
  border: 1.5px solid rgba(239, 68, 68, 0.4);
  color: #f87171;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 600;
  font-size: 14px;
  letter-spacing: 0.5px;
}

.btn-delete-ghost:hover {
  border-color: rgba(239, 68, 68, 0.8);
  color: #fca5a5;
  background: rgba(239, 68, 68, 0.15);
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.2);
  transform: translateY(-2px);
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
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
  border: 1.5px solid rgba(244, 63, 94, 0.15);
  border-radius: 20px;
  padding: 28px;
  width: 100%;
  max-width: 420px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.6), 0 0 30px rgba(244, 63, 94, 0.1);
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
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 0.5px;
  background: linear-gradient(135deg, #f43f5e 0%, #e11d48 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.close-btn {
  background: transparent;
  border: none;
  color: #94a3b8;
  font-size: 24px;
  cursor: pointer;
  transition: all 0.3s ease;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
}

.close-btn:hover {
  color: #f43f5e;
  background: rgba(244, 63, 94, 0.1);
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
  color: #f8fafc;
  font-size: 13px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.glass-input {
  background: rgba(255, 255, 255, 0.95);
  border: 1.5px solid rgba(244, 63, 94, 0.3);
  border-radius: 12px;
  padding: 12px 14px;
  color: #000000;
  outline: none;
  transition: all 0.3s ease;
  font-size: 14px;
  font-weight: 500;
}

.glass-input:focus {
  border-color: #f43f5e;
  box-shadow: 0 0 0 4px rgba(244, 63, 94, 0.2);
  background: rgba(255, 255, 255, 0.98);
}

select.glass-input {
  appearance: none;
  -webkit-appearance: none;
  -moz-appearance: none;
  background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23000000' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6 9 12 15 18 9'%3e%3c/polyline%3e%3c/svg%3e");
  background-repeat: no-repeat;
  background-position: right 12px center;
  background-size: 18px;
  padding-right: 40px;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 8px;
}

.btn-ghost {
  background: transparent;
  border: 1.5px solid rgba(255, 255, 255, 0.2);
  color: #cbd5e1;
  padding: 10px 18px;
  border-radius: 10px;
  cursor: pointer;
  font-weight: 600;
  font-size: 14px;
  transition: all 0.3s ease;
}

/* 价格自动生成提示样式 */
.price-auto-info {
  background: rgba(244, 63, 94, 0.1);
  border: 1.5px solid rgba(244, 63, 94, 0.3);
  border-radius: 12px;
  padding: 12px 14px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.price-auto-info .price-value {
  color: #f43f5e;
  font-size: 18px;
  font-weight: 700;
}

.price-auto-info .price-hint {
  color: #94a3b8;
  font-size: 12px;
}

.btn-ghost:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.4);
  color: #f8fafc;
}

/* Time Selector Panel */
.time-selector-panel {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.6) 0%, rgba(248, 250, 252, 0.4) 100%);
  border: 1.5px solid rgba(244, 63, 94, 0.2);
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 24px;
  backdrop-filter: blur(12px);
  box-shadow: 0 4px 16px rgba(244, 63, 94, 0.08);
}

.time-selector-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 24px;
  flex-wrap: wrap;
}

.time-input-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.time-input-group label {
  color: #0f172a;
  font-size: 13px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.8px;
  background: linear-gradient(135deg, #f43f5e 0%, #e11d48 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.time-inputs {
  display: flex;
  align-items: center;
  gap: 8px;
}

.time-inputs input {
  background: rgba(255, 255, 255, 0.92);
  border: 1.5px solid rgba(244, 63, 94, 0.25);
  border-radius: 10px;
  padding: 10px 12px;
  color: #1e293b;
  outline: none;
  transition: all 0.3s ease;
  font-size: 13px;
  font-weight: 500;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.time-inputs input:focus {
  border-color: #f43f5e;
  box-shadow: 0 0 0 4px rgba(244, 63, 94, 0.15);
  background: rgba(255, 255, 255, 0.98);
  transform: translateY(-1px);
}

.separator {
  color: #64748b;
  font-weight: 700;
  font-size: 14px;
}

.time-info {
  display: flex;
  align-items: center;
}

.time-range-text {
  margin: 0;
  color: #1e293b;
  font-size: 14px;
  font-weight: 600;
  padding: 10px 16px;
  background: linear-gradient(135deg, rgba(244, 63, 94, 0.12) 0%, rgba(225, 29, 72, 0.08) 100%);
  border-radius: 10px;
  border: 1px solid rgba(244, 63, 94, 0.25);
  box-shadow: 0 2px 8px rgba(244, 63, 94, 0.08);
}
</style>
