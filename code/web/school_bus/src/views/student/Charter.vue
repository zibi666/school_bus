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
                <span v-if="timePickerData.date && timePickerData.startTime && timePickerData.endTime" class="time-display">
                  {{ timePickerData.date }} {{ timePickerData.startTime }}-{{ timePickerData.endTime }}
                </span>
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
                    
                    <div class="time-section time-grid">
                      <div class="time-item time-grid-col">
                        <label for="startHour">开始 - 时</label>
                        <select id="startHour" v-model="timePickerData.startHour" class="time-select">
                          <option value="">时</option>
                          <option v-for="h in availableHours" :key="h" :value="h">{{ h }}</option>
                        </select>
                      </div>
                      <div class="time-item time-grid-col">
                        <label for="startMin">开始 - 分</label>
                        <select id="startMin" v-model="timePickerData.startMin" class="time-select">
                          <option value="">分</option>
                          <option v-for="m in availableMins" :key="m" :value="m">{{ m }}</option>
                        </select>
                      </div>
                      <div class="time-item time-grid-col">
                        <label for="endHour">结束 - 时</label>
                        <select id="endHour" v-model="timePickerData.endHour" class="time-select">
                          <option value="">时</option>
                          <option v-for="h in availableHours" :key="h + '-end'" :value="h">{{ h }}</option>
                        </select>
                      </div>
                      <div class="time-item time-grid-col">
                        <label for="endMin">结束 - 分</label>
                        <select id="endMin" v-model="timePickerData.endMin" class="time-select">
                          <option value="">分</option>
                          <option v-for="m in availableMins" :key="m + '-end'" :value="m">{{ m }}</option>
                        </select>
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
              <option value="大巴">大巴 (45座)</option>
              <option value="中巴">中巴 (20座)</option>
              <option value="商务车">商务车 (7座)</option>
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

    <!-- 支付弹窗 -->
    <div v-if="showPaymentModal" class="payment-modal" @click.self="cancelPayment">
      <div class="payment-content">
        <div class="payment-header">
          <h3>确认支付</h3>
          <button type="button" class="close-btn" @click="cancelPayment">×</button>
        </div>
        
        <div class="payment-body">
          <div class="payment-info">
            <div class="info-row">
              <span class="label">目的地</span>
              <span class="value">{{ form.destination }}</span>
            </div>
            <div class="info-row">
              <span class="label">用车时间</span>
              <span class="value">{{ timePickerData.date }} {{ timePickerData.startTime }}-{{ timePickerData.endTime }}</span>
            </div>
            <div class="info-row">
              <span class="label">车型</span>
              <span class="value">{{ form.requestedCarType }}</span>
            </div>
            <div class="info-row">
              <span class="label">用车时长</span>
              <span class="value">{{ priceInfo.formattedHours }}</span>
            </div>
            <div class="info-row price-row">
              <span class="label">应付金额</span>
              <span class="price-value">¥{{ priceInfo.price }}</span>
            </div>
          </div>
          
          <div class="payment-notice">
            <p>⚠️ 支付成功后，订单将提交至管理员审核</p>
            <p>💡 审核通过后可在"我的订单"查看车辆信息</p>
          </div>
        </div>
        
        <div class="payment-footer">
          <button type="button" class="btn-cancel-pay" @click="cancelPayment">取消支付</button>
          <button type="button" class="btn-pay" @click="handlePayment">确认支付</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { createOrder, calculateOrderPrice, payOrder } from '../../api' 
import LoadingSpinner from '../../components/LoadingSpinner.vue'

const router = useRouter()
const loading = ref(false)
const showTimePicker = ref(false)
const showPaymentModal = ref(false)
const currentOrderId = ref(null)
const priceInfo = reactive({
  price: 0,
  hours: 0,
  formattedHours: ''
})
const timePickerData = reactive({
  date: '',
  // 分别存小时与分钟
  startHour: '',
  startMin: '',
  endHour: '',
  endMin: '',
  // 保持兼容用于显示/提交
  startTime: '',
  endTime: ''
})

// 可选小时和分钟（小时从05到23，分钟只允许00和30）
const availableHours = Array.from({ length: 19 }, (_, i) => String(i + 5).padStart(2, '0'))
const availableMins = [ '00', '30' ]
const form = reactive({
  destination: '',
  requestedCarType: ''
})



