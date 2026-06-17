<template>
  <div class="trace-timeline">
    <div
      v-for="(step, index) in steps"
      :key="index"
      class="timeline-step"
      :class="{ completed: step.completed, current: step.current }"
    >
      <div class="timeline-node">
        <div class="node-dot">
          <el-icon v-if="step.completed" :size="14"><Check /></el-icon>
          <div v-else class="dot-pulse"></div>
        </div>
        <div v-if="index < steps.length - 1" class="node-line" :class="{ filled: step.completed }"></div>
      </div>
      <div class="timeline-content">
        <div class="timeline-title">{{ step.title }}</div>
        <div class="timeline-date">{{ step.date }}</div>
        <div v-if="step.description" class="timeline-desc">{{ step.description }}</div>
        <div v-if="step.tags" class="timeline-tags">
          <span v-for="tag in step.tags" :key="tag" class="timeline-tag">{{ tag }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  steps: {
    type: Array,
    default: () => []
    // { title, date, description, tags, completed, current }
  }
})
</script>

<style scoped>
.trace-timeline {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.timeline-step {
  display: flex;
  gap: 16px;
  position: relative;
}

.timeline-node {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 24px;
  flex-shrink: 0;
}

.node-dot {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-surface);
  border: 2px solid var(--border-light);
  z-index: 1;
  transition: all 0.3s var(--transition-smooth);
}

.timeline-step.completed .node-dot {
  background: var(--accent);
  border-color: var(--accent);
  color: #fff;
}

.timeline-step.current .node-dot {
  background: var(--primary);
  border-color: var(--primary);
  box-shadow: 0 0 0 4px rgba(212, 168, 83, 0.2);
}

.dot-pulse {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--primary);
}

.node-line {
  width: 2px;
  flex: 1;
  min-height: 40px;
  background: var(--border-light);
  margin: 4px 0;
  transition: background 0.3s;
}

.node-line.filled {
  background: var(--accent);
}

.timeline-content {
  padding-bottom: 24px;
  flex: 1;
}

.timeline-step:last-child .timeline-content {
  padding-bottom: 0;
}

.timeline-title {
  font-size: 0.9375rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 2px;
}

.timeline-step.completed .timeline-title {
  color: var(--text-secondary);
}

.timeline-date {
  font-size: 0.75rem;
  color: var(--text-muted);
  margin-bottom: 4px;
}

.timeline-desc {
  font-size: 0.8125rem;
  color: var(--text-secondary);
  line-height: 1.5;
}

.timeline-tags {
  display: flex;
  gap: 6px;
  margin-top: 8px;
  flex-wrap: wrap;
}

.timeline-tag {
  font-size: 0.6875rem;
  padding: 3px 10px;
  border-radius: 4px;
  background: rgba(212, 168, 83, 0.1);
  color: var(--primary);
  border: 1px solid rgba(212, 168, 83, 0.2);
}
</style>
