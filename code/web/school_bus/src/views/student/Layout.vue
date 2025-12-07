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
