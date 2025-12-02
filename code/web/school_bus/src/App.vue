<template>
  <div class="app-container">
    <h1 class="title">学生汽车包车预定系统</h1>

    <!-- 顶部当前登录信息 -->
    <div class="header" v-if="currentUser">
      <div>
        当前登录：
        <strong>{{ currentUser.name }}</strong>
        （{{ currentUser.role === 'student' ? '学生' : '管理员' }}）
      </div>
      <button class="btn btn-danger" @click="logout">退出登录</button>
    </div>

    <!-- 未登录：显示登录/注册页面 -->
    <div v-if="!currentUser" class="card auth-card">
      <div class="tabs">
        <button
            class="tab"
            :class="{ active: authTab === 'student' }"
            @click="authTab = 'student'"
        >
          学生登录/注册
        </button>
        <button
            class="tab"
            :class="{ active: authTab === 'admin' }"
            @click="authTab = 'admin'"
        >
          管理员登录/注册
        </button>
      </div>

      <!-- 学生登录注册 -->
      <div v-if="authTab === 'student'" class="auth-body">
        <div class="auth-switch">
          <button
              class="btn"
              :class="{ 'btn-primary': studentAuthMode === 'login' }"
              @click="studentAuthMode = 'login'"
          >
            学生登录
          </button>
          <button
              class="btn"
              :class="{ 'btn-primary': studentAuthMode === 'register' }"
              @click="studentAuthMode = 'register'"
          >
            学生注册
          </button>
        </div>

        <!-- 学生注册表单 -->
        <form v-if="studentAuthMode === 'register'" @submit.prevent="handleStudentRegister">
          <div class="form-group">
            <label>姓名</label>
            <input v-model="studentRegisterForm.name" required />
          </div>
          <div class="form-group">
            <label>性别</label>
            <select v-model="studentRegisterForm.gender" required>
              <option value="" disabled>请选择</option>
              <option>男</option>
              <option>女</option>
            </select>
          </div>
          <div class="form-group">
            <label>班级</label>
            <input v-model="studentRegisterForm.className" required />
          </div>
          <div class="form-group">
            <label>学号（主键）</label>
            <input v-model="studentRegisterForm.studentNo" required />
          </div>
          <div class="form-group">
            <label>密码</label>
            <input type="password" v-model="studentRegisterForm.password" required />
          </div>
          <button class="btn btn-primary" type="submit">注册</button>
        </form>

        <!-- 学生登录表单 -->
        <form v-else @submit.prevent="handleStudentLogin">
          <div class="form-group">
            <label>学号</label>
            <input v-model="studentLoginForm.studentNo" required />
          </div>
          <div class="form-group">
            <label>密码</label>
            <input type="password" v-model="studentLoginForm.password" required />
          </div>
          <button class="btn btn-primary" type="submit">登录</button>
        </form>
      </div>

      <!-- 管理员登录注册 -->
      <div v-else class="auth-body">
        <div class="auth-switch">
          <button
              class="btn"
              :class="{ 'btn-primary': adminAuthMode === 'login' }"
              @click="adminAuthMode = 'login'"
          >
            管理员登录
          </button>
          <button
              class="btn"
              :class="{ 'btn-primary': adminAuthMode === 'register' }"
              @click="adminAuthMode = 'register'"
          >
            管理员注册
          </button>
        </div>

        <!-- 管理员注册 -->
        <form v-if="adminAuthMode === 'register'" @submit.prevent="handleAdminRegister">
          <div class="form-group">
            <label>姓名</label>
            <input v-model="adminRegisterForm.name" required />
          </div>
          <div class="form-group">
            <label>账号（主键）</label>
            <input v-model="adminRegisterForm.account" required />
          </div>
          <div class="form-group">
            <label>密码</label>
            <input type="password" v-model="adminRegisterForm.password" required />
          </div>
          <button class="btn btn-primary" type="submit">注册</button>
        </form>

        <!-- 管理员登录 -->
        <form v-else @submit.prevent="handleAdminLogin">
          <div class="form-group">
            <label>账号</label>
            <input v-model="adminLoginForm.account" required />
          </div>
          <div class="form-group">
            <label>密码</label>
            <input type="password" v-model="adminLoginForm.password" required />
          </div>
          <button class="btn btn-primary" type="submit">登录</button>
        </form>
      </div>
    </div>

    <!-- 学生端主界面 -->
    <div v-else-if="currentUser.role === 'student'" class="main-layout">
      <div class="sidebar">
        <button
            class="menu-item"
            :class="{ active: studentPage === 'book' }"
            @click="studentPage = 'book'"
        >
          学生包车购票
        </button>
        <button
            class="menu-item"
            :class="{ active: studentPage === 'join' }"
            @click="studentPage = 'join'"
        >
          通过邀请码乘车
        </button>
        <button
            class="menu-item"
            :class="{ active: studentPage === 'myTickets' }"
            @click="studentPage = 'myTickets'"
        >
          查看我的乘车信息/退票
        </button>
        <button
            class="menu-item"
            :class="{ active: studentPage === 'profile' }"
            @click="studentPage = 'profile'"
        >
          个人信息
        </button>
      </div>

      <div class="content">
        <!-- 包车购票页面 -->
        <div v-if="studentPage === 'book'">
          <h2>学生包车购票</h2>
          <form class="card" @submit.prevent="handleBookCar">
            <div class="form-group">
              <label>用车日期</label>
              <input type="date" v-model="bookForm.date" required />
            </div>
            <div class="form-group">
              <label>车型</label>
              <select v-model="bookForm.vehicleType" required>
                <option value="" disabled>请选择</option>
                <option value="客车">客车（48人，3888.88元）</option>
                <option value="巴士">巴士（22人，2999.99元）</option>
              </select>
            </div>
            <div class="form-group">
              <label>出发地</label>
              <input v-model="bookForm.origin" required />
            </div>
            <div class="form-group">
              <label>目的地</label>
              <input v-model="bookForm.destination" required />
            </div>
            <button class="btn btn-primary" type="submit">提交购票（模拟支付）</button>
          </form>

          <div v-if="lastBooking" class="card info-card">
            <h3>购票成功</h3>
            <p>车型：{{ lastBooking.vehicleType }}</p>
            <p>车牌号：{{ lastBooking.plateNo }}</p>
            <p>用车日期：{{ lastBooking.date }}</p>
            <p>出发地：{{ lastBooking.origin }} → 目的地：{{ lastBooking.destination }}</p>
            <p>价格：{{ lastBooking.price.toFixed(2) }} 元</p>
            <p>司机：{{ lastBooking.driverName }}（{{ lastBooking.driverPhone }}）</p>
            <p>您的座位号：1</p>
            <p>邀请码：<strong>{{ lastBooking.inviteCode }}</strong></p>
            <small>请将邀请码分享给需要同乘的同学，他们可以通过邀请码加入该车辆。</small>
          </div>
        </div>

        <!-- 邀请码乘车 -->
        <div v-else-if="studentPage === 'join'">
          <h2>通过邀请码乘坐车辆</h2>
          <form class="card" @submit.prevent="handleJoinByInvite">
            <div class="form-group">
              <label>输入邀请码（6位）</label>
              <input v-model="joinForm.inviteCode" required minlength="6" maxlength="6" />
            </div>
            <button class="btn btn-primary" type="submit">加入车辆</button>
          </form>

          <div v-if="lastJoinResult" class="card info-card">
            <h3>加入成功</h3>
            <p>车型：{{ lastJoinResult.vehicleType }}</p>
            <p>车牌号：{{ lastJoinResult.plateNo }}</p>
            <p>用车日期：{{ lastJoinResult.date }}</p>
            <p>出发地：{{ lastJoinResult.origin }} → 目的地：{{ lastJoinResult.destination }}</p>
            <p>您的座位号：<strong>{{ lastJoinResult.seatNo }}</strong></p>
          </div>
        </div>

        <!-- 我的乘车信息 + 退票 -->
        <div v-else-if="studentPage === 'myTickets'">
          <h2>我的乘车信息</h2>
          <div v-if="myRides.length === 0" class="card">
            当前没有乘车记录。
          </div>
          <div v-else>
            <div v-for="ride in myRides" :key="ride.plateNo" class="card ride-card">
              <div class="ride-header">
                <div>
                  <strong>{{ ride.vehicleType }}</strong> - {{ ride.plateNo }}
                </div>
                <div>
                  用车日期：{{ ride.date }}
                </div>
              </div>
              <p>出发地：{{ ride.origin }} → 目的地：{{ ride.destination }}</p>
              <p>价格：{{ ride.price.toFixed(2) }} 元</p>
              <p>司机：{{ ride.driverName }}（{{ ride.driverPhone }}）</p>
              <p>我的座位号：{{ ride.mySeatNo }}</p>
              <p>身份：{{ ride.isOwner ? '包车人（可退整车）' : '乘车人（可单独退票）' }}</p>
              <button
                  class="btn btn-danger"
                  @click="handleRefund(ride)"
              >
                退票
              </button>
            </div>
          </div>
        </div>

        <!-- 学生个人信息 -->
        <div v-else-if="studentPage === 'profile'">
          <h2>学生个人信息</h2>
          <div class="card">
            <p>姓名：{{ currentStudent.name }}</p>
            <p>性别：{{ currentStudent.gender }}</p>
            <p>班级：{{ currentStudent.className }}</p>
            <p>学号：{{ currentStudent.studentNo }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 管理员端主界面 -->
    <div v-else-if="currentUser.role === 'admin'" class="main-layout">
      <div class="sidebar">
        <button
            class="menu-item"
            :class="{ active: adminPage === 'trips' }"
            @click="adminPage = 'trips'"
        >
          车次查看/管理
        </button>
        <button
            class="menu-item"
            :class="{ active: adminPage === 'drivers' }"
            @click="adminPage = 'drivers'"
        >
          新增/查看司机
        </button>
      </div>

      <div class="content">
        <!-- 车次查看/管理 -->
        <div v-if="adminPage === 'trips'">
          <h2>车次表</h2>
          <div v-if="trips.length === 0" class="card">当前没有车次。</div>
          <table v-else class="table">
            <thead>
            <tr>
              <th>车牌号</th>
              <th>车型</th>
              <th>用车日期</th>
              <th>出发地</th>
              <th>目的地</th>
              <th>人数</th>
              <th>司机</th>
              <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="trip in trips" :key="trip.plateNo">
              <td>{{ trip.plateNo }}</td>
              <td>{{ trip.vehicleType }}</td>
              <td>{{ trip.date }}</td>
              <td>{{ trip.origin }}</td>
              <td>{{ trip.destination }}</td>
              <td>{{ trip.personCount }}</td>
              <td>{{ trip.driverName }}（{{ trip.driverPhone }}）</td>
              <td>
                <button class="btn btn-sm" @click="selectTrip(trip)">查看乘车人员</button>
              </td>
            </tr>
            </tbody>
          </table>

          <!-- 右侧车次详情 -->
          <div v-if="selectedTrip" class="card">
            <h3>车次详情：{{ selectedTrip.plateNo }}</h3>
            <p>车型：{{ selectedTrip.vehicleType }}</p>
            <p>用车日期：{{ selectedTrip.date }}</p>
            <p>路线：{{ selectedTrip.origin }} → {{ selectedTrip.destination }}</p>
            <p>当前人数：{{ selectedTrip.personCount }}</p>

            <h4>乘车人员信息（乘车人员信息表）</h4>
            <table class="table small-table" v-if="selectedTripPassengers.length > 0">
              <thead>
              <tr>
                <th>id</th>
                <th>学号</th>
                <th>姓名</th>
                <th>座位号</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="p in selectedTripPassengers" :key="p.id">
                <td>{{ p.id }}</td>
                <td>{{ p.studentNo }}</td>
                <td>{{ p.name }}</td>
                <td>{{ p.seatNo }}</td>
              </tr>
              </tbody>
            </table>
            <div v-else>暂无乘车人员。</div>

            <!-- 修改司机 -->
            <h4>修改该车次司机</h4>
            <div class="form-inline">
              <select v-model="changeDriverForm.phone">
                <option value="" disabled>选择司机</option>
                <option v-for="d in drivers" :key="d.phone" :value="d.phone">
                  {{ d.name }}（{{ d.phone }}）
                </option>
              </select>
              <button class="btn btn-primary" @click="handleChangeDriver" :disabled="!changeDriverForm.phone">
                保存
              </button>
            </div>
          </div>
        </div>

        <!-- 管理司机 -->
        <div v-else-if="adminPage === 'drivers'">
          <h2>司机表</h2>
          <form class="card" @submit.prevent="handleAddDriver">
            <div class="form-group">
              <label>司机姓名</label>
              <input v-model="driverForm.name" required />
            </div>
            <div class="form-group">
              <label>电话号码（主键）</label>
              <input v-model="driverForm.phone" required />
            </div>
            <button class="btn btn-primary" type="submit">新增司机</button>
          </form>

          <table v-if="drivers.length > 0" class="table">
            <thead>
            <tr>
              <th>姓名</th>
              <th>电话号码</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="d in drivers" :key="d.phone">
              <td>{{ d.name }}</td>
              <td>{{ d.phone }}</td>
            </tr>
            </tbody>
          </table>
          <div v-else class="card">当前还没有司机，请先新增。</div>
        </div>
      </div>
    </div>

    <!-- 全局消息提示（简陋版） -->
    <div v-if="message" class="toast">{{ message }}</div>
  </div>
</template>

<script setup>
import { reactive, ref, computed } from 'vue'

/**
 * ============================
 * “表”数据（用数组模拟数据库）
 * ============================
 *
 * 所有字段都不能为空：
 * - 学生信息表
 * - 管理员信息表
 * - 司机表
 * - 学生购票信息表
 * - 乘车人员信息表
 * - 车次表
 */

// 学生信息表：{ name, gender, className, studentNo (PK), password }
const students = reactive([
  // 可以留空，或者预置一条测试数据
  // {
  //   name: '张三',
  //   gender: '男',
  //   className: '计科2201',
  //   studentNo: '20220001',
  //   password: '123456'
  // }
])

// 管理员信息表：{ name, account (PK), password }
const admins = reactive([
  {
    name: '系统管理员',
    account: 'admin',
    password: 'admin123'
  }
])

// 司机表（电话号码是主键）：{ name, phone }
const drivers = reactive([
  { name: '司机A', phone: '13800000001' },
  { name: '司机B', phone: '13800000002' }
])

// 假定“车库”里有一些车，供系统随机选择车牌号用
const carPool = [
  { vehicleType: '客车', plateNo: '辽A00001' },
  { vehicleType: '客车', plateNo: '辽A00002' },
  { vehicleType: '巴士', plateNo: '辽B10001' },
  { vehicleType: '巴士', plateNo: '辽B10002' }
]

// 学生购票信息表（车牌号是主键）
// { vehicleType, plateNo (PK), seatNo, date, origin, destination, personCount, driverPhone, price, inviteCode, ownerStudentNo }
const studentTickets = reactive([])

// 乘车人员信息表（id主键）
// { id, plateNo, seatNo, studentNo, name }
const passengers = reactive([])

// 车次表（车牌号主键）
// { vehicleType, plateNo (PK), date, origin, destination, personCount, driverPhone, driverName }
const trips = reactive([])

/**
 * 工具：根据车型获取核载人数、价格
 */
function getCapacityAndPriceByType(vehicleType) {
  if (vehicleType === '客车') {
    return { capacity: 48, price: 3888.88 }
  } else if (vehicleType === '巴士') {
    return { capacity: 22, price: 2999.99 }
  }
  return { capacity: 0, price: 0 }
}

/**
 * 工具：根据司机电话获取司机姓名
 */
function getDriverNameByPhone(phone) {
  const d = drivers.find(d => d.phone === phone)
  return d ? d.name : ''
}

/**
 * 工具：简单随机 6 位邀请码
 */
function generateInviteCode() {
  let code = ''
  for (let i = 0; i < 6; i++) {
    code += Math.floor(Math.random() * 10)
  }
  return code
}

/**
 * 工具：根据车型和车牌号查找当前已占用座位号，返回下一个可用座位号（>1 且 < 核载人数）
 */
function getNextAvailableSeatNo(vehicleType, plateNo) {
  const { capacity } = getCapacityAndPriceByType(vehicleType)
  const usedSeat = passengers
      .filter(p => p.plateNo === plateNo)
      .map(p => p.seatNo)

  for (let s = 2; s < capacity; s++) {
    if (!usedSeat.includes(s)) {
      return s
    }
  }
  return null // 已满
}

/**
 * 登录相关状态
 */
const currentUser = ref(null) // { role: 'student' | 'admin', name, ... }

const authTab = ref('student') // student | admin

const studentAuthMode = ref('login') // login | register
const adminAuthMode = ref('login') // login | register

const studentRegisterForm = reactive({
  name: '',
  gender: '',
  className: '',
  studentNo: '',
  password: ''
})

const studentLoginForm = reactive({
  studentNo: '',
  password: ''
})

const adminRegisterForm = reactive({
  name: '',
  account: '',
  password: ''
})

const adminLoginForm = reactive({
  account: '',
  password: ''
})

const message = ref('')
let messageTimer = null
function showMessage(msg) {
  message.value = msg
  if (messageTimer) {
    clearTimeout(messageTimer)
  }
  messageTimer = setTimeout(() => {
    message.value = ''
  }, 2500)
}

/**
 * 学生注册
 */
function handleStudentRegister() {
  const { name, gender, className, studentNo, password } = studentRegisterForm
  if (!name || !gender || !className || !studentNo || !password) {
    showMessage('所有字段不能为空')
    return
  }
  const exists = students.some(s => s.studentNo === studentNo)
  if (exists) {
    showMessage('学号已存在')
    return
  }
  students.push({
    name,
    gender,
    className,
    studentNo,
    password
  })
  showMessage('注册成功，请登录')
  // 清空
  studentRegisterForm.name = ''
  studentRegisterForm.gender = ''
  studentRegisterForm.className = ''
  studentRegisterForm.studentNo = ''
  studentRegisterForm.password = ''
  studentAuthMode.value = 'login'
}

/**
 * 学生登录
 */
function handleStudentLogin() {
  const { studentNo, password } = studentLoginForm
  const stu = students.find(s => s.studentNo === studentNo && s.password === password)
  if (!stu) {
    showMessage('学号或密码错误')
    return
  }
  currentUser.value = {
    role: 'student',
    name: stu.name,
    studentNo: stu.studentNo
  }
  studentPage.value = 'book'
  showMessage('登录成功')
}

/**
 * 管理员注册
 */
function handleAdminRegister() {
  const { name, account, password } = adminRegisterForm
  if (!name || !account || !password) {
    showMessage('所有字段不能为空')
    return
  }
  const exists = admins.some(a => a.account === account)
  if (exists) {
    showMessage('账号已存在')
    return
  }
  admins.push({ name, account, password })
  showMessage('管理员注册成功，请登录')
  adminRegisterForm.name = ''
  adminRegisterForm.account = ''
  adminRegisterForm.password = ''
  adminAuthMode.value = 'login'
}

/**
 * 管理员登录
 */
function handleAdminLogin() {
  const { account, password } = adminLoginForm
  const ad = admins.find(a => a.account === account && a.password === password)
  if (!ad) {
    showMessage('账号或密码错误')
    return
  }
  currentUser.value = {
    role: 'admin',
    name: ad.name,
    account: ad.account
  }
  adminPage.value = 'trips'
  showMessage('登录成功')
}

/**
 * 退出登录
 */
function logout() {
  currentUser.value = null
}

/**
 * ============================
 * 学生端：页面与业务
 * ============================
 */

const studentPage = ref('book') // book | join | myTickets | profile

// 包车购票表单
const bookForm = reactive({
  date: '',
  vehicleType: '',
  origin: '',
  destination: ''
})

const lastBooking = ref(null)

/**
 * 学生包车购票
 */
function handleBookCar() {
  const { date, vehicleType, origin, destination } = bookForm
  if (!date || !vehicleType || !origin || !destination) {
    showMessage('所有字段不能为空')
    return
  }

  // 随机从 carPool 里选一个同车型的车牌号
  const candidates = carPool.filter(c => c.vehicleType === vehicleType)
  if (candidates.length === 0) {
    showMessage('暂无该车型可用车辆')
    return
  }
  const chosen = candidates[Math.floor(Math.random() * candidates.length)]
  const plateNo = chosen.plateNo

  // 检查车牌号在车次表/购票信息表是否已存在（主键唯一），这里简单地当作同一时间只能被包一次
  const tripExists = trips.some(t => t.plateNo === plateNo)
  if (tripExists) {
    showMessage('该车当前已被占用，请稍后重试')
    return
  }

  const { capacity, price } = getCapacityAndPriceByType(vehicleType)
  if (capacity === 0) {
    showMessage('车型错误')
    return
  }

  // 随机选一个司机
  if (drivers.length === 0) {
    showMessage('暂无司机，请联系管理员添加司机')
    return
  }
  const d = drivers[Math.floor(Math.random() * drivers.length)]

  const inviteCode = generateInviteCode()
  const ownerStudentNo = currentUser.value.studentNo

  // 车次表插入记录
  trips.push({
    vehicleType,
    plateNo,
    date,
    origin,
    destination,
    personCount: 1, // 包车人自己先占 1 个座位
    driverPhone: d.phone,
    driverName: d.name
  })

  // 学生购票信息表插入记录（车牌号主键）
  studentTickets.push({
    vehicleType,
    plateNo,
    seatNo: 1,
    date,
    origin,
    destination,
    personCount: 1,
    driverPhone: d.phone,
    price,
    inviteCode,
    ownerStudentNo
  })

  // 乘车人员信息表插入记录（id 主键，这里简单用时间戳+随机数）
  passengers.push({
    id: Date.now() + Math.floor(Math.random() * 1000),
    plateNo,
    seatNo: 1,
    studentNo: ownerStudentNo,
    name: currentUser.value.name
  })

  lastBooking.value = {
    vehicleType,
    plateNo,
    date,
    origin,
    destination,
    price,
    driverName: d.name,
    driverPhone: d.phone,
    inviteCode
  }

  // 清空表单
  bookForm.date = ''
  bookForm.vehicleType = ''
  bookForm.origin = ''
  bookForm.destination = ''
  showMessage('购票成功（模拟支付成功）')
}

/**
 * 邀请码乘车
 */
const joinForm = reactive({
  inviteCode: ''
})

const lastJoinResult = ref(null)

function handleJoinByInvite() {
  const { inviteCode } = joinForm
  if (!inviteCode) {
    showMessage('邀请码不能为空')
    return
  }

  // 通过学生购票信息表找到车
  const ticket = studentTickets.find(t => t.inviteCode === inviteCode)
  if (!ticket) {
    showMessage('邀请码无效')
    return
  }

  const trip = trips.find(t => t.plateNo === ticket.plateNo)
  if (!trip) {
    showMessage('对应车次不存在')
    return
  }

  const { capacity } = getCapacityAndPriceByType(ticket.vehicleType)

  // 计算该车当前已有人数（以乘车人员信息表为准）
  const currentPassengers = passengers.filter(p => p.plateNo === ticket.plateNo)
  if (currentPassengers.length >= capacity) {
    showMessage('该车已满员')
    return
  }

  // 查找本学生是否已经在车上
  const exists = currentPassengers.some(p => p.studentNo === currentUser.value.studentNo)
  if (exists) {
    showMessage('你已经是该车乘客，无需重复加入')
    return
  }

  const seatNo = getNextAvailableSeatNo(ticket.vehicleType, ticket.plateNo)
  if (!seatNo) {
    showMessage('找不到可用座位号（已满）')
    return
  }

  // 插入乘车人员记录
  const id = Date.now() + Math.floor(Math.random() * 1000)
  passengers.push({
    id,
    plateNo: ticket.plateNo,
    seatNo,
    studentNo: currentUser.value.studentNo,
    name: currentUser.value.name
  })

  // 更新车次表人数
  trip.personCount = (trip.personCount || 0) + 1

  // 更新学生购票信息表人数
  ticket.personCount = (ticket.personCount || 0) + 1

  lastJoinResult.value = {
    vehicleType: ticket.vehicleType,
    plateNo: ticket.plateNo,
    date: ticket.date,
    origin: ticket.origin,
    destination: ticket.destination,
    seatNo
  }

  joinForm.inviteCode = ''
  showMessage('加入成功')
}

/**
 * 当前登录学生对象
 */
const currentStudent = computed(() => {
  if (!currentUser.value || currentUser.value.role !== 'student') return null
  return students.find(s => s.studentNo === currentUser.value.studentNo) || null
})

/**
 * 我的乘车信息：
 * 以“乘车人员信息表”为起点，找到当前学号对应的记录，
 * 再关联“学生购票信息表”和“司机表”，组装出完整信息。
 */
const myRides = computed(() => {
  if (!currentUser.value || currentUser.value.role !== 'student') return []
  const sn = currentUser.value.studentNo

  const myPassengerRows = passengers.filter(p => p.studentNo === sn)

  return myPassengerRows.map(p => {
    const ticket = studentTickets.find(t => t.plateNo === p.plateNo)
    if (!ticket) return null
    const driverName = getDriverNameByPhone(ticket.driverPhone)
    const isOwner = ticket.ownerStudentNo === sn
    return {
      plateNo: ticket.plateNo,
      vehicleType: ticket.vehicleType,
      date: ticket.date,
      origin: ticket.origin,
      destination: ticket.destination,
      price: ticket.price,
      driverName,
      driverPhone: ticket.driverPhone,
      mySeatNo: p.seatNo,
      isOwner
    }
  }).filter(Boolean)
})

/**
 * 学生退票：
 * - 若是包车人（owner）：删车次表记录 + 对应车的所有乘车人员 + 学生购票信息表记录
 * - 若是普通乘客：只删自己的乘车人员记录，顺带更新人数
 */
function handleRefund(ride) {
  const sn = currentUser.value.studentNo
  if (ride.isOwner) {
    // 包车人退票：整车取消
    const plateNo = ride.plateNo

    // 删除车次
    const tripIdx = trips.findIndex(t => t.plateNo === plateNo)
    if (tripIdx !== -1) trips.splice(tripIdx, 1)

    // 删除该车所有乘车人员
    for (let i = passengers.length - 1; i >= 0; i--) {
      if (passengers[i].plateNo === plateNo) {
        passengers.splice(i, 1)
      }
    }

    // 删除学生购票信息表记录
    const ticketIdx = studentTickets.findIndex(t => t.plateNo === plateNo)
    if (ticketIdx !== -1) studentTickets.splice(ticketIdx, 1)

    showMessage('退票成功（整车取消）')
  } else {
    // 普通乘客退票：只删除自己的乘车人员记录
    const pIdx = passengers.findIndex(p => p.plateNo === ride.plateNo && p.studentNo === sn)
    if (pIdx !== -1) passengers.splice(pIdx, 1)

    // 更新人数
    const trip = trips.find(t => t.plateNo === ride.plateNo)
    if (trip && trip.personCount > 0) trip.personCount--

    const ticket = studentTickets.find(t => t.plateNo === ride.plateNo)
    if (ticket && ticket.personCount > 0) ticket.personCount--

    showMessage('退票成功（已从该车乘车人员中移除）')
  }
}

/**
 * ============================
 * 管理员端：页面与业务
 * ============================
 */
const adminPage = ref('trips')

// 当前选中的车次
const selectedTrip = ref(null)
const changeDriverForm = reactive({
  phone: ''
})

/**
 * 选择车次查看详情/乘客
 */
function selectTrip(trip) {
  selectedTrip.value = trip
  changeDriverForm.phone = trip.driverPhone
}

// 当前选中车次的乘车人员
const selectedTripPassengers = computed(() => {
  if (!selectedTrip.value) return []
  return passengers.filter(p => p.plateNo === selectedTrip.value.plateNo)
})

/**
 * 修改司机：同时更新
 * - 车次表（trips）
 * - 学生购票信息表（studentTickets）
 */
function handleChangeDriver() {
  if (!selectedTrip.value) {
    showMessage('请先选择车次')
    return
  }
  const newDriver = drivers.find(d => d.phone === changeDriverForm.phone)
  if (!newDriver) {
    showMessage('司机不存在')
    return
  }

  // 更新车次表
  selectedTrip.value.driverPhone = newDriver.phone
  selectedTrip.value.driverName = newDriver.name

  // 更新学生购票信息表里对应车牌号的所有记录
  studentTickets.forEach(t => {
    if (t.plateNo === selectedTrip.value.plateNo) {
      t.driverPhone = newDriver.phone
    }
  })

  showMessage('司机修改成功')
}

/**
 * 管理员新增司机
 */
const driverForm = reactive({
  name: '',
  phone: ''
})

function handleAddDriver() {
  const { name, phone } = driverForm
  if (!name || !phone) {
    showMessage('司机姓名和电话号码不能为空')
    return
  }
  const exists = drivers.some(d => d.phone === phone)
  if (exists) {
    showMessage('该电话号码已经存在')
    return
  }
  drivers.push({ name, phone })
  driverForm.name = ''
  driverForm.phone = ''
  showMessage('司机新增成功')
}
</script>

<style scoped>
.app-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 16px;
  font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}

