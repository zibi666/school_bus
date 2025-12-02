<template>
  <router-view />
</template>

<style>
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '????', Arial, sans-serif;
  background-color: #f0f2f5;
  color: #303133;
}
#app {
  height: 100%;
}

/* Global transition for cards */
.el-card {
  transition: all 0.3s;
}
.el-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
</style>
