<template>
  <header class="header-bar">
    <!-- Breadcrumb -->
    <div class="header-left">
      <div class="breadcrumb">
        <el-icon :size="16" color="var(--primary)"><HomeFilled /></el-icon>
        <span class="breadcrumb-sep">/</span>
        <span class="breadcrumb-current">{{ currentTitle }}</span>
      </div>
    </div>

    <!-- Right Actions -->
    <div class="header-right">
      <!-- Search -->
      <div class="global-search">
        <el-input
          v-model="searchText"
          placeholder="搜索种子批次号..."
          :prefix-icon="Search"
          size="default"
          class="search-input"
          @keyup.enter="handleSearch"
        />
      </div>

      <!-- Notifications -->
      <el-badge :value="3" :max="9" class="header-badge">
        <button class="header-btn">
          <el-icon :size="20"><Bell /></el-icon>
        </button>
      </el-badge>

      <!-- User Menu -->
      <el-dropdown trigger="click" placement="bottom-end">
        <div class="user-info">
          <div class="user-avatar">
            <span>{{ userStore.userInfo?.name?.charAt(0) || '管' }}</span>
          </div>
          <div class="user-meta">
            <span class="user-name">{{ userStore.userInfo?.name || '管理员' }}</span>
            <span class="user-role">{{ userStore.getRoleName(userStore.userInfo?.role || 'admin') }}</span>
          </div>
          <el-icon :size="14" class="user-arrow"><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item>
              <el-icon><User /></el-icon> 个人信息
            </el-dropdown-item>
            <el-dropdown-item>
              <el-icon><Setting /></el-icon> 系统设置
            </el-dropdown-item>
            <el-dropdown-item divided @click="handleLogout">
              <el-icon><SwitchButton /></el-icon> 退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </header>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { Search } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const searchText = ref('')

const currentTitle = computed(() => route.meta?.title || '')

function handleSearch() {
  if (searchText.value.trim()) {
    router.push({ path: '/trace', query: { code: searchText.value.trim() } })
  }
}

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.header-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  padding: 0 28px;
  background: rgba(13, 13, 13, 0.7);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-bottom: 1px solid var(--border);
  position: sticky;
  top: 0;
  z-index: 50;
}

/* Breadcrumb */
.header-left {
  display: flex;
  align-items: center;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.8125rem;
  color: var(--text-muted);
}

.breadcrumb-sep {
  color: var(--border-light);
}

.breadcrumb-current {
  color: var(--text-primary);
  font-weight: 500;
}

/* Right Section */
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.global-search {
  width: 260px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 8px;
  background: var(--bg-surface) !important;
  box-shadow: 0 0 0 1px var(--border) !important;
  padding: 0 12px;
  height: 36px;
}

.search-input :deep(.el-input__inner) {
  font-size: 0.8125rem;
}

/* Header Buttons */
.header-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: transparent;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.25s var(--transition-smooth);
}

.header-btn:hover {
  color: var(--primary);
  border-color: var(--border-light);
  background: var(--bg-card);
}

.header-badge {
  display: flex;
}

/* User Info */
.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 4px 12px 4px 4px;
  border: 1px solid var(--border);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.25s var(--transition-smooth);
}

.user-info:hover {
  border-color: var(--border-light);
  background: var(--bg-card);
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: var(--gold-gradient);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.875rem;
  font-weight: 600;
  color: #1a1a1a;
}

.user-meta {
  display: none;
  flex-direction: column;
  line-height: 1.2;
}

@media (min-width: 900px) {
  .user-meta {
    display: flex;
  }
}

.user-name {
  font-size: 0.8125rem;
  font-weight: 500;
  color: var(--text-primary);
}

.user-role {
  font-size: 0.6875rem;
  color: var(--text-muted);
}

.user-arrow {
  color: var(--text-muted);
  display: none;
}

@media (min-width: 900px) {
  .user-arrow {
    display: block;
  }
}
</style>
