<template>
  <div class="register-page">
    <div class="login-bg">
      <div class="bg-orb-large"></div>
      <div class="bg-orb-small"></div>
      <div class="bg-grid"></div>
    </div>

    <div class="register-form-section">
      <div class="form-container glass">
        <!-- 返回按钮 -->
        <button class="back-btn" @click="$router.push('/login')">
          <el-icon :size="18"><ArrowLeft /></el-icon>
          <span>返回登录</span>
        </button>

        <div class="form-header">
          <h2>创建账户</h2>
          <p>注册小麦种子追溯系统账号</p>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" label-width="0" class="register-form">
          <!-- 基本信息 -->
          <div class="form-section-title">基本信息</div>
          <el-row :gutter="12">
            <el-col :span="12">
              <el-form-item prop="name">
                <el-input v-model="form.name" placeholder="真实姓名" :prefix-icon="User" size="large" class="custom-input" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item prop="username">
                <el-input v-model="form.username" placeholder="用户名" :prefix-icon="UserFilled" size="large" class="custom-input" />
              </el-form-item>
            </el-col>
          </el-row>

          <!-- 手机号 -->
          <el-form-item prop="phone">
            <el-input v-model="form.phone" placeholder="手机号" :prefix-icon="Phone" size="large" class="custom-input" />
          </el-form-item>

          <!-- 短信验证码 -->
          <el-form-item prop="phoneCode">
            <div class="code-row">
              <el-input v-model="form.phoneCode" placeholder="短信验证码" size="large" class="custom-input code-input" maxlength="6" />
              <el-button class="code-btn" :disabled="codeCountdown > 0" @click="sendPhoneCode">
                {{ codeCountdown > 0 ? `${codeCountdown}s` : '获取验证码' }}
              </el-button>
            </div>
          </el-form-item>

          <!-- 角色选择 -->
          <div class="form-section-title">选择角色</div>
          <el-form-item prop="role">
            <div class="role-selector">
              <div
                v-for="role in availableRoles"
                :key="role.key"
                class="role-card"
                :class="{ selected: form.role === role.key }"
                @click="form.role = role.key"
              >
                <div class="role-card-icon" :style="{ background: role.color }">
                  {{ role.avatar }}
                </div>
                <div class="role-card-name">{{ role.label }}</div>
                <div class="role-card-desc">{{ role.desc }}</div>
                <div v-if="form.role === role.key" class="role-check">
                  <el-icon :size="14"><CircleCheck /></el-icon>
                </div>
              </div>
            </div>
          </el-form-item>

          <!-- 密码 -->
          <div class="form-section-title">设置密码</div>
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="设置密码（至少6位）" :prefix-icon="Lock" size="large" show-password class="custom-input" />
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" :prefix-icon="Lock" size="large" show-password class="custom-input" />
          </el-form-item>

          <el-form-item prop="agreed">
            <el-checkbox v-model="form.agreed">
              <span class="agree-text">我已阅读并同意 <a href="#" @click.prevent="showServiceAgreement = true">《用户服务协议》</a> 和 <a href="#" @click.prevent="showPrivacyPolicy = true">《隐私政策》</a></span>
            </el-checkbox>
          </el-form-item>

          <el-button type="primary" size="large" class="register-btn" :loading="registering" @click="handleRegister">
            {{ registering ? '注册中...' : '注 册' }}
          </el-button>
        </el-form>

        <div class="login-link">
          已有账户？<a href="#" @click.prevent="$router.push('/login')">立即登录</a>
        </div>
      </div>

      <!-- 用户服务协议弹窗 -->
      <el-dialog v-model="showServiceAgreement" title="用户服务协议" width="640px" class="custom-dialog agreement-dialog" :close-on-click-modal="false">
        <div class="agreement-content">
          <p class="agreement-update">更新日期：2026年5月1日 &nbsp;|&nbsp; 生效日期：2026年5月15日</p>

          <h4>一、总则</h4>
          <p>1.1 欢迎使用「麦溯」小麦种子追溯系统（以下简称"本系统"）。</p>
          <p>1.2 本协议是您（以下简称"用户"）与本系统运营方之间关于使用本系统服务所订立的协议。</p>
          <p>1.3 您在注册过程中勾选"我已阅读并同意"即视为您已充分阅读、理解并接受本协议的全部内容。</p>

          <h4>二、账户注册与管理</h4>
          <p>2.1 用户应提供真实、准确、完整的注册信息，包括但不限于真实姓名、手机号码等。</p>
          <p>2.2 每个手机号仅限注册一个账户，用户应妥善保管账户及密码信息。</p>
          <p>2.3 用户对所注册账户下的所有操作行为承担全部责任。</p>
          <p>2.4 用户不得将账户转让、出借或与他人共享使用。</p>

          <h4>三、角色与权限</h4>
          <p>3.1 本系统设有多类角色：种植人员、加工人员、质检人员、销售商。各类角色具有不同的操作权限。</p>
          <p>3.2 用户应根据自身实际业务角色选择对应身份注册，不得冒用他人身份或越权操作。</p>
          <p>3.3 系统管理员有权审核、调整用户角色及权限。</p>

          <h4>四、数据录入规范</h4>
          <p>4.1 用户应按照"种子信息 → 种植记录 → 加工记录 → 质检记录 → 销售记录"的业务流程顺序录入数据。</p>
          <p>4.2 前一环节未完成数据录入的，后续环节不可操作，以确保追溯链条的完整性。</p>
          <p>4.3 用户录入的数据应真实、准确、完整，不得伪造、篡改与他人的追溯数据。</p>
          <p>4.4 非管理员用户可在录入数据后3分钟内对本人创建的记录进行修改，超时后仅管理员可修改。</p>

          <h4>五、用户义务</h4>
          <p>5.1 用户不得利用本系统从事任何违法违规活动。</p>
          <p>5.2 用户不得干扰本系统的正常运营，不得进行任何可能损害系统安全的行为。</p>
          <p>5.3 用户发现系统漏洞或安全隐患时，应及时向管理员报告。</p>

          <h4>六、免责声明</h4>
          <p>6.1 因不可抗力、系统维护等原因导致的服务中断，本系统不承担责任。</p>
          <p>6.2 因用户自身原因（如密码泄露、操作失误等）造成的损失，由用户自行承担。</p>

          <h4>七、其他</h4>
          <p>7.1 本协议的解释、效力及争议的解决，适用中华人民共和国法律。</p>
          <p>7.2 本系统保留对本协议进行修改的权利，修改后的协议将在系统中公示，继续使用即视为同意修改后的协议。</p>
        </div>
        <template #footer>
          <el-button type="primary" @click="showServiceAgreement = false">已 阅 读</el-button>
        </template>
      </el-dialog>

      <!-- 隐私政策弹窗 -->
      <el-dialog v-model="showPrivacyPolicy" title="隐私政策" width="640px" class="custom-dialog agreement-dialog" :close-on-click-modal="false">
        <div class="agreement-content">
          <p class="agreement-update">更新日期：2026年5月1日 &nbsp;|&nbsp; 生效日期：2026年5月15日</p>

          <h4>一、信息收集</h4>
          <p>1.1 本系统在注册及使用过程中可能收集以下信息：</p>
          <p style="padding-left:16px">a) 身份信息：真实姓名、用户名、手机号码、电子邮箱；</p>
          <p style="padding-left:16px">b) 业务数据：种子批次信息、种植记录、加工记录、质检记录、销售记录等；</p>
          <p style="padding-left:16px">c) 操作日志：用户在本系统中的操作时间、操作内容及IP地址。</p>
          <p>1.2 上述信息的收集均出于小麦种子追溯业务管理的必要需求。</p>

          <h4>二、信息使用</h4>
          <p>2.1 收集的信息仅用于以下目的：</p>
          <p style="padding-left:16px">a) 实现小麦种子从种植到销售的全链条追溯；</p>
          <p style="padding-left:16px">b) 保障种子质量安全，满足监管合规要求；</p>
          <p style="padding-left:16px">c) 提供系统功能服务与技术支持。</p>
          <p>2.2 未经用户明确同意，本系统不会将用户信息用于上述目的之外的用途。</p>

          <h4>三、信息存储与保护</h4>
          <p>3.1 用户信息存储于中华人民共和国境内的服务器。</p>
          <p>3.2 本系统采用加密传输、访问控制、日志审计等技术手段保护用户信息安全。</p>
          <p>3.3 用户密码采用不可逆加密算法存储，系统管理员亦无法获取明文密码。</p>

          <h4>四、信息共享与披露</h4>
          <p>4.1 除以下情形外，本系统不会向第三方共享用户信息：</p>
          <p style="padding-left:16px">a) 法律法规要求或政府部门依法调取；</p>
          <p style="padding-left:16px">b) 追溯链条中下游环节查看上游必要信息（如批次号关联）；</p>
          <p style="padding-left:16px">c) 经用户明确授权同意。</p>

          <h4>五、用户权利</h4>
          <p>5.1 用户有权查阅、更正本人的注册信息。</p>
          <p>5.2 用户有权要求删除本人账户（业务追溯数据因合规要求除外）。</p>
          <p>5.3 用户有权就个人信息处理问题向本系统提出咨询和投诉。</p>

          <h4>六、未成年人保护</h4>
          <p>6.1 本系统面向农业从业人员，原则上不收集未成年人信息。</p>
          <p>6.2 如发现误收集了未成年人信息，将及时予以删除。</p>

          <h4>七、政策更新</h4>
          <p>7.1 本隐私政策可能适时修订，修订后的政策将在系统中公示。</p>
          <p>7.2 重大变更将通过系统通知或短信方式告知用户。</p>
        </div>
        <template #footer>
          <el-button type="primary" @click="showPrivacyPolicy = false">已 阅 读</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, User, UserFilled, Phone, Lock, CircleCheck } from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref()
