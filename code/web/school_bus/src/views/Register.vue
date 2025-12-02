<template>
  <div class="register-container">
    <div class="register-content">
      <div class="register-header">
        <h1 class="title">欢迎加入</h1>
        <p class="subtitle">注册您的账号以开始使用</p>
      </div>
      <el-card class="register-card" shadow="always">
        <el-tabs v-model="activeTab" class="custom-tabs">
          <el-tab-pane label="学生注册" name="student">
            <el-form :model="studentForm" label-width="60px" size="large">
              <el-form-item label="学号">
                <el-input v-model="studentForm.studentId" placeholder="请输入学号" :prefix-icon="User" />
              </el-form-item>
              <el-form-item label="姓名">
                <el-input v-model="studentForm.name" placeholder="请输入姓名" :prefix-icon="User" />
              </el-form-item>
              <el-form-item label="性别">
                <el-select v-model="studentForm.gender" placeholder="请选择性别" style="width: 100%">
                  <el-option label="男" value="男"></el-option>
                  <el-option label="女" value="女"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="班级">
                <el-input v-model="studentForm.clazz" placeholder="请输入班级" :prefix-icon="School" />
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
          </el-tab-pane>
          <el-tab-pane label="管理员注册" name="admin">
            <el-form :model="adminForm" label-width="60px" size="large">
              <el-form-item label="账号">
                <el-input v-model="adminForm.username" placeholder="请输入账号" :prefix-icon="UserFilled" />
              </el-form-item>
              <el-form-item label="姓名">
                <el-input v-model="adminForm.name" placeholder="请输入姓名" :prefix-icon="User" />
              </el-form-item>
              <el-form-item label="密码">
                <el-input v-model="adminForm.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" show-password />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" class="full-width-btn" @click="handleAdminRegister">注册</el-button>
              </el-form-item>
              <div class="form-footer">
                <el-button link type="primary" @click="$router.push('/login')">已有账号？去登录</el-button>
              </div>
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
import { User, Lock, UserFilled, School } from '@element-plus/icons-vue'
import { registerStudent, registerAdmin } from '../api'

const router = useRouter()
const activeTab = ref('student')

const studentForm = ref({
  studentId: '',
  name: '',
  gender: '',
  clazz: '',
  password: ''
})

const adminForm = ref({
  username: '',
  name: '',
  password: ''
})

const handleStudentRegister = async () => {
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

const handleAdminRegister = async () => {
  try {
    const res = await registerAdmin(adminForm.value)
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
