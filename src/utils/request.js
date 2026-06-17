import axios from 'axios'

const getToken = () => localStorage.getItem('token')

// 适配 Tomcat 部署：加上项目名 /xiaomai
const request = axios.create({
  baseURL: '/xiaomai/api',
  timeout: 10000,
  withCredentials: true
})

// 请求拦截器：携带token
request.interceptors.request.use(config => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截器
request.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default request