<template>
  <!-- 模板、样式 完全保留你原有代码，无需修改 -->
  <div class="page-container">
    <div class="page-header">
      <div>
        <h1>加工记录</h1>
        <p v-if="perm.canAdd('processing')">管理小麦种子加工环节的所有记录</p>
        <p v-else class="readonly-hint">只读模式 — 仅加工人员和管理员可操作</p>
      </div>
      <el-button v-if="perm.canAdd('processing')" type="primary" :icon="Plus" @click="openAddDialog">新增记录</el-button>
    </div>

    <div class="filter-bar glass-light">
      <el-input v-model="searchKey" placeholder="搜索批次号..." :prefix-icon="Search" clearable class="filter-input" @input="onFilterChange" />
      <el-select v-model="filterType" placeholder="加工类型" clearable class="filter-select" @change="onFilterChange">
        <el-option label="精选加工" value="精选加工" /><el-option label="包衣处理" value="包衣处理" /><el-option label="烘干处理" value="烘干处理" />
      </el-select>
      <el-button :icon="Download" @click="handleExport">导出</el-button>
    </div>

    <div class="table-card glass-light">
      <el-table :data="pagedData" style="width: 100%" v-loading="loading">
        <el-table-column prop="batchNo" label="种子批次号" min-width="150">
          <template #default="{ row }"><BatchNoCell :code="row.batchNo" /></template>
        </el-table-column>
        <el-table-column prop="type" label="加工类型" width="100">
          <template #default="{ row }"><el-tag size="small" effect="dark" round>{{ row.type }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="date" label="加工日期" width="110" />
        <el-table-column prop="process" label="加工工艺" min-width="150" />
        <el-table-column prop="inputQty" label="投入量(吨)" min-width="100" />
        <el-table-column prop="outputQty" label="产出量(吨)" min-width="100" />
        <el-table-column prop="rate" label="出成率" width="90" />
        <el-table-column prop="operator" label="操作人" min-width="100" />
        <el-table-column label="操作" :width="perm.isAdmin ? 200 : 120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="showDetail(row)">详情</el-button>
            <el-button v-if="perm.canEdit('processing', row)" link type="primary" size="small" @click="openEditDialog(row)">
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
        <el-pagination v-model:current-page="currentPage" :total="filteredData.length" :page-size="pageSize" layout="prev, pager, next" size="small" />
      </div>
    </div>

    <el-dialog v-model="showFormDialog" :title="isEditing ? '编辑加工记录' : '新增加工记录'" width="560px" class="custom-dialog">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item prop="batchNo" label="种子批次号">
          <el-select v-model="form.batchNo" filterable placeholder="请先选择已种植的批次" style="width:100%">
            <el-option v-for="bn in availableBatchNos" :key="bn" :label="bn" :value="bn" />
          </el-select>
        </el-form-item>
        <el-form-item prop="type" label="加工类型">
          <el-select v-model="form.type" style="width:100%">
            <el-option label="精选加工" value="精选加工" /><el-option label="包衣处理" value="包衣处理" /><el-option label="烘干处理" value="烘干处理" />
          </el-select>
        </el-form-item>
        <el-form-item prop="process" label="加工工艺"><el-input v-model="form.process" /></el-form-item>
        <el-form-item label="投入量(吨)"><el-input-number v-model="form.inputQty" :min="0" style="width:100%" /></el-form-item>
        <el-form-item label="产出量(吨)"><el-input-number v-model="form.outputQty" :min="0" style="width:100%" /></el-form-item>
        <!-- 补充隐藏字段：加工日期、操作人（后端自动/前端默认） -->
        <el-form-item prop="date" label="加工日期">
          <el-date-picker v-model="form.date" type="date" placeholder="选择加工日期" style="width:100%"/>
        </el-form-item>
        <el-form-item prop="operator" label="操作人">
          <el-input v-model="form.operator" placeholder="请输入操作人" />
        </el-form-item>
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

// 基础变量
const loading = ref(false)
const searchKey = ref('')
const filterType = ref('')
const currentPage = ref(1)
const pageSize = 10

// 表格数据源（后端真实数据）
const processingList = ref([])
// 种子批次号（从种子接口获取）
const availableBatchNos = ref([])

// 弹窗&表单
const showFormDialog = ref(false)
const isEditing = ref(false)
const editingId = ref(null)
const saving = ref(false)
const formRef = ref()

// 表单默认值（和后端实体字段对齐）
const defaultForm = () => ({
  batchNo: '',
  type: '',
  process: '',
  inputQty: 0,
  outputQty: 0,
  date: '',
  operator: '',
  equipment: '',
  workshop: ''
})
const form = reactive(defaultForm())

// 表单校验规则
const formRules = {
  batchNo: [{ required: true, message: '请选择种子批次号', trigger: 'change' }],
  type: [{ required: true, message: '请选择加工类型', trigger: 'change' }],
  process: [{ required: true, message: '请输入加工工艺', trigger: 'blur' }],
  date: [{ required: true, message: '请选择加工日期', trigger: 'change' }],
  operator: [{ required: true, message: '请输入操作人', trigger: 'blur' }]
}

// 筛选逻辑
const filteredData = computed(() => {
  let data = processingList.value
  if (searchKey.value) {
    const kw = searchKey.value.toLowerCase()
    data = data.filter(r => r.batchNo?.toLowerCase().includes(kw) || r.type?.includes(kw))
  }
  if (filterType.value) {
    data = data.filter(r => r.type === filterType.value)
  }
  return data
})

