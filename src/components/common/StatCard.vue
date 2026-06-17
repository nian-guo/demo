<template>
  <div class="stat-card" :class="{ 'stat-card-hover': hoverable }">
    <div class="stat-card-header">
      <div class="stat-icon-box" :style="{ background: `${iconBg}20`, color: iconBg }">
        <el-icon :size="20"><component :is="icon" /></el-icon>
      </div>
      <div v-if="trend" class="stat-trend" :class="trend > 0 ? 'up' : 'down'">
        <el-icon :size="14">
          <CaretTop v-if="trend > 0" />
          <CaretBottom v-else />
        </el-icon>
        <span>{{ Math.abs(trend) }}%</span>
      </div>
    </div>
    <div class="stat-value">
      <span class="stat-number">{{ animatedValue }}</span>
      <span v-if="unit" class="stat-unit">{{ unit }}</span>
    </div>
    <div class="stat-label">{{ label }}</div>
    <div v-if="description" class="stat-desc">{{ description }}</div>
    <!-- Decorative line -->
    <div class="stat-line" :style="{ background: iconBg }"></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const props = defineProps({
  icon: { type: String, required: true },
  label: { type: String, required: true },
  value: { type: [Number, String], default: 0 },
  unit: { type: String, default: '' },
  trend: { type: Number, default: 0 },
  iconBg: { type: String, default: '#d4a853' },
  description: { type: String, default: '' },
  hoverable: { type: Boolean, default: true }
})

const animatedValue = ref(0)

onMounted(() => {
  const target = typeof props.value === 'string' ? parseInt(props.value) : props.value
  if (typeof target === 'number' && !isNaN(target)) {
    animateCount(0, target, 1200)
  } else {
    animatedValue.value = props.value
  }
})

function animateCount(start, end, duration) {
  const startTime = performance.now()
  function step(currentTime) {
    const elapsed = currentTime - startTime
    const progress = Math.min(elapsed / duration, 1)
    const eased = 1 - Math.pow(1 - progress, 3)
    animatedValue.value = Math.floor(start + (end - start) * eased)
    if (progress < 1) {
      requestAnimationFrame(step)
    } else {
      animatedValue.value = end
    }
  }
  requestAnimationFrame(step)
}
</script>

<style scoped>
.stat-card {
  position: relative;
  padding: 24px;
  border-radius: 16px;
  background: var(--bg-card);
  border: 1px solid var(--border);
  overflow: hidden;
  transition: all 0.35s var(--transition-smooth);
}

.stat-card-hover:hover {
  transform: translateY(-2px);
  border-color: var(--border-light);
  box-shadow: var(--card-shadow-hover);
}

.stat-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.stat-icon-box {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 2px;
  font-size: 0.75rem;
  font-weight: 600;
  padding: 3px 8px;
  border-radius: 6px;
}

.stat-trend.up {
  color: #4ade80;
  background: rgba(74, 222, 128, 0.1);
}

.stat-trend.down {
  color: #f87171;
  background: rgba(248, 113, 113, 0.1);
}

.stat-value {
  display: flex;
  align-items: baseline;
  gap: 4px;
  margin-bottom: 4px;
}

.stat-number {
  font-size: 2rem;
  font-weight: 700;
  letter-spacing: -0.03em;
  color: var(--text-primary);
  font-variant-numeric: tabular-nums;
}

.stat-unit {
  font-size: 0.8125rem;
  color: var(--text-muted);
  font-weight: 500;
}

.stat-label {
  font-size: 0.8125rem;
  color: var(--text-secondary);
  font-weight: 500;
}

.stat-desc {
  margin-top: 4px;
  font-size: 0.75rem;
  color: var(--text-muted);
}

.stat-line {
  position: absolute;
  bottom: 0;
  left: 24px;
  right: 24px;
  height: 2px;
  border-radius: 1px;
  opacity: 0.3;
}
</style>
