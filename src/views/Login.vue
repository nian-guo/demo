<template>
  <div class="login-page">
    <!-- Background -->
    <div class="login-bg">
      <div class="bg-particles">
        <div v-for="n in 20" :key="n" class="particle" :style="particleStyle(n)"></div>
      </div>
      <div class="bg-orb-large"></div>
      <div class="bg-orb-small"></div>
      <div class="bg-grid"></div>
    </div>

    <!-- Left Brand Section -->
    <div class="login-brand">
      <div class="brand-content">
        <div class="brand-icon">
          <svg viewBox="0 0 80 80" fill="none" class="wheat-svg">
            <g opacity="0.95">
              <path d="M40 8C32 16 16 24 12 36C8 48 16 56 24 60C26 54 32 48 40 44C48 48 54 54 56 60C64 56 72 48 68 36C64 24 48 16 40 8Z" fill="url(#wheat-grad-login)"/>
              <path d="M40 16C36 20 28 26 26 32" stroke="#f0d57a" stroke-width="2" stroke-linecap="round"/>
              <path d="M40 16C44 20 52 26 54 32" stroke="#f0d57a" stroke-width="2" stroke-linecap="round"/>
              <path d="M40 24V64" stroke="#f0d57a" stroke-width="2" stroke-linecap="round"/>
            </g>
            <defs>
              <linearGradient id="wheat-grad-login" x1="8" y1="8" x2="72" y2="72">
                <stop stop-color="#d4a853"/><stop offset="0.5" stop-color="#c49b3c"/><stop offset="1" stop-color="#8b6914"/>
              </linearGradient>
            </defs>
          </svg>
        </div>
        <h1 class="brand-title">麦 溯</h1>
        <p class="brand-subtitle">WHEAT TRACEABILITY</p>
        <div class="brand-divider"></div>
        <p class="brand-desc">
          全链路小麦种子追溯系统<br/>
          从田间到餐桌，每一粒种子都有迹可循
        </p>
        <div class="brand-features">
          <div class="feature-item">
            <div class="feature-icon"><el-icon><CircleCheck /></el-icon></div>
            <span>一物一码精准追溯</span>
          </div>
          <div class="feature-item">
            <div class="feature-icon"><el-icon><CircleCheck /></el-icon></div>
            <span>全流程信息透明</span>
          </div>
          <div class="feature-item">
            <div class="feature-icon"><el-icon><CircleCheck /></el-icon></div>
            <span>区块链数据存证</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Right Login Form -->
    <div class="login-form-section">
      <div class="form-container glass">
        <div class="form-header">
          <h2>欢迎回来</h2>
          <p>登录您的账户以继续</p>
        </div>

        <el-form ref="formRef" :model="loginForm" :rules="rules" class="login-form" @submit.prevent="handleLogin">
          <el-form-item prop="username">
            <el-input v-model="loginForm.username" placeholder="请输入用户名" :prefix-icon="User" size="large" class="custom-input" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" size="large" show-password class="custom-input" @keyup.enter="handleLogin" />
          </el-form-item>

          <div class="form-options">
            <el-checkbox v-model="rememberMe"><span class="text-sm text-stone-400">记住我</span></el-checkbox>
            <a href="#" class="forgot-link" @click.prevent="openForgotDialog">忘记密码?</a>
          </div>

          <el-button type="primary" size="large" class="login-btn" :loading="loading" @click="handleLogin">
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form>

        <div class="register-link">
          还没有账户？<a href="#" @click.prevent="$router.push('/register')">立即注册</a>
        </div>

        <!-- Quick Login Demos -->
        <div class="demo-login">
          <p class="demo-title">— 演示账号快速登录 —</p>
          <div class="demo-buttons">
            <button v-for="role in demoRoles" :key="role.key" class="demo-btn" @click="quickLogin(role)">
              <span class="demo-role-icon" :style="{ background: role.color }">{{ role.avatar }}</span>
              <span class="demo-role-name">{{ role.label }}</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 忘记密码弹窗 -->
    <el-dialog v-model="showForgotDialog" title="重置密码" width="460px" class="custom-dialog" :close-on-click-modal="false">
      <div v-if="forgotStep === 1" class="forgot-step">
        <p class="forgot-desc">请输入您注册时绑定的手机号以验证身份</p>
        <el-form ref="phoneFormRef" :model="phoneForm" :rules="phoneRules" label-width="0">
          <el-form-item prop="phone">
            <el-input v-model="phoneForm.phone" placeholder="请输入手机号" size="large" :prefix-icon="Phone" class="custom-input" />
          </el-form-item>
          <el-form-item prop="code">
            <div class="code-row">
              <el-input v-model="phoneForm.code" placeholder="验证码" size="large" class="custom-input code-input" maxlength="6" />
              <el-button class="code-btn" :disabled="codeCountdown > 0" @click="sendCode">
                {{ codeCountdown > 0 ? `${codeCountdown}s` : '获取验证码' }}
              </el-button>
            </div>
          </el-form-item>
        </el-form>
        <div class="forgot-actions">
          <el-button @click="showForgotDialog = false">取消</el-button>
          <el-button type="primary" :loading="verifying" @click="verifyPhone">验证身份</el-button>
        </div>
      </div>

      <div v-if="forgotStep === 2" class="forgot-step">
        <div class="forgot-success-tip">
          <el-icon :size="20" color="var(--accent)"><CircleCheck /></el-icon>
          <span>身份验证成功，请设置新密码</span>
        </div>
        <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="0">
          <el-form-item prop="newPassword">
            <el-input v-model="pwdForm.newPassword" type="password" placeholder="请输入新密码" size="large" :prefix-icon="Lock" show-password class="custom-input" />
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input v-model="pwdForm.confirmPassword" type="password" placeholder="请再次输入新密码" size="large" :prefix-icon="Lock" show-password class="custom-input" />
          </el-form-item>
        </el-form>
        <div class="forgot-actions">
          <el-button @click="showForgotDialog = false">取消</el-button>
          <el-button type="primary" :loading="resetting" @click="resetPassword">重置密码</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
