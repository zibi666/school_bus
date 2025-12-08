<template>
  <div class="page-container">
    <div class="header-row">
      <div>
        <p class="pill">一键预约 · 校园出行</p>
        <h2 class="page-title">申请包车</h2>
        <p class="subhead">填写行程需求，管理员将快速完成审核与车辆调度。</p>
      </div>
      <div class="stat-ribbon">
        <div class="stat">
          <span class="stat-num">3</span>
          <span class="stat-label">车型可选</span>
        </div>
        <div class="stat">
          <span class="stat-num">5 min</span>
          <span class="stat-label">平均审核</span>
        </div>
        <div class="stat">
          <span class="stat-num">专车</span>
          <span class="stat-label">专属司机</span>
        </div>
      </div>
    </div>

    <div class="grid">
      <div class="card card-main">
        <div class="card-head">
          <h3>填写行程</h3>
          <span class="badge">实时提交</span>
        </div>
        <form @submit.prevent="submitOrder" class="apply-form">
          <div class="form-group">
            <label for="destination">目的地</label>
            <input id="destination" type="text" v-model="form.destination" placeholder="请输入目的地" required />
          </div>

          <div class="form-group">
            <label for="usage">使用时间段</label>
            <div class="time-picker-container">
              <div class="time-input" @click="showTimePicker = true">
                <span v-if="form.usageTime" class="time-display">{{ form.usageTime }}</span>
                <span v-else class="time-placeholder">请选择时间段</span>
                <svg class="time-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                  <circle cx="12" cy="12" r="10"></circle>
                  <polyline points="12 6 12 12 16 14"></polyline>
                </svg>
              </div>
              
              <div v-if="showTimePicker" class="time-picker-modal" @click.self="showTimePicker = false">
                <div class="time-picker-content">
                  <div class="time-picker-header">
                    <h3>选择时间段</h3>
                    <button type="button" class="close-btn" @click="showTimePicker = false">×</button>
                  </div>
                  
                  <div class="time-picker-body">
                    <div class="date-section">
                      <label>日期</label>
                      <input type="date" v-model="timePickerData.date" />
                    </div>
                    
                    <div class="time-section">
                      <div class="time-item">
                        <label>开始时间</label>
                        <input type="time" v-model="timePickerData.startTime" />
                      </div>
                      <div class="time-item">
                        <label>结束时间</label>
                        <input type="time" v-model="timePickerData.endTime" />
                      </div>
                    </div>
                  </div>
                  
                  <div class="time-picker-footer">
                    <button type="button" class="btn-cancel" @click="showTimePicker = false">取消</button>
                    <button type="button" class="btn-confirm" @click="confirmTimeSelection">确认</button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="form-group">
            <label for="carType">需求车型</label>
            <select id="carType" v-model="form.requestedCarType" required:class="{ 'is-empty': form.requestedCarType === '' }">
              <option value="" disabled>请选择车型</option>
              <option>大巴 (45座)</option>
              <option>中巴 (20座)</option>
              <option>商务车 (7座)</option>
            </select>
          </div>

          <div class="actions">
            <button type="submit" class="btn-primary" :disabled="loading">
              {{ loading ? '提交中...' : '提交申请' }}
            </button>
          </div>
        </form>
      </div>

      <div class="card card-side">
        <h4>小贴士</h4>
        <ul class="tips">
          <li>时间段尽量精确，便于调度车辆与司机。</li>
          <li>车辆审核通过后，可在“我的订单”查看车牌与司机信息。</li>
          <li>如需临时修改，请联系管理员或重新提交新申请。</li>
        </ul>
        <div class="mini-cards">
          <div class="mini">
            <span class="tag">安全</span>
            <p>全程保险与校方备案</p>
          </div>
          <div class="mini">
            <span class="tag">准点</span>
            <p>行程延误将优先补位</p>
          </div>
          <div class="mini">
            <span class="tag">舒适</span>
            <p>精选商务、豪华巴士</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { createOrder } from '../../api' 

const router = useRouter()
const loading = ref(false)
const showTimePicker = ref(false)
const timePickerData = reactive({
  date: '',
  startTime: '',
  endTime: ''
})
const form = reactive({
  destination: '',
  usageTime: '',
  requestedCarType: ''
})

const formatDateForDisplay = (date, startTime, endTime) => {
  if (!date || !startTime || !endTime) return ''
  
  const dateObj = new Date(date)
  const month = dateObj.getMonth() + 1
  const day = dateObj.getDate()
  
  return `${month}月${day}日 ${startTime}-${endTime}`
}

