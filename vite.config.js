import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from '@tailwindcss/vite'
import path from 'path'

export default defineConfig({
  // GitHub Pages 部署基础路径，仓库名demo
  base: "/demo/",
  plugins: [vue(), tailwindcss()],
  resolve: {
    alias: {
      // @ 映射 src 文件夹
      '@': path.resolve(__dirname, 'src')
    }
  },
  // 本地开发接口代理
  server: {
    proxy: {
      '/xiaomai': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path
      }
    }
  }
})