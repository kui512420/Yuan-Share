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
        { path: 'index', name:'index', component: () =>import('../components/HomeIndex.vue'), meta: {
          keepAlive: false,
        }},
        { path: 'UserList', component: () => import('../components/UserList.vue')},
        { path: 'Edit', name:'edit',component: () => import('../components/EditMark.vue') },
      ],

    },

  ],
})
// 全局前置守卫
router.beforeEach((to, from, next) => {
  // 用户是否登录的状态
  const isLoggedIn = window.sessionStorage.getItem('token');
  if (to.name === 'edit' && !isLoggedIn) {
    // 如果用户未登录且要访问 edit 页面，重定向到 Home 页面
    next({ name: 'Login' });
  } else if(to.name === 'home'){
    // 其他情况正常跳转
    next({ name: 'index' });
  }else{
    next();
  }
});

export default router
