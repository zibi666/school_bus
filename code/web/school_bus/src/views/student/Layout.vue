<template>
  <el-container class="layout-container">
    <el-header class="header">
      <div class="logo">学生包车系统 - 学生端</div>
      <div class="user-info">
        <span>欢迎, {{ userInfo.name }}</span>
        <el-button type="danger" size="small" @click="logout" style="margin-left: 10px">退出</el-button>
      </div>
    </el-header>
    <el-container>
      <el-aside width="200px">
        <el-menu router :default-active="$route.path">
          <el-menu-item index="/student/charter">
            <el-icon><Van /></el-icon>
            <span>我要包车</span>
          </el-menu-item>
          <el-menu-item index="/student/trips">
            <el-icon><Ticket /></el-icon>
            <span>我的行程</span>
          </el-menu-item>
          <el-menu-item index="/student/profile">
            <el-icon><User /></el-icon>
            <span>个人中心</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Van, Ticket, User } from '@element-plus/icons-vue'

const router = useRouter()
const userInfo = ref({})

onMounted(() => {
  const info = localStorage.getItem('userInfo')
  if (info) {
    userInfo.value = JSON.parse(info)
  } else {
    router.push('/login')
  }
})

const logout = () => {
  localStorage.clear()
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.header {
  background: linear-gradient(90deg, #409eff 0%, #66b1ff 100%);
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  z-index: 10;
}
.logo {
  font-size: 20px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 10px;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}
.el-aside {
  background-color: #fff;
  border-right: 1px solid #e6e6e6;
  box-shadow: 2px 0 8px rgba(0,0,0,0.05);
}
.el-menu {
  border-right: none;
}
.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
