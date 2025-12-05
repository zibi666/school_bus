<template>
  <div class="page-container">
    <div class="content-card">
      <div class="header">
        <h2>申请包车</h2>
        <p>填写下方的表单来预约您的行程</p>
      </div>

      <form @submit.prevent="submitOrder" class="apply-form">
        <div class="form-group">
          <label>目的地</label>
          <input type="text" v-model="form.destination" placeholder="请输入目的地" required />
        </div>

        <div class="form-group">
          <label>使用时间段</label>
          <input type="text" v-model="form.usageTime" placeholder="例如：12月20日 9:00-12:00" required />
        </div>

        <div class="form-group">
          <label>需求车型</label>
          <select v-model="form.requestedCarType" required>
            <option value="" disabled>请选择车型</option>
            <option>大巴 (45座)</option>
            <option>中巴 (20座)</option>
            <option>商务车 (7座)</option>
          </select>
        </div>

        <div class="actions">
          <button type="submit" class="submit-btn" :disabled="loading">
            {{ loading ? '提交中...' : '提交申请' }}
          </button>
        </div>
      </form>
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
  padding: 2rem;
  max-width: 800px;
  margin: 0 auto;
}

.content-card {
  background: white;
  border-radius: 20px;
  padding: 3rem;
  box-shadow: 0 10px 30px rgba(0,0,0,0.05);
}

.header {
  margin-bottom: 2.5rem;
  text-align: center;
}

.header h2 {
  font-size: 1.8rem;
  color: #2d3748;
  margin-bottom: 0.5rem;
}

.header p {
  color: #718096;
}

.apply-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #4a5568;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e2e8f0;
  border-radius: 10px;
  font-size: 1rem;
  transition: border-color 0.2s;
  outline: none;
}

.form-group input:focus,
.form-group select:focus {
  border-color: #8b5cf6;
}

.submit-btn {
  width: 100%;
  padding: 14px;
  background: #8b5cf6;
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 1.1rem;
  font-weight: bold;
  cursor: pointer;
  transition: background 0.2s;
}

.submit-btn:hover {
  background: #7c3aed;
}

.submit-btn:disabled {
  background: #cbd5e0;
  cursor: not-allowed;
}
</style>
