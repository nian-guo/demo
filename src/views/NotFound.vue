<template>
  <div class="not-found-page">
    <div class="bg-orb"></div>
    <div class="bg-grid"></div>

    <div class="nf-content">
      <div class="nf-code">
        <span class="nf-digit">4</span>
        <div class="nf-icon">
          <svg viewBox="0 0 60 60" fill="none" class="wheat-svg">
            <path d="M30 3C24 9 12 18 9 27C6 36 12 42 18 45C19.5 40.5 24 36 30 33C36 36 40.5 40.5 42 45C48 42 54 36 51 27C48 18 36 9 30 3Z" fill="url(#wheat-404)" opacity="0.8"/>
            <path d="M30 9C27 12 21 16.5 19.5 21" stroke="#d4a853" stroke-width="1.5" stroke-linecap="round"/>
            <path d="M30 9C33 12 39 16.5 40.5 21" stroke="#d4a853" stroke-width="1.5" stroke-linecap="round"/>
            <path d="M30 18V48" stroke="#d4a853" stroke-width="1.5" stroke-linecap="round"/>
            <defs>
              <linearGradient id="wheat-404" x1="6" y1="3" x2="54" y2="54">
                <stop stop-color="#d4a853"/><stop offset="1" stop-color="#8b6914"/>
              </linearGradient>
            </defs>
          </svg>
        </div>
        <span class="nf-digit">4</span>
      </div>

      <h1 class="nf-title">页面未找到</h1>
      <p class="nf-desc">您访问的页面不存在或已被移除<br/>请检查URL是否正确，或返回首页</p>

      <div class="nf-actions">
        <el-button type="primary" size="large" @click="$router.push('/dashboard')">
          <el-icon><HomeFilled /></el-icon> 返回首页
        </el-button>
        <el-button size="large" @click="$router.go(-1)">
          <el-icon><Back /></el-icon> 返回上页
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { HomeFilled, Back } from '@element-plus/icons-vue'
</script>

<style scoped>
.not-found-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: var(--bg-dark);
  position: relative;
  overflow: hidden;
}

.bg-orb {
  position: absolute;
  width: 500px; height: 500px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(212,168,83,0.06), transparent 70%);
  top: 50%; left: 50%;
  transform: translate(-50%, -50%);
  animation: float 8s ease-in-out infinite;
}

.bg-grid {
  position: absolute; inset: 0;
  background-image:
    linear-gradient(rgba(212,168,83,0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(212,168,83,0.03) 1px, transparent 1px);
  background-size: 60px 60px;
}

.nf-content {
  text-align: center;
  position: relative;
  z-index: 1;
}

.nf-code {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 24px;
}

.nf-digit {
  font-size: 8rem;
  font-weight: 900;
  line-height: 1;
  background: var(--gold-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.nf-icon {
  width: 80px; height: 80px;
  opacity: 0.6;
}

.wheat-svg { width: 100%; height: 100%; }

.nf-title {
  font-size: 1.5rem; font-weight: 600;
  color: var(--text-primary); margin-bottom: 12px;
}

.nf-desc {
  font-size: 0.9375rem; color: var(--text-secondary);
  line-height: 1.8; margin-bottom: 36px;
}

.nf-actions {
  display: flex; gap: 12px; justify-content: center;
}
</style>
