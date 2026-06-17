<template>
  <aside class="sidebar" :class="{ collapsed: appStore.sidebarCollapsed }">
    <!-- Logo -->
    <div class="sidebar-logo" @click="$router.push('/dashboard')">
      <div class="logo-icon">
        <svg viewBox="0 0 40 40" fill="none" class="logo-svg">
          <path d="M20 4C16 8 8 12 6 18C4 24 8 28 12 30C13 27 16 24 20 22C24 24 27 27 28 30C32 28 36 24 34 18C32 12 24 8 20 4Z" fill="url(#wheat-grad)" opacity="0.9"/>
          <path d="M20 8C18 10 14 13 13 16" stroke="url(#wheat-grad)" stroke-width="1.5" stroke-linecap="round"/>
          <path d="M20 8C22 10 26 13 27 16" stroke="url(#wheat-grad)" stroke-width="1.5" stroke-linecap="round"/>
          <path d="M20 12V32" stroke="url(#wheat-grad)" stroke-width="1.5" stroke-linecap="round"/>
          <defs>
            <linearGradient id="wheat-grad" x1="4" y1="4" x2="36" y2="36">
              <stop stop-color="#d4a853"/>
              <stop offset="1" stop-color="#b8860b"/>
            </linearGradient>
          </defs>
        </svg>
      </div>
      <transition name="page">
        <div v-if="!appStore.sidebarCollapsed" class="logo-text">
          <span class="logo-title">麦 溯</span>
          <span class="logo-subtitle">WHEAT TRACE</span>
        </div>
      </transition>
    </div>

    <!-- Navigation -->
    <nav class="sidebar-nav">
      <router-link
        v-for="item in menuItems"
        :key="item.path"
        :to="item.path"
        class="nav-item"
        :class="{ active: isActive(item.path) }"
      >
        <el-icon :size="20"><component :is="item.icon" /></el-icon>
        <span v-if="!appStore.sidebarCollapsed" class="nav-label">{{ item.label }}</span>
        <div v-if="isActive(item.path)" class="nav-active-indicator"></div>
      </router-link>
    </nav>

    <!-- Collapse Toggle -->
    <div class="sidebar-footer">
      <button class="collapse-btn" @click="appStore.toggleSidebar()">
        <el-icon :size="18">
          <DArrowLeft v-if="!appStore.sidebarCollapsed" />
          <DArrowRight v-else />
        </el-icon>
      </button>
    </div>
  </aside>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const appStore = useAppStore()
const userStore = useUserStore()

const allMenuItems = [
  { path: '/dashboard', label: '数据看板', icon: 'DataAnalysis', allRoles: true },
  { path: '/seed', label: '种子信息', icon: 'Collection', allRoles: true },
  { path: '/planting', label: '种植记录', icon: 'Sunny', allRoles: true },
  { path: '/processing', label: '加工记录', icon: 'SetUp', allRoles: true },
  { path: '/quality', label: '质检记录', icon: 'Checked', allRoles: true },
  { path: '/sales', label: '销售记录', icon: 'Sell', allRoles: true },
  { path: '/trace', label: '追溯查询', icon: 'Search', allRoles: true },
  { path: '/report', label: '统计报表', icon: 'TrendCharts', allRoles: true },
  { path: '/users', label: '用户管理', icon: 'User', adminOnly: true },
]

const menuItems = computed(() => {
  return allMenuItems.filter(item => {
    if (item.adminOnly && !userStore.isAdmin) return false
    return true
  })
})

function isActive(path) {
  return route.path.startsWith(path)
}
</script>

<style scoped>
.sidebar {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 100;
  width: 240px;
  height: 100vh;
  background: rgba(13, 13, 13, 0.85);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-right: 1px solid var(--border);
  display: flex;
  flex-direction: column;
  transition: width 0.35s var(--transition-smooth);
  overflow: hidden;
}

.sidebar.collapsed {
  width: 72px;
}

/* Logo */
.sidebar-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 18px;
  cursor: pointer;
  border-bottom: 1px solid var(--border);
  min-height: 68px;
}

.logo-icon {
  width: 36px;
  height: 36px;
  flex-shrink: 0;
}

.logo-svg {
  width: 100%;
  height: 100%;
}

.logo-text {
  display: flex;
  flex-direction: column;
  gap: 0;
  white-space: nowrap;
}

.logo-title {
  font-size: 1.15rem;
  font-weight: 700;
  background: var(--gold-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 0.15em;
  line-height: 1.2;
}

.logo-subtitle {
  font-size: 0.55rem;
  font-weight: 500;
  color: var(--text-muted);
  letter-spacing: 0.25em;
  line-height: 1;
}

/* Navigation */
.sidebar-nav {
  flex: 1;
  padding: 16px 10px;
  display: flex;
  flex-direction: column;
  gap: 2px;
  overflow-y: auto;
}

.nav-item {
  position: relative;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 11px 14px;
  border-radius: 10px;
  color: var(--text-secondary);
  text-decoration: none;
  font-size: 0.875rem;
  font-weight: 500;
  transition: all 0.25s var(--transition-smooth);
  white-space: nowrap;
  overflow: hidden;
}

.nav-item:hover {
  color: var(--text-primary);
  background: rgba(212, 168, 83, 0.06);
}

.nav-item.active {
  color: var(--primary);
  background: rgba(212, 168, 83, 0.1);
}

.nav-active-indicator {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 20px;
  background: var(--gold-gradient);
  border-radius: 0 2px 2px 0;
}

.nav-label {
  letter-spacing: 0.02em;
}

/* Footer */
.sidebar-footer {
  padding: 12px 18px;
  border-top: 1px solid var(--border);
}

.collapse-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: transparent;
  color: var(--text-muted);
  cursor: pointer;
  transition: all 0.25s var(--transition-smooth);
}

.collapse-btn:hover {
  color: var(--primary);
  border-color: var(--primary);
  background: rgba(212, 168, 83, 0.08);
}
</style>