const registering = ref(false)
const codeCountdown = ref(0)
const showServiceAgreement = ref(false)
const showPrivacyPolicy = ref(false)
let countdownTimer = null

const form = reactive({
  name: '',
  username: '',
  phone: '',
  phoneCode: '',
  role: '',
  password: '',
  confirmPassword: '',
  agreed: false
})

const availableRoles = [
  { key: 'planter', label: '种植人员', avatar: 'P', color: '#5b8c5a', desc: '管理种植环节记录' },
  { key: 'processor', label: '加工人员', avatar: 'R', color: '#4a90d9', desc: '管理加工环节记录' },
  { key: 'inspector', label: '质检人员', avatar: 'I', color: '#6c5ce7', desc: '管理质检环节记录' },
  { key: 'seller', label: '销售商', avatar: 'S', color: '#e87a4a', desc: '管理销售环节记录' },
]

// 已注册用户名和手机号（模拟）
const existingUsers = ['admin', 'planter', 'processor', 'inspector', 'seller']
const existingPhones = ['13800000001', '13800000002', '13800000003', '13800000004', '13800000005']

const validateUsername = (rule, value, callback) => {
  if (!value) return callback(new Error('请输入用户名'))
  if (value.length < 3) return callback(new Error('用户名至少3个字符'))
  if (existingUsers.includes(value.toLowerCase())) return callback(new Error('该用户名已被注册'))
  callback()
}

