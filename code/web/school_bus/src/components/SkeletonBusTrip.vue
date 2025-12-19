<template>
  <div class="skeleton-bus-trip">
    <!-- Header: Route Info -->
    <div class="skeleton-header">
      <div class="skeleton-route">
        <div class="skeleton-dest skeleton-shimmer"></div>
        <div class="skeleton-time skeleton-shimmer"></div>
      </div>
      <div class="skeleton-badge skeleton-shimmer"></div>
    </div>
    
    <!-- Body: Details Grid -->
    <div class="skeleton-body">
      <div class="skeleton-grid">
        <div v-for="i in 4" :key="i" class="skeleton-item">
          <div class="skeleton-label skeleton-shimmer"></div>
          <div class="skeleton-value skeleton-shimmer"></div>
        </div>
      </div>
    </div>

    <!-- Footer: Passengers -->
    <div class="skeleton-footer">
      <div class="skeleton-section-title skeleton-shimmer"></div>
      <div class="skeleton-table">
        <div class="skeleton-th skeleton-shimmer"></div>
        <div v-for="i in 3" :key="i" class="skeleton-tr skeleton-shimmer"></div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.skeleton-bus-trip {
  background: rgba(15, 23, 42, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 18px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  height: auto;
  min-height: 260px; /* Adjusted to better match typical trip card height */
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
  border-radius: 4px;
}

@keyframes shimmer {
  0% { background-position: 100% 50%; }
  100% { background-position: 0 50%; }
}

/* Header */
.skeleton-header {
  padding: 16px 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.skeleton-route {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.skeleton-dest {
  width: 150px;
  height: 24px;
}

.skeleton-time {
  width: 200px;
  height: 14px;
}

.skeleton-badge {
  width: 60px;
  height: 24px;
  border-radius: 20px;
}

/* Body */
.skeleton-body {
  padding: 24px;
}

.skeleton-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.skeleton-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.skeleton-label {
  width: 60px;
  height: 12px;
}

.skeleton-value {
  width: 100px;
  height: 16px;
}

/* Footer */
.skeleton-footer {
  padding: 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  background: rgba(0, 0, 0, 0.2);
}

.skeleton-section-title {
  width: 120px;
  height: 20px;
  margin-bottom: 16px;
}

.skeleton-table {
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  overflow: hidden;
}

.skeleton-th {
  height: 40px;
  width: 100%;
  background: rgba(255, 255, 255, 0.02);
  margin-bottom: 1px;
}

.skeleton-tr {
  height: 48px;
  width: 100%;
  margin-top: 1px;
}
</style>
