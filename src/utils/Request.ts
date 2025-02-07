import axios from 'axios'
// 创建实例时配置默认值
const instance = axios.create({
  baseURL: 'http://localhost:5173/',
  timeout:5000 //默认超时5s
});

// 创建实例后修改默认值
export default instance
