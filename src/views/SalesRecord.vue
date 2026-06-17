<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <h1>销售记录</h1>
        <p v-if="perm.canAdd('sales')">管理小麦种子的销售信息</p>
        <p v-else class="readonly-hint">只读模式 - 仅销售商和管理员可操作</p>
      </div>
      <el-button v-if="perm.canAdd('sales')" type="primary" :icon="Plus" @click="openAddDialog">新增记录</el-button>
    </div>

    <div class="filter-bar glass-light">
      <el-input v-model="searchKey" placeholder="搜索批次号或客户..." :prefix-icon="Search" clearable class="filter-input" @input="onFilterChange" />
      <el-button :icon="Download" @click="handleExport">导出</el-button>
    </div>

    <div class="table-card glass-light">
      <!-- 增加加载动画 + 修正字段 -->
      <el-table :data="pagedData" v-loading="loading" style="width: 100%">
        <el-table-column prop="batchNo" label="种子批次号" min-width="150">
          <template #default="{ row }"><BatchNoCell :code="row.batchNo" /></template>
        </el-table-column>
        <!-- 字段 date 改为 salesDate -->
        <el-table-column prop="salesDate" label="销售日期" width="110" />
        <el-table-column prop="quantity" label="数量(吨)" min-width="90" />
        <el-table-column prop="price" label="单价(元/吨)" min-width="110" />
        <!-- 字段 total 改为 totalAmount -->
        <el-table-column prop="totalAmount" label="总金额(元)" min-width="130">
          <template #default="{ row }">
            <span class="price-text">{{ (row.totalAmount / 10000).toFixed(1) }} 万元</span>
          </template>
        </el-table-column>
        <el-table-column prop="destination" label="销售地区" min-width="110" />
        <el-table-column label="操作" :width="perm.isAdmin ? 200 : 120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="showDetail(row)">详情</el-button>
            <el-button v-if="perm.canEdit('sales', row)" link type="primary" size="small" @click="openEditDialog(row)">
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
        <span class="table-summary">共 {{ filteredData.length }} 条记录 · 销售总额 {{ totalSales }} 万元</span>
        <!-- 修复分页：small 改为 size="small" -->
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

    <el-dialog v-model="showFormDialog" :title="isEditing ? '编辑销售记录' : '新增销售记录'" width="560px" class="custom-dialog">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="种子批次号" prop="batchNo">
          <el-select v-model="form.batchNo" filterable placeholder="请先选择已质检的批次" style="width:100%">
            <el-option v-for="bn in availableBatchNos" :key="bn" :label="bn" :value="bn" />
          </el-select>
        </el-form-item>
        <el-form-item label="客户名称" prop="customer">
          <el-input v-model="form.customer" />
        </el-form-item>
        <!-- 新增：销售日期（后端必填） -->
        <el-form-item label="销售日期" prop="salesDate">
          <el-date-picker v-model="form.salesDate" type="date" placeholder="选择销售日期" style="width:100%" />
        </el-form-item>
        <el-form-item label="数量(吨)">
          <el-input-number v-model="quantity" :min="0" style="width:100%" />
        </el-form-item>
        <el-form-item label="单价(元/吨)">
          <el-input-number v-model="form.price" :min="0" :step="100" style="width:100%" />
        </el-form-item>
        <el-form-item label="销售地区">
          <el-input v-model="form.destination" />
        </el-form-item>
        <!-- 新增：合同编号 -->
        <el-form-item label="合同编号" prop="contractNo" :disabled="isEditing">
          <el-input v-model="form.contractNo" placeholder="新增自动生成" />
        </el-form-item>
        <!-- 新增：联系人 -->
        <el-form-item label="联系人">
          <el-input v-model="form.contact" placeholder="可选填写" />
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
// 移除 Pinia，引入全局请求工具
import request from '@/utils/request'

const perm = usePermission()

// 全局状态
const loading = ref(false)
const searchKey = ref('')
const currentPage = ref(1)
const pageSize = 10

// 后端真实数据源
const salesList = ref([])
// 已质检的批次号（下拉选项）
const availableBatchNos = ref([])

// 弹窗控制
const showFormDialog = ref(false)
const isEditing = ref(false)
const editingId = ref(null)
const saving = ref(false)
const formRef = ref()

// 表单默认值（和后端 SalesRecord 实体完全对齐）
const defaultForm = () => ({
  batchNo: '',
  customer: '',
  salesDate: '',
  quantity: 0,
  price: 4000,
  destination: '',
  contractNo: '',
  contact: ''
})
const form = reactive(defaultForm())

// 表单校验规则
const formRules = {
  batchNo: [{ required: true, message: '请选择种子批次号', trigger: 'change' }],
  customer: [{ required: true, message: '请输入客户名称', trigger: 'blur' }],
  salesDate: [{ required: true, message: '请选择销售日期', trigger: 'change' }],
  contractNo: [{ required: true, message: '合同编号不能为空', trigger: 'blur' }]
}

// 筛选逻辑
const filteredData = computed(() => {
  let data = salesList.value
  if (searchKey.value) {
    const kw = searchKey.value.toLowerCase()
    data = data.filter(r =>
      r.batchNo?.toLowerCase().includes(kw) ||
      r.customer?.includes(kw) ||
      r.destination?.includes(kw)
    )
  }
  return data
})

// 分页截取数据
const pagedData = computed(() => {
  return filteredData.value.slice((currentPage.value - 1) * pageSize, currentPage.value * pageSize)
})

