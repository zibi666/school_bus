<template>
  <div class="skeleton-bus-card">
    <div class="card-top">
      <div class="skeleton-icon-box skeleton-shimmer"></div>
      <div class="skeleton-status skeleton-shimmer"></div>
    </div>
    
    <div class="bus-info">
      <div class="skeleton-plate skeleton-shimmer"></div>
      <div class="skeleton-row" v-for="i in 3" :key="i">
        <div class="skeleton-label skeleton-shimmer"></div>
        <div class="skeleton-value skeleton-shimmer"></div>
      </div>
    </div>

    <div class="skeleton-btn skeleton-shimmer"></div>
  </div>
</template>

<style scoped>
.skeleton-bus-card {
  background: linear-gradient(135deg, rgba(15, 23, 42, 0.95) 0%, rgba(30, 41, 59, 0.9) 100%);
  border: 1px solid rgba(244, 63, 94, 0.15);
  border-radius: 20px;
  padding: 24px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.25);
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 280px; /* Increased height to match actual card */
  height: 100%; /* Ensure it fills the grid cell */
  box-sizing: border-box;
}

.skeleton-shimmer {
  background: linear-gradient(
    90deg,
    rgba(255, 255, 255, 0.03) 25%,
    rgba(255, 255, 255, 0.08) 37%,
    rgba(255, 255, 255, 0.03) 63%
  );
  background-size: 400% 100%;
  animation: shimmer 1.4s ease infinite;
  border-radius: 6px;
}

@keyframes shimmer {
  0% { background-position: 100% 50%; }
  100% { background-position: 0 50%; }
}

.card-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.skeleton-icon-box {
  width: 52px;
  height: 52px;
  border-radius: 14px;
}

.skeleton-status {
  width: 60px;
  height: 24px;
  border-radius: 20px;
}

.bus-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex: 1;
}

.skeleton-plate {
  width: 120px;
  height: 28px;
  margin-bottom: 8px;
}

.skeleton-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.skeleton-label {
  width: 40px;
  height: 14px;
}

.skeleton-value {
  width: 80px;
  height: 14px;
}

.skeleton-btn {
  width: 100%;
  height: 40px;
  border-radius: 10px;
  margin-top: 8px;
}
</style>
