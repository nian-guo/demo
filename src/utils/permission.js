import { ref, computed, onUnmounted } from 'vue'
import { useUserStore } from '@/stores/user'

// 模块级响应式定时器 — 驱动倒计时实时刷新
const tick = ref(Date.now())
const timer = setInterval(() => { tick.value = Date.now() }, 1000)

/**
 * 权限控制 composable
 * 规则：
 *  - 管理员：所有表全权限（增删改查）
 *  - 其他角色：仅可在自己所属的表增加记录，且3分钟内可修改自己添加的记录，不可删除
 */
export function usePermission() {
  const userStore = useUserStore()

  const isAdmin = computed(() => userStore.isAdmin)
  const currentRole = computed(() => userStore.currentRole)

  function canAdd(table) {
    return userStore.canAccessTable(table)
  }

  function canDelete() {
    return userStore.canDelete()
  }

  /**
   * 检查能否编辑此条记录
   * 通过 tick.value 注入响应式依赖，使模板每秒重新求值
   */
  function canEdit(table, record) {
    if (!userStore.canAccessTable(table)) return false
    return userStore.canEditRecord(record, tick.value)
  }

  function editRemaining(record) {
    return userStore.editRemainingSeconds(record, tick.value)
  }

  // 格式化剩余时间（实时倒数）
  function formatRemaining(record) {
    const secs = editRemaining(record)
    if (secs <= 0) return '已超时'
    const m = Math.floor(secs / 60)
    const s = secs % 60
    return m > 0 ? `${m}分${s}秒` : `${s}秒`
  }

  function creatorStamp() {
    // 兜底：未登录时禁止创建记录
    if (!userStore.userInfo?.id) return null
    return {
      createdBy: userStore.userInfo.id,
      createdAt: new Date().toISOString()
    }
  }

  // 【新增1】页面销毁时清除定时器，防止内存泄漏
  onUnmounted(() => {
    clearInterval(timer)
  })

  // 【新增2】登录成功同步角色到本地存储（解决刷新丢失权限）
  function syncRoleToLocal() {
    localStorage.setItem('userRole', userStore.currentRole)
    localStorage.setItem('isAdminFlag', userStore.isAdmin)
  }

  // 【新增3】页面初始化读取本地缓存恢复角色（解决刷新识别不到管理员）
  function loadRoleFromLocal() {
    const savedRole = localStorage.getItem('userRole')
    const savedAdmin = localStorage.getItem('isAdminFlag') === 'true'
    if (savedRole) {
      userStore.setUserRole(savedRole, savedAdmin)
    }
  }

  // 【新增4】退出登录清空权限缓存
  function clearPermissionCache() {
    localStorage.removeItem('userRole')
    localStorage.removeItem('isAdminFlag')
    userStore.logoutReset()
  }

  return {
    userStore,
    isAdmin,
    currentRole,
    canAdd,
    canDelete,
    canEdit,
    editRemaining,
    formatRemaining,
    creatorStamp,
    syncRoleToLocal,
    loadRoleFromLocal,
    clearPermissionCache
  }
}