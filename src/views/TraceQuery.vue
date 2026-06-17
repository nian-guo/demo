<template>
  <div class="page-container">
    <div class="page-header">
      <h1>追溯查询</h1>
      <p>输入追溯码或扫描二维码，查看小麦种子的完整生命周期</p>
    </div>

    <!-- Search Section -->
    <div class="search-section glass-light">
      <div class="search-box">
        <el-input
          v-model="traceCode"
          placeholder="请输入追溯码，如 WM2026-05-089 A3"
          size="large"
          class="trace-input"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon :size="20"><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" size="large" class="search-btn" @click="handleSearch" :loading="searching">
          <el-icon :size="18"><Search /></el-icon>
          <span>追溯查询</span>
        </el-button>
      </div>

      <!-- QR Code Demo -->
      <div class="qr-section">
        <div class="qr-card">
          <div class="qr-code-placeholder">
            <svg viewBox="0 0 120 120" class="qr-svg">
              <rect x="4" y="4" width="112" height="112" rx="8" fill="#fff" />
              <!-- QR pattern simulation -->
              <g fill="#1a1a1a">
                <rect x="12" y="12" width="28" height="28" rx="2" />
                <rect x="16" y="16" width="20" height="20" rx="1" fill="#fff" />
                <rect x="20" y="20" width="12" height="12" rx="1" fill="#1a1a1a" />
                <rect x="80" y="12" width="28" height="28" rx="2" />
                <rect x="84" y="16" width="20" height="20" rx="1" fill="#fff" />
                <rect x="88" y="20" width="12" height="12" rx="1" fill="#1a1a1a" />
                <rect x="12" y="80" width="28" height="28" rx="2" />
                <rect x="16" y="84" width="20" height="20" rx="1" fill="#fff" />
                <rect x="20" y="88" width="12" height="12" rx="1" fill="#1a1a1a" />
                <!-- Small blocks -->
                <rect v-for="b in qrBlocks" :key="b" :x="b.x" :y="b.y" width="4" height="4" rx="0.5" />
              </g>
            </svg>
          </div>
          <p class="qr-label">扫码追溯</p>
          <p class="qr-hint">移动端扫描种子包装二维码</p>
        </div>
      </div>
    </div>

    <!-- Result Section -->
    <div v-if="showResult" class="result-section">
      <div class="info-cards">
        <div class="info-card glass-light">
          <div class="info-label">种子批次号</div>
          <div class="info-value batch-code" @click="copyCode(result.batchNo)" title="点击复制">{{ result.batchNo }}</div>
        </div>
        <div class="info-card glass-light">
          <div class="info-label">品种</div>
          <div class="info-value">{{ result.variety }}</div>
        </div>
        <div class="info-card glass-light">
          <div class="info-label">产地</div>
          <div class="info-value">{{ result.origin }}</div>
        </div>
        <div class="info-card glass-light">
          <div class="info-label">供种单位</div>
          <div class="info-value">{{ result.supplier }}</div>
        </div>
        <div class="info-card glass-light">
          <div class="info-label">当前状态</div>
          <div class="info-value">
            <el-tag :type="statusTagType(result.status)" size="small" effect="dark" round>
              {{ result.statusText }}
            </el-tag>
          </div>
        </div>
      </div>

      <div class="trace-timeline-card glass-light">
        <div class="timeline-header">
          <h3>全流程追溯时间线</h3>
          <span class="timeline-subtitle">共 {{ result.timeline.length }} 个环节</span>
        </div>
        <div class="timeline-container">
          <TraceTimeline :steps="result.timeline" />
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-if="!showResult && !searching && searched" class="empty-state glass-light">
      <el-icon :size="64" color="var(--text-muted)"><Search /></el-icon>
      <h3>未找到相关追溯信息</h3>
      <p>请检查追溯码是否正确，或尝试其他批次号</p>
    </div>

    <!-- Initial State -->
    <div v-if="!showResult && !searched" class="empty-state glass-light">
      <div class="empty-icon">
        <svg viewBox="0 0 100 100" width="80" height="80" fill="none">
          <circle cx="50" cy="50" r="45" stroke="var(--border)" stroke-width="1.5" stroke-dasharray="8 4"/>
          <path d="M35 42C35 30 65 30 65 42C65 58 50 62 50 62C50 62 35 58 35 42Z" stroke="var(--primary)" stroke-width="2" fill="rgba(212,168,83,0.1)"/>
          <circle cx="50" cy="74" r="3" fill="var(--primary)" opacity="0.6"/>
        </svg>
      </div>
      <h3>输入追溯码开始查询</h3>
      <p>支持批次号精确搜索或关键词模糊搜索</p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import TraceTimeline from '@/components/common/TraceTimeline.vue'
// 引入全局请求工具（项目通用 request）
import request from '@/utils/request'

const route = useRoute()
const traceCode = ref('')
const searching = ref(false)
const searched = ref(false)
const showResult = ref(false)

// 数据容器：和后端 VO 字段完全对齐
const result = reactive({
  batchNo: '',
  variety: '',
  origin: '',
  supplier: '',
  status: '',
  statusText: '',
  timeline: []
})