.title {
  text-align: center;
  margin-bottom: 16px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 8px 12px;
  background: #f5f5f5;
  border-radius: 6px;
}

.card {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 16px;
  margin-bottom: 16px;
}

.auth-card {
  max-width: 520px;
  margin: 0 auto;
}

.tabs {
  display: flex;
  margin-bottom: 12px;
}

.tab {
  flex: 1;
  padding: 8px;
  border: none;
  border-bottom: 2px solid transparent;
  cursor: pointer;
  background: #f7f7f7;
}

.tab.active {
  border-bottom-color: #409eff;
  background: #fff;
  font-weight: 600;
}

.auth-body {
  margin-top: 8px;
}

.auth-switch {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.form-group {
  margin-bottom: 12px;
  display: flex;
  flex-direction: column;
}

.form-group label {
  margin-bottom: 4px;
  font-size: 14px;
}

.form-group input,
.form-group select {
  padding: 6px 8px;
  border-radius: 4px;
  border: 1px solid #ccc;
}

.btn {
  padding: 6px 12px;
  border: 1px solid #409eff;
  background: #fff;
  color: #409eff;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn-primary {
  background: #409eff;
  color: #fff;
}

.btn-danger {
  border-color: #f56c6c;
  background: #f56c6c;
  color: #fff;
}

.btn-sm {
  padding: 4px 8px;
  font-size: 12px;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.main-layout {
  display: flex;
  gap: 16px;
  margin-top: 8px;
}

.sidebar {
  width: 200px;
}

.menu-item {
  width: 100%;
  text-align: left;
  margin-bottom: 8px;
  padding: 8px 10px;
  border-radius: 4px;
  border: 1px solid #ddd;
  cursor: pointer;
  background: #fff;
}

.menu-item.active {
  background: #409eff;
  color: #fff;
  border-color: #409eff;
}

.content {
  flex: 1;
}

.table {
  width: 100%;
  border-collapse: collapse;
  background: #fff;
  border-radius: 6px;
  overflow: hidden;
  font-size: 14px;
}

.table th,
.table td {
  padding: 8px;
  border: 1px solid #ddd;
}

.small-table th,
.small-table td {
  font-size: 13px;
}

.ride-card .ride-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
}

.form-inline {
  display: flex;
  gap: 8px;
  align-items: center;
  margin-top: 8px;
}

.form-inline select {
  padding: 4px 8px;
  border-radius: 4px;
  border: 1px solid #ccc;
}

.toast {
  position: fixed;
  right: 16px;
  bottom: 16px;
  background: rgba(0, 0, 0, 0.8);
  color: #fff;
  padding: 8px 12px;
  border-radius: 4px;
  font-size: 14px;
}

.info-card p {
  margin: 4px 0;
}
</style>