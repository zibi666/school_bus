<template>
  <div class="layout-container">
    <!-- Sidebar -->
    <aside class="sidebar">
      <div class="logo-area">
        <div class="logo-icon">A</div>
        <span class="logo-text">管理后台</span>
      </div>
      
      <nav class="nav-menu">
        <router-link to="/admin/trips" class="nav-item" active-class="active">
          <span class="icon">📋</span>
          <span class="text">订单审核</span>
        </router-link>
        <router-link to="/admin/drivers" class="nav-item" active-class="active">
          <span class="icon">🚌</span>
          <span class="text">车辆管理</span>
        </router-link>
      </nav>

      <div class="user-profile">
        <div class="user-info">
          <span class="user-name">管理员</span>
          <span class="user-role">Admin</span>
        </div>
        <button @click="logout" class="logout-btn" title="退出登录">
          ⏻
        </button>
      </div>
    </aside>

    <!-- Main Content -->
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const logout = () => {
  localStorage.clear()
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  display: flex;
  height: 100vh;
  background: var(--bg-primary);
  overflow: hidden;
}

.sidebar {
  width: 260px;
  background: rgba(15, 23, 42, 0.6);
  backdrop-filter: blur(20px);
  border-right: 1px solid rgba(255, 255, 255, 0.05);
  display: flex;
  flex-direction: column;
  z-index: 10;
}

.logo-area {
  padding: 30px 24px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #f43f5e 0%, #e11d48 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  color: white;
  font-size: 20px;
  box-shadow: 0 0 15px rgba(244, 63, 94, 0.4);
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: #f8fafc;
  letter-spacing: 0.5px;
}

.nav-menu {
  flex: 1;
  padding: 20px 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border-radius: 14px;
  color: #94a3b8;
  text-decoration: none;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.03);
  color: #e2e8f0;
}

.nav-item.active {
  background: rgba(244, 63, 94, 0.1);
  color: #f43f5e;
  border-color: rgba(244, 63, 94, 0.2);
  box-shadow: 0 0 20px rgba(244, 63, 94, 0.1);
}

.icon {
  font-size: 18px;
}

.text {
  font-weight: 500;
}

.user-profile {
  padding: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(0, 0, 0, 0.2);
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  color: #f8fafc;
  font-size: 14px;
  font-weight: 600;
}

.user-role {
  color: #64748b;
  font-size: 12px;
}

.logout-btn {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: transparent;
  color: #94a3b8;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logout-btn:hover {
  background: rgba(255, 255, 255, 0.05);
  color: #f43f5e;
  border-color: rgba(244, 63, 94, 0.3);
}

.main-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  position: relative;
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