const confirmTimeSelection = () => {
  if (!timePickerData.date || !timePickerData.startTime || !timePickerData.endTime) {
    alert('请填写完整的时间信息')
    return
  }
  
  if (timePickerData.startTime >= timePickerData.endTime) {
    alert('开始时间必须早于结束时间')
    return
  }
  
  // 格式化用于显示
  form.usageTime = formatDateForDisplay(
    timePickerData.date,
    timePickerData.startTime,
    timePickerData.endTime
  )
  
  showTimePicker.value = false
}

const submitOrder = async () => {
  loading.value = true
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const studentId = userInfo.studentId
    
    if (!studentId) {
        alert('请先登录')
        router.push('/login')
        return
    }

    // 构建ISO格式的时间戳发送给后端
    const startDateTime = `${timePickerData.date}T${timePickerData.startTime}:00`
    const endDateTime = `${timePickerData.date}T${timePickerData.endTime}:00`

    const res = await createOrder({
        destination: form.destination,
        usageTime: form.usageTime,
        startTime: startDateTime,
        endTime: endDateTime,
        requestedCarType: form.requestedCarType,
        studentId
    })
    
    if (res.code === 200) {
        alert('申请提交成功！请等待管理员审核。')
        router.push('/student/trips')
    } else {
        alert(res.message || '提交失败')
    }
  } catch (e) {
    console.error(e)
    alert('提交异常')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.page-container {
  padding: 24px;
  width: 100%;
  overflow: hidden;
  box-sizing: border-box;
}
/* --- 统一设置字体大小 --- */
.form-group input, 
.form-group select, 
.time-input {
  font-size: 14px; /* 统一字体大小 */
}



.header-row {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  margin-bottom: 24px;
}

.page-title {
  margin: 12px 0 10px;
  color: #f8fafc;
  font-size: 36px;
  font-weight: 900;
  background: linear-gradient(135deg, #22d3ee 0%, #8b5cf6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.5px;
}

.subhead {
  color: #94a3b8;
  font-size: 15px;
  line-height: 1.6;
  margin: 0;
  font-weight: 400;
}

.pill {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: linear-gradient(135deg, rgba(34, 211, 238, 0.2) 0%, rgba(139, 92, 246, 0.2) 100%);
  border: 1px solid rgba(34, 211, 238, 0.4);
  border-radius: 24px;
  color: #22d3ee;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.5px;
  margin: 0 0 8px;
  backdrop-filter: blur(8px);
}

.stat-ribbon {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
}

.stat {
  padding: 16px 12px;
  border-radius: 16px;
  border: none;
  background: linear-gradient(135deg, rgba(34, 211, 238, 0.15) 0%, rgba(139, 92, 246, 0.15) 100%);
  text-align: center;
  box-shadow: 0 4px 16px rgba(34, 211, 238, 0.2);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(34, 211, 238, 0.3);
  transition: all 0.3s ease;
}

.stat:hover {
  background: linear-gradient(135deg, rgba(34, 211, 238, 0.25) 0%, rgba(139, 92, 246, 0.25) 100%);
  box-shadow: 0 8px 24px rgba(34, 211, 238, 0.3);
  transform: translateY(-2px);
}

.stat-num {
  display: block;
  color: #22d3ee;
  font-weight: 800;
  font-size: 24px;
  margin-bottom: 4px;
}

.stat-label {
  color: #000000;
  font-size: 13px;
  font-weight: 600;
}

.grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 18px;
  width: 100%;
  max-width: 100%;
}

.card {
  padding: 22px;
  border-radius: 18px;
  background: rgba(12, 18, 34, 0.92);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: var(--shadow-1);
  overflow: hidden;
  box-sizing: border-box;
}

.card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.card-head h3 {
  margin: 0;
  color: #f8fafc;
}

.badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  background: linear-gradient(135deg, rgba(34, 211, 238, 0.2) 0%, rgba(59, 130, 246, 0.2) 100%);
  border: 1px solid rgba(34, 211, 238, 0.4);
  border-radius: 20px;
  color: #22d3ee;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.5px;
  backdrop-filter: blur(8px);
  transition: all 0.3s ease;
}

.badge::before {
  content: '';
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #22d3ee;
  animation: pulse-badge 2s infinite;
}

