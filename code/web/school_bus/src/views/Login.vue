<template>
  <div class="login-container">
    <div class="login-content">
      <div class="login-header">
        <h1 class="title">学生汽车包车预定系统</h1>
        <p class="subtitle">便捷 · 安全 · 舒适</p>
      </div>
      <el-card class="login-card" shadow="always">
        <el-tabs v-model="activeTab" class="custom-tabs">
          <el-tab-pane label="学生登录" name="student">
            <el-form :model="studentForm" label-width="0" size="large">
              <el-form-item>
                <el-input v-model="studentForm.studentId" placeholder="请输入学号" :prefix-icon="User" />
              </el-form-item>
              <el-form-item>
                <el-input v-model="studentForm.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" show-password />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" class="full-width-btn" @click="handleStudentLogin">登录</el-button>
              </el-form-item>
              <div class="form-footer">
                <el-button link type="primary" @click="$router.push('/register')">没有账号？去注册</el-button>
              </div>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="管理员登录" name="admin">
            <el-form :model="adminForm" label-width="0" size="large">
              <el-form-item>
                <el-input v-model="adminForm.username" placeholder="请输入账号" :prefix-icon="UserFilled" />
              </el-form-item>
              <el-form-item>
                <el-input v-model="adminForm.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" show-password />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" class="full-width-btn" @click="handleAdminLogin">登录</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, UserFilled } from '@element-plus/icons-vue'
import { loginStudent, loginAdmin } from '../api'

const router = useRouter()
const activeTab = ref('student')

const studentForm = ref({
  studentId: '',
  password: ''
})

const adminForm = ref({
  username: '',
  password: ''
})

const handleStudentLogin = async () => {
  try {
    // 本地测试账号：student / 123456（无需访问后端）
    if (studentForm.value.studentId === 'student' && studentForm.value.password === '123456') {
      const mockUser = {
        role: 'student',
        studentId: 2021001,
        name: '测试学生',
        gender: '男',
        clazz: '测试班级'
      }
      localStorage.setItem('role', mockUser.role)
      localStorage.setItem('userInfo', JSON.stringify(mockUser))
      ElMessage.success('测试账号登录成功（本地模拟）')
      router.push('/student/charter')
      return
    }

    const res = await loginStudent(studentForm.value)
    if (res.code === 200) {
      const userInfo = res.data.userInfo || {}
      localStorage.setItem('role', userInfo.role || 'student')
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
      ElMessage.success('登录成功')
      router.push('/student/charter')
    } else {
      ElMessage.error(res.message || '登录失败')
    }
  } catch (error) {
    ElMessage.error('登录异常')
  }
}

const handleAdminLogin = async () => {
  try {
    // 本地测试账号：admin / 123456（无需访问后端）
    if (adminForm.value.username === 'admin' && adminForm.value.password === '123456') {
      const mockUser = {
        role: 'admin',
        adminId: 9001,
        name: '测试管理员'
      }
      localStorage.setItem('role', mockUser.role)
      localStorage.setItem('userInfo', JSON.stringify(mockUser))
      ElMessage.success('测试管理员登录成功（本地模拟）')
      router.push('/admin/trips')
      return
    }

    const res = await loginAdmin(adminForm.value)
    if (res.code === 200) {
      const userInfo = res.data.userInfo || {}
      localStorage.setItem('role', userInfo.role || 'admin')
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
      ElMessage.success('登录成功')
      router.push('/admin/trips')
    } else {
      ElMessage.error(res.message || '登录失败')
    }
  } catch (error) {
    ElMessage.error('登录异常')
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-size: cover;
}

.login-content {
  text-align: center;
  width: 100%;
  max-width: 420px;
  padding: 20px;
}

.login-header {
  margin-bottom: 30px;
  color: white;
}

.title {
  font-size: 28px;
  font-weight: 600;
  margin: 0 0 10px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.2);
}

.subtitle {
  font-size: 16px;
  opacity: 0.8;
  margin: 0;
}

.login-card {
  border-radius: 12px;
  border: none;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
}

.custom-tabs :deep(.el-tabs__nav-wrap::after) {
  height: 1px;
  background-color: #e4e7ed;
}

.full-width-btn {
  width: 100%;
  font-weight: bold;
  letter-spacing: 1px;
}

.form-footer {
  text-align: right;
  margin-top: -10px;
}
</style>
