<template>
  <div class="user-page-wrap">
    <!-- 顶部标题+新增按钮 -->
    <div class="page-header">
      <div class="title-group">
        <h1>用户管理</h1>
        <p>管理系统的用户账户及角色权限</p>
      </div>
      <el-button v-if="perm.canAdd('user')" type="warning" :icon="Plus" class="add-btn" @click="openAddDialog">+ 新增用户</el-button>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar glass-dark">
      <el-input v-model="searchKey" placeholder="搜索用户名或手机号..." :prefix-icon="Search" clearable class="filter-input" @input="onFilterChange" />
      <el-select v-model="filterRole" placeholder="角色筛选" clearable class="filter-select" @change="onFilterChange">
        <el-option label="系统管理员" value="admin" />
        <el-option label="种植人员" value="planter" />
        <el-option label="加工人员" value="processor" />
        <el-option label="质检人员" value="inspector" />
        <el-option label="销售商" value="seller" />
      </el-select>
    </div>

    <!-- 表格卡片 -->
    <div class="table-card glass-dark">
      <el-table :data="pagedData" v-loading="loading" border style="width:100%">
        <el-table-column prop="name" label="姓名" align="center" />
        <el-table-column prop="username" label="用户名" align="center" />
        <el-table-column prop="role" label="角色" align="center">
          <template #default="{ row }">
            <el-tag :type="getRoleTag(row.role)" size="small" effect="dark" round>
              {{ getRoleName(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" align="center" />
        <el-table-column prop="email" label="邮箱" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch
              :model-value="row.status === 'active'"
              active-color="#409EFF"
              :disabled="!perm.isAdmin || row.role === 'admin'"
              @change="() => handleToggleStatus(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建日期" align="center" />
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="showDetail(row)">详情</el-button>
            <el-button link type="primary" size="small" @click="handleResetPwd(row)">重置密码</el-button>
            <el-button v-if="row.role !== 'admin'" link type="danger" size="small" @click="handleToggleStatus(row)">禁用</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 表格底部统计分页 -->
      <div class="table-footer">
        <span class="total-text">共 {{ filteredData.length }} 个用户</span>
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="showFormDialog" :title="isEditing ? '编辑用户' : '新增用户'" width="560px" class="dark-dialog" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入用户姓名" />
        </el-form-item>
        <el-form-item label="用户名" prop="username" :disabled="isEditing">
          <el-input v-model="form.username" placeholder="登录账号，新增后不可修改" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" style="width:100%">
            <el-option label="系统管理员" value="admin" />
            <el-option label="种植人员" value="planter" />
            <el-option label="加工人员" value="processor" />
            <el-option label="质检人员" value="inspector" />
            <el-option label="销售商" value="seller" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="选填，用于接收通知" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showFormDialog = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">确 定</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <InfoDialog v-model="showDetailDialog" :title="detailTitle" :fields="detailFields" />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import InfoDialog from '@/components/common/InfoDialog.vue'
import { usePermission } from '@/utils/permission'
import request from '@/utils/request'

const perm = usePermission()

// 分页、筛选状态
const loading = ref(false)
const searchKey = ref('')
const filterRole = ref('')
const currentPage = ref(1)
const pageSize = 10
const userList = ref([])

// 弹窗控制
const showFormDialog = ref(false)
const isEditing = ref(false)
const editingId = ref(null)
const saving = ref(false)
const formRef = ref()

// 表单默认结构
const defaultForm = () => ({
  name: '',
  username: '',
  role: 'planter',
  phone: '',
  email: ''
})
const form = reactive(defaultForm())

// 表单校验
const formRules = {
  name: [{ required: true, message: '请填写姓名', trigger: 'blur' }],
  username: [{ required: true, message: '请填写登录用户名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择用户角色', trigger: 'change' }],
  phone: [{ required: true, message: '请填写手机号', trigger: 'blur' }],
}

// 筛选分页逻辑
const filteredData = computed(() => {
  let data = [...userList.value]
  const kw = searchKey.value.trim().toLowerCase()
  if (kw) {
    data = data.filter(item => {
      const name = item.name ?? ''
      const username = item.username ?? ''
      const phone = item.phone ?? ''
      return name.toLowerCase().includes(kw) || username.toLowerCase().includes(kw) || phone.includes(kw)
    })
  }
  if (filterRole.value) {
    data = data.filter(item => item.role === filterRole.value)
  }
  return data
})
const pagedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  const end = currentPage.value * pageSize
  return filteredData.value.slice(start, end)
})
function onFilterChange() {
  currentPage.value = 1
}

// 角色名称、标签颜色映射
function getRoleName(role) {
  const map = { admin: '系统管理员', planter: '种植人员', processor: '加工人员', inspector: '质检人员', seller: '销售商' }
  return map[role] || role
}
function getRoleTag(role) {
  const map = { admin: 'warning', planter: 'success', processor: 'info', inspector: 'primary', seller: 'danger' }
  return map[role] || ''
}

// 加载列表接口
async function loadUserList() {
  loading.value = true
  try {
    const res = await request.get('/user/list')
    if (res.code === 200) userList.value = res.data
  } catch (err) {
    ElMessage.error('加载用户列表失败')
    console.error(err)
  } finally {
    loading.value = false
  }
}

// 新增弹窗
function openAddDialog() {
  if (!perm.canAdd('user')) return ElMessage.warning('您没有新增权限')
  isEditing.value = false
  editingId.value = null
  Object.assign(form, defaultForm())
  showFormDialog.value = true
}
// 保存新增/编辑
async function handleSave() {
  await formRef.value.validate(async valid => {
    if (!valid) return
    saving.value = true
    try {
      if (isEditing.value) await request.put(`/user/update/${editingId.value}`, form)
      else await request.post('/user/add', form)
      ElMessage.success(isEditing ? '修改成功' : '新增成功，初始密码123456')
      showFormDialog.value = false
      loadUserList()
    } catch (err) {
      ElMessage.error('保存失败')
    } finally {
      saving.value = false
    }
  })
}
// 切换账号状态
async function handleToggleStatus(row) {
  if (!perm.isAdmin || row.role === 'admin') return ElMessage.warning('管理员不可禁用')
  try {
    await request.put(`/user/status/${row.id}`)
    ElMessage.success('状态修改成功')
    loadUserList()
  } catch (err) {
    ElMessage.error('操作失败')
  }
}
// 重置密码
async function handleResetPwd(row) {
  if (!perm.isAdmin) return ElMessage.warning('仅管理员可重置密码')
  try {
    await request.put(`/user/resetPwd/${row.id}`)
    ElMessage.success(`用户${row.username}密码重置为123456`)
    loadUserList()
  } catch (err) {
    ElMessage.error('重置失败')
  }
}

// 详情弹窗
const showDetailDialog = ref(false)
const detailTitle = ref('')
const detailFields = ref([])
function showDetail(row) {
  detailTitle.value = `用户详情 — ${row.name}`
  detailFields.value = [
    { label: '姓名', value: row.name },
    { label: '用户名', value: row.username },
    { label: '角色', value: getRoleName(row.role), tag: getRoleTag(row.role) },
    { label: '手机号', value: row.phone },
    { label: '邮箱', value: row.email || '无' },
    { label: '账号状态', value: row.status === 'active' ? '正常启用' : '已禁用' },
    { label: '创建时间', value: row.createdAt || '-' }
  ]
  showDetailDialog.value = true
}

onMounted(() => loadUserList())
</script>

<style scoped>
.user-page-wrap {
  width: 100%;
  max-width: 1400px;
}
/* 顶部标题区域 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}
.title-group h1 {
  font-size: 24px;
  color: #fff;
  margin: 0 0 6px;
}
.title-group p {
  font-size: 14px;
  color: #999;
  margin: 0;
}
.add-btn {
  background-color: #d4a046;
  border-color: #d4a046;
  color: #000;
}
/* 筛选栏深色玻璃卡片 */
.filter-bar {
  background-color: rgba(26,26,26,0.7);
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 10px;
  padding: 16px 20px;
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}
.filter-input {
  width: 260px;
}
.filter-select {
  width: 180px;
}
/* 表格卡片 */
.table-card {
  background-color: rgba(26,26,26,0.7);
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 10px;
  overflow: hidden;
}
/* 表格底部分页区域 */
.table-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-top: 1px solid rgba(255,255,255,0.08);
}
.total-text {
  font-size: 14px;
  color: #cccccc;
}
/* Element Plus 深色主题覆盖，适配全局深色变量 */
:deep(.el-table) {
  --el-table-bg-color: transparent;
  --el-table-header-bg-color: rgba(40,40,40,0.6);
  --el-table-row-bg-color: transparent;
  --el-table-row-hover-bg-color: rgba(60,60,60,0.4);
  --el-table-text-color: #eeeeee;
  --el-table-border-color: rgba(255,255,255,0.08);
}
:deep(.el-input__wrapper) {
  background-color: rgba(40,40,40,0.6);
  box-shadow: 0 0 0 1px rgba(255,255,255,0.1) inset;
}
:deep(.el-input__inner) {
  color: #fff;
}
:deep(.el-select__wrapper) {
  background-color: rgba(40,40,40,0.6);
  box-shadow: 0 0 0 1px rgba(255,255,255,0.1) inset;
}
:deep(.el-dialog) {
  background-color: #1a1a1a;
}
:deep(.el-dialog__header) {
  border-bottom: 1px solid rgba(255,255,255,0.1);
}
:deep(.el-dialog__title) {
  color: #fff;
}
:deep(.el-form-item__label) {
  color: #cccccc !important;
}
</style>