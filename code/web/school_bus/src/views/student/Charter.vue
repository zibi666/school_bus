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
            <input id="usage" type="text" v-model="form.usageTime" placeholder="例如：12月20日 9:00-12:00" required />
          </div>

          <div class="form-group">
            <label for="carType">需求车型</label>
            <select id="carType" v-model="form.requestedCarType" required>
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
const form = reactive({
  destination: '',
  usageTime: '',
  requestedCarType: ''
})

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

    const res = await createOrder({
        ...form,
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
  padding: 8px;
}

.header-row {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  margin-bottom: 18px;
}

.page-title {
  margin: 4px 0 6px;
  color: #f8fafc;
  font-size: 28px;
}

.stat-ribbon {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 10px;
}

.stat {
  padding: 12px;
  border-radius: 14px;
  border: 1px solid var(--border);
  background: rgba(8, 15, 30, 0.9);
  text-align: center;
  box-shadow: var(--shadow-2);
}

.stat-num {
  display: block;
  color: #f8fafc;
  font-weight: 800;
}

.stat-label {
  color: var(--text-secondary);
  font-size: 12px;
}

.grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 18px;
}

.card {
  padding: 22px;
  border-radius: 18px;
  background: rgba(12, 18, 34, 0.92);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: var(--shadow-1);
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
  padding: 12px 14px;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(15, 23, 42, 0.86);
  color: #f8fafc;
  outline: none;
  transition: border 0.2s ease, box-shadow 0.2s ease;
}

.form-group input:focus,
.form-group select:focus {
  border-color: rgba(34, 211, 238, 0.5);
  box-shadow: 0 0 0 4px rgba(34, 211, 238, 0.12);
}

.actions {
  margin-top: 4px;
}

.btn-primary {
  width: 100%;
}

.card-side h4 {
  color: #f8fafc;
  margin: 0 0 10px;
}

.tips {
  color: var(--text-secondary);
  padding-left: 18px;
  margin: 0 0 14px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.mini-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 10px;
}

.mini {
  padding: 12px;
  border-radius: 12px;
  border: 1px solid var(--border);
  background: rgba(255, 255, 255, 0.08);
  color: #ffffff;
}

.tag {
  display: inline-flex;
  padding: 4px 8px;
  border-radius: 10px;
  border: 1px solid rgba(34, 211, 238, 0.3);
  color: #22d3ee;
  font-size: 12px;
  margin-bottom: 6px;
}

@media (max-width: 1024px) {
  .grid {
    grid-template-columns: 1fr;
  }
}
</style>
