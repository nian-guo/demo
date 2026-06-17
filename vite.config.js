import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from '@tailwindcss/vite'
import path from 'path'

export default defineConfig({
  plugins: [vue(), tailwindcss()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  // 新增代理配置
  server: {
    proxy: {
      // 匹配所有 /xiaomai 开头的接口
      '/xiaomai': {
        target: 'http://localhost:8080', // 后端服务地址
        changeOrigin: true, // 修改请求源，解决跨域
        rewrite: (path) => path // 路径不重写，保持 /xiaomai/api/login
      }
    }
  }
})