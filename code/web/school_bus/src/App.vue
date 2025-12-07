<template>
  <div class="app-shell">
    <div class="glow glow-1"></div>
    <div class="glow glow-2"></div>
    <div class="glow glow-3"></div>
    <main class="page-shell glass-wrap">
      <router-view />
    </main>
  </div>
</template>

<style>
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
}

.app-shell {
  position: relative;
  min-height: 100vh;
}

.glass-wrap {
  position: relative;
  z-index: 2;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 24px;
  padding: 28px;
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.25);
}

.glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.45;
  z-index: 0;
}

.glow-1 {
  width: 360px;
  height: 360px;
  background: #8b5cf6;
  top: -120px;
  left: -80px;
}

.glow-2 {
  width: 420px;
  height: 420px;
  background: #22d3ee;
  bottom: -160px;
  right: -120px;
}

.glow-3 {
  width: 300px;
  height: 300px;
  background: #ec4899;
  top: 40%;
  left: 40%;
}

.el-card {
  transition: transform 0.25s ease, box-shadow 0.25s ease;
}

.el-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 14px 45px rgba(0, 0, 0, 0.2);
}
</style>
