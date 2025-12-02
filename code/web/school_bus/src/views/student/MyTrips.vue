<template>
  <div class="trips-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>我的行程</span>
        </div>
      </template>
      <el-table :data="trips" style="width: 100%" stripe border>
        <el-table-column prop="plateNumber" label="车牌号" width="120">
          <template #default="scope">
            <el-tag effect="dark">{{ scope.row.plateNumber }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="vehicleType" label="车型" width="100" />
        <el-table-column prop="date" label="日期" width="120" sortable />
        <el-table-column prop="startLocation" label="出发地" />
        <el-table-column prop="endLocation" label="目的地" />
        <el-table-column prop="seatNumber" label="座位号" width="80" align="center">
          <template #default="scope">
            <span style="font-weight: bold; color: #409eff">{{ scope.row.seatNumber }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="driverName" label="司机" width="100" />
        <el-table-column prop="driverPhone" label="司机电话" width="120" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="scope">
            ¥{{ scope.row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="inviteCode" label="邀请码" width="100">
          <template #default="scope">
            <el-tag type="warning" v-if="scope.row.inviteCode">{{ scope.row.inviteCode }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button type="danger" size="small" plain @click="handleRefund(scope.row)">退票</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyTrips, refundTicket } from '../../api'

const trips = ref([])

const fetchTrips = async () => {
  try {
    const res = await getMyTrips()
    if (res.code === 200) {
      trips.value = res.data
    }
  } catch (error) {
    ElMessage.error('获取行程失败')
  }
}

const handleRefund = async (row) => {
  try {
    await ElMessageBox.confirm('确定要退票吗？如果是包车人，整车将被取消。', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await refundTicket(row.plateNumber) // Assuming plateNumber or ticketId is used
    if (res.code === 200) {
      ElMessage.success('退票成功')
      fetchTrips()
    } else {
      ElMessage.error(res.message || '退票失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

onMounted(() => {
  fetchTrips()
})
</script>