// 引入权限工具
import { usePermission } from '@/utils/permission'
import { ElMessage } from 'element-plus'
import { User, Lock, Phone, CircleCheck } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
// 角色缓存方法
const { syncRoleToLocal } = usePermission()

const formRef = ref()
const loading = ref(false)
const rememberMe = ref(true)

const loginForm = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const demoRoles = [
  { key: 'admin', label: '系统管理员', avatar: 'A', username: 'admin', color: '#d4a853' },
  { key: 'planter', label: '种植人员', avatar: 'P', username: 'planter', color: '#5b8c5a' },
  { key: 'processor', label: '加工人员', avatar: 'R', username: 'processor', color: '#4a90d9' },
  { key: 'inspector', label: '质检人员', avatar: 'I', username: 'inspector', color: '#6c5ce7' },
  { key: 'seller', label: '销售商', avatar: 'S', username: 'seller', color: '#e87a4a' },
]

// 普通登录
async function handleLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const result = await userStore.loginApi(loginForm.username, loginForm.password)
    if (result.ok) {
      ElMessage.success('登录成功')
      // 同步角色到本地缓存
      syncRoleToLocal()
      const redirect = route.query.redirect || '/dashboard'
      router.push(redirect)
    } else {
      ElMessage.error(result.message || '登录失败')
    }
  } catch (e) {
    ElMessage.error('登录失败，请检查网络连接')
  }
  loading.value = false
}

// 快捷登录（隔离表单变量，防污染）
async function quickLogin(role) {
  if (loading.value) return
  const username = role.username
  const password = '123456'
  loading.value = true
  try {
    const result = await userStore.loginApi(username, password)
    if (result.ok) {
      // 同步角色到本地缓存
      syncRoleToLocal()
      const redirect = route.query.redirect || '/dashboard'
      router.push(redirect)
    } else {
      ElMessage.error(result.message || '账号密码错误')
      console.log('快捷登录失败：', username, password)
    }
  } catch (e) {
    ElMessage.error('登录失败，请检查网络连接')
    console.error('快捷登录异常：', e)
  } finally {
    loading.value = false
  }
}

// ===== 忘记密码相关 =====
const showForgotDialog = ref(false)
const forgotStep = ref(1)
const verifying = ref(false)
const resetting = ref(false)
const codeCountdown = ref(0)
let countdownTimer = null

const phoneForm = reactive({ phone: '', code: '' })
const phoneFormRef = ref()
const phoneRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号', trigger: 'blur' }
  ],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

const pwdForm = reactive({ newPassword: '', confirmPassword: '' })
const pwdFormRef = ref()
let verifiedPhone = ''
let resetToken = ''

const validateNewPassword = (rule, value, callback) => {
  if (!value) return callback(new Error('请输入新密码'))
  if (value.length < 6) return callback(new Error('密码长度不能少于6位'))
  callback()
}

