import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import 'element-plus/dist/index.css'
import './assets/styles/main.css'
import App from './App.vue'
import router from './router'
// 新增：导入权限工具
import { usePermission } from '@/utils/permission'

const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: zhCn })

// 新增：页面初始化，读取本地缓存恢复角色权限（解决刷新丢失管理员权限）
const { loadRoleFromLocal } = usePermission()
loadRoleFromLocal()

app.mount('#app')