@keyframes pulse-badge {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.badge:hover {
  background: linear-gradient(135deg, rgba(34, 211, 238, 0.3) 0%, rgba(59, 130, 246, 0.3) 100%);
  box-shadow: 0 4px 12px rgba(34, 211, 238, 0.2);
}

.apply-form {
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
  color: #e2e8f0;
}

.form-group input,
.form-group select {
  width: 100%;
  box-sizing: border-box; 
  padding: 12px 14px;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(15, 23, 42, 0.86);
  color: #f8fafc;
  outline: none;
  transition: border 0.2s ease, box-shadow 0.2s ease;
}

.form-group select {
  background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2394a3b8' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6 9 12 15 18 9'%3e%3c/polyline%3e%3c/svg%3e");
  background-repeat: no-repeat;
  background-position: right 16px center;
  background-size: 18px;
  padding-right: 50px;
  appearance: none;
  -webkit-appearance: none;
  -moz-appearance: none;
}

.form-group input:focus,
.form-group select:focus {
  border-color: rgba(34, 211, 238, 0.5);
  box-shadow: 0 0 0 4px rgba(34, 211, 238, 0.12);
}

.actions {
  margin-top: 80px;
}

.btn-primary {
  width: 100%;
  padding: 14px 24px;
  background: linear-gradient(135deg, #f97316 0%, #ec4899 100%);
  color: #ffffff;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 16px rgba(249, 115, 22, 0.3);
  letter-spacing: 0.5px;
  position: relative;
  overflow: hidden;
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
  box-shadow: 0 8px 24px rgba(249, 115, 22, 0.4);
}

.btn-primary:hover::before {
  left: 100%;
}

.btn-primary:active {
  transform: translateY(0);
}

.card-side h4 {
  color: #f8fafc;
  margin: 0 0 10px;
}

.tips {
  color: var(--text-secondary);
  padding-left: 18px;
  color: #fff; 
  margin: 0 0 14px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.mini-cards {
  display: grid;
  grid-template-columns: 1fr;
  gap: 6px;
}

.mini {
  padding: 8px 10px;
  border-radius: 12px;
  border: 1px solid var(--border);
  background: rgba(255, 255, 255, 0.08);
  color: #ffffff;
}

.tag {
  display: inline-flex;
  padding: 2px 6px;
  border-radius: 10px;
  border: 1px solid rgba(34, 211, 238, 0.3);
  color: #22d3ee;
  font-size: 10px;
  margin-bottom: 3px;
}

@media (max-width: 1024px) {
  .grid {
    grid-template-columns: 1fr;
  }
}

.time-picker-container {
  position: relative;
}

.time-input {
  width: 100%;
  padding: 12px 14px;
  box-sizing: border-box; 
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(15, 23, 42, 0.86);
  color: #f8fafc;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: all 0.2s ease;
}

.time-input:hover {
  border-color: rgba(34, 211, 238, 0.5);
  background: rgba(15, 23, 42, 0.95);
}

.time-input:focus {
  border-color: rgba(34, 211, 238, 0.5);
  box-shadow: 0 0 0 4px rgba(34, 211, 238, 0.12);
}

.time-display {
  color: #f8fafc;
}

.time-placeholder {
  color: #64748b;
}

.time-icon {
  width: 20px;
  height: 20px;
  color: #94a3b8;
  flex-shrink: 0;
}

.time-picker-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
  box-sizing: border-box;
  overflow: auto;
}

.time-picker-content {
  background: rgba(15, 23, 42, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 24px;
  width: 100%;
  max-width: 380px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(16px);
  max-height: 90vh;
  overflow-y: auto;
}

.time-picker-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.time-picker-header h3 {
  margin: 0;
  color: #f8fafc;
  font-size: 18px;
}

.close-btn {
  background: none;
  border: none;
  color: #94a3b8;
  font-size: 28px;
  cursor: pointer;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.2s ease;
}

.close-btn:hover {
  color: #f8fafc;
}

.time-picker-body {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 20px;
}

.date-section,
.time-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.time-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.time-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.date-section label,
.time-item label {
  color: #e2e8f0;
  font-size: 14px;
  font-weight: 500;
}

.date-section input,
.time-item input {
  width: 100%;
  box-sizing: border-box;
  padding: 10px 12px;
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(0, 0, 0, 0.2);
  color: #f8fafc;
  font-size: 14px;
}

.date-section input:focus,
.time-item input:focus {
  outline: none;
  border-color: rgba(34, 211, 238, 0.5);
  background: rgba(34, 211, 238, 0.1);
}

.time-picker-footer {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.btn-cancel,
.btn-confirm {
  padding: 12px 20px;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-cancel {
  background: rgba(255, 255, 255, 0.08);
  color: #e2e8f0;
}

.btn-cancel:hover {
  background: rgba(255, 255, 255, 0.12);
}

.btn-confirm {
  background: linear-gradient(135deg, #22d3ee 0%, #8b5cf6 100%);
  color: #ffffff;
  box-shadow: 0 4px 12px rgba(34, 211, 238, 0.3);
}

.btn-confirm:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(34, 211, 238, 0.4);
}
</style>
