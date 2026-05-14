import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建 Axios 实例，配置基础路径和超时时间
const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 请求拦截器 - 自动附加 JWT Token
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
}, error => Promise.reject(error))

// 响应拦截器 - 统一处理错误
request.interceptors.response.use(response => {
  const res = response.data
  if (res.code !== 200) {
    ElMessage.error(res.message || '请求失败')
    return Promise.reject(new Error(res.message))
  }
  return res
}, error => {
  if (error.response?.status === 401) {
    ElMessage.error('请先登录')
    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('userName')
    localStorage.removeItem('studentId')
    localStorage.removeItem('userRole')
    window.location.href = '/login'
  } else if (error.response?.status === 403) {
    ElMessage.error('没有权限执行此操作')
  } else {
    ElMessage.error(error.message || '网络错误，请稍后重试')
  }
  return Promise.reject(error)
})

export default request