const validatePhone = (rule, value, callback) => {
  if (!value) return callback(new Error('请输入手机号'))
  if (!/^1[3-9]\d{9}$/.test(value)) return callback(new Error('请输入有效的手机号'))
  if (existingPhones.includes(value)) return callback(new Error('该手机号已被注册'))
  callback()
}

const rules = {
  name: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  username: [{ required: true, validator: validateUsername, trigger: 'blur' }],
  phone: [{ required: true, validator: validatePhone, trigger: 'blur' }],
  phoneCode: [{ required: true, message: '请输入短信验证码', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  password: [
    { required: true, message: '请设置密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== form.password) callback(new Error('两次输入的密码不一致'))
        else callback()
      },
      trigger: 'blur'
    }
  ],
  agreed: [{
    validator: (rule, value, callback) => {
      if (!value) callback(new Error('请阅读并同意服务协议'))
      else callback()
    },
    trigger: 'change'
  }]
}

function sendPhoneCode() {
  formRef.value?.validateField('phone', (err) => {
    if (err) return
    codeCountdown.value = 60
    ElMessage.success('验证码已发送至 ' + form.phone + '（演示验证码：123456）')
    countdownTimer = setInterval(() => {
      codeCountdown.value--
      if (codeCountdown.value <= 0) {
        clearInterval(countdownTimer)
        countdownTimer = null
      }
    }, 1000)
  })
}

function handleRegister() {
  formRef.value?.validate(valid => {
    if (!valid) return
    if (form.phoneCode !== '123456') {
      ElMessage.error('验证码错误（演示验证码：123456）')
      return
    }
    registering.value = true
    setTimeout(() => {
      existingUsers.push(form.username.toLowerCase())
      existingPhones.push(form.phone)
      ElMessage.success('注册成功！请使用新账号登录')
      registering.value = false
      router.push('/login')
    }, 800)
  })
}
</script>

<style scoped>
.register-page {
  display: flex; min-height: 100vh; background: var(--bg-dark);
  position: relative; overflow: hidden; align-items: center; justify-content: center;
}

.login-bg { position: fixed; inset: 0; pointer-events: none; z-index: 0; }
.bg-orb-large { position: absolute; width: 600px; height: 600px; border-radius: 50%; background: radial-gradient(circle, rgba(212,168,83,0.06), transparent 70%); top: -150px; right: 25%; animation: float 12s ease-in-out infinite; }
.bg-orb-small { position: absolute; width: 350px; height: 350px; border-radius: 50%; background: radial-gradient(circle, rgba(91,140,90,0.05), transparent 70%); bottom: -80px; left: 15%; animation: float 10s ease-in-out infinite reverse; }
.bg-grid { position: absolute; inset: 0; background-image: linear-gradient(rgba(212,168,83,0.03) 1px, transparent 1px), linear-gradient(90deg, rgba(212,168,83,0.03) 1px, transparent 1px); background-size: 60px 60px; }

.register-form-section { position: relative; z-index: 1; width: 560px; padding: 40px 20px; }
.form-container { padding: 36px 40px; border-radius: 24px; background: rgba(20, 20, 20, 0.5); }

.back-btn { display: inline-flex; align-items: center; gap: 4px; padding: 6px 10px; border: 1px solid var(--border); border-radius: 8px; background: transparent; color: var(--text-muted); cursor: pointer; font-size: 0.8125rem; margin-bottom: 24px; transition: all 0.25s; }
.back-btn:hover { color: var(--primary); border-color: var(--primary); }

.form-header { margin-bottom: 28px; }
.form-header h2 { font-size: 1.75rem; font-weight: 700; color: var(--text-primary); margin-bottom: 6px; }
.form-header p { font-size: 0.875rem; color: var(--text-muted); }

.form-section-title { font-size: 0.75rem; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.1em; margin-bottom: 12px; padding-top: 8px; border-top: 1px solid var(--border); }
.form-section-title:first-of-type { border-top: none; padding-top: 0; }

.custom-input :deep(.el-input__wrapper) { background: var(--bg-surface) !important; box-shadow: 0 0 0 1px var(--border) !important; border-radius: 10px; padding: 4px 14px; height: 48px; }
.custom-input :deep(.el-input__wrapper:hover) { box-shadow: 0 0 0 1px var(--border-light) !important; }
.custom-input :deep(.el-input__wrapper.is-focus) { box-shadow: 0 0 0 1px var(--primary) !important; }
.custom-input :deep(.el-input__inner) { color: var(--text-primary) !important; font-size: 0.875rem; }

.code-row { display: flex; gap: 10px; width: 100%; }
.code-input { flex: 1; }
.code-btn { height: 48px; border-radius: 10px; font-size: 0.8125rem; white-space: nowrap; border: 1px solid var(--border); background: var(--bg-surface); color: var(--text-secondary); min-width: 110px; }
.code-btn:hover:not(:disabled) { border-color: var(--primary); color: var(--primary); }

/* 角色选择器 */
.role-selector { display: grid; grid-template-columns: repeat(2, 1fr); gap: 10px; width: 100%; }
.role-card {
  position: relative; padding: 14px; border-radius: 12px;
  border: 1.5px solid var(--border); background: var(--bg-surface);
  cursor: pointer; transition: all 0.3s var(--transition-smooth); text-align: center;
}
.role-card:hover { border-color: var(--border-light); }
.role-card.selected { border-color: var(--primary); background: rgba(212,168,83,0.06); }
.role-card-icon { width: 34px; height: 34px; border-radius: 10px; display: flex; align-items: center; justify-content: center; font-size: 0.8125rem; font-weight: 700; color: #1a1a1a; margin: 0 auto 8px; }
.role-card-name { font-size: 0.8125rem; font-weight: 600; color: var(--text-primary); margin-bottom: 2px; }
.role-card-desc { font-size: 0.6875rem; color: var(--text-muted); }
.role-check { position: absolute; top: 8px; right: 8px; color: var(--primary); }

.agree-text { font-size: 0.8125rem; color: var(--text-muted); }
.agree-text a { color: var(--primary); text-decoration: none; }

.register-btn { width: 100%; height: 48px; border-radius: 10px; font-size: 1rem; font-weight: 600; letter-spacing: 0.15em; background: var(--gold-gradient) !important; border: none !important; margin-top: 8px; }

.login-link { text-align: center; margin-top: 20px; font-size: 0.875rem; color: var(--text-muted); }
.login-link a { color: var(--primary); text-decoration: none; font-weight: 500; }
.login-link a:hover { color: var(--primary-light); }

.agreement-content { max-height: 50vh; overflow-y: auto; font-size: 0.8125rem; color: var(--text-secondary); line-height: 1.8; padding-right: 8px; }
.agreement-content h4 { font-size: 0.9375rem; font-weight: 600; color: var(--text-primary); margin: 16px 0 8px; }
.agreement-content h4:first-of-type { margin-top: 0; }
.agreement-content p { margin: 4px 0; }
.agreement-update { color: var(--text-muted); font-size: 0.75rem; margin-bottom: 16px !important; padding-bottom: 12px; border-bottom: 1px solid var(--border); }
.agreement-dialog :deep(.el-dialog__body) { padding-top: 8px; }

@media (max-width: 600px) {
  .register-form-section { width: 100%; padding: 16px; }
  .form-container { padding: 24px 20px; }
  .role-selector { grid-template-columns: 1fr; }
}
</style>
