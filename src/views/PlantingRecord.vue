<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <h1>种植记录</h1>
        <p v-if="perm.canAdd('planting')">管理小麦种子种植环节的所有记录</p>
        <p v-else class="readonly-hint">只读模式 — 仅种植人员和管理员可操作</p>
      </div>
      <el-button v-if="perm.canAdd('planting')" type="primary" :icon="Plus" @click="openAddDialog">新增记录</el-button>
    </div>

    <div class="filter-bar glass-light">
      <el-input v-model="searchKey" placeholder="搜索批次号..." :prefix-icon="Search" clearable class="filter-input" @input="onFilterChange" />
      <el-select v-model="filterBase" placeholder="种植基地" clearable class="filter-select" @change="onFilterChange">
        <el-option label="豫北试验田" value="豫北试验田" />
        <el-option label="豫中示范基地" value="豫中示范基地" />
        <el-option label="豫南良种场" value="豫南良种场" />
      </el-select>
      <el-button :icon="Download" @click="handleExport">导出</el-button>
    </div>

    <div class="table-card glass-light">
      <el-table :data="pagedData" style="width: 100%" v-loading="loading">
        <el-table-column prop="batchNo" label="种子批次号" min-width="150">
          <template #default="{ row }"><BatchNoCell :code="row.batchNo" /></template>
        </el-table-column>
        <el-table-column prop="base" label="种植基地" min-width="120" />
        <el-table-column prop="plot" label="地块编号" min-width="100" />
        <el-table-column prop="area" label="面积(亩)" width="90" />
        <el-table-column prop="date" label="种植日期" width="110" />
        <el-table-column prop="fertilizer" label="施肥记录" min-width="140" />
        <el-table-column prop="operator" label="操作人" min-width="100" />
        <el-table-column label="操作" :width="perm.isAdmin ? 200 : 120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="showDetail(row)">详情</el-button>
            <el-button v-if="perm.canEdit('planting', row)" link type="primary" size="small" @click="openEditDialog(row)">
              编辑<span v-if="!perm.isAdmin" class="edit-timer">{{ perm.formatRemaining(row) }}</span>
            </el-button>
            <el-popconfirm v-if="perm.canDelete()" title="确定删除该记录吗？" @confirm="handleDelete(row)">
              <template #reference><el-button link type="danger" size="small">删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <div class="table-footer">
        <span class="table-summary">共 {{ filteredData.length }} 条记录</span>
        <el-pagination v-model:current-page="currentPage" :total="filteredData.length" :page-size="pageSize" layout="prev, pager, next" background small />
      </div>
    </div>

    <el-dialog v-model="showFormDialog" :title="isEditing ? '编辑种植记录' : '新增种植记录'" width="560px" class="custom-dialog">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="种子批次号" prop="batchNo">
          <el-select v-model="form.batchNo" filterable placeholder="请先选择种子批次" style="width:100%">
            <el-option v-for="bn in availableBatchNos" :key="bn" :label="bn" :value="bn" />
          </el-select>
        </el-form-item>
        <el-form-item label="种植基地" prop="base">
          <el-select v-model="form.base" style="width:100%">
            <el-option label="豫北试验田" value="豫北试验田" />
            <el-option label="豫中示范基地" value="豫中示范基地" />
            <el-option label="豫南良种场" value="豫南良种场" />
          </el-select>
        </el-form-item>
        <el-form-item label="地块编号" prop="plot"><el-input v-model="form.plot" /></el-form-item>
        <el-form-item label="种植面积(亩)" prop="area"><el-input-number v-model="form.area" :min="0" style="width:100%" /></el-form-item>
        <el-form-item label="施肥记录"><el-input v-model="form.fertilizer" /></el-form-item>
        <el-form-item label="操作人"><el-input v-model="form.operator" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showFormDialog = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">确 定</el-button>
      </template>
    </el-dialog>

    <InfoDialog v-model="showDetailDialog" :title="detailTitle" :fields="detailFields" />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Plus, Download } from '@element-plus/icons-vue'
import InfoDialog from '@/components/common/InfoDialog.vue'
import BatchNoCell from '@/components/common/BatchNoCell.vue'
import { usePermission } from '@/utils/permission'
import request from '@/utils/request'

const perm = usePermission()

// 筛选
const searchKey = ref('')
const filterBase = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)

// 列表数据（从后端获取）
const plantings = ref([])

// 批次号（从种子表获取）
const availableBatchNos = ref([])

