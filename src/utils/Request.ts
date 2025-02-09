import router from '@/router';
import axios from 'axios'
import { ElMessage } from 'element-plus';
// 创建实例时配置默认值
const instance = axios.create({
  baseURL: 'http://localhost:5173/',
  timeout:5000 //默认超时5s
});
// 添加请求拦截器
instance.interceptors.request.use(function (config) {
  // 在发送请求之前做些什么
  const token = window.sessionStorage.getItem('token')
  console.log(111111111)
  if(token=="" || token==null || token==undefined){
    router.push('/management')
  }
  config.headers['token'] = `${token}`;
  return config;
}, function (error) {
  // 对请求错误做些什么
  return Promise.reject(error);
});
// 添加响应拦截器
instance.interceptors.response.use(function (response) {
  if(response.data.code==205){
    ElMessage({
      message:response.data.msg
    })
    router.push('/management')
    window.sessionStorage.removeItem('token')
  }

  if(response.data.code==206){
    ElMessage({
      message:response.data.msg,
      type:"error"
    })
  }

  return response;
}, function (error) {
  // 超出 2xx 范围的状态码都会触发该函数。
  // 对响应错误做点什么
  return Promise.reject(error);
});

// 创建实例后修改默认值
export default instance
