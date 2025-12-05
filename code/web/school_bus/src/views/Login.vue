<template>
  <div class="login-container">
    <div class="glass-card">
      <h2 class="title">校园包车系统</h2>
      
      <!-- Role Switcher -->
      <div class="role-switch">
        <button 
          :class="['switch-btn', role === 'student' ? 'active' : '']" 
          @click="role = 'student'"
        >
          学生端
        </button>
        <button 
          :class="['switch-btn', role === 'admin' ? 'active' : '']" 
          @click="role = 'admin'"
        >
          管理员
        </button>
      </div>

      <form @submit.prevent="handleLogin">
        <div class="input-group">
          <label>{{ role === 'student' ? '学号' : '管理员账号' }}</label>
          <input 
            type="text" 
            v-model="form.username" 
            :placeholder="role === 'student' ? '请输入学号' : '请输入账号'"
            required
          >
        </div>
        
        <div class="input-group">
          <label>密码</label>
          <input 
            type="password" 
            v-model="form.password" 
            placeholder="请输入密码"
            required
          >
        </div>

        <button type="submit" class="submit-btn">
          {{ loading ? '登录中...' : '立即登录' }}
        </button>
      </form>

      <p class="footer-text" v-if="role === 'student'">
        还没有账号？ <router-link to="/register">去注册</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { loginStudent, loginAdmin } from '../api'

const router = useRouter()
const role = ref('student')
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const handleLogin = async () => {
  loading.value = true
  try {
    let res;
    if (role.value === 'student') {
      res = await loginStudent({ studentId: form.username, password: form.password })
    } else {
      res = await loginAdmin({ account: form.username, password: form.password })
    }

    if (res && res.code === 200) {
      localStorage.setItem('role', role.value)
      localStorage.setItem('userInfo', JSON.stringify(res.data))
      
      if (role.value === 'student') {
        router.push('/student/charter')
      } else {
        router.push('/admin/trips')
      }
    } else {
      alert(res.message || '登录失败')
    }
  } catch (e) {
    console.error(e)
    alert('登录请求失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-family: 'Segoe UI', sans-serif;
}

.glass-card {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  padding: 3rem;
  border-radius: 24px;
  width: 100%;
  max-width: 420px;
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.37);
  color: white;
}

.title {
  text-align: center;
  font-size: 2rem;
  margin-bottom: 2rem;
  font-weight: 700;
  text-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.role-switch {
  display: flex;
  background: rgba(0, 0, 0, 0.2);
  padding: 4px;
  border-radius: 12px;
  margin-bottom: 2rem;
}

.switch-btn {
  flex: 1;
  padding: 10px;
  border: none;
  background: transparent;
  color: rgba(255, 255, 255, 0.7);
  cursor: pointer;
  border-radius: 8px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.switch-btn.active {
  background: white;
  color: #764ba2;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.input-group {
  margin-bottom: 1.5rem;
}

.input-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
  opacity: 0.9;
}

.input-group input {
  width: 100%;
  padding: 12px 16px;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.1);
  color: white;
  font-size: 1rem;
  outline: none;
  transition: all 0.3s;
}

.input-group input::placeholder {
  color: rgba(255, 255, 255, 0.5);
}

.input-group input:focus {
  background: rgba(255, 255, 255, 0.2);
  border-color: white;
}

.submit-btn {
  width: 100%;
  padding: 14px;
  border: none;
  border-radius: 12px;
  background: #a78bfa;
  background: linear-gradient(to right, #a78bfa, #8b5cf6);
  color: white;
  font-size: 1.1rem;
  font-weight: bold;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  margin-top: 1rem;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.4);
}

.footer-text {
  text-align: center;
  margin-top: 1.5rem;
  font-size: 0.9rem;
  opacity: 0.8;
}

.footer-text a {
  color: white;
  text-decoration: underline;
  font-weight: bold;
}
</style>
