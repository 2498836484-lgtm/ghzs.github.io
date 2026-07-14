import { createRouter, createWebHistory } from 'vue-router'
import { getToken, getUserType } from '@/utils/auth'
import { USER_TYPE } from '@/utils/constants'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/user/Login.vue'),
    meta: { title: '用户登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/user/Register.vue'),
    meta: { title: '用户注册' }
  },
  {
    path: '/admin-login',
    name: 'AdminLogin',
    component: () => import('@/views/admin/AdminLogin.vue'),
    meta: { title: '管理员登录' }
  },
  {
    path: '/user',
    name: 'User',
    component: () => import('@/views/user/UserLayout.vue'),
    meta: { requiresAuth: true, userType: USER_TYPE.USER },
    children: [
      {
        path: 'home',
        name: 'UserHome',
        component: () => import('@/views/user/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'apply',
        name: 'ApplyResident',
        component: () => import('@/views/user/ApplyResident.vue'),
        meta: { title: '入住申请' }
      },
      {
        path: 'payment',
        component: () => import('@/views/user/Payment.vue'),
        redirect: '/user/payment/home',
        children: [
          {
            path: 'home',
            name: 'PaymentHome',
            component: () => import('@/views/user/Payment/PaymentHome.vue'),
            meta: { title: '缴费管理' }
          },
          {
            path: 'pay-now',
            name: 'PayNow',
            component: () => import('@/views/user/Payment/PayNow.vue'),
            meta: { title: '立即缴费' }
          },
          {
            path: 'record',
            name: 'PaymentRecord',
            component: () => import('@/views/user/Payment/PaymentRecord.vue'),
            meta: { title: '缴费记录' }
          }
        ]
      },
      {
        path: 'messages',
        name: 'Messages',
        component: () => import('@/views/user/Messages.vue'),
        meta: { title: '消息中心' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/user/Profile.vue'),
        meta: { title: '个人中心' }
      }
    ]
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/views/admin/AdminLayout.vue'),
    meta: { requiresAuth: true, userType: USER_TYPE.ADMIN },
    children: [
      {
        path: 'audit',
        name: 'AuditManage',
        component: () => import('@/views/admin/AuditManage.vue'),
        meta: { title: '入住审核' }
      },
      {
        path: 'resident',
        name: 'ResidentManage',
        component: () => import('@/views/admin/ResidentManage.vue'),
        meta: { title: '住户管理' }
      },
      {
        path: 'payment',
        name: 'PaymentManage',
        component: () => import('@/views/admin/PaymentManage.vue'),
        meta: { title: '缴费管理' }
      },
      {
        path: 'admin-manage',
        name: 'AdminManage',
        component: () => import('@/views/admin/AdminManage.vue'),
        meta: { title: '管理员信息' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = getToken()
  const userType = getUserType()

  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 物业管理系统` : '物业管理系统'

  // 需要认证的页面
  if (to.meta.requiresAuth) {
    if (!token) {
      // 未登录，跳转到登录页
      next(to.meta.userType === USER_TYPE.ADMIN ? '/admin-login' : '/login')
    } else if (to.meta.userType && to.meta.userType !== userType) {
      // 用户类型不匹配
      next(userType === USER_TYPE.ADMIN ? '/admin/audit' : '/user/home')
    } else {
      next()
    }
  } else {
    // 已登录用户访问登录页，跳转到主页
    if (token && (to.path === '/login' || to.path === '/admin-login')) {
      next(userType === USER_TYPE.ADMIN ? '/admin/audit' : '/user/home')
    } else {
      next()
    }
  }
})

export default router
