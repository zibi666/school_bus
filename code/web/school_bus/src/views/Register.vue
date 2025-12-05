<template>
  <div class="register-container">
    <div class="register-content">
      <div class="register-header">
        <h1 class="title">欢迎加入</h1>
        <p class="subtitle">注册您的账号以开始使用</p>
      </div>
      <el-card class="register-card" shadow="always">
        <h3 style="text-align: center; margin-bottom: 20px; color: #333;">学生注册</h3>
        <el-form :model="studentForm" label-width="60px" size="large">
          <el-form-item label="学号">
            <el-input v-model="studentForm.studentId" placeholder="请输入学号" :prefix-icon="User" />
          </el-form-item>
          <el-form-item label="姓名">
            <el-input v-model="studentForm.name" placeholder="请输入姓名" :prefix-icon="User" />
          </el-form-item>
          <el-form-item label="所在地">
            <el-input v-model="studentForm.location" placeholder="请输入所在地 (如: 南校区)" :prefix-icon="School" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="studentForm.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" class="full-width-btn" @click="handleStudentRegister">注册</el-button>
          </el-form-item>
          <div class="form-footer">
            <el-button link type="primary" @click="$router.push('/login')">已有账号？去登录</el-button>
          </div>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, School } from '@element-plus/icons-vue'
import { registerStudent } from '../api'

const router = useRouter()

const studentForm = ref({
  studentId: '',
  name: '',
  location: '',
  password: ''
})

const handleStudentRegister = async () => {
  if (!studentForm.value.studentId || !studentForm.value.password) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    const res = await registerStudent(studentForm.value)
    if (res.code === 200) {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } else {
      ElMessage.error(res.message || '注册失败')
    }
  } catch (error) {
    ElMessage.error('注册异常')
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-size: cover;
  padding: 20px;
}

.register-content {
  text-align: center;
  width: 100%;
  max-width: 480px;
}

.register-header {
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

.register-card {
  border-radius: 12px;
  border: none;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
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
