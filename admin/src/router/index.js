import { createRouter, createWebHistory } from 'vue-router'


const routes = [
  {
    path: '/',
    component: () => import('../views/main.vue'),
    children: [{
      path: 'welcome',
      component: () => import('../views/main/welcome.vue'),
    }, {
      path: 'ablut',
      component: () => import('../views/main/about.vue'),
    }

    ]
  }, {
    path: '',
    redirect: '/welcome'
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})



export default router
