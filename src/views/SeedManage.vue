<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div>
        <h1>种子信息</h1>
        <p v-if="perm.canAdd('seed')">管理所有小麦种子基础信息</p>
        <p v-else class="readonly-hint">只读模式 — 仅管理员可操作</p>
      </div>
      <el-button
        v-if="perm.canAdd('seed')"
        type="primary"
        :icon="Plus"
        @click="openAddDialog"
      >
        新增种子
      </el-button>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar glass-light">
      <el-input
        v-model="searchKey"
        placeholder="搜索批次号/种子名称"
        :prefix-icon="Search"
        clearable
        class="filter-input"
        @input="onFilterChange"
      />
      <el-button :icon="Download" @click="handleExport">导出数据</el-button>
    </div>

    <!-- 表格区域 -->
    <div class="table-card glass-light">
      <el-table
        :data="pagedData"
        style="width: 100%"
        v-loading="loading"
        border
      >
        <el-table-column prop="batchNo" label="种子批次号" min-width="160" />
        <el-table-column prop="name" label="种子名称" min-width="120" />
        <el-table-column prop="variety" label="品种" min-width="120" />
        <el-table-column prop="origin" label="产地" min-width="160" />
        <el-table-column
          prop="createTime"
          label="创建时间"
          width="180"
          :formatter="formatTime"
        />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="perm.canAdd('seed')"
              link
              type="primary"
              size="small"
              @click="openEditDialog(row)"
            >
              编辑
            </el-button>
            <el-popconfirm
              v-if="perm.canDelete()"
              title="确定删除该种子记录？删除后关联种植数据将无法正常使用！"
              @confirm="handleDelete(row)"
            >
              <template #reference>
                <el-button link type="danger" size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="table-footer">
        <span class="table-summary">共 {{ filteredData.length }} 条数据</span>
        <el-pagination
          v-model:current-page="currentPage"
          :total="filteredData.length"
          :page-size="pageSize"
          layout="prev, pager, next"
          size="small"
        />
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="showFormDialog"
      :title="isEditing ? '编辑种子信息' : '新增种子信息'"
      width="520px"
      class="custom-dialog"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="种子批次号" prop="batchNo">
          <el-input
            v-model="form.batchNo"
            placeholder="请输入唯一批次号"
            :disabled="isEditing"
          />
          <div class="tip-text">批次号不可重复，编辑状态无法修改</div>
        </el-form-item>
        <el-form-item label="种子名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入种子名称" />
        </el-form-item>
        <el-form-item label="品种" prop="variety">
          <el-input v-model="form.variety" placeholder="请输入品种" />
        </el-form-item>
        <el-form-item label="产地" prop="origin">
          <el-input v-model="form.origin" placeholder="请输入产地" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showFormDialog = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">
          确认提交
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Plus, Download } from '@element-plus/icons-vue'
import { usePermission } from '@/utils/permission'
import request from '@/utils/request'

// 权限实例
const perm = usePermission()

// 基础分页、筛选变量
const searchKey = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)

// 表格原始数据
const seedList = ref([])

// 弹窗、表单相关
const showFormDialog = ref(false)
const isEditing = ref(false)
const editId = ref(null)
const saving = ref(false)
const formRef = ref()

// 表单默认值
const defaultForm = () => ({
  batchNo: '',
  name: '',
  variety: '',
  origin: ''
})
const form = reactive(defaultForm())

// 表单校验规则
const formRules = {
  batchNo: [{ required: true, message: '请输入种子批次号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入种子名称', trigger: 'blur' }],
  variety: [{ required: true, message: '请输入品种', trigger: 'blur' }],
  origin: [{ required: true, message: '请输入产地', trigger: 'blur' }]
}

// 数据筛选
const filteredData = computed(() => {
  let data = seedList.value
  if (searchKey.value) {
    const kw = searchKey.toLowerCase()
    data = data.filter(
      item =>
        item.batchNo.toLowerCase().includes(kw) ||
        item.name.toLowerCase().includes(kw)
    )
  }
  return data
})

// 分页切割数据
const pagedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = currentPage.value * pageSize.value
  return filteredData.value.slice(start, end)
})

// 筛选重置页码
function onFilterChange() {
  currentPage.value = 1
}

// 时间格式化
const formatTime = (row) => {
  if (!row.createTime) return ''
  return row.createTime.split('T')[0] + ' ' + row.createTime.split('T')[1].slice(0, 8)
}

// 加载种子列表
async function loadSeedList() {
  loading.value = true
  try {
    const res = await request.get('/seed/list')
    if (res.code === 200) {
      seedList.value = res.data
    }
  } catch (err) {
    ElMessage.error('种子数据加载失败')
  } finally {
    loading.value = false
  }
}

// 打开新增弹窗
function openAddDialog() {
  isEditing.value = false
  editId.value = null
  Object.assign(form, defaultForm())
  showFormDialog.value = true
}

// 打开编辑弹窗
function openEditDialog(row) {
  if (!perm.canAdd('seed')) return ElMessage.warning('暂无操作权限')
  isEditing.value = true
  editId.value = row.id
  Object.assign(form, row)
  showFormDialog.value = true
}

// 保存（新增/编辑）
async function handleSave() {
  await formRef.value.validate(async valid => {
    if (!valid) return
    saving.value = true
    try {
      if (isEditing.value) {
        await request.put(`/seed/update/${editId.value}`, form)
        ElMessage.success('编辑成功')
      } else {
        await request.post('/seed/add', form)
        ElMessage.success('新增成功')
      }
      showFormDialog.value = false
      loadSeedList()
    } catch (err) {
      ElMessage.error('提交失败，批次号可能重复')
    } finally {
      saving.value = false
    }
  })
}

// 删除数据
async function handleDelete(row) {
  if (!perm.canDelete()) return ElMessage.warning('仅管理员可删除')
  try {
    await request.delete(`/seed/delete/${row.id}`)
    ElMessage.success('删除成功')
    loadSeedList()
  } catch (err) {
    ElMessage.error('删除失败')
  }
}

// 导出 Excel
async function handleExport() {
  try {
    const res = await request({
      url: '/seed/export',
      method: 'GET',
      responseType: 'blob'
    })
    const blob = new Blob([res])
    const contentDisposition = res.headers['content-disposition']
    let fileName = "种子信息报表.xlsx"
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

// 页面挂载加载数据
onMounted(() => {
  loadSeedList()
})
</script>

<style scoped>
.page-container {
  max-width: var(--page-max-width);
}
.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 12px;
}
.page-header h1 {
  font-size: 1.5rem;
  font-weight: 700;
}
.page-header p {
  font-size: 0.8125rem;
  color: var(--text-muted);
  margin-top: 4px;
}
.readonly-hint {
  color: #e8c44a !important;
}
.filter-bar {
  display: flex;
  gap: 10px;
  padding: 14px 18px;
  border-radius: 12px;
  margin-bottom: 18px;
  flex-wrap: wrap;
}
.filter-input {
  width: 280px;
}
.table-card {
  border-radius: 14px;
  padding: 4px;
  overflow: hidden;
}
.table-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-top: 1px solid var(--border);
}
.table-summary {
  font-size: 0.75rem;
  color: var(--text-muted);
}
.tip-text {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}
</style>