const pwdRules = {
  newPassword: [
    { required: true, validator: validateNewPassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== pwdForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

function openForgotDialog() {
  forgotStep.value = 1
  phoneForm.phone = ''
  phoneForm.code = ''
  pwdForm.newPassword = ''
  pwdForm.confirmPassword = ''
  verifiedPhone = ''
  resetToken = ''
  showForgotDialog.value = true
}

async function sendCode() {
  try { await phoneFormRef.value?.validateField('phone') } catch { return }
  const result = await userStore.sendCode(phoneForm.phone, 'forgot_password')
  if (result.ok) {
    codeCountdown.value = 60
    ElMessage.success(result.message)
    countdownTimer = setInterval(() => {
      codeCountdown.value--
      if (codeCountdown.value <= 0) {
        clearInterval(countdownTimer)
        countdownTimer = null
      }
    }, 1000)
  } else {
    ElMessage.warning(result.message)
  }
}

async function verifyPhone() {
  const valid = await phoneFormRef.value?.validate().catch(() => false)
  if (!valid) return
  verifying.value = true
  try {
    const result = await userStore.verifyPhone(phoneForm.phone, phoneForm.code)
    if (result.ok) {
      verifiedPhone = phoneForm.phone
      resetToken = result.data?.resetToken
      forgotStep.value = 2
    } else {
      ElMessage.error(result.message)
    }
  } catch (e) {
    ElMessage.error('验证失败，请检查网络连接')
  }
  verifying.value = false
}

async function resetPassword() {
  const valid = await pwdFormRef.value?.validate().catch(() => false)
  if (!valid) return
  resetting.value = true
  try {
    const result = await userStore.resetPassword(verifiedPhone, resetToken, pwdForm.newPassword)
    if (result.ok) {
      ElMessage.success('密码重置成功，请使用新密码登录')
      showForgotDialog.value = false
      loginForm.password = pwdForm.newPassword
    } else {
      ElMessage.error(result.message)
    }
  } catch (e) {
    ElMessage.error('重置失败，请稍后再试')
  }
  resetting.value = false
}

function particleStyle(n) {
  return {
    left: `${(n * 17 + 3) % 100}%`,
    top: `${(n * 23 + 7) % 100}%`,
    animationDelay: `${n * 0.4}s`,
    animationDuration: `${8 + (n % 5) * 2}s`,
    width: `${2 + (n % 3)}px`,
    height: `${2 + (n % 3)}px`,
    opacity: 0.15 + (n % 5) * 0.08
  }
}
</script>

<style scoped>
:root {
  --bg-dark: #121212;
  --bg-surface: #1e1e1e;
  --text-primary: #ffffff;
  --text-secondary: #c9c9c9;
  --text-muted: #888888;
  --border: #333333;
  --border-light: #555555;
  --primary: #d4a853;
  --primary-light: #e2b865;
  --accent: #5b8c5a;
  --gold-gradient: linear-gradient(135deg, #d4a853 0%, #8b6914 100%);
  --transition-smooth: cubic-bezier(0.4, 0, 0.2, 1);
}

.login-page {
  display: flex; min-height: 100vh; background: var(--bg-dark);
  position: relative; overflow: hidden;
}

.login-bg { position: fixed; inset: 0; pointer-events: none; z-index: 0; }
.bg-particles { position: absolute; inset: 0; }
.particle { position: absolute; background: var(--primary); border-radius: 50%; animation: float 6s ease-in-out infinite; }
.bg-orb-large { position: absolute; width: 700px; height: 700px; border-radius: 50%; background: radial-gradient(circle, rgba(212,168,83,0.08), transparent 70%); top: -200px; right: 20%; animation: float 12s ease-in-out infinite; }
.bg-orb-small { position: absolute; width: 400px; height: 400px; border-radius: 50%; background: radial-gradient(circle, rgba(91,140,90,0.06), transparent 70%); bottom: -100px; left: 10%; animation: float 10s ease-in-out infinite reverse; }
.bg-grid { position: absolute; inset: 0; background-image: linear-gradient(rgba(212,168,83,0.04) 1px, transparent 1px), linear-gradient(90deg, rgba(212,168,83,0.04) 1px, transparent 1px); background-size: 80px 80px; }

@keyframes float {
  0%,100% { transform: translateY(0) translateZ(0); }
  50% { transform: translateY(-20px) translateZ(0); }
}

.login-brand { flex: 1; display: flex; align-items: center; justify-content: center; position: relative; z-index: 1; padding: 60px; }
.brand-content { text-align: center; max-width: 420px; }
.brand-icon { width: 80px; height: 80px; margin: 0 auto 24px; }
.wheat-svg { width: 100%; height: 100%; filter: drop-shadow(0 0 20px rgba(212,168,83,0.3)); }
.brand-title { font-size: 3.5rem; font-weight: 900; letter-spacing: 0.3em; background: var(--gold-gradient); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; margin-bottom: 4px; }
.brand-subtitle { font-size: 0.75rem; font-weight: 600; color: var(--text-muted); letter-spacing: 0.5em; margin-bottom: 32px; }
.brand-divider { width: 60px; height: 2px; background: var(--gold-gradient); margin: 0 auto 24px; border-radius: 1px; }
.brand-desc { font-size: 0.9375rem; color: var(--text-secondary); line-height: 1.8; margin-bottom: 40px; }
.brand-features { display: flex; flex-direction: column; gap: 12px; align-items: flex-start; }
.feature-item { display: flex; align-items: center; gap: 10px; font-size: 0.8125rem; color: var(--text-muted); }
.feature-icon { display: flex; align-items: center; justify-content: center; width: 20px; height: 20px; border-radius: 50%; background: rgba(212,168,83,0.15); color: var(--primary); font-size: 12px; }

.login-form-section { width: 480px; display: flex; align-items: center; justify-content: center; position: relative; z-index: 1; padding: 40px 40px 40px 20px; }
.form-container { width: 100%; max-width: 400px; padding: 44px 40px; border-radius: 24px; background: rgba(20, 20, 20, 0.5); backdrop-filter: blur(8px); }
.form-header { margin-bottom: 36px; }
.form-header h2 { font-size: 1.75rem; font-weight: 700; color: var(--text-primary); margin-bottom: 6px; letter-spacing: -0.02em; }
.form-header p { font-size: 0.875rem; color: var(--text-muted); }

.custom-input :deep(.el-input__wrapper) { background: var(--bg-surface) !important; box-shadow: 0 0 0 1px var(--border) !important; border-radius: 10px; padding: 4px 14px; height: 48px; transition: all 0.25s; }
.custom-input :deep(.el-input__wrapper:hover) { box-shadow: 0 0 0 1px var(--border-light) !important; }
.custom-input :deep(.el-input__wrapper.is-focus) { box-shadow: 0 0 0 1px var(--primary) !important; }
.custom-input :deep(.el-input__inner) { color: var(--text-primary) !important; font-size: 0.9375rem; }

.form-options { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.forgot-link { font-size: 0.8125rem; color: var(--primary); text-decoration: none; transition: color 0.2s; }
.forgot-link:hover { color: var(--primary-light); }

.login-btn { width: 100%; height: 48px; border-radius: 10px; font-size: 1rem; font-weight: 600; letter-spacing: 0.15em; background: var(--gold-gradient) !important; border: none !important; transition: all 0.3s var(--transition-smooth); }
.login-btn:hover { transform: translateY(-1px); box-shadow: 0 8px 32px rgba(212, 168, 83, 0.25); }

.register-link { text-align: center; margin-top: 20px; font-size: 0.875rem; color: var(--text-muted); }
.register-link a { color: var(--primary); text-decoration: none; font-weight: 500; margin-left: 4px; }
.register-link a:hover { color: var(--primary-light); }

.demo-login { margin-top: 24px; text-align: center; }
.demo-title { font-size: 0.75rem; color: var(--text-muted); margin-bottom: 14px; }
.demo-buttons { display: flex; gap: 6px; justify-content: center; flex-wrap: wrap; }
.demo-btn { display: flex; align-items: center; gap: 5px; padding: 7px 12px; border: 1px solid var(--border); border-radius: 8px; background: transparent; color: var(--text-secondary); cursor: pointer; font-size: 0.71875rem; transition: all 0.25s var(--transition-smooth); }
.demo-btn:hover { border-color: var(--primary); color: var(--primary); background: rgba(212, 168, 83, 0.08); }
.demo-role-icon { width: 20px; height: 20px; border-radius: 5px; display: flex; align-items: center; justify-content: center; font-size: 0.625rem; font-weight: 700; color: #1a1a1a; }

.forgot-step { padding: 4px 0; }
.forgot-desc { font-size: 0.875rem; color: var(--text-secondary); margin-bottom: 20px; }
.forgot-success-tip { display: flex; align-items: center; gap: 8px; padding: 12px 16px; background: rgba(91,140,90,0.1); border: 1px solid rgba(91,140,90,0.25); border-radius: 10px; margin-bottom: 20px; font-size: 0.875rem; color: var(--accent); }
.code-row { display: flex; gap: 10px; width: 100%; }
.code-input { flex: 1; }
.code-btn { height: 48px; border-radius: 10px; font-size: 0.8125rem; white-space: nowrap; border: 1px solid var(--border); background: var(--bg-surface); color: var(--text-secondary); min-width: 110px; }
.code-btn:hover:not(:disabled) { border-color: var(--primary); color: var(--primary); }
.forgot-actions { display: flex; justify-content: flex-end; gap: 10px; margin-top: 8px; }

@media (max-width: 860px) {
  .login-brand { display: none; }
  .login-form-section { width: 100%; padding: 20px; }
}
</style>