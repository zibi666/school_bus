<template>
  <div class="layout-container">
    <aside class="sidebar">
      <div class="logo">
        包车系统
        <span class="logo-sub">Student Space</span>
      </div>
      <nav class="nav-menu">
        <router-link to="/student/charter" class="nav-item" active-class="active">
          申请包车
        </router-link>
        <router-link to="/student/trips" class="nav-item" active-class="active">
          我的订单
        </router-link>
        <router-link to="/student/profile" class="nav-item" active-class="active">
          个人信息
        </router-link>
      </nav>
      
      <div class="join-section">
        <button @click="showJoinModal = true" class="join-btn">
          <span>🔗</span> 加入订单
        </button>
      </div>
      
      <div class="logout-section">
        <button @click="logout" class="logout-btn">退出登录</button>
      </div>
    </aside>

    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
    
    <!-- Join Order Modal -->
    <div v-if="showJoinModal" class="modal-overlay" @click.self="showJoinModal = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>🔗 加入订单</h3>
          <button type="button" class="close-btn" @click="showJoinModal = false">×</button>
        </div>
        
        <div class="modal-body">
          <p class="modal-tip">输入朋友分享的邀请码，即可查看车辆信息并加入该订单</p>
          
          <div class="code-input-wrapper">
            <input 
              v-model="invitationCodeInput" 
              type="text" 
              placeholder="例如：ABC12345" 
              class="code-input"
              @keyup.enter="handleJoinOrder"
              maxlength="8"
            />
            <div class="input-hint">{{ invitationCodeInput.length }}/8</div>
          </div>
          
          <div v-if="joinError" class="error-message">
            <span>❌</span> {{ joinError }}
          </div>
          <div v-if="joinSuccess" class="success-message">
            <span>✓</span> {{ joinSuccess }}
          </div>
        </div>
        
        <div class="modal-footer">
          <button type="button" class="btn-cancel" @click="showJoinModal = false">取消</button>
          <button type="button" class="btn-join" @click="handleJoinOrder" :disabled="!invitationCodeInput || joinLoading">
            {{ joinLoading ? '加载中...' : '加入订单' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ref } from 'vue'
import { getOrderByInvitationCode } from '../../api'

const router = useRouter()
const showJoinModal = ref(false)
const invitationCodeInput = ref('')
const joinError = ref('')
const joinSuccess = ref('')
const joinLoading = ref(false)

const logout = () => {
  localStorage.clear()
  router.push('/login')
}

const handleJoinOrder = async () => {
  if (!invitationCodeInput.value.trim()) {
    joinError.value = '请输入邀请码'
    return
  }
  
  joinError.value = ''
  joinSuccess.value = ''
  joinLoading.value = true
  
  try {
    const res = await getOrderByInvitationCode(invitationCodeInput.value)
    if (res.code === 200) {
      joinSuccess.value = `✓ 成功！订单已添加到您的订单列表`
      // 清空输入
      invitationCodeInput.value = ''
      // 1秒后关闭弹窗并跳转到我的订单
      setTimeout(() => {
        showJoinModal.value = false
        router.push('/student/trips')
      }, 1000)
    } else {
      joinError.value = res.message || '加入失败'
    }
  } catch (e) {
    console.error(e)
    if (e && e.message) {
      joinError.value = e.message
    } else {
      joinError.value = '加入失败，请检查邀请码是否正确'
    }
  } finally {
    joinLoading.value = false
  }
}
</script>

<style scoped>
.layout-container {
  display: flex;
  min-height: 100vh;
  background: transparent;
}

.sidebar {
  width: 260px;
  background: linear-gradient(165deg, rgba(15, 23, 42, 0.9), rgba(16, 37, 86, 0.9));
  display: flex;
  flex-direction: column;
  border-right: 1px solid var(--border);
  box-shadow: var(--shadow-2);
  backdrop-filter: blur(12px);
  z-index: 10;
}

.logo {
  padding: 2rem;
  font-size: 1.4rem;
  font-weight: 800;
  color: #f8fafc;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  line-height: 1.2;
}

.logo-sub {
  display: block;
  font-size: 12px;
  font-weight: 500;
  color: var(--text-secondary);
  margin-top: 8px;
}

.nav-menu {
  flex: 1;
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.nav-item {
  padding: 12px 16px;
  border-radius: 12px;
  color: #cbd5e1;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 10px;
}

.nav-item:hover {
  background: #0f172a;
  color: #e0f2fe;
}

.nav-item.active {
  background: linear-gradient(120deg, #7c3aed, #22d3ee);
  color: #fff;
  font-weight: 700;
  box-shadow: 0 10px 26px rgba(124, 58, 237, 0.25);
}

.join-section {
  padding: 0 1.5rem 1rem;
}

.join-btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #22d3ee 0%, #8b5cf6 100%);
  color: #ffffff;
  border: none;
  border-radius: 10px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 14px;
}

.join-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(34, 211, 238, 0.3);
}

.logout-section {
  padding: 1.5rem;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
}

.logout-btn {
  width: 100%;
  padding: 10px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: #0f172a;
  color: #fca5a5;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
}

.logout-btn:hover {
  background: rgba(239, 68, 68, 0.18);
}

.main-content {
  flex: 1;
  overflow-y: auto;
  padding: 2rem;
}

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
  animation: fadeIn 0.3s ease;
}

