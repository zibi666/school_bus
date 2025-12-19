<template>
  <div class="skeleton-card">
    <!-- Header -->
    <div class="skeleton-header">
      <div class="skeleton-badge skeleton-shimmer"></div>
      <div class="skeleton-date skeleton-shimmer"></div>
    </div>
    
    <!-- Body -->
    <div class="skeleton-body">
      <div class="skeleton-title skeleton-shimmer"></div>
      
      <div class="skeleton-row" v-for="i in 3" :key="i">
        <div class="skeleton-label skeleton-shimmer"></div>
        <div class="skeleton-value skeleton-shimmer"></div>
      </div>
    </div>

    <!-- Footer -->
    <div class="skeleton-footer">
      <div class="skeleton-btn skeleton-shimmer"></div>
    </div>
  </div>
</template>

<style scoped>
.skeleton-card {
  background: rgba(15, 23, 42, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 18px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  height: auto; /* Changed from 100% to auto to avoid overflow issues */
  min-height: 200px;
  box-sizing: border-box; /* Ensure padding is included in width/height */
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
  border-radius: 4px;
}

@keyframes shimmer {
  0% { background-position: 100% 50%; }
  100% { background-position: 0 50%; }
}

/* Header */
.skeleton-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.skeleton-badge {
  width: 60px;
  height: 24px;
  border-radius: 12px;
}

.skeleton-date {
  width: 100px;
  height: 16px;
}

/* Body */
.skeleton-body {
  flex: 1;
}

.skeleton-title {
  width: 70%;
  height: 28px;
  margin-bottom: 24px;
}

.skeleton-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}

.skeleton-label {
  width: 40px;
  height: 16px;
}

.skeleton-value {
  width: 120px;
  height: 16px;
}

/* Footer */
.skeleton-footer {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  display: flex;
  justify-content: flex-end;
}

.skeleton-btn {
  width: 80px;
  height: 32px;
  border-radius: 8px;
}
</style>
