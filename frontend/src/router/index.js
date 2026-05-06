import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: {
      title: 'Home - Circuit Agent',
      description: 'Circuit Agent provides an algorithm-focused RAG assistant and a general tool-calling AI agent.'
    }
  },
  {
    path: '/algorithm-master',
    name: 'AlgorithmMaster',
    component: () => import('../views/AlgorithmMaster.vue'),
    meta: {
      title: 'Algorithm Master - Circuit Agent',
      description: 'Algorithm Master is the RAG-based assistant in Circuit Agent for algorithm and data-structure questions.'
    }
  },
  {
    path: '/super-agent',
    name: 'SuperAgent',
    component: () => import('../views/SuperAgent.vue'),
    meta: {
      title: 'Super Agent - Circuit Agent',
      description: 'Super Agent is the general tool-calling assistant in Circuit Agent for multi-step tasks and professional Q&A.'
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
