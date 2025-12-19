<template>
  <div class="skeleton-table-wrapper">
    <div class="skeleton-table-header">
      <div v-for="i in columns" :key="`head-${i}`" class="skeleton-th skeleton-shimmer"></div>
    </div>
    <div class="skeleton-table-body">
      <div v-for="row in rows" :key="`row-${row}`" class="skeleton-tr">
        <div v-for="col in columns" :key="`cell-${row}-${col}`" class="skeleton-td skeleton-shimmer"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  rows: {
    type: Number,
    default: 5
  },
  columns: {
    type: Number,
    default: 7
  }
})
</script>

<style scoped>
.skeleton-table-wrapper {
  /* Removed container styles to avoid double wrapping in Admin/Trips.vue */
  width: 100%;
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

.skeleton-table-header {
  display: flex;
  padding: 18px 20px;
  background: linear-gradient(135deg, rgba(15, 23, 42, 0.95) 0%, rgba(30, 41, 59, 0.9) 100%);
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  gap: 16px;
}

.skeleton-th {
  height: 20px;
  flex: 1;
}

.skeleton-tr {
  display: flex;
  padding: 0 20px;
  height: 60px;
  align-items: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  gap: 16px;
  box-sizing: border-box;
}

.skeleton-tr:last-child {
  border-bottom: none;
}

.skeleton-td {
  height: 20px;
  flex: 1;
}
</style>