<template>
  <div class="trips-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>车次管理</span>
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
        <el-table-column prop="passengerCount" label="人数" width="80" align="center">
          <template #default="scope">
            <el-tag type="info" round>{{ scope.row.passengerCount }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="driverName" label="司机" width="100" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewPassengers(scope.row)" plain>查看乘客</el-button>
            <el-button type="primary" size="small" @click="openChangeDriver(scope.row)" plain>修改司机</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Passengers Dialog -->
    <el-dialog v-model="passengersVisible" title="乘车人员信息" width="600px">
      <el-table :data="passengers" border stripe>
        <el-table-column prop="seatNumber" label="座位号" width="100" align="center" />
        <el-table-column prop="studentId" label="学号" />
        <el-table-column prop="studentName" label="姓名" />
      </el-table>
    </el-dialog>

    <!-- Change Driver Dialog -->
    <el-dialog v-model="driverVisible" title="修改司机" width="500px">
      <el-form label-position="top">
        <el-form-item label="选择司机">
          <el-select v-model="selectedDriver" placeholder="请选择司机" style="width: 100%" size="large">
            <el-option
              v-for="item in drivers"
              :key="item.phone"
              :label="item.name + ' (' + item.phone + ')'"
              :value="item.phone"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="driverVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmChangeDriver">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAllTrips, getTripPassengers, updateTripDriver, getAllDrivers } from '../../api'

const trips = ref([])
const passengers = ref([])
const drivers = ref([])
const passengersVisible = ref(false)
const driverVisible = ref(false)
const currentTrip = ref(null)
const selectedDriver = ref('')

const fetchTrips = async () => {
  try {
    const res = await getAllTrips()
    if (res.code === 200) {
      trips.value = res.data
    }
  } catch (error) {
    ElMessage.error('获取车次失败')
  }
}

const viewPassengers = async (row) => {
  try {
    const res = await getTripPassengers(row.plateNumber)
    if (res.code === 200) {
      passengers.value = res.data
      passengersVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取乘客信息失败')
  }
}

const openChangeDriver = async (row) => {
  currentTrip.value = row
  try {
    const res = await getAllDrivers()
    if (res.code === 200) {
      drivers.value = res.data
      selectedDriver.value = row.driverPhone // Assuming row has driverPhone
      driverVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取司机列表失败')
  }
}

const confirmChangeDriver = async () => {
  if (!selectedDriver.value) return
  try {
    const res = await updateTripDriver(currentTrip.value.plateNumber, selectedDriver.value)
    if (res.code === 200) {
      ElMessage.success('修改成功')
      driverVisible.value = false
      fetchTrips()
    } else {
      ElMessage.error(res.message || '修改失败')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  fetchTrips()
})
</script>
