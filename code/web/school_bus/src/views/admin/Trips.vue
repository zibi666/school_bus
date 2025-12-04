<template>
  <div class="trips-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card header="新增车次">
          <el-form :model="createForm" label-width="90px">
            <el-form-item label="车牌号">
              <el-input v-model="createForm.plateNumber" placeholder="如 京A12345" />
            </el-form-item>
            <el-form-item label="车型">
              <el-input v-model="createForm.vehicleType" placeholder="如 大巴/商务车" />
            </el-form-item>
            <el-form-item label="用车日期">
              <el-date-picker
                v-model="createForm.date"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item label="出发地">
              <el-input v-model="createForm.startLocation" />
            </el-form-item>
            <el-form-item label="目的地">
              <el-input v-model="createForm.endLocation" />
            </el-form-item>
            <el-form-item label="座位数">
              <el-input-number v-model="createForm.maxSeats" :min="1" :max="80" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="creating" @click="handleCreateTrip" style="width: 100%">
                创建车次
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>车次管理</span>
              <el-button text type="primary" @click="fetchTrips">刷新</el-button>
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
            <el-table-column prop="passengerCount" label="已预约" width="90" align="center">
              <template #default="scope">
                <el-tag type="info" round>{{ scope.row.passengerCount }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="maxSeats" label="总座位" width="90" />
            <el-table-column prop="remainingSeats" label="剩余" width="80">
              <template #default="scope">
                <el-tag :type="scope.row.remainingSeats > 0 ? 'success' : 'danger'" round>
                  {{ scope.row.remainingSeats }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="driverName" label="司机" width="100" />
            <el-table-column prop="driverPhone" label="司机电话" width="130" />
            <el-table-column label="操作" width="220" fixed="right">
              <template #default="scope">
                <el-button size="small" @click="viewPassengers(scope.row)" plain>查看乘客</el-button>
                <el-button type="primary" size="small" @click="openChangeDriver(scope.row)" plain>修改司机</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- Passengers Dialog -->
    <el-dialog v-model="passengersVisible" title="乘车人员信息" width="600px">
      <el-table :data="passengers" border stripe>
        <el-table-column prop="seatNumber" label="座位号" width="100" align="center" />
        <el-table-column prop="studentId" label="学号" />
        <el-table-column prop="name" label="姓名" />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { createTrip, getAllTrips, getTripPassengers, updateTripDriver, getAllDrivers } from '../../api'

const trips = ref([])
const passengers = ref([])
const drivers = ref([])
const passengersVisible = ref(false)
const driverVisible = ref(false)
const currentTrip = ref(null)
const selectedDriver = ref('')
const creating = ref(false)

const createForm = reactive({
  plateNumber: '',
  vehicleType: '',
  date: '',
  startLocation: '',
  endLocation: '',
  maxSeats: 40
})

const resetCreateForm = () => {
  createForm.plateNumber = ''
  createForm.vehicleType = ''
  createForm.date = ''
  createForm.startLocation = ''
  createForm.endLocation = ''
  createForm.maxSeats = 40
}

const handleCreateTrip = async () => {
  if (!createForm.plateNumber || !createForm.vehicleType || !createForm.date) {
    ElMessage.warning('请完整填写车次信息')
    return
  }
  creating.value = true
  try {
    const res = await createTrip(createForm)
    if (res.code === 200) {
      ElMessage.success('创建成功')
      resetCreateForm()
      fetchTrips()
    } else {
      ElMessage.error(res.message || '创建失败')
    }
  } catch (error) {
    ElMessage.error('创建失败')
  } finally {
    creating.value = false
  }
}

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
      selectedDriver.value = row.driverPhone
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
