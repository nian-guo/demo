import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', noAuth: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册', noAuth: true }
  },
  {
    path: '/',
    component: () => import('@/components/layout/AppLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '数据看板', icon: 'DataAnalysis' }
      },
      {
        path: 'seed',
        name: 'SeedManage',
        component: () => import('@/views/SeedManage.vue'),
        meta: { title: '种子信息管理', icon: 'Collection' }
      },
      {
        path: 'planting',
        name: 'PlantingRecord',
        component: () => import('@/views/PlantingRecord.vue'),
        meta: { title: '种植记录', icon: 'Sunny' }
      },
      {
        path: 'processing',
        name: 'ProcessingRecord',
        component: () => import('@/views/ProcessingRecord.vue'),
        meta: { title: '加工记录', icon: 'SetUp' }
      },
      {
        path: 'quality',
        name: 'QualityRecord',
        component: () => import('@/views/QualityRecord.vue'),
        meta: { title: '质检记录', icon: 'Checked' }
      },
      {
        path: 'sales',
        name: 'SalesRecord',
        component: () => import('@/views/SalesRecord.vue'),
        meta: { title: '销售记录', icon: 'Sell' }
      },
      {
        path: 'trace',
        name: 'TraceQuery',
        component: () => import('@/views/TraceQuery.vue'),
        meta: { title: '追溯查询', icon: 'Search' }
      },
      {
        path: 'report',
        name: 'Report',
        component: () => import('@/views/Report.vue'),
        meta: { title: '统计报表', icon: 'TrendCharts' }
      },
      {
        path: 'users',
        name: 'UserManage',
        component: () => import('@/views/UserManage.vue'),
        meta: { title: '用户管理', icon: 'User' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '页面未找到', noAuth: true }
  }
]

const router = createRouter({
  // 关键修改：hash路由模式，适配GitHub Pages静态站点
  history: createWebHashHistory(),
  routes
})

// 路由守卫：未登录跳转登录页
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  if (to.meta.noAuth) {
    // 已登录用户访问登录页，重定向到看板
    if (to.name === 'Login' && token) {
      return next('/dashboard')
    }
    return next()
  }

  if (!token) {
    return next({ name: 'Login', query: { redirect: to.fullPath } })
  }

  // 动态设置页面标题
  document.title = `${to.meta.title || '麦溯'} - 小麦种子追溯系统`
  next()
})

export default router