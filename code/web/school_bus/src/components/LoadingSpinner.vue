<template>
  <transition name="fade">
    <div v-if="loading" class="loading-container" :class="{ 'fullscreen': fullscreen, 'inline': !fullscreen }">
      <div class="spinner-box">
        <div class="spinner"></div>
        <div v-if="text" class="loading-text">{{ text }}</div>
      </div>
    </div>
  </transition>
</template>

<script setup>
defineProps({
  loading: {
    type: Boolean,
    default: false
  },
  fullscreen: {
    type: Boolean,
    default: false
  },
  text: {
    type: String,
    default: ''
  }
})
</script>

<style scoped>
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.loading-container.fullscreen {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(15, 23, 42, 0.6);
  backdrop-filter: blur(4px);
}

.loading-container.inline {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.8);
  border-radius: inherit;
}

.spinner-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  border-top-color: #f43f5e;
  animation: spin 1s ease-in-out infinite;
}

.inline .spinner {
  border-color: rgba(244, 63, 94, 0.2);
  border-top-color: #f43f5e;
}

.loading-text {
  color: white;
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.5px;
}

.inline .loading-text {
  color: #1e293b;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
