import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import prismjs from 'vite-plugin-prismjs';

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
    prismjs({
      languages: 'all',
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  //服务器配置
  server: {
    //配置端口
    host: '0.0.0.0', //本机地址
    port:80,  //通过5000端口号访问

    //配置反向代理
    proxy: {
      //凡是以 /api 地址开头的请求, 都要做代理操作
      '/api': {
        //目的地址 => 代理服务器,需要向该地址发起请求
        target: 'http://localhost:8080',
        //是否跨域
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/management/, ''),
      },
    },
  },
})