const confirmTimeSelection = () => {
  // 必须填写日期和时分
  if (!timePickerData.date || !timePickerData.startHour || !timePickerData.startMin || !timePickerData.endHour || !timePickerData.endMin) {
    alert('请填写完整的时间信息')
    return
  }

  // 验证日期不能是今天之前
  const selectedDate = new Date(timePickerData.date)
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  selectedDate.setHours(0, 0, 0, 0)
  if (selectedDate < today) {
    alert('预约日期不能是今天之前')
    return
  }

  // 组合时间字符串并保存（用于显示与提交）
  const startTimeStr = `${timePickerData.startHour}:${timePickerData.startMin}`
  const endTimeStr = `${timePickerData.endHour}:${timePickerData.endMin}`

  // 验证开始时间范围：5:00 - 21:00
  const startMinutes = Number(timePickerData.startHour) * 60 + Number(timePickerData.startMin)
  if (startMinutes < 5 * 60 || startMinutes > 21 * 60) {
    alert('开始时间必须在 05:00 至 21:00 之间')
    return
  }

  // 验证结束时间范围：最晚 23:00
  const endMinutes = Number(timePickerData.endHour) * 60 + Number(timePickerData.endMin)
  if (endMinutes > 23 * 60) {
    alert('结束时间最晚为 23:00')
    return
  }

  // 验证开始时间必须早于结束时间
  if (startMinutes >= endMinutes) {
    alert('开始时间必须早于结束时间')
    return
  }

  // 验证租车时间不少于2小时
  const durationMinutes = endMinutes - startMinutes
  if (durationMinutes < 120) {
    alert('租车时间不得少于2小时')
    return
  }

  // 保存用于显示并关闭模态
  timePickerData.startTime = startTimeStr
  timePickerData.endTime = endTimeStr

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

      // 构建ISO格式的时间戳（如果没有 startTime 就从小时/分钟组合）
    const startTimeStr = timePickerData.startTime || `${timePickerData.startHour}:${timePickerData.startMin}`
    const endTimeStr = timePickerData.endTime || `${timePickerData.endHour}:${timePickerData.endMin}`
    const startDateTime = `${timePickerData.date}T${startTimeStr}:00`
    const endDateTime = `${timePickerData.date}T${endTimeStr}:00`

    // 先计算价格，传递实际的时间而不是文本描述
    const priceRes = await calculateOrderPrice({
      startTime: startDateTime,
      endTime: endDateTime,
      requestedCarType: form.requestedCarType
    })
    
    if (priceRes.code !== 200) {
      alert(priceRes.message || '价格计算失败')
      return
    }
    
    // 保存价格信息
    priceInfo.price = priceRes.data.price
    priceInfo.hours = priceRes.data.hours
    priceInfo.formattedHours = priceRes.data.formattedHours

    const res = await createOrder({
        destination: form.destination,
        startTime: startDateTime,
        endTime: endDateTime,
        requestedCarType: form.requestedCarType,
        price: priceRes.data.price,
        studentId
    })
    
    if (res.code === 200) {
        // 保存订单ID并显示支付弹窗
        currentOrderId.value = res.data.orderId
        showPaymentModal.value = true
    } else {
        alert(res.message || '提交失败')
    }
  } catch (e) {
    console.error(e)
    // 显示后端返回的具体错误信息，而不是通用的"提交异常"
    if (e && e.message) {
      alert(e.message)
    } else {
      alert('提交异常')
    }
  } finally {
    loading.value = false
  }
}

const handlePayment = async () => {
  if (!currentOrderId.value) return
  
  try {
    const res = await payOrder(currentOrderId.value)
    if (res.code === 200) {
      alert('支付成功！订单已提交，请等待管理员审核。')
      showPaymentModal.value = false
      router.push('/student/trips')
    } else {
      alert(res.message || '支付失败')
    }
  } catch (e) {
    console.error(e)
    if (e && e.message) {
      alert(e.message)
    } else {
      alert('支付异常')
    }
  }
}

