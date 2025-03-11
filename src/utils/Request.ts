import router from '@/router';
import axios from 'axios'
import { ElMessage } from 'element-plus';
// 创建实例时配置默认值
const instance = axios.create({
  baseURL: '/',
  timeout:50000 //默认超时5s
});
// 添加请求拦截器
instance.interceptors.request.use(function (config) {
  // 在发送请求之前做些什么
  const path = router.currentRoute.value.path
  const token = window.localStorage.getItem('token')
  if(token=="" || token==null || token==undefined){
    if(path!=="/" && !path.startsWith("/article")){
      router.push('/management')
    }
  }
  config.headers['token'] = `${token}`;
  return config;
}, function (error) {
  // 对请求错误做些什么
  return Promise.reject(error);
});
// 添加响应拦截器
instance.interceptors.response.use(function (response) {
  if(response.data.code==205 || response.data.code==207 || response.data.code==204 || response.data.code==500){
    if(localStorage.getItem("token")){
      ElMessage({
        message:response.data.msg
      })
    }
    router.push('/management')
    window.localStorage.removeItem('token')
  }

  if(response.data.code==206){
    ElMessage({
      message:response.data.msg,
      type:"error"
    })
  }

  return response;
}, function (error) {
  return Promise.reject(error);
});

// 创建实例后修改默认值
export default instance
