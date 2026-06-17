import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import request from '@/utils/request'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))
  const isLoggedIn = ref(!!token.value)

  const roles = {
    admin: { name: '系统管理员', color: '#d4a853' },
    planter: { name: '种植人员', color: '#5b8c5a' },
    processor: { name: '加工人员', color: '#4a90d9' },
    inspector: { name: '质检人员', color: '#6c5ce7' },
    seller: { name: '销售商', color: '#e87a4a' }
  }

  const roleTableMap = {
    admin: ['seed', 'planting', 'processing', 'quality', 'users'],
    planter: ['planting'],
    processor: ['processing'],
    inspector: ['quality'],
    seller: ['sales']
  }

  // 本地保存登录信息
  function login(tokenVal, info) {
    token.value = tokenVal
    // 统一转小写，避免大小写问题
    if (info.role) {
      info.role = info.role.toLowerCase()
    }
    userInfo.value = info
    isLoggedIn.value = true
    localStorage.setItem('token', tokenVal)
    localStorage.setItem('userInfo', JSON.stringify(info))
    console.log('[Pinia] 登录完成，当前用户信息：', userInfo.value)
  }

  // 清空登录状态
  function logout() {
    token.value = ''
    userInfo.value = null
    isLoggedIn.value = false
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  function logoutReset() {
    logout()
  }

  // 从本地重载用户信息
  function reloadUserFromLocal() {
    token.value = localStorage.getItem('token') || ''
    const localUser = localStorage.getItem('userInfo')
    if (localUser) {
      const parseUser = JSON.parse(localUser)
      // 角色统一小写
      if (parseUser.role) {
        parseUser.role = parseUser.role.toLowerCase()
      }
      userInfo.value = parseUser
    } else {
      userInfo.value = null
    }
    isLoggedIn.value = !!token.value
    console.log('[Pinia] 重载本地用户：', userInfo.value)
  }

  // 设置用户角色
  function setUserRole(role) {
    if (!userInfo.value) return
    userInfo.value.role = role?.toLowerCase()
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }

  function getRoleName(role) {
    return roles[role]?.name || role
  }

  function getRoleColor(role) {
    return roles[role]?.color || '#909399'
  }

  // 管理员判断（增加空值兜底）
  const isAdmin = computed(() => {
    const role = userInfo.value?.role || ''
    const res = role === 'admin'
    console.log('[权限判断] isAdmin =', res, '当前角色：', role)
    return res
  })

  const currentRole = computed(() => userInfo.value?.role || '')

  // 菜单权限
  function canAccessTable(table) {
    if (!userInfo.value) return false
    const role = userInfo.value.role || ''
    if (role === 'admin') return true
    return roleTableMap[role]?.includes(table) || false
  }

  // 删除权限（兜底 + 日志）
  function canDelete() {
    const role = userInfo.value?.role || ''
    const res = role === 'admin'
    console.log('[权限判断] canDelete =', res)
    return res
  }

  // 编辑权限
  function canEditRecord(record, now = Date.now()) {
    if (!userInfo.value) return false
    const role = userInfo.value.role || ''
    // 管理员永久可编辑
    if (role === 'admin') return true
    if (record.createdBy !== userInfo.value.id) return false
    const created = new Date(record.createdAt).getTime()
    return (now - created) < 3 * 60 * 1000
  }

  // 编辑倒计时
  function editRemainingSeconds(record, now = Date.now()) {
    if (!record?.createdAt) return 0
    const created = new Date(record.createdAt).getTime()
    const remaining = 3 * 60 * 1000 - (now - created)
    return Math.max(0, Math.floor(remaining / 1000))
  }

  // 登录接口
  async function loginApi(username, password) {
    try {
      const res = await request.post('/login', { username, password })
      if (res.code === 200) {
        const fakeToken = `${res.userId}_${Date.now()}`
        const user = {
          id: res.userId,
          username: res.username,
          role: res.role // 接收后端角色
        }
        login(fakeToken, user)
        return { ok: true, message: res.msg }
      } else {
        return { ok: false, message: res.msg }
      }
    } catch (err) {
      console.error('登录请求异常：', err)
      if (!err.response) {
        return { ok: false, message: '无法连接后端服务，请检查服务或跨域配置' }
      }
      return { ok: false, message: `请求异常，状态码：${err.response.status}` }
    }
  }

  // 忘记密码占位接口
  async function sendCode() {
    return { ok: false, message: '功能暂未开放' }
  }
  async function verifyPhone() {
    return { ok: false, message: '功能暂未开放' }
  }
  async function resetPassword() {
    return { ok: false, message: '功能暂未开放' }
  }

  return {
    token, userInfo, isLoggedIn, roles, roleTableMap,
    isAdmin, currentRole,
    login, logout, logoutReset, reloadUserFromLocal, setUserRole,
    getRoleName, getRoleColor,
    canAccessTable, canDelete, canEditRecord, editRemainingSeconds,
    loginApi, sendCode, verifyPhone, reloadUserFromLocal, resetPassword
  }
})