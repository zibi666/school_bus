<template>
  <div class="network-icon-wrapper" :style="{ width: size + 'px', height: size + 'px' }">
    <img 
      v-show="!loading && !error"
      :src="src" 
      :alt="alt" 
      class="network-icon"
      @load="onLoad"
      @error="onError"
    />
    <div v-if="loading" class="icon-placeholder">
      <div class="loader"></div>
    </div>
    <div v-if="error" class="icon-error" title="图标加载失败">
      <span>?</span>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  src: {
    type: String,
    required: true
  },
  alt: {
    type: String,
    default: 'icon'
  },
  width: {
    type: [Number, String],
    default: 20
  },
  height: {
    type: [Number, String],
    default: 20
  }
})

const loading = ref(true)
const error = ref(false)

// Use the larger dimension for the square wrapper if needed, or just respect width/height
const size = computed(() => Math.max(parseInt(props.width), parseInt(props.height)))

const onLoad = () => {
  loading.value = false
}

const onError = () => {
  loading.value = false
  error.value = true
}
</script>

<style scoped>
.network-icon-wrapper {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.network-icon {
  width: 100%;
  height: 100%;
  object-fit: contain;
  transition: opacity 0.3s ease;
}

.icon-placeholder {
  position: absolute;
  inset: 0;
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.loader {
  width: 50%;
  height: 50%;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: currentColor;
  animation: spin 1s linear infinite;
}

.icon-error {
  position: absolute;
  inset: 0;
  background-color: rgba(255, 0, 0, 0.1);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #f87171;
  font-size: 12px;
  font-weight: bold;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
