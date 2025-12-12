/**
 * Vue 3 应用主入口文件
 * 
 * 功能说明：
 * 1. 创建 Vue 3 应用实例
 * 2. 注册全局插件（Vue Router、Element Plus）
 * 3. 挂载应用到 DOM 根元素
 * 
 * Vue Router: 用于前端路由管理，支持登录/注册/学生/管理员页面导航
 * Element Plus: 企业级 UI 组件库，提供表单、按钮、表格等组件
 */

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// 创建应用实例
const app = createApp(App)

// 使用 Vue Router 进行路由管理
app.use(router)

// 使用 Element Plus 组件库
app.use(ElementPlus)

// 将应用挂载到 index.html 中 id 为 'app' 的 DOM 元素
app.mount('#app')