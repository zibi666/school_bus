<template>
  <div class="layout-container">
    <!-- Sidebar -->
    <aside class="sidebar">
      <div class="logo">
        包车系统
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
  background: white;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #e5e7eb;
  box-shadow: 4px 0 24px rgba(0,0,0,0.02);
  z-index: 10;
}

.logo {
  padding: 2rem;
  font-size: 1.5rem;
  font-weight: 800;
  color: #7c3aed;
  border-bottom: 1px solid #f3f4f6;
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
  color: #4b5563;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 10px;
}

.nav-item:hover {
  background: #f9fafb;
  color: #7c3aed;
}

.nav-item.active {
  background: #f5f3ff;
  color: #7c3aed;
  font-weight: 600;
}

.logout-section {
  padding: 1.5rem;
  border-top: 1px solid #f3f4f6;
}

.logout-btn {
  width: 100%;
  padding: 10px;
  border: 1px solid #fee2e2;
  background: #fef2f2;
  color: #dc2626;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.logout-btn:hover {
  background: #fee2e2;
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
