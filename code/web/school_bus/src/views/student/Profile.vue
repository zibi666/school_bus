<template>
  <div class="profile-container">
    <el-card class="profile-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
        </div>
      </template>
      <div class="profile-content">
        <div class="avatar-section">
          <el-avatar :size="100" :icon="UserFilled" class="avatar" />
          <h3>{{ profile.name }}</h3>
          <el-tag>{{ profile.role === 'student' ? '学生' : '管理员' }}</el-tag>
        </div>
        <el-descriptions :column="1" border size="large" class="info-list">
          <el-descriptions-item label="学号">{{ profile.studentId }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ profile.name }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ profile.gender }}</el-descriptions-item>
          <el-descriptions-item label="班级">{{ profile.clazz }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStudentProfile } from '../../api'
import { ElMessage } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'

const profile = ref({})

onMounted(async () => {
  try {
    const res = await getStudentProfile()
    if (res.code === 200) {
      profile.value = res.data
    }
  } catch (error) {
    ElMessage.error('获取个人信息失败')
  }
})
</script>

<style scoped>
.profile-container {
  display: flex;
  justify-content: center;
  padding-top: 40px;
}
.profile-card {
  width: 600px;
}
.profile-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.avatar-section {
  text-align: center;
  margin-bottom: 30px;
}
.avatar {
  background-color: #409eff;
  margin-bottom: 10px;
}
.info-list {
  width: 100%;
}
</style>
