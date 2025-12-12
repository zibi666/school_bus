/**
 * Vue Router 路由配置模块
 * 
 * 功能说明：
 * 定义应用的所有页面路由，支持以下场景：
 * 1. 登录/注册页面（公开访问）
 * 2. 学生端页面（需身份认证）
 * 3. 管理员端页面（需身份认证）
 * 
 * 路由使用懒加载（动态导入），优化首屏加载性能
 */

import { createRouter, createWebHistory } from 'vue-router'

/**
 * 创建 Vue Router 实例
 * 
 * history: 使用 HTML5 History API 进行路由管理
 * routes: 定义所有路由
 */
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // ==================== 首页重定向 ====================
    {
      path: '/',
      redirect: '/login'  // 首页重定向到登录页
    },

    // ==================== 公开页面（无需认证） ====================
    
    /**
     * 登录页面
     * 学生和管理员都使用此页面进行身份认证
     */
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/Login.vue')  // 懒加载组件
    },

    /**
     * 注册页面
     * 学生可以通过此页面创建新账号
     */
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/Register.vue')  // 懒加载组件
    },

    // ==================== 学生端页面（嵌套路由） ====================
    /**
     * 学生界面
     * 包含学生端的所有子页面
     * 基础路由：/student
     */
    {
      path: '/student',
      name: 'student',
      component: () => import('../views/student/Layout.vue'),  // 学生布局模板（包含导航栏等）
      children: [
        /**
         * 包车申请页面
         * 学生在此页面申请校车或拼车
         * 路由：/student/charter
         * 功能：选择目的地、时间、车型，提交订单
         */
        {
          path: 'charter',
          name: 'student-charter',
          component: () => import('../views/student/Charter.vue')
        },

        /**
         * 我的行程页面
         * 显示学生的所有订单（包括创建的和加入的）
         * 路由：/student/trips
         * 功能：查看订单状态、支付订单、加入拼车、申请退票
         */
        {
          path: 'trips',
          name: 'student-trips',
          component: () => import('../views/student/MyTrips.vue')
        },

        /**
         * 个人资料页面
         * 学生可以查看和修改个人信息
         * 路由：/student/profile
         * 功能：修改姓名、位置、密码等
         */
        {
          path: 'profile',
          name: 'student-profile',
          component: () => import('../views/student/Profile.vue')
        }
      ]
    },

    // ==================== 管理员端页面（嵌套路由） ====================
    /**
     * 管理员界面
     * 包含管理员端的所有子页面
     * 基础路由：/admin
     */
    {
      path: '/admin',
      name: 'admin',
      component: () => import('../views/admin/Layout.vue'),  // 管理员布局模板（包含导航栏等）
      children: [
        /**
         * 订单审核页面
         * 管理员在此页面审核学生提交的订单申请
         * 路由：/admin/trips
         * 功能：查看待审核订单、批准或拒绝、分配车辆
         */
        {
          path: 'trips',
          name: 'admin-trips',
          component: () => import('../views/admin/Trips.vue')
        },

        /**
         * 车辆管理页面
         * 管理员管理校车信息和司机信息
         * 路由：/admin/drivers
         * 功能：添加车辆、编辑车辆信息、删除车辆、查看车辆利用率
         */
        {
          path: 'drivers',
          name: 'admin-drivers',
          component: () => import('../views/admin/Drivers.vue')
        }
      ]
    }
  ]
})

export default router