// 分页逻辑
const pagedData = computed(() => {
  return filteredData.value.slice(
    (currentPage.value - 1) * pageSize,
    currentPage.value * pageSize
  )
})

function onFilterChange() {
  currentPage.value = 1
}

// ========== 接口请求方法 ==========
// 1. 加载加工记录列表
async function loadProcessingList() {
  loading.value = true
  try {
    const res = await request.get('/processing/list')
    if (res.code === 200) {
      processingList.value = res.data
    }
  } catch (err) {
    ElMessage.error('加工记录加载失败')
  } finally {
    loading.value = false
  }
}

// 2. 加载种子批次号（下拉选项）
async function loadBatchNos() {
  try {
    const res = await request.get('/seed/batch-nos')
    if (res.code === 200) {
      availableBatchNos.value = res.data
    }
  } catch (err) {}
}

// 3. 打开新增弹窗
function openAddDialog() {
  if (!perm.canAdd('processing')) return ElMessage.warning('您没有此操作的权限')
  if (availableBatchNos.value.length === 0) return ElMessage.warning('暂无已种植的批次，请先在「种植记录」中登记')
  isEditing.value = false
  editingId.value = null
  Object.assign(form, defaultForm())
  showFormDialog.value = true
}

// 4. 打开编辑弹窗
function openEditDialog(row) {
  if (!perm.canEdit('processing', row)) return ElMessage.warning('您没有编辑此记录的权限或已超时')
  isEditing.value = true
  editingId.value = row.id
  Object.assign(form, row)
  showFormDialog.value = true
}

// 5. 保存（新增/编辑）
async function handleSave() {
  await formRef.value.validate(async valid => {
    if (!valid) return
    saving.value = true
    try {
      if (isEditing.value) {
        // 编辑接口
        await request.put(`/processing/update/${editingId.value}`, form)
        ElMessage.success('加工记录更新成功')
      } else {
        // 新增接口
        await request.post('/processing/add', form)
        ElMessage.success('新增加工记录成功')
      }
      showFormDialog.value = false
      // 刷新列表
      loadProcessingList()
    } catch (err) {
      ElMessage.error('保存失败')
    } finally {
      saving.value = false
    }
  })
}

// 6. 删除记录
async function handleDelete(row) {
  if (!perm.canDelete()) return ElMessage.warning('仅管理员可以删除记录')
  try {
    await request.delete(`/processing/delete/${row.id}`)
    ElMessage.success('删除成功')
    loadProcessingList()
  } catch (err) {}
}

// 7. 详情弹窗
const showDetailDialog = ref(false)
const detailTitle = ref('')
const detailFields = ref([])
function showDetail(row) {
  detailTitle.value = `加工记录详情 — ${row.batchNo}`
  detailFields.value = [
    { label: '种子批次号', value: row.batchNo },
    { label: '加工类型', value: row.type },
    { label: '加工日期', value: row.date },
    { label: '加工工艺', value: row.process },
    { label: '投入量(吨)', value: row.inputQty },
    { label: '产出量(吨)', value: row.outputQty },
    { label: '出成率', value: row.rate },
    { label: '设备型号', value: row.equipment || '-' },
    { label: '操作人', value: row.operator },
    { label: '加工车间', value: row.workshop || '-' },
  ]
  showDetailDialog.value = true
}

// 8. Excel 导出
async function handleExport() {
  try {
    const res = await request({
      url: '/processing/export',
      method: 'GET',
      responseType: 'blob'
    })
    const blob = new Blob([res])
    const contentDisposition = res.headers['content-disposition']
    let fileName = "加工记录报表.xlsx"
    if (contentDisposition) {
      fileName = decodeURIComponent(contentDisposition.split("filename=")[1])
    }
    const aTag = document.createElement('a')
    aTag.href = URL.createObjectURL(blob)
    aTag.download = fileName
    document.body.appendChild(aTag)
    aTag.click()
    URL.revokeObjectURL(aTag.href)
    document.body.removeChild(aTag)
    ElMessage.success('导出成功')
  } catch (err) {
    ElMessage.error('导出失败')
  }
}

// 页面挂载：加载数据
onMounted(() => {
  loadProcessingList()
  loadBatchNos()
})
</script>

<style scoped>
/* 原有样式完全保留 */
.page-container { max-width: var(--page-max-width); }
.page-header { display: flex; align-items: flex-start; justify-content: space-between; margin-bottom: 20px; flex-wrap: wrap; gap: 12px; }
.page-header h1 { font-size: 1.5rem; font-weight: 700; }
.page-header p { font-size: 0.8125rem; color: var(--text-muted); margin-top: 4px; }
.readonly-hint { color: #e8c44a !important; }
.filter-bar { display: flex; gap: 10px; padding: 14px 18px; border-radius: 12px; margin-bottom: 18px; flex-wrap: wrap; }
.filter-input { width: 240px; }
.filter-select { width: 150px; }
.table-card { border-radius: 14px; padding: 4px; overflow: hidden; }
.table-footer { display: flex; align-items: center; justify-content: space-between; padding: 12px 16px; border-top: 1px solid var(--border); }
.table-summary { font-size: 0.75rem; color: var(--text-muted); }
.edit-timer { font-size: 0.625rem; color: var(--text-muted); margin-left: 2px; }
</style>