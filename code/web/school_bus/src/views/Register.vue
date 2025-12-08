<template>
  <div class="auth-container">
    <!-- 动态背景装饰 -->
    <div class="bg-decoration orbs-1"></div>
    <div class="bg-decoration orbs-2"></div>

    <div class="auth-content">
      <!-- 左侧：宣传区 -->
      <div class="auth-hero">
        <div class="hero-text-content">
          <div class="brand-pill">
            <span class="icon">✨</span>
            <span>一步完成 · 智慧上车</span>
          </div>
          
          <h1 class="headline">
            创建您的<br />
            <span class="text-gradient">学生账号</span>
          </h1>
          
          <p class="subhead">
            完善信息后即可预约包车，随时查看订单进度。全程数据加密，安全无忧。
          </p>

          <div class="hero-features">
            <div class="feature-item">
              <span class="check-icon">✓</span>
              <span>实时审核进度通知</span>
            </div>
            <div class="feature-item">
              <span class="check-icon">✓</span>
              <span>移动端完美适配</span>
            </div>
            <div class="feature-item">
              <span class="check-icon">✓</span>
              <span>专属行程管理</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：注册卡片 -->
      <div class="auth-form-wrapper">
        <div class="auth-card glass-panel">
          <div class="card-header">
            <div class="header-text">
              <h2>填写基础信息</h2>
              <p class="hint">全部字段均为必填，请确保准确。</p>
            </div>
          </div>

          <form @submit.prevent="handleStudentRegister" class="register-form">
            <div class="form-group">
              <label>学号</label>
              <div class="input-wrapper">
                <input v-model="studentForm.studentId" placeholder="请输入学号" required />
                <span class="input-icon">🎓</span>
              </div>
            </div>

            <div class="form-group">
              <label>姓名</label>
              <div class="input-wrapper">
                <input v-model="studentForm.name" placeholder="请输入姓名" required />
                <span class="input-icon">👤</span>
              </div>
            </div>

            <div class="form-group">
              <label>所在地</label>
              <div class="input-wrapper">
                <input v-model="studentForm.location" placeholder="例如：南校区" required />
                <span class="input-icon">📍</span>
              </div>
            </div>

            <div class="form-group">
              <label>密码</label>
              <div class="input-wrapper">
                <input v-model="studentForm.password" type="password" placeholder="设置登录密码" required />
                <span class="input-icon">🔒</span>
              </div>
            </div>

            <div class="form-actions">
              <button type="submit" class="btn-primary">立即注册</button>
            </div>
          </form>

          <div class="card-footer">
            <p>已经有账号？ <router-link to="/login" class="link-highlight">返回登录</router-link></p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { registerStudent } from '../api'

const router = useRouter()

const studentForm = reactive({
  studentId: '',
  name: '',
  location: '',
  password: ''
})

const handleStudentRegister = async () => {
  if (!studentForm.studentId || !studentForm.password) {
    alert('请填写完整信息')
    return
  }
  try {
    const res = await registerStudent(studentForm)
    if (res.code === 200) {
      alert('注册成功，请登录')
      // 返回登录页时恢复滚动
      document.body.style.overflow = 'auto'
      router.push('/login')
    } else {
      alert(res.message || '注册失败')
    }
  } catch (error) {
    console.error(error)
    alert('注册异常')
  }
}
</script>

<style scoped>
.auth-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  overflow: hidden;
  background-color: #020617;
  background-image: 
    radial-gradient(at 0% 0%, rgba(139, 92, 246, 0.15) 0px, transparent 50%),
    radial-gradient(at 100% 100%, rgba(6, 182, 212, 0.15) 0px, transparent 50%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
  color: #fff;
  z-index: 1000;
}

/* 动态背景球体 */
.bg-decoration {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  z-index: 0;
  opacity: 0.5;
}
.orbs-1 {
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, #22d3ee, transparent 70%);
  top: -200px;
  right: -100px;
  animation: float 14s infinite alternate ease-in-out;
}
.orbs-2 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, #8b5cf6, transparent 70%);
  bottom: -50px;
  left: -50px;
  animation: float 12s infinite alternate-reverse ease-in-out;
}

@keyframes float {
  0% { transform: translate(0, 0); }
  100% { transform: translate(30px, 50px); }
}

.auth-content {
  width: 100%;
  max-width: 1100px;
  display: grid;
  grid-template-columns: 1.2fr 1fr;
  gap: 70px;
  padding: 40px 60px 40px 40px;
  z-index: 1;
  align-items: center;
}

