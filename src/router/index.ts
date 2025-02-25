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
        { path: 'Article/Add', name:'add',component: () => import('@/components/Management/ArticleComponents/AddMark.vue') },
        { path: 'Article/Edit', name:'edit',component: () => import('@/components/Management/ArticleComponents/EditMark.vue') },
        { path: 'LoginLog', name:'login_log',component: () => import('@/components/Management/LogComponents/LoginLogList.vue') },
        { path: 'SigninLog', name:'signinLog',component: () => import('@/components/Management/LogComponents/SigninLogList.vue') },
        { path: 'Message', name:'message',component: () => import('@/components/Management/MessageComponents/MessageList.vue') },
        {
          path: '/management/setting',
          name: 'person',
          component: () => import('../components/Management/SettingComponents/PersonComponent.vue'),
          meta: {
            keepAlive: false,
          },
        },
      ],
      meta: {
        keepAlive: false,
      },
    },

  ],
})


export default router
