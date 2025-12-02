<template>
  <div class="drivers-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>司机管理</span>
          <el-button type="primary" @click="dialogVisible = true" :icon="Plus">新增司机</el-button>
        </div>
      </template>
      <el-table :data="drivers" style="width: 100%" stripe border>
        <el-table-column prop="name" label="姓名" width="180">
          <template #default="scope">
            <div style="display: flex; align-items: center">
              <el-avatar :size="30" :icon="UserFilled" style="margin-right: 10px" />
              {{ scope.row.name }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="电话号码" />
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="新增司机" width="500px">
      <el-form :model="form" label-width="80px" size="large">
        <el-form-item label="姓名">
          <el-input v-model="form.name" placeholder="请输入姓名" :prefix-icon="User"></el-input>
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone" placeholder="请输入电话号码" :prefix-icon="Phone"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleAdd">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAllDrivers, addDriver } from '../../api'
import { Plus, User, Phone, UserFilled } from '@element-plus/icons-vue'

const drivers = ref([])
const dialogVisible = ref(false)
const form = ref({
  name: '',
  phone: ''
})

const fetchDrivers = async () => {
  try {
    const res = await getAllDrivers()
    if (res.code === 200) {
      drivers.value = res.data
    }
  } catch (error) {
    ElMessage.error('获取司机列表失败')
  }
}

const handleAdd = async () => {
  if (!form.value.name || !form.value.phone) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    const res = await addDriver(form.value)
    if (res.code === 200) {
      ElMessage.success('添加成功')
      dialogVisible.value = false
      form.value = { name: '', phone: '' }
      fetchDrivers()
    } else {
      ElMessage.error(res.message || '添加失败')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  fetchDrivers()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