/* --- Hero Section --- */
.hero-text-content {
  padding-right: 20px;
}

.brand-pill {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 30px;
  font-size: 13px;
  color: #e2e8f0;
  margin-bottom: 24px;
  backdrop-filter: blur(4px);
}

.headline {
  font-size: 3.5rem;
  line-height: 1.1;
  font-weight: 800;
  margin-bottom: 24px;
  letter-spacing: -1px;
}

.text-gradient {
  background: linear-gradient(135deg, #e0f2fe 0%, #f0e7ff 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  display: inline-block;
}

.subhead {
  font-size: 1.1rem;
  color: #94a3b8;
  line-height: 1.6;
  margin-bottom: 40px;
  max-width: 90%;
}

.hero-features {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #e2e8f0;
  font-size: 15px;
}

.check-icon {
  width: 24px;
  height: 24px;
  background: rgba(34, 211, 238, 0.2);
  color: #22d3ee;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
}

/* --- Auth Card Section --- */
.auth-form-wrapper {
  display: flex;
  justify-content: center;
}

.auth-card {
  width: 100%;
  max-width: 420px;
  padding: 36px;
  border-radius: 24px;
  background: rgba(15, 23, 42, 0.7);
  border: 1px solid rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(20px);
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
  position: relative;
  max-height: min(85vh, 720px);
  overflow-y: auto;
  scrollbar-width: thin;
}

.auth-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(34, 211, 238, 0.5), transparent);
}

.card-header {
  margin-bottom: 24px;
  text-align: center;
}

.header-text h2 {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 6px;
  color: #fff;
}

.header-text .hint {
  color: #94a3b8;
  font-size: 14px;
}

/* 表单样式 */
.register-form {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.form-group label {
  display: block;
  font-size: 13px;
  color: #cbd5e1;
  margin-bottom: 6px;
  font-weight: 500;
}

.input-wrapper {
  position: relative;
}

.input-wrapper input {
  width: 100%;
  box-sizing: border-box; 
  padding: 12px 16px;
  padding-right: 40px;
  background: rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  color: #fff;
  font-size: 14px;
  transition: all 0.2s ease;
  outline: none;
}

.input-wrapper input::placeholder {
  color: rgba(255, 255, 255, 0.2);
}

.input-wrapper input:focus {
  border-color: #22d3ee;
  background: rgba(0, 0, 0, 0.4);
  box-shadow: 0 0 0 3px rgba(34, 211, 238, 0.15);
}

.input-icon {
  position: absolute;
  right: 14px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 14px;
  opacity: 0.5;
  pointer-events: none;
}

.btn-primary {
  width: 100%;
  padding: 12px;
  margin-top: 8px;
  background: linear-gradient(135deg, #22d3ee 0%, #8b5cf6 100%);
  border: none;
  border-radius: 12px;
  color: #fff;
  font-weight: 600;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 8px 20px -6px rgba(34, 211, 238, 0.4);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 25px -8px rgba(34, 211, 238, 0.5);
}

.btn-primary:active {
  transform: translateY(0);
}

.card-footer {
  margin-top: 20px;
  text-align: center;
  font-size: 13px;
  color: #94a3b8;
}

.link-highlight {
  color: #8b5cf6;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.2s;
}

.link-highlight:hover {
  color: #a78bfa;
  text-decoration: underline;
}

/* --- 响应式设计 --- */
@media (max-width: 1024px) {
  .headline { font-size: 2.8rem; }
  .auth-content { padding: 30px; gap: 40px; }
}

@media (max-width: 900px) {
  .auth-content {
    grid-template-columns: 1fr;
    max-width: 500px;
    padding: 20px 20px 40px;
  }

  .auth-card {
    padding: 30px;
    max-height: none;
  }

  .auth-hero {
    text-align: center;
    margin-bottom: 20px;
  }
  
  .hero-text-content { padding-right: 0; }
  
  .subhead { margin: 0 auto 30px; }
  
  .hero-features {
    align-items: center;
  }

  .auth-form-wrapper {
    justify-content: center;
  }
  
  .headline { font-size: 2.2rem; }
}

@media (max-height: 720px) {
  .auth-content {
    padding: 20px;
    gap: 20px;
  }

  .auth-card {
    max-height: calc(100vh - 80px);
  }
}
</style>
