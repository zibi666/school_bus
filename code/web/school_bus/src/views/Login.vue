<template>
  <div class="auth-container">
    <!-- 动态背景装饰 (亮色弥散风格) -->
    <div class="bg-layer">
      <div class="blob blob-1"></div>
      <div class="blob blob-2"></div>
      <div class="blob blob-3"></div>
    </div>

    <div class="auth-content">
      <!-- 左侧：宣传与视觉区 -->
      <div class="auth-hero">
        <div class="hero-inner">
          <div class="brand-pill">
            <span class="pill-icon">✨</span>
            <span>智慧校园 · 极速通勤</span>
          </div>
          
          <h1 class="headline">
            开启您的<br />
            <span class="text-gradient">多彩校园生活</span>
          </h1>
          
          <p class="subhead">
            统一入口连接学生与管理后台。<br>实时掌握车辆状态，让出行更自由。
          </p>

          <div class="hero-stats">
            <div class="stat-card">
              <div class="stat-icon icon-blue">✈️</div>
              <div class="stat-info">
                <span class="stat-num">24/7</span>
                <span class="stat-label">随时预约</span>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon icon-purple">⚡️</div>
              <div class="stat-info">
                <span class="stat-num">5min</span>
                <span class="stat-label">极速审核</span>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon icon-pink">🛡️</div>
              <div class="stat-info">
                <span class="stat-num">100%</span>
                <span class="stat-label">安全保障</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：登录卡片 -->
      <div class="auth-form-wrapper">
        <div class="auth-card">
          <div class="card-header">
            <div class="header-text">
              <h2>欢迎登录</h2>
              <p class="hint">选择身份进入管理空间</p>
            </div>
            <!-- 身份切换器 -->
            <div class="role-switcher">
              <div 
                class="switch-background" 
                :style="{ transform: role === 'student' ? 'translateX(0)' : 'translateX(100%)' }"
              ></div>
              <button
                type="button"
                :class="['switch-btn', role === 'student' ? 'active' : '']"
                @click="role = 'student'"
              >学生</button>
              <button
                type="button"
                :class="['switch-btn', role === 'admin' ? 'active' : '']"
                @click="role = 'admin'"
              >管理员</button>
            </div>
          </div>

          <form @submit.prevent="handleLogin" class="login-form">
            <div class="form-group">
              <label :for="role === 'student' ? 'student-id' : 'admin-account'">
                {{ role === 'student' ? '学号' : '管理员账号' }}
              </label>
              <div class="input-wrapper">
                <input
                  :id="role === 'student' ? 'student-id' : 'admin-account'"
                  type="text"
                  v-model="form.username"
                  :placeholder="role === 'student' ? '请输入学号' : '请输入账号'"
                  autocomplete="off"
                  required
                />
                <span class="input-icon">👤</span>
              </div>
            </div>

            <div class="form-group">
              <label for="password">密码</label>
              <div class="input-wrapper">
                <input
                  id="password"
                  type="password"
                  v-model="form.password"
                  placeholder="请输入密码"
                  required
                />
                <span class="input-icon">🔒</span>
              </div>
            </div>

            <div class="form-actions">
              <button type="submit" class="btn-primary" :disabled="loading">
                <span v-if="!loading">立即登录</span>
                <span v-else class="loader"></span>
              </button>
            </div>
          </form>

          <div class="card-footer" v-if="role === 'student'">
            <span class="footer-text">还没有账号？</span>
            <router-link to="/register" class="link-highlight">立即注册</router-link>
          </div>
        </div>
      </div>
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
  // 模拟请求延迟看效果
  // await new Promise(r => setTimeout(r, 1000))
  try {
    let res
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
    alert('登录请求失败，请检查网络')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 核心重置：解决输入框溢出问题的关键 */
*, *::before, *::after {
  box-sizing: border-box;
}

.auth-container {
  /* 关键：限制高度，禁止滚动 */
  height: 100vh;
  width: 100vw;
  overflow: hidden;
  position: relative;
  background-color: #f8fafc; /* 浅色背景 */
  font-family: 'PingFang SC', 'Inter', system-ui, sans-serif;
  color: #1e293b;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* --- 动态多彩背景 (Light Mode) --- */
.bg-layer {
  position: absolute;
  inset: 0;
  z-index: 0;
  overflow: hidden;
  background: white;
}

.blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  opacity: 0.8;
  animation: float 10s infinite alternate cubic-bezier(0.45, 0.05, 0.55, 0.95);
}

.blob-1 {
  top: -10%;
  left: -10%;
  width: 50vw;
  height: 50vw;
  background: radial-gradient(circle, #c4b5fd, #a78bfa); /* 浅紫色 */
}

.blob-2 {
  bottom: -10%;
  right: -10%;
  width: 45vw;
  height: 45vw;
  background: radial-gradient(circle, #67e8f9, #22d3ee); /* 青色 */
  animation-delay: -2s;
}

.blob-3 {
  top: 40%;
  left: 30%;
  width: 30vw;
  height: 30vw;
  background: radial-gradient(circle, #f9a8d4, #f472b6); /* 粉色 */
  opacity: 0.6;
  animation-duration: 15s;
}

@keyframes float {
  0% { transform: translate(0, 0) scale(1); }
  100% { transform: translate(20px, 40px) scale(1.05); }
}

/* --- 内容布局 --- */
.auth-content {
  position: relative;
  z-index: 10;
  width: 100%;
  max-width: 1100px;
  height: 100%;
  /* Flexbox 布局让左右两侧垂直居中 */
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 40px;
  gap: 60px;
}

/* --- 左侧 Hero --- */
.auth-hero {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.brand-pill {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 14px;
  background: rgba(255, 255, 255, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
  margin-bottom: 24px;
  width: fit-content;
  backdrop-filter: blur(4px);
}

.brand-pill span {
  font-size: 13px;
  font-weight: 600;
  color: #475569;
}

.headline {
  font-size: 3.5rem;
  line-height: 1.15;
  font-weight: 900;
  color: #0f172a;
  margin-bottom: 20px;
  letter-spacing: -0.02em;
}

.text-gradient {
  background: linear-gradient(135deg, #7c3aed 0%, #db2777 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.subhead {
  font-size: 1.1rem;
  color: #64748b;
  line-height: 1.6;
  margin-bottom: 40px;
  max-width: 90%;
}

.hero-stats {
  display: flex;
  gap: 20px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.5);
  padding: 12px 16px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s;
}

.stat-card:hover {
  transform: translateY(-3px);
  background: rgba(255, 255, 255, 0.85);
}

.stat-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
}
.icon-blue { background: #e0f2fe; color: #0284c7; }
.icon-purple { background: #f3e8ff; color: #9333ea; }
.icon-pink { background: #fce7f3; color: #db2777; }

.stat-info {
  display: flex;
  flex-direction: column;
}
.stat-num { font-weight: 700; color: #334155; font-size: 16px; }
.stat-label { font-size: 12px; color: #64748b; }

/* --- 右侧 Form 卡片 --- */
.auth-form-wrapper {
  flex: 0 0 420px; /* 固定宽度，防止过宽 */
}

.auth-card {
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(20px);
  padding: 40px;
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 
    0 20px 25px -5px rgba(0, 0, 0, 0.05), 
    0 8px 10px -6px rgba(0, 0, 0, 0.01);
}

.header-text {
  text-align: center;
  margin-bottom: 24px;
}
.header-text h2 {
  font-size: 26px;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 8px;
}
.header-text .hint {
  color: #64748b;
  font-size: 14px;
}

/* 身份切换 - 亮色版 */
.role-switcher {
  background: #f1f5f9;
  padding: 4px;
  border-radius: 12px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  position: relative;
  margin-bottom: 28px;
}

.switch-background {
  position: absolute;
  top: 4px;
  left: 4px;
  width: calc(50% - 4px);
  height: calc(100% - 8px);
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.switch-btn {
  position: relative;
  z-index: 1;
  background: transparent;
  border: none;
  padding: 10px;
  font-size: 14px;
  font-weight: 600;
  color: #64748b;
  cursor: pointer;
  transition: color 0.3s;
}

.switch-btn.active {
  color: #0f172a; /* 选中时深色 */
}

/* 表单输入 - 修正溢出 */
.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  /* 确保父容器没有奇怪的 margin 导致溢出 */
  width: 100%; 
}

.form-group label {
  font-size: 13px;
  font-weight: 600;
  color: #334155;
  margin-bottom: 8px;
  margin-left: 2px;
}

.input-wrapper {
  position: relative;
  width: 100%; /* 确保 wrapper 宽度正确 */
}

.input-wrapper input {
  width: 100%; /* 修正：宽度占满父容器 */
  padding: 12px 16px;
  padding-right: 40px; /* 给 Icon 留位置 */
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  font-size: 15px;
  color: #1e293b;
  outline: none;
  transition: all 0.2s ease;
  /* 阴影让输入框更有层次 */
  box-shadow: 0 1px 2px rgba(0,0,0,0.02);
}

.input-wrapper input::placeholder {
  color: #94a3b8;
}

.input-wrapper input:focus {
  border-color: #8b5cf6;
  box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.15);
}

.input-icon {
  position: absolute;
  right: 14px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 16px;
  color: #94a3b8;
  pointer-events: none;
}

/* 按钮 - 渐变亮色 */
.btn-primary {
  width: 100%;
  padding: 14px;
  margin-top: 12px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  border: none;
  border-radius: 12px;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
}

.btn-primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(99, 102, 241, 0.4);
}

.btn-primary:active {
  transform: translateY(0);
}

.card-footer {
  margin-top: 24px;
  text-align: center;
  font-size: 14px;
}

.footer-text {
  color: #64748b;
}

.link-highlight {
  color: #6366f1;
  font-weight: 600;
  text-decoration: none;
  margin-left: 4px;
}
.link-highlight:hover {
  text-decoration: underline;
}

/* Loader */
.loader {
  display: inline-block;
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255,255,255,0.3);
  border-radius: 50%;
  border-top-color: #fff;
  animation: spin 0.8s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

/* 响应式适配 */
@media (max-width: 960px) {
  .auth-content {
    flex-direction: column;
    justify-content: center;
    gap: 40px;
    padding: 20px;
    /* 小屏下如果内容超高，不得不允许滚动，或者缩小比例 */
  }

  .auth-hero {
    flex: 0;
    text-align: center;
    align-items: center;
  }

  .headline { font-size: 2.5rem; }
  .subhead { margin-bottom: 20px; font-size: 1rem; }
  .hero-stats { display: none; /* 空间不足时隐藏 */ }
  
  .auth-form-wrapper {
    flex: 0;
    width: 100%;
    max-width: 400px;
  }
}
</style>