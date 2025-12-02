import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/Login.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/Register.vue')
    },
    {
      path: '/student',
      name: 'student',
      component: () => import('../views/student/Layout.vue'),
      children: [
        {
          path: 'charter',
          name: 'student-charter',
          component: () => import('../views/student/Charter.vue')
        },
        {
          path: 'trips',
          name: 'student-trips',
          component: () => import('../views/student/MyTrips.vue')
        },
        {
          path: 'profile',
          name: 'student-profile',
          component: () => import('../views/student/Profile.vue')
        }
      ]
    },
    {
      path: '/admin',
      name: 'admin',
      component: () => import('../views/admin/Layout.vue'),
      children: [
        {
          path: 'trips',
          name: 'admin-trips',
          component: () => import('../views/admin/Trips.vue')
        },
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