.modal-content {
  background: linear-gradient(135deg, rgba(15, 23, 42, 0.98) 0%, rgba(30, 41, 59, 0.98) 100%);
  border-radius: 20px;
  border: 1px solid rgba(148, 163, 184, 0.15);
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.5), inset 0 1px 1px rgba(255, 255, 255, 0.08);
  max-width: 420px;
  width: 90%;
  backdrop-filter: blur(20px);
  animation: slideUp 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  overflow: hidden;
}

.modal-header {
  padding: 24px 28px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.12);
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(90deg, rgba(34, 211, 238, 0.05) 0%, rgba(139, 92, 246, 0.05) 100%);
}

.modal-header h3 {
  margin: 0;
  color: #f8fafc;
  font-size: 20px;
  font-weight: 700;
  letter-spacing: -0.5px;
  background: linear-gradient(135deg, #22d3ee 0%, #8b5cf6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.close-btn {
  background: none;
  border: none;
  color: #94a3b8;
  font-size: 32px;
  cursor: pointer;
  transition: all 0.2s;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  line-height: 1;
}

.close-btn:hover {
  color: #f8fafc;
  background: rgba(139, 92, 246, 0.1);
  transform: rotate(90deg);
}

.modal-body {
  padding: 32px 28px;
}

.modal-tip {
  color: #cbd5e1;
  font-size: 14px;
  margin: 0 0 24px;
  text-align: center;
  line-height: 1.6;
  font-weight: 500;
}

.code-input-wrapper {
  position: relative;
  margin-bottom: 20px;
}

.code-input {
  width: 100%;
  padding: 14px 40px 14px 16px;
  background: rgba(15, 23, 42, 0.6);
  border: 2px solid rgba(148, 163, 184, 0.25);
  border-radius: 12px;
  color: #f8fafc;
  font-size: 15px;
  text-transform: uppercase;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  letter-spacing: 1px;
  font-weight: 600;
  font-family: 'Courier New', monospace;
  box-sizing: border-box;
}

.input-hint {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #64748b;
  font-size: 12px;
  font-weight: 600;
  font-family: 'Courier New', monospace;
  pointer-events: none;
  transition: color 0.3s ease;
}

.code-input:focus ~ .input-hint {
  color: #22d3ee;
}

.code-input:focus {
  outline: none;
  border-color: #22d3ee;
  background: rgba(15, 23, 42, 0.8);
  box-shadow: 0 0 20px rgba(34, 211, 238, 0.25), inset 0 1px 2px rgba(255, 255, 255, 0.05);
  transform: scale(1.01);
}

.code-input::placeholder {
  color: #64748b;
  letter-spacing: 0.5px;
}

.error-message {
  color: #ff6b6b;
  font-size: 13px;
  margin-top: 16px;
  text-align: center;
  animation: slideIn 0.3s ease;
  padding: 12px 16px;
  background: rgba(255, 107, 107, 0.1);
  border-radius: 8px;
  border-left: 3px solid #ff6b6b;
  font-weight: 500;
}

.success-message {
  color: #34d399;
  font-size: 13px;
  margin-top: 16px;
  text-align: center;
  animation: slideIn 0.3s ease;
  font-weight: 600;
  padding: 12px 16px;
  background: rgba(52, 211, 153, 0.1);
  border-radius: 8px;
  border-left: 3px solid #34d399;
}

.modal-footer {
  padding: 20px 28px;
  border-top: 1px solid rgba(148, 163, 184, 0.12);
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  background: rgba(15, 23, 42, 0.3);
}

.btn-cancel {
  padding: 12px 24px;
  background: rgba(30, 41, 59, 0.8);
  color: #cbd5e1;
  border: 1px solid rgba(148, 163, 184, 0.25);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  font-weight: 600;
  font-size: 14px;
}

.btn-cancel:hover {
  background: rgba(30, 41, 59, 1);
  color: #f8fafc;
  border-color: rgba(148, 163, 184, 0.4);
  transform: translateY(-2px);
}

.btn-cancel:active {
  transform: translateY(0);
}

.btn-join {
  padding: 12px 32px;
  background: linear-gradient(135deg, #22d3ee 0%, #8b5cf6 100%);
  color: #ffffff;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  font-weight: 700;
  font-size: 14px;
  letter-spacing: 0.5px;
  box-shadow: 0 8px 20px rgba(34, 211, 238, 0.3);
  position: relative;
  overflow: hidden;
}

.btn-join::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.btn-join:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 12px 28px rgba(34, 211, 238, 0.4);
}

.btn-join:hover:not(:disabled)::before {
  left: 100%;
}

.btn-join:active:not(:disabled) {
  transform: translateY(-1px);
}

.btn-join:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  box-shadow: 0 4px 12px rgba(34, 211, 238, 0.15);
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-8px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* Transition */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