// 加载种植记录
async function loadPlantings() {
  loading.value = true
  try {
    const res = await request.get('/planting/list')
    if (res.code === 200) {
      plantings.value = res.data
    }
  } catch (err) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

// 加载种子批次号
async function loadBatchNos() {
  try {
    const res = await request.get('/seed/batch-nos')
    if (res.code === 200) {
      availableBatchNos.value = res.data
    }
  } catch (err) {}
}

// 筛选后数据
const filteredData = computed(() => {
  let data = plantings.value
  if (searchKey.value) {
    const kw = searchKey.value.toLowerCase()
    data = data.filter(r =>
      r.batchNo?.toLowerCase().includes(kw) ||
      r.base?.includes(kw) ||
      r.operator?.includes(kw)
    )
  }
  if (filterBase.value) {
    data = data.filter(r => r.base === filterBase.value)
  }
  return data
})

const pagedData = computed(() => {
  return filteredData.value.slice(
    (currentPage.value - 1) * pageSize.value,
    currentPage.value * pageSize.value
  )
})

function onFilterChange() {
  currentPage.value = 1
}

// 表单
const showFormDialog = ref(false)
const isEditing = ref(false)
const editingId = ref(null)
const saving = ref(false)
const formRef = ref()

const defaultForm = () => ({
  batchNo: '',
  base: '',
  plot: '',
  area: 0,
  fertilizer: '',
  operator: ''
})

const form = reactive(defaultForm())

const formRules = {
  batchNo: [{ required: true, message: '请选择种子批次号', trigger: 'change' }],
  base: [{ required: true, message: '请选择种植基地', trigger: 'change' }],
  plot: [{ required: true, message: '请输入地块编号', trigger: 'blur' }],
  area: [{ required: true, message: '请输入面积', trigger: 'blur' }],
}

// 新增
function openAddDialog() {
  if (!perm.canAdd('planting')) return ElMessage.warning('无权限')
  isEditing.value = false
  editingId.value = null
  Object.assign(form, defaultForm())
  showFormDialog.value = true
}

// 编辑
function openEditDialog(row) {
  if (!perm.canEdit('planting', row)) return ElMessage.warning('无权限或已过期')
  isEditing.value = true
  editingId.value = row.id
  Object.assign(form, row)
  showFormDialog.value = true
}

// 保存（新增 / 修改）
async function handleSave() {
  await formRef.value?.validate(async valid => {
    if (!valid) return
    saving.value = true
    try {
      if (isEditing.value) {
        await request.put(`/planting/update/${editingId.value}`, form)
        ElMessage.success('修改成功')
      } else {
        await request.post('/planting/add', form)
        ElMessage.success('新增成功')
      }
      showFormDialog.value = false
      loadPlantings()
    } catch (err) {
      ElMessage.error('保存失败')
    } finally {
      saving.value = false
    }
  })
}

// 详情
const showDetailDialog = ref(false)
const detailTitle = ref('')
const detailFields = ref([])

function showDetail(row) {
  detailTitle.value = `种植记录 — ${row.batchNo}`
  detailFields.value = [
    { label: '批次号', value: row.batchNo },
    { label: '种植基地', value: row.base },
    { label: '地块', value: row.plot },
    { label: '面积', value: row.area + ' 亩' },
    { label: '种植日期', value: row.date },
    { label: '施肥', value: row.fertilizer },
    { label: '操作人', value: row.operator }
  ]
  showDetailDialog.value = true
}

// 删除
async function handleDelete(row) {
  if (!perm.canDelete()) return ElMessage.warning('仅管理员可删除')
  try {
    await request.delete(`/planting/delete/${row.id}`)
    ElMessage.success('删除成功')
    loadPlantings()
  } catch (err) {
    ElMessage.error('删除失败')
  }
}

// 导出
function handleExport() {
  ElMessage.success('正在导出...')
  window.open('/planting/export')
}

// 页面加载时获取数据
onMounted(() => {
  loadPlantings()
  loadBatchNos()
})
</script>

<style scoped>
.page-container { max-width: var(--page-max-width); }
.page-header { display: flex; align-items: flex-start; justify-content: space-between; margin-bottom: 20px; flex-wrap: wrap; gap: 12px; }
.page-header h1 { font-size: 1.5rem; font-weight: 700; }
.page-header p { font-size: 0.8125rem; color: var(--text-muted); margin-top: 4px; }
.readonly-hint { color: #e8c44a !important; }
.filter-bar { display: flex; gap: 10px; padding: 14px 18px; border-radius: 12px; margin-bottom: 18px; flex-wrap: wrap; }
.filter-input { width: 240px; }
.filter-select { width: 160px; }
.table-card { border-radius: 14px; padding: 4px; overflow: hidden; }
.table-footer { display: flex; align-items: center; justify-content: space-between; padding: 12px 16px; border-top: 1px solid var(--border); }
.table-summary { font-size: 0.75rem; color: var(--text-muted); }
.edit-timer { font-size: 0.625rem; color: var(--text-muted); margin-left: 2px; }
</style>