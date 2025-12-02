<template>
  <div class="charter-container">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card header="我要包车">
          <el-form :model="charterForm" label-width="100px">
            <el-form-item label="用车日期">
              <el-date-picker v-model="charterForm.date" type="date" placeholder="选择日期" value-format="YYYY-MM-DD"></el-date-picker>
            </el-form-item>
            <el-form-item label="出发地">
              <el-input v-model="charterForm.startLocation" placeholder="请输入出发地"></el-input>
            </el-form-item>
            <el-form-item label="目的地">
              <el-input v-model="charterForm.endLocation" placeholder="请输入目的地"></el-input>
            </el-form-item>
            <el-form-item label="车型">
              <el-radio-group v-model="charterForm.vehicleType">
                <el-radio label="客车">客车 (48人, ¥3888.88)</el-radio>
                <el-radio label="巴士">巴士 (22人, ¥2999.99)</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleCharter">确认包车</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card header="加入包车">
          <el-form :inline="true">
            <el-form-item label="邀请码">
              <el-input v-model="inviteCode" placeholder="请输入6位邀请码"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="success" @click="handleJoin">加入</el-button>
            </el-form-item>
          </el-form>
          <div class="tips">
            <p>提示：输入同学分享的邀请码即可加入对应的车辆。</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { charterBus, joinByInviteCode } from '../../api'
import { useRouter } from 'vue-router'

const router = useRouter()

const charterForm = ref({
  date: '',
  startLocation: '',
  endLocation: '',
  vehicleType: '客车'
})

const inviteCode = ref('')

const handleCharter = async () => {
  try {
    await ElMessageBox.confirm('确定要支付并包车吗？(模拟支付)', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await charterBus(charterForm.value)
    if (res.code === 200) {
      ElMessage.success('包车成功！邀请码：' + res.data.inviteCode)
      router.push('/student/trips')
    } else {
      ElMessage.error(res.message || '包车失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const handleJoin = async () => {
  if (!inviteCode.value) {
    ElMessage.warning('请输入邀请码')
    return
  }
  try {
    const res = await joinByInviteCode(inviteCode.value)
    if (res.code === 200) {
      ElMessage.success('加入成功')
      router.push('/student/trips')
    } else {
      ElMessage.error(res.message || '加入失败')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}
</script>

<style scoped>
.tips {
  margin-top: 20px;
  color: #909399;
  font-size: 14px;
}
</style>
