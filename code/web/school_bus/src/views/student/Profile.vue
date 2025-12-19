<template>
  <div class="page-container">
    <LoadingSpinner :loading="saving" :fullscreen="true" text="保存中..." />
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
          <button class="btn-primary" @click="showEditModal = true">修改信息</button>
        </div>
      </div>
    </div>

    <!-- 修改信息弹窗 -->
    <div v-if="showEditModal" class="modal-overlay" @click.self="showEditModal = false">
      <div class="modal-content edit-modal">
        <div class="modal-header">
          <h3>修改个人信息</h3>
          <button class="close-btn" @click="showEditModal = false">×</button>
        </div>

        <div class="modal-body">
          <div class="form-group">
            <label>学号</label>
            <input type="text" :value="profile.studentId" disabled class="disabled-input" />
          </div>

          <div class="form-group">
            <label>姓名</label>
            <input type="text" v-model="editForm.name" placeholder="请输入姓名" />
          </div>

          <div class="form-group">
            <label>所在地</label>
            <input type="text" v-model="editForm.location" placeholder="请输入所在地" />
          </div>

          <div class="form-group">
            <label>密码</label>
            <input type="password" v-model="editForm.password" placeholder="请输入新密码（可选）" />
          </div>
        </div>

        <div class="modal-footer">
          <button class="btn-cancel" @click="showEditModal = false">取消</button>
          <button class="btn-confirm" @click="confirmEdit" :disabled="saving">
            {{ saving ? '保存中...' : '保存' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStudentProfile, updateStudentProfile } from '../../api'
import LoadingSpinner from '../../components/LoadingSpinner.vue'

const fallbackProfile = {
  studentId: '2313001044',
  name: '李强',
  location: '江苏省南京市鼓楼区',
  role: 'student'
}

const profile = ref({})
const showEditModal = ref(false)
const saving = ref(false)
const editForm = ref({
  name: '',
  location: '',
  password: ''
})

const getAvatarText = (name) => {
  return name ? name.charAt(0) : 'U'
}

const confirmEdit = async () => {
  if (!editForm.value.name.trim()) {
    alert('姓名不能为空')
    return
  }

  saving.value = true
  try {
    const updateData = {
      name: editForm.value.name,
      location: editForm.value.location
    }
    
    // 如果输入了新密码，才更新密码
    if (editForm.value.password.trim()) {
      updateData.password = editForm.value.password
    }

    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const res = await updateStudentProfile(userInfo.studentId, updateData)

    if (res.code === 200) {
      // 更新本地显示
      profile.value.name = editForm.value.name
      profile.value.location = editForm.value.location

      alert('修改成功')
      showEditModal.value = false
    } else {
      alert(res.message || '修改失败')
    }
  } catch (error) {
    console.error('updateStudentProfile failed', error)
    alert('修改异常')
  } finally {
    saving.value = false
  }
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
  height: 100%;
  overflow-y: auto;
}

.profile-wrapper {
  width: 100%;
  max-width: 400px;
  perspective: 1000px;
  margin: auto;
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

/* 修改信息弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
  box-sizing: border-box;
}

.modal-content {
  background: rgba(15, 23, 42, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 24px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(16px);
}

.edit-modal {
  background: linear-gradient(135deg, rgba(15, 23, 42, 0.96) 0%, rgba(20, 28, 48, 0.96) 100%);
  border: 1px solid rgba(34, 211, 238, 0.2);
  box-shadow: 0 20px 60px rgba(34, 211, 238, 0.15), inset 0 1px 0 rgba(255, 255, 255, 0.05);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.modal-header h3 {
  margin: 0;
  color: #f8fafc;
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 0.5px;
}

.close-btn {
  background: none;
  border: none;
  color: #94a3b8;
  font-size: 28px;
  cursor: pointer;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.2s ease;
}

.close-btn:hover {
  color: #f8fafc;
}

.modal-body {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  color: #e2e8f0;
  font-size: 14px;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.3px;
}

.form-group input {
  width: 100%;
  padding: 12px 14px;
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(0, 0, 0, 0.3);
  color: #f8fafc;
  font-size: 14px;
  box-sizing: border-box;
  transition: all 0.2s ease;
}

.form-group input::placeholder {
  color: #64748b;
}

.form-group input:focus {
  outline: none;
  border-color: rgba(34, 211, 238, 0.5);
  background: rgba(34, 211, 238, 0.1);
  box-shadow: 0 0 0 3px rgba(34, 211, 238, 0.1);
}

.form-group input.disabled-input {
  background: rgba(0, 0, 0, 0.5);
  border-color: rgba(255, 255, 255, 0.04);
  color: #94a3b8;
  cursor: not-allowed;
}

.form-group input.disabled-input:focus {
  border-color: rgba(255, 255, 255, 0.04);
  background: rgba(0, 0, 0, 0.5);
  box-shadow: none;
}

.modal-footer {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.btn-cancel,
.btn-confirm {
  padding: 12px 20px;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.btn-cancel {
  background: rgba(255, 255, 255, 0.08);
  color: #e2e8f0;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.btn-cancel:hover {
  background: rgba(255, 255, 255, 0.12);
  border-color: rgba(255, 255, 255, 0.2);
}

.btn-confirm {
  background: linear-gradient(135deg, #22d3ee 0%, #8b5cf6 100%);
  color: #ffffff;
  box-shadow: 0 4px 12px rgba(34, 211, 238, 0.3);
  position: relative;
  overflow: hidden;
}

.btn-confirm::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.btn-confirm:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(34, 211, 238, 0.4);
}

.btn-confirm:hover:not(:disabled)::before {
  left: 100%;
}

.btn-confirm:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}
</style>
