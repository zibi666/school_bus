<template>
  <div class="charter-container">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>可预约车次</span>
              <div class="card-actions">
                <el-button text type="primary" @click="fetchAvailableTrips">刷新</el-button>
              </div>
            </div>
          </template>
          <el-empty description="暂无可预约车次" v-if="!loading && availableTrips.length === 0" />
          <el-table v-else :data="availableTrips" v-loading="loading" stripe border>
            <el-table-column prop="plateNumber" label="车牌号" width="120">
              <template #default="scope">
                <el-tag effect="dark">{{ scope.row.plateNumber }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="vehicleType" label="车型" width="100" />
            <el-table-column prop="date" label="日期" width="120" sortable />
            <el-table-column prop="startLocation" label="出发地" />
            <el-table-column prop="endLocation" label="目的地" />
            <el-table-column prop="maxSeats" label="可乘人数" width="120">
              <template #default="scope">
                <el-tag type="success" round>{{ scope.row.maxSeats }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="140" fixed="right">
              <template #default="scope">
                <el-button type="primary" size="small" @click="handleBook(scope.row)">
                  立即预约
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card header="加入已预约车次">
          <el-form label-position="top">
            <el-form-item label="邀请码">
              <el-input v-model="inviteCode" placeholder="请输入 6 位邀请码" maxlength="6"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="success" @click="handleJoin" style="width: 100%">加入</el-button>
            </el-form-item>
          </el-form>
          <div class="tips">
            <p>提示：首位预约者会得到邀请码，分享给同乘同学后即可加入该车次。</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAvailableTrips, bookTrip, joinByInviteCode } from '../../api'
import { useRouter } from 'vue-router'

const router = useRouter()
const availableTrips = ref([])
const loading = ref(false)
const inviteCode = ref('')

const fetchAvailableTrips = async () => {
  loading.value = true
  try {
    const res = await getAvailableTrips()
    if (res.code === 200) {
      availableTrips.value = res.data
    } else {
      ElMessage.error(res.message || '获取车次失败')
    }
  } catch (error) {
    ElMessage.error('获取车次失败')
  } finally {
    loading.value = false
  }
}

const handleBook = async (row) => {
  try {
    await ElMessageBox.confirm(`确认要预约 ${row.plateNumber} 吗？系统会自动生成邀请码。`, '确认预约', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await bookTrip(row.plateNumber)
    if (res.code === 200) {
      ElMessage.success(`预约成功！邀请码：${res.data.inviteCode}`)
      router.push('/student/trips')
    } else {
      ElMessage.error(res.message || '预约失败')
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
    const res = await joinByInviteCode(inviteCode.value.trim())
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

onMounted(() => {
  fetchAvailableTrips()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-actions {
  display: flex;
  gap: 8px;
}

.tips {
  margin-top: 20px;
  color: #909399;
  font-size: 14px;
}
</style>
