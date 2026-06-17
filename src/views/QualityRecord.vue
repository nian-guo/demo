<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <h1>质检记录</h1>
        <p v-if="perm.canAdd('quality')">管理小麦种子质量检测环节的所有记录</p>
        <p v-else class="readonly-hint">只读模式 — 仅质检人员和管理员可操作</p>
      </div>
      <el-button v-if="perm.canAdd('quality')" type="primary" :icon="Plus" @click="openAddDialog">新增记录</el-button>
    </div>

    <div class="filter-bar glass-light">
      <el-input v-model="searchKey" placeholder="搜索批次号..." :prefix-icon="Search" clearable class="filter-input" @input="onFilterChange" />
      <el-select v-model="filterResult" placeholder="检测结果" clearable class="filter-select" @change="onFilterChange">
        <el-option label="合格" value="合格" />
        <el-option label="不合格" value="不合格" />
      </el-select>
      <el-button :icon="Download" @click="handleExport">导出报告</el-button>
    </div>

    <div class="table-card glass-light">
      <!-- 绑定正确分页数据 + 加载动画 + 全部字段对齐后端实体 -->
      <el-table :data="pagedData" v-loading="loading" style="width: 100%">
        <el-table-column prop="batchNo" label="种子批次号" min-width="150">
          <template #default="{ row }">
            <BatchNoCell :code="row.batchNo" />
          </template>
        </el-table-column>
        <el-table-column prop="checkDate" label="检测日期" width="110" />
        <el-table-column prop="purity" label="纯度(%)" min-width="80" />
        <el-table-column prop="germinationRate" label="发芽率(%)" min-width="90" />
        <el-table-column prop="moisture" label="水分(%)" min-width="80" />
        <el-table-column prop="reportNo" label="检测报告号" min-width="130" />
        <el-table-column prop="result" label="结果" width="90">
          <template #default="{ row }">
            <el-tag :type="row.result === '合格' ? 'success' : 'danger'" size="small" effect="dark" round>
              {{ row.result }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="checker" label="检测员" min-width="100" />
        <el-table-column label="操作" :width="perm.isAdmin ? 200 : 140" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="showDetail(row)">详情</el-button>
            <el-button v-if="perm.canEdit('quality', row)" link type="primary" size="small" @click="openEditDialog(row)">
              编辑<span v-if="!perm.isAdmin" class="edit-timer">{{ perm.formatRemaining(row) }}</span>
            </el-button>
            <el-popconfirm v-if="perm.canDelete()" title="确定删除该记录吗？" @confirm="handleDelete(row)">
              <template #reference>
                <el-button link type="danger" size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <div class="table-footer">
        <span class="table-summary">共 {{ filteredData.length }} 条记录</span>
        <!-- 修复分页警告：small 改为 size="small" -->
        <el-pagination
          v-model:current-page="currentPage"
          :total="filteredData.length"
          :page-size="pageSize"
          layout="prev, pager, next"
          background
          size="small"
        />
      </div>
    </div>

    <el-dialog v-model="showFormDialog" :title="isEditing ? '编辑质检记录' : '新增质检记录'" width="560px" class="custom-dialog">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="种子批次号" prop="batchNo">
          <el-select v-model="form.batchNo" filterable placeholder="请先选择已加工的批次" style="width:100%" :disabled="isEditing">
            <el-option v-for="bn in availableBatchNos" :key="bn" :label="bn" :value="bn" />
          </el-select>
        </el-form-item>
        <el-form-item label="检测日期" prop="checkDate">
          <el-date-picker v-model="form.checkDate" type="date" placeholder="选择检测日期" style="width:100%" />
        </el-form-item>
        <el-form-item label="检测报告号" prop="reportNo" :disabled="isEditing">
          <el-input v-model="form.reportNo" placeholder="新增自动生成" />
        </el-form-item>
        <el-form-item label="纯度(%)">
          <el-input-number v-model="form.purity" :min="0" :max="100" :precision="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="发芽率(%)">
          <el-input-number v-model="form.germinationRate" :min="0" :max="100" :precision="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="水分(%)">
          <el-input-number v-model="form.moisture" :min="0" :max="100" :precision="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="检测员" prop="checker">
          <el-input v-model="form.checker" placeholder="请输入检测员姓名" />
        </el-form-item>
        <el-form-item label="检测结果" prop="result">
          <el-radio-group v-model="form.result">
            <el-radio value="合格">合格</el-radio>
            <el-radio value="不合格">不合格</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" rows="2" placeholder="复检备注" />
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
// 移除本地Pinia模拟，使用真实后端请求
import request from '@/utils/request'

const perm = usePermission()

// 页面加载状态
const loading = ref(false)
const searchKey = ref('')
const filterResult = ref('')
const currentPage = ref(1)
const pageSize = 10

// 后端接口返回真实质检数据
const qualityList = ref([])
// 批次下拉选项（调用种子批次接口）
const availableBatchNos = ref([])

// 弹窗控制变量
const showFormDialog = ref(false)
const isEditing = ref(false)
const editingId = ref(null)
const saving = ref(false)
const formRef = ref()

// 表单默认对象：字段完全匹配后端QualityCheck实体
const defaultForm = () => ({
  batchNo: '',
  checkDate: '',
  purity: 98.0,
  germinationRate: 95.0,
  moisture: 12.5,
  reportNo: '',
  result: '合格',
  checker: '',
  remark: ''
})
const form = reactive(defaultForm())

// 表单校验规则
const formRules = {
  batchNo: [{ required: true, message: '请选择种子批次号', trigger: 'change' }],
  checkDate: [{ required: true, message: '请选择检测日期', trigger: 'change' }],
  reportNo: [{ required: true, message: '报告号不能为空', trigger: 'blur' }],
  checker: [{ required: true, message: '请填写检测员', trigger: 'blur' }],
  result: [{ required: true, message: '请选择检测结果', trigger: 'change' }]
}

// 筛选逻辑
const filteredData = computed(() => {
  let data = qualityList.value
  if (searchKey.value) {
    const kw = searchKey.value.toLowerCase()
    data = data.filter(item =>
      item.batchNo?.toLowerCase().includes(kw) || item.reportNo?.toLowerCase().includes(kw)
    )
  }
  if (filterResult.value) {
    data = data.filter(item => item.result === filterResult.value)
  }
  return data
})
// 分页截取数据
const pagedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  const end = currentPage.value * pageSize
  return filteredData.value.slice(start, end)
})
// 筛选重置页码
function onFilterChange() {
  currentPage.value = 1
}

