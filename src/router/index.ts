import { createRouter, createWebHashHistory } from 'vue-router'

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'qiantai',
      component: () => import('../views/QianTai/indexView.vue'),
    },
    {
      path: '/article/:id',
      name: 'article',
      component: () => import('../views/Management/ViewArticlePage.vue'),
    },
    {
      path: '/management',
      name: 'Login',
      component: () => import('../views/Management/LoginPage.vue'),
      meta: {
        keepAlive: false,
      },
    },
    {
      path: '/management/register',
      name: 'register',
      component: () => import('../views/Management/RegisterPage.vue'),
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
        { path: 'Article/ViewArticle', name:'view',component: () => import('@/components/Management/ArticleComponents/ViewArticle.vue') },
        { path: 'Article/ArticleList', component: () => import('@/components/Management/ArticleComponents/ArticleList.vue')},
        { path: 'User/UserList', component: () => import('@/components/Management/UserComponests/UserList.vue')},
        { path: 'Article/Edit', name:'edit',component: () => import('@/components/Management/ArticleComponents/EditMark.vue') },
        { path: 'LoginLog', name:'login_log',component: () => import('@/components/Management/LogComponents/LoginLogList.vue') },
        { path: 'SigninLog', name:'signinLog',component: () => import('@/components/Management/LogComponents/SigninLogList.vue') },
      ],
      meta: {
        keepAlive: false,
      },
    },

  ],
})
// 全局前置守卫
router.beforeEach((to, from, next) => {
  // 用户是否登录的状态
  const isLoggedIn = window.sessionStorage.getItem('token');
  if (to.name === 'edit' && isLoggedIn==null) {
    // 如果用户未登录且要访问 edit 页面，重定向到 Home 页面
    next({ name: 'Login' });
  }else{
    next();
  }
});

export default router
