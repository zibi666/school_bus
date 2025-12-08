<template>
  <div class="page-container">
    <div class="profile-wrapper">
      <div class="profile-card">
        <div class="profile-header">
          <div class="avatar-container">
            <div class="avatar-glow"></div>
            <div class="avatar-circle">
              <span class="avatar-text">{{ getAvatarText(profile.name) }}</span>
            </div>
          </div>
          <h2 class="user-name">{{ profile.name || '加载中...' }}</h2>
          <span class="user-role">{{ profile.role === 'student' ? '学生' : '管理员' }}</span>
        </div>

        <div class="info-section">
          <div class="info-item">
            <span class="label">学号</span>
            <span class="value">{{ profile.studentId }}</span>
          </div>
          <div class="info-item">
            <span class="label">所在地</span>
            <span class="value">{{ profile.location }}</span>
          </div>
          <div class="info-item">
            <span class="label">账户状态</span>
            <span class="value status-active">正常</span>
          </div>
        </div>

        <div class="actions">
          <button class="btn-primary">修改信息</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStudentProfile } from '../../api'

const fallbackProfile = {
  studentId: '2313001044',
  name: '李强',
  location: '江苏省南京市鼓楼区',
  role: 'student'
}

const profile = ref({})

const getAvatarText = (name) => {
  return name ? name.charAt(0) : 'U'
}

onMounted(async () => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')

  if (!userInfo.studentId) {
    profile.value = fallbackProfile
    return
  }

  try {
    const res = await getStudentProfile(userInfo.studentId)
    if (res.code === 200) {
      profile.value = {
        ...res.data,
        role: 'student'
      }
    } else {
      profile.value = fallbackProfile
    }
  } catch (error) {
    console.error('getStudentProfile failed', error)
    profile.value = fallbackProfile
  }
})
</script>

<style scoped>
.page-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
}

.profile-wrapper {
  width: 100%;
  max-width: 400px;
  perspective: 1000px;
}

.profile-card {
  background: rgba(15, 23, 42, 0.8);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 24px;
  padding: 40px 30px;
  text-align: center;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.3);
  position: relative;
  overflow: hidden;
}

.profile-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 120px;
  background: linear-gradient(180deg, rgba(34, 211, 238, 0.1) 0%, transparent 100%);
  pointer-events: none;
}

.avatar-container {
  position: relative;
  width: 100px;
  height: 100px;
  margin: 0 auto 20px;
}

.avatar-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 120px;
  height: 120px;
  background: radial-gradient(circle, rgba(34, 211, 238, 0.4) 0%, transparent 70%);
  border-radius: 50%;
  animation: pulse 3s infinite;
}

.avatar-circle {
  position: relative;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #22d3ee 0%, #0ea5e9 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(34, 211, 238, 0.3);
  border: 4px solid rgba(15, 23, 42, 0.8);
}

.avatar-text {
  font-size: 36px;
  font-weight: bold;
  color: #fff;
}

.user-name {
  margin: 0 0 8px;
  color: #f8fafc;
  font-size: 24px;
}

.user-role {
  display: inline-block;
  padding: 4px 12px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  font-size: 12px;
  color: #94a3b8;
  margin-bottom: 30px;
}

.info-section {
  text-align: left;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 24px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.info-item:last-child {
  border-bottom: none;
}

.label {
  color: #94a3b8;
  font-size: 14px;
}

.value {
  color: #f8fafc;
  font-weight: 500;
}

.status-active {
  color: #34d399;
}

.full-width {
  width: 100%;
}

.actions {
  margin-top: 24px;
}

.btn-primary {
  width: 100%;
  padding: 14px 24px;
  background: linear-gradient(135deg, #f97316 0%, #ec4899 100%);
  color: #ffffff;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 16px rgba(249, 115, 22, 0.3);
  letter-spacing: 0.5px;
  position: relative;
  overflow: hidden;
}

.btn-primary::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.5s ease;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(249, 115, 22, 0.4);
}

.btn-primary:hover::before {
  left: 100%;
}

.btn-primary:active {
  transform: translateY(0);
}

@keyframes pulse {
  0% { transform: translate(-50%, -50%) scale(0.95); opacity: 0.5; }
  50% { transform: translate(-50%, -50%) scale(1.1); opacity: 0.8; }
  100% { transform: translate(-50%, -50%) scale(0.95); opacity: 0.5; }
}
</style>
