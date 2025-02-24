const breadcrumbConfig: { [key: string]: { name: string; to?: { path: string } }[] } = {
  '/management/home/index': [
    { name: '首页', to: { path: '/management/home/index' } }
  ],
  '/management/home/Article': [
    { name: '首页', to: { path: '/management/home/index' } },
    { name: '文章管理' }
  ],
  '/management/home/Article/Add': [
    { name: '文章管理'},
    { name: '文章列表' , to: { path: '/management/home/Article/ArticleList' } },
    { name: '添加文章' , to: { path: '/management/home/Article/Add' } }
  ],
  '/management/home/Article/Edit': [
    { name: '文章管理'},
    { name: '文章列表' , to: { path: '/management/home/Article/ArticleList' } },
    { name: '编辑文章' , to: { path: '/management/home/Article/Edit' } }
  ],
  '/management/home/Article/ArticleList': [
    { name: '文章管理'},
    { name: '文章列表' , to: { path: '/management/home/Article/ArticleList' } }
  ],
  '/management/home/Article/ViewArticle': [
    { name: '文章管理'},
    { name: '文章列表' , to: { path: '/management/home/Article/ArticleList' } },
    { name: '预览文章' , to: { path: '/management/home/Article/ViewArticle' } }
  ],
  '/management/home/User/UserList': [
    { name: '用户管理'},
    { name: '用户列表' , to: { path: '/management/home/User/UserList' } }
  ]
};

export default breadcrumbConfig;
