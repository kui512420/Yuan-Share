// 导入全局样式
import './assets/main.css';
// 导入科技主题特效样式
import './assets/tech-theme.css';
// 导入 ElementPlus 基础样式和暗黑模式样式
import 'element-plus/dist/index.css';
import 'element-plus/theme-chalk/dark/css-vars.css';

// 导入 Markdown 编辑器相关基础样式和主题样式
import '@kangc/v-md-editor/lib/style/base-editor.css';
import '@kangc/v-md-editor/lib/theme/style/github.css';
import '@kangc/v-md-editor/lib/style/preview.css';

// 导入 Markdown 编辑器插件样式
import '@kangc/v-md-editor/lib/plugins/copy-code/copy-code.css';

// 导入 Vue 和相关库
import { createApp } from 'vue';
import { createPinia } from 'pinia';

// 导入应用组件和路由配置
import App from './App.vue';
import router from './router';

// 导入 ElementPlus 组件库
import ElementPlus from 'element-plus';

// 导入 Markdown 编辑器及相关插件
import VueMarkdownEditor from '@kangc/v-md-editor';
import VMdPreview from '@kangc/v-md-editor/lib/preview';
import githubTheme from '@kangc/v-md-editor/lib/theme/github.js';
import hljs from 'highlight.js';
import createCopyCodePlugin from '@kangc/v-md-editor/lib/plugins/copy-code/index';
import createLineNumbertPlugin from '@kangc/v-md-editor/lib/plugins/line-number/index';

// 配置 Markdown 编辑器和预览组件
VueMarkdownEditor
  .use(githubTheme, { Hljs: hljs })
  .use(createCopyCodePlugin())
  .use(createLineNumbertPlugin());

  VMdPreview.use(githubTheme, { Hljs: hljs })
  .use(createCopyCodePlugin())
  .use(createLineNumbertPlugin({
    //是否显示行号
    showLineNumber: true
  }));

// 创建 Vue 应用实例
const app = createApp(App);

// 使用 Pinia 状态管理、ElementPlus 组件库、Markdown 编辑器和预览组件、路由
app
  .use(createPinia())
  .use(ElementPlus)
  .use(VueMarkdownEditor)
  .use(VMdPreview)
  .use(router);

// 挂载应用到 DOM
app.mount('#app');
