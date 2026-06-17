<template>
  <span class="batch-no-cell" @click="handleCopy" :title="'点击复制 ' + code">
    {{ code }}
    <el-icon v-if="showCopied" class="copied-icon" :size="14"><CircleCheck /></el-icon>
  </span>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { CircleCheck } from '@element-plus/icons-vue'

const props = defineProps({
  code: { type: String, required: true }
})

const showCopied = ref(false)

async function handleCopy() {
  try {
    await navigator.clipboard?.writeText(props.code)
    showCopied.value = true
    ElMessage.success({ message: `已复制批次号`, duration: 1200, showClose: false })
    setTimeout(() => { showCopied.value = false }, 1500)
  } catch {
    ElMessage.info('请手动复制批次号')
  }
}
</script>

<style scoped>
.batch-no-cell {
  font-family: 'SF Mono', 'Fira Code', monospace;
  color: var(--primary);
  font-weight: 500;
  font-size: 0.8125rem;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 2px 4px;
  border-radius: 4px;
  transition: all 0.2s;
  user-select: all;
}

.batch-no-cell:hover {
  color: var(--primary-light);
  background: rgba(212, 168, 83, 0.08);
}

.copied-icon {
  color: var(--accent);
  animation: popIn 0.3s var(--transition-smooth);
}

@keyframes popIn {
  from { transform: scale(0); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}
</style>
