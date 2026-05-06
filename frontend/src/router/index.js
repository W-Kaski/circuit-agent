import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: {
      title: 'Home - EK AI Intelligent Agent Platform',
      description: 'EK AI Intelligent Agent Platform provides AI Algorithm Master and AI Super Agent services, meeting all your AI conversation needs.'
    }
  },
  {
    path: '/love-master',
    name: 'LoveMaster',
    component: () => import('../views/AlgorithmMaster.vue'),
    meta: {
      title: 'AI Algorithm Master - EK AI Intelligent Agent Platform',
      description: 'AI Algorithm Master is the professional algorithm advisor on the EK AI Intelligent Agent Platform, helping you solve algorithm problems and providing algorithm guidance.'
    }
  },
  {
    path: '/super-agent',
    name: 'SuperAgent',
    component: () => import('../views/SuperAgent.vue'),
    meta: {
      title: 'AI Super Agent - EK AI Intelligent Agent Platform',
      description: 'The AI Super Agent is the all-in-one assistant of the EK AI Intelligent Agent Platform. It can answer a wide range of professional questions and provide accurate advice and tailored solutions.'
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局导航守卫，设置文档标题
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = to.meta.title
  }
  next()
})

export default router 