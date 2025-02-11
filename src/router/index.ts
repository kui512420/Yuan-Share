import { createRouter, createWebHashHistory } from 'vue-router'

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: [

    {
      path: '/management',
      name: 'Login',
      component: () => import('../views/Management/LoginPage.vue'),
      meta: {
        keepAlive: false,
      },
    },
    {
      path: '/management/Home',
      name: 'home',
      component: () => import('../views/Management/HomeView.vue'),
      children: [
        { path: 'index', name:'index', component: () =>import('@/components/Management/PageComponents/HomeIndex.vue'), meta: {
          keepAlive: false,
        }},
        { path: 'ArticleList', component: () => import('@/components/Management/ArticleComponents/ArticleList.vue')},
        { path: 'UserList', component: () => import('@/components/Management/UserComponests/UserList.vue')},
        { path: 'Edit', name:'edit',component: () => import('@/components/Management/ArticleComponents/EditMark.vue') },
        { path: 'LoginLog', name:'login_log',component: () => import('@/components/Management/LogComponents/LoginLogList.vue') },
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