// 加载质检列表接口
async function loadQualityList() {
  loading.value = true
  try {
    const res = await request.get('/quality/list')
    console.log('后端质检完整数据：', res.data) // 控制台打印校验接口返回
    if (res.code === 200) {
      qualityList.value = res.data
    }
  } catch (err) {
    ElMessage.error('加载质检记录失败，请检查后端服务或数据库')
    console.error('接口请求报错：', err)
  } finally {
    loading.value = false
  }
}

// 加载种子批次下拉
async function loadBatchNos() {
  try {
    const res = await request.get('/seed/batch-nos')
    if (res.code === 200) {
      availableBatchNos.value = res.data
    }
  } catch (err) {
    ElMessage.warning('加载批次号下拉失败')
  }
}

// 打开新增弹窗
function openAddDialog() {
  if (!perm.canAdd('quality')) return ElMessage.warning('您没有新增记录权限')
  if (availableBatchNos.value.length === 0) return ElMessage.warning('暂无加工批次，请先完成加工记录登记')
  isEditing.value = false
  editingId.value = null
  Object.assign(form, defaultForm())
  // 自动生成报告编号
  form.reportNo = `QJ-2026-${String(qualityList.value.length + 1).padStart(4, '0')}`
  showFormDialog.value = true
}

// 打开编辑弹窗
function openEditDialog(row) {
  if (!perm.canEdit('quality', row)) return ElMessage.warning('您无编辑权限或编辑超时')
  isEditing.value = true
  editingId.value = row.id
  Object.assign(form, row)
  showFormDialog.value = true
}

// 保存新增/编辑接口
async function handleSave() {
  await formRef.value.validate(async valid => {
    if (!valid) return
    saving.value = true
    try {
      if (isEditing.value) {
        await request.put(`/quality/update/${editingId.value}`, form)
        ElMessage.success('质检记录修改成功')
      } else {
        await request.post('/quality/add', form)
        ElMessage.success('质检记录新增成功')
      }
      showFormDialog.value = false
      loadQualityList() // 保存后刷新后端数据
    } catch (err) {
      ElMessage.error('保存失败，请检查数据库连接')
    } finally {
      saving.value = false
    }
  })
}

// 真实删除后端接口（数据库同步删除）
async function handleDelete(row) {
  if (!perm.canDelete()) return ElMessage.warning('仅管理员可删除记录')
  try {
    await request.delete(`/quality/delete/${row.id}`)
    ElMessage.success('删除成功')
    loadQualityList() // 刷新列表
  } catch (err) {
    ElMessage.error('删除失败，后端数据库操作异常')
  }
}

// 详情弹窗展示完整字段
const showDetailDialog = ref(false)
const detailTitle = ref('')
const detailFields = ref([])
function showDetail(row) {
  detailTitle.value = `质检记录详情 — ${row.batchNo}`
  detailFields.value = [
    { label: '种子批次号', value: row.batchNo },
    { label: '检测日期', value: row.checkDate },
    { label: '检测报告号', value: row.reportNo },
    { label: '纯度(%)', value: row.purity },
    { label: '发芽率(%)', value: row.germinationRate },
    { label: '水分(%)', value: row.moisture },
    { label: '检测结果', value: row.result, tag: row.result === '合格' ? 'success' : 'danger' },
    { label: '检测员', value: row.checker },
    { label: '备注', value: row.remark || '无备注' },
    { label: '创建时间', value: row.createTime || '-' }
  ]
  showDetailDialog.value = true
}

// Excel导出文件流
async function handleExport() {
  try {
    const res = await request({
      url: '/quality/export',
      method: 'GET',
      responseType: 'blob'
    })
    const blob = new Blob([res])
    const a = document.createElement('a')
    a.href = URL.createObjectURL(blob)
    a.download = '质检记录报表.xlsx'
    a.click()
    URL.revokeObjectURL(a.href)
    ElMessage.success('报表导出完成')
  } catch (err) {
    ElMessage.error('导出失败')
  }
}

// 页面挂载自动请求后端数据（解决空白核心）
onMounted(() => {
  loadQualityList()
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
.filter-select { width: 150px; }
.table-card { border-radius: 14px; padding: 4px; overflow: hidden; }
.table-footer { display: flex; align-items: center; justify-content: space-between; padding: 12px 16px; border-top: 1px solid var(--border); }
.table-summary { font-size: 0.75rem; color: var(--text-muted); }
.edit-timer { font-size: 0.625rem; color: var(--text-muted); margin-left: 2px; }
</style>