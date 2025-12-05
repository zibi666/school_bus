<template>
  <div class="layout-container">
    <!-- Sidebar -->
    <aside class="sidebar">
      <div class="logo">
        管理后台
      </div>
      <nav class="nav-menu">
        <router-link to="/admin/trips" class="nav-item" active-class="active">
          订单审核
        </router-link>
        <router-link to="/admin/drivers" class="nav-item" active-class="active">
          车辆管理
        </router-link>
      </nav>
      <div class="logout-section">
        <button @click="logout" class="logout-btn">退出登录</button>
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
  background: #f3f4f6;
}

.sidebar {
  width: 260px;
  background: #1f2937; /* Dark sidebar for admin */
  color: white;
  display: flex;
  flex-direction: column;
  box-shadow: 4px 0 24px rgba(0,0,0,0.05);
  z-index: 10;
}

.logo {
  padding: 2rem;
  font-size: 1.5rem;
  font-weight: 800;
  color: white;
  border-bottom: 1px solid #374151;
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
  color: #9ca3af;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 10px;
}

.nav-item:hover {
  background: #374151;
  color: white;
}

.nav-item.active {
  background: #4b5563;
  color: white;
  font-weight: 600;
}

.logout-section {
  padding: 1.5rem;
  border-top: 1px solid #374151;
}

.logout-btn {
  width: 100%;
  padding: 10px;
  border: 1px solid #4b5563;
  background: transparent;
  color: #9ca3af;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.logout-btn:hover {
  background: #374151;
  color: white;
}

.main-content {
  flex: 1;
  overflow-y: auto;
  padding: 2rem;
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