const cancelPayment = () => {
  showPaymentModal.value = false
  alert('已取消支付')
  router.push('/student/trips')
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

/* 修复自动填充样式 */
.form-group input:-webkit-autofill,
.form-group input:-webkit-autofill:hover,
.form-group input:-webkit-autofill:focus {
  -webkit-box-shadow: 0 0 0 30px rgba(15, 23, 42, 0.86) inset !important;
  -webkit-text-fill-color: #f8fafc !important;
  border-color: rgba(34, 211, 238, 0.5) !important;
}

.form-group input:-webkit-autofill::first-line {
  color: #f8fafc;
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
  gap: 12px;
}

/* 两列布局：一列时，一列分；每个时间（开始/结束）占一行 */
.time-grid {
  grid-template-columns: repeat(2, 1fr);
}

.time-grid-col {
  display: flex;
  flex-direction: column;
  gap: 8px;
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

/* 修复日期和时间输入框的图标颜色 */
.date-section input[type="date"]::-webkit-calendar-picker-indicator,
.time-item input[type="time"]::-webkit-calendar-picker-indicator {
  filter: brightness(0) invert(1);
  cursor: pointer;
}

.date-section input[type="date"],
.time-item input[type="time"] {
  color-scheme: dark;
}

.date-section input:focus,
.time-item input:focus,
.time-select:focus {
  outline: none;
  border-color: rgba(34, 211, 238, 0.5);
  background: rgba(34, 211, 238, 0.1);
}

.time-select {
  width: 100%;
  box-sizing: border-box;
  padding: 10px 12px;
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.06);
  background: #0f172a; /* 暗黑背景 */
  color: #ffffff; /* 白字 */
  font-size: 14px;
  appearance: none;
  -webkit-appearance: none;
  -moz-appearance: none;
  background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23ffffff' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6 9 12 15 18 9'%3e%3c/polyline%3e%3c/svg%3e");
  background-repeat: no-repeat;
  background-position: right 12px center;
  background-size: 16px;
  padding-right: 40px;
}

.time-select option {
  background: #0f172a;
  color: #ffffff;
}

/* 某些浏览器在展开下拉时需要这个样式以确保项为暗色 */
.time-select::-ms-expand { display: none; }
.time-select:focus {
  background: #0b1220;
  color: #ffffff;
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

/* 支付弹窗样式 */
.payment-modal {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.payment-content {
  background: linear-gradient(135deg, rgba(15, 23, 42, 0.95), rgba(30, 41, 59, 0.95));
  border: 1px solid rgba(34, 211, 238, 0.3);
  border-radius: 24px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
  animation: slideUp 0.3s ease;
  overflow: hidden;
}

@keyframes slideUp {
  from {
    transform: translateY(30px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.payment-header {
  padding: 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.payment-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #f8fafc;
}

.payment-header .close-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: rgba(255, 255, 255, 0.08);
  color: #cbd5e1;
  font-size: 24px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.payment-header .close-btn:hover {
  background: rgba(255, 255, 255, 0.12);
  color: #f8fafc;
}

.payment-body {
  padding: 24px;
}

.payment-info {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 20px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.info-row:last-child {
  border-bottom: none;
}

.info-row .label {
  color: #94a3b8;
  font-size: 14px;
}

.info-row .value {
  color: #e2e8f0;
  font-size: 14px;
  font-weight: 500;
}

.price-row {
  padding-top: 16px;
  margin-top: 8px;
  border-top: 2px solid rgba(34, 211, 238, 0.3) !important;
}

.price-value {
  color: #22d3ee;
  font-size: 28px;
  font-weight: 800;
}

.payment-notice {
  background: rgba(34, 211, 238, 0.08);
  border: 1px solid rgba(34, 211, 238, 0.2);
  border-radius: 12px;
  padding: 16px;
}

.payment-notice p {
  margin: 0 0 8px 0;
  color: #94a3b8;
  font-size: 13px;
  line-height: 1.6;
}

.payment-notice p:last-child {
  margin-bottom: 0;
}

.payment-footer {
  padding: 20px 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.btn-cancel-pay,
.btn-pay {
  padding: 14px 20px;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-cancel-pay {
  background: rgba(255, 255, 255, 0.08);
  color: #e2e8f0;
}

.btn-cancel-pay:hover {
  background: rgba(255, 255, 255, 0.12);
}

.btn-pay {
  background: linear-gradient(135deg, #22d3ee 0%, #8b5cf6 100%);
  color: #ffffff;
  box-shadow: 0 4px 16px rgba(34, 211, 238, 0.4);
}

.btn-pay:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(34, 211, 238, 0.5);
}

.btn-confirm:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(34, 211, 238, 0.4);
}
</style>