// 模拟二维码点位（保留原有UI）
const qrBlocks = Array.from({ length: 35 }, () => ({
  x: 44 + Math.floor(Math.random() * 60),
  y: 44 + Math.floor(Math.random() * 60)
}))

// 标签类型映射（适配 Element Plus 规范）
function statusTagType(status) {
  const map = {
    planting: 'warning',
    processing: '',
    inspect: 'info',
    selling: 'danger',
    sold: 'success'
  }
  return map[status] || 'info'
}

// 复制批次号
function copyCode(code) {
  navigator.clipboard?.writeText(code).then(() => {
    ElMessage.success({ message: `已复制追溯码: ${code}`, duration: 1500 })
  }).catch(() => {
    ElMessage.info('复制失败，请手动复制')
  })
}

// 【核心修改】对接后端真实追溯接口
async function handleSearch() {
  const code = traceCode.value.trim()
  if (!code) return

  searching.value = true
  searched.value = true
  showResult.value = false

  try {
    // 调用后端接口：/api/trace/query
    const res = await request.get('/trace/query', {
      params: {
        batchNo: code
      }
    })

    if (res.code === 200) {
      // 赋值后端返回数据
      const data = res.data
      result.batchNo = data.batchNo
      result.variety = data.variety
      result.origin = data.origin
      result.supplier = data.supplier
      result.status = data.status
      result.statusText = data.statusText
      result.timeline = data.timeline

      showResult.value = true
    } else {
      // 接口返回失败（无数据）
      ElMessage.warning(res.msg || '未找到相关追溯信息')
    }
  } catch (err) {
    // 接口请求异常（后端未启动/网络错误）
    ElMessage.error('接口请求失败，请检查后端服务')
    console.error('追溯接口异常：', err)
  } finally {
    searching.value = false
  }
}

// 路由回显（地址栏携带追溯码自动查询）
onMounted(() => {
  if (route.query.code) {
    traceCode.value = route.query.code
    handleSearch()
  }
})
</script>

<style scoped>
.page-container { max-width: var(--page-max-width); }
.page-header { margin-bottom: 24px; }
.page-header h1 { font-size: 1.5rem; font-weight: 700; }
.page-header p { font-size: 0.8125rem; color: var(--text-muted); margin-top: 4px; }

/* Search Section */
.search-section {
  display: flex; gap: 28px; padding: 28px; border-radius: 16px;
  margin-bottom: 24px; flex-wrap: wrap;
}

.search-box {
  flex: 1; display: flex; gap: 12px; align-items: center; min-width: 300px;
}
.trace-input { flex: 1; }
.trace-input :deep(.el-input__wrapper) {
  background: var(--bg-surface) !important; border-radius: 12px;
  box-shadow: 0 0 0 1px var(--border) !important; padding: 6px 16px; height: 52px;
}
.trace-input :deep(.el-input__inner) { font-size: 0.9375rem; font-family: 'SF Mono', 'Fira Code', monospace; }
.search-btn {
  height: 52px; border-radius: 12px; padding: 0 28px; font-size: 0.9375rem;
  font-weight: 600; background: var(--gold-gradient) !important; border: none !important;
  white-space: nowrap; gap: 6px;
}

/* QR Section */
.qr-section { flex-shrink: 0; }
.qr-card {
  text-align: center; padding: 16px 20px; border-radius: 12px;
  background: var(--bg-surface); border: 1px solid var(--border);
}
.qr-code-placeholder {
  width: 120px; height: 120px; margin: 0 auto 8px;
}
.qr-svg { width: 100%; height: 100%; }
.qr-label { font-size: 0.8125rem; font-weight: 600; color: var(--text-primary); }
.qr-hint { font-size: 0.6875rem; color: var(--text-muted); margin-top: 2px; }

/* Info Cards */
.info-cards {
  display: grid; grid-template-columns: repeat(5, 1fr); gap: 14px;
  margin-bottom: 20px;
}
@media (max-width: 900px) { .info-cards { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 600px) { .info-cards { grid-template-columns: 1fr; } }

.info-card {
  padding: 18px 20px; border-radius: 14px; text-align: center;
}
.info-label { font-size: 0.75rem; color: var(--text-muted); margin-bottom: 6px; }
.info-value { font-size: 1rem; font-weight: 600; }
.batch-code { font-family: 'SF Mono', 'Fira Code', monospace; color: var(--primary); font-size: 0.875rem; cursor: pointer; transition: all 0.2s; }
.batch-code:hover { color: var(--primary-light); }

/* Timeline Card */
.trace-timeline-card {
  border-radius: 16px; padding: 28px;
}
.timeline-header {
  display: flex; align-items: baseline; justify-content: space-between;
  margin-bottom: 24px; padding-bottom: 16px;
  border-bottom: 1px solid var(--border);
}
.timeline-header h3 { font-size: 1.0625rem; font-weight: 600; }
.timeline-subtitle { font-size: 0.8125rem; color: var(--text-muted); }

/* Empty State */
.empty-state {
  text-align: center; padding: 80px 40px; border-radius: 16px;
}
.empty-state h3 { font-size: 1.125rem; font-weight: 600; margin: 16px 0 8px; }
.empty-state p { color: var(--text-muted); font-size: 0.875rem; }
.empty-icon { margin-bottom: 8px; opacity: 0.5; }
</style>