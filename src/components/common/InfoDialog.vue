<template>
  <el-dialog v-model="visible" :title="title" :width="width" class="custom-dialog">
    <div class="info-grid">
      <div v-for="field in fields" :key="field.key" class="info-item" :class="{ 'info-full': field.full }">
        <span class="info-label">{{ field.label }}</span>
        <span class="info-value" :class="field.valueClass">
          <slot :name="`field-${field.key}`" :field="field" :value="field.value">
            <el-tag v-if="field.tag" :type="field.tag" size="small" effect="dark" round>{{ field.value }}</el-tag>
            <span v-else>{{ field.value || '-' }}</span>
          </slot>
        </span>
      </div>
    </div>
    <template #footer>
      <el-button @click="visible = false">关 闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  title: { type: String, default: '详细信息' },
  fields: { type: Array, default: () => [] },
  width: { type: String, default: '560px' }
})

const emit = defineEmits(['update:modelValue'])

const visible = computed({
  get: () => props.modelValue,
  set: (v) => emit('update:modelValue', v)
})
</script>

<style scoped>
.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px 24px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-full {
  grid-column: 1 / -1;
}

.info-label {
  font-size: 0.75rem;
  color: var(--text-muted);
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.info-value {
  font-size: 0.9375rem;
  color: var(--text-primary);
  font-weight: 500;
}

.mono {
  font-family: 'SF Mono', 'Fira Code', monospace;
  color: var(--primary);
  font-size: 0.8125rem;
}

.green { color: var(--accent); }
</style>
