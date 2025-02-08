import { createRouter, createWebHashHistory } from 'vue-router'

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/management',
      name: 'Login',
      component: () => import('../components/LoginPage.vue'),
      meta: {
        keepAlive: false,
      },
    },
    {
      path: '/management/Home',
      name: 'home',
      component: () => import('../components/HomePage.vue'),
      children: [
        { path: 'index', component: () =>import('../components/HomeIndex.vue'), meta: {
          keepAlive: false,
        }},
        { path: 'UserList', component: () => import('../components/UserList.vue')},
        { path: 'Edit', component: () => import('../components/EditMark.vue') },
      ],

    },

  ],
})

export default router