// 计算总销售额（万元）
const totalSales = computed(() => {
  const total = filteredData.value.reduce((sum, item) => sum + Number(item.totalAmount || 0), 0)
  return (total / 10000).toFixed(1)
})

// 筛选重置页码
function onFilterChange() {
  currentPage.value = 1
}

// 加载销售记录列表（接口：/api/sales/list）
async function loadSalesList() {
  loading.value = true
  try {
    const res = await request.get('/sales/list')
    if (res.code === 200) {
      salesList.value = res.data
    }
  } catch (err) {
    ElMessage.error('加载销售记录失败，请检查后端服务')
    console.error(err)
  } finally {
    loading.value = false
  }
}

// 加载已完成质检的批次号（用于下拉选择）
async function loadQualityBatch() {
  try {
    const res = await request.get('/quality/list')
    if (res.code === 200) {
      // 批次去重
      const batchArr = [...new Set(res.data.map(item => item.batchNo))]
      availableBatchNos.value = batchArr
    }
  } catch (err) {
    ElMessage.warning('加载批次号失败')
  }
}

// 打开新增弹窗
function openAddDialog() {
  if (!perm.canAdd('sales')) return ElMessage.warning('您没有此操作的权限')
  if (availableBatchNos.value.length === 0) return ElMessage.warning('暂无已完成质检的批次，请先在「质检记录」中登记')
  isEditing.value = false
  editingId.value = null
  Object.assign(form, defaultForm())
  // 自动生成合同编号
  form.contractNo = `HT-2026-${String(salesList.value.length + 1).padStart(4, '0')}`
  showFormDialog.value = true
}

// 打开编辑弹窗
function openEditDialog(row) {
  if (!perm.canEdit('sales', row)) return ElMessage.warning('您没有编辑此记录的权限或已超时')
  isEditing.value = true
  editingId.value = row.id
  Object.assign(form, row)
  showFormDialog.value = true
}

// 保存（新增/编辑）对接后端接口
async function handleSave() {
  await formRef.value.validate(async valid => {
    if (!valid) return
    saving.value = true
    try {
      if (isEditing.value) {
        // 编辑接口 /api/sales/update/{id}
        await request.put(`/sales/update/${editingId}`, form)
        ElMessage.success('销售记录已更新')
      } else {
        // 新增接口 /api/sales/add
        await request.post('/sales/add', form)
        ElMessage.success('新增销售记录成功')
      }
      showFormDialog.value = false
      loadSalesList() // 刷新后端数据
    } catch (err) {
      ElMessage.error('保存失败')
    } finally {
      saving.value = false
    }
  })
}

// 删除记录（真实后端删除）
async function handleDelete(row) {
  if (!perm.canDelete()) return ElMessage.warning('仅管理员可以删除记录')
  try {
    // 删除接口 /api/sales/delete/{id}
    await request.delete(`/sales/delete/${row.id}`)
    ElMessage.success('已删除该记录')
    loadSalesList() // 刷新列表
  } catch (err) {
    ElMessage.error('删除失败')
  }
}

// 详情弹窗
const showDetailDialog = ref(false)
const detailTitle = ref('')
const detailFields = ref([])
function showDetail(row) {
  detailTitle.value = `销售记录详情 - ${row.batchNo}`
  detailFields.value = [
    { label: '种子批次号', value: row.batchNo, valueClass: 'mono' },
    { label: '客户名称', value: row.customer },
    { label: '销售日期', value: row.salesDate },
    { label: '数量(吨)', value: row.quantity },
    { label: '单价(元/吨)', value: row.price.toLocaleString() },
    { label: '总金额', value: '¥ ' + Number(row.totalAmount).toLocaleString(), valueClass: 'green' },
    { label: '销售地区', value: row.destination },
    { label: '合同编号', value: row.contractNo, valueClass: 'mono' },
    { label: '联系人', value: row.contact || '暂无' },
    { label: '创建时间', value: row.createTime || '-' }
  ]
  showDetailDialog.value = true
}

// 导出 Excel（对接后端文件流）
async function handleExport() {
  try {
    const res = await request({
      url: '/sales/export',
      method: 'GET',
      responseType: 'blob'
    })
    const blob = new Blob([res])
    const a = document.createElement('a')
    a.href = URL.createObjectURL(blob)
    a.download = '小麦种子销售报表.xlsx'
    a.click()
    URL.revokeObjectURL(a.href)
    ElMessage.success('导出成功')
  } catch (err) {
    ElMessage.error('导出失败')
  }
}

// 页面挂载：自动加载数据
onMounted(() => {
  loadSalesList()
  loadQualityBatch()
})
</script>

<style scoped>
.page-container { max-width: var(--page-max-width); }
.page-header { display: flex; align-items: flex-start; justify-content: space-between; margin-bottom: 20px; flex-wrap: wrap; gap: 12px; }
.page-header h1 { font-size: 1.5rem; font-weight: 700; }
.page-header p { font-size: 0.8125rem; color: var(--text-muted); margin-top: 4px; }
.readonly-hint { color: #e8c44a !important; }
.filter-bar { display: flex; gap: 10px; padding: 14px 18px; border-radius: 12px; margin-bottom: 18px; flex-wrap: wrap; }
.filter-input { width: 260px; }
.table-card { border-radius: 14px; padding: 4px; overflow: hidden; }
.price-text { color: var(--primary); font-weight: 600; }
.table-footer { display: flex; align-items: center; justify-content: space-between; padding: 12px 16px; border-top: 1px solid var(--border); }
.table-summary { font-size: 0.75rem; color: var(--text-muted); }
.edit-timer { font-size: 0.625rem; color: var(--text-muted); margin-left: 2px; }
</style>