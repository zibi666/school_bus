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
          <h3>加入订单</h3>
          <button type="button" class="close-btn" @click="showJoinModal = false">×</button>
        </div>
        
        <div class="modal-body">
          <p class="modal-tip">输入邀请码即可查看车辆信息并加入该订单</p>
          <input 
            v-model="invitationCodeInput" 
            type="text" 
            placeholder="请输入邀请码（例如：ABC12345）" 
            class="code-input"
            @keyup.enter="handleJoinOrder"
          />
          <div v-if="joinError" class="error-message">{{ joinError }}</div>
          <div v-if="joinSuccess" class="success-message">{{ joinSuccess }}</div>
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
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: linear-gradient(135deg, rgba(15, 23, 42, 0.95), rgba(30, 41, 59, 0.95));
  border-radius: 16px;
  border: 1px solid rgba(148, 163, 184, 0.1);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.4);
  max-width: 400px;
  width: 90%;
  backdrop-filter: blur(12px);
}

.modal-header {
  padding: 20px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  color: #f8fafc;
  font-size: 18px;
  font-weight: 700;
}

.close-btn {
  background: none;
  border: none;
  color: #94a3b8;
  font-size: 28px;
  cursor: pointer;
  transition: color 0.2s;
}

.close-btn:hover {
  color: #cbd5e1;
}

.modal-body {
  padding: 20px;
}

.modal-tip {
  color: #cbd5e1;
  font-size: 14px;
  margin: 0 0 16px;
  text-align: center;
}

.code-input {
  width: 100%;
  padding: 12px;
  background: rgba(15, 23, 42, 0.5);
  border: 1px solid rgba(148, 163, 184, 0.2);
  border-radius: 8px;
  color: #f8fafc;
  font-size: 14px;
  text-transform: uppercase;
  transition: all 0.2s;
}

.code-input:focus {
  outline: none;
  border-color: #22d3ee;
  background: rgba(15, 23, 42, 0.7);
  box-shadow: 0 0 12px rgba(34, 211, 238, 0.2);
}

.code-input::placeholder {
  color: #64748b;
}

.error-message {
  color: #f87171;
  font-size: 13px;
  margin-top: 12px;
  text-align: center;
  animation: slideIn 0.3s ease;
}

.success-message {
  color: #34d399;
  font-size: 13px;
  margin-top: 12px;
  text-align: center;
  animation: slideIn 0.3s ease;
  font-weight: 600;
}

.modal-footer {
  padding: 16px 20px;
  border-top: 1px solid rgba(148, 163, 184, 0.1);
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.btn-cancel {
  padding: 10px 20px;
  background: #1e293b;
  color: #cbd5e1;
  border: 1px solid rgba(148, 163, 184, 0.2);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 500;
}

.btn-cancel:hover {
  background: #0f172a;
  color: #f8fafc;
}

.btn-join {
  padding: 10px 20px;
  background: linear-gradient(135deg, #22d3ee 0%, #8b5cf6 100%);
  color: #ffffff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 600;
}

.btn-join:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(34, 211, 238, 0.3);
}

.btn-join:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-4px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
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
