<script setup lang="ts">
import { RouterView, useRoute } from 'vue-router'
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import ChatWindow from '@/components/Management/AiComponents/ChatWindow.vue'
import TechBackground from '@/components/TechBackground.vue'
import { useTheme } from '@/composables/useTheme'

const route = useRoute()
const { initTheme, applyTheme, currentTheme } = useTheme()

// 判断是否为前台页面（只有首页路径为 '/' 的页面是前台页面）
const isFrontendPage = computed(() => {
  return route.path === '/'
})

// 判断是否应该显示ChatWindow
const shouldShowChatWindow = computed(() => {
  // ChatWindow只在前台页面显示
  return isFrontendPage.value
})

// 判断是否应该显示TechBackground
const shouldShowTechBackground = computed(() => {
  // 只在前台页面且科技主题下显示
  return isFrontendPage.value && currentTheme.value === 'tech'
})

// 监听路由变化，动态添加/移除body的tech-theme类
watch(isFrontendPage, (newVal) => {
  if (newVal) {
    document.body.classList.add('tech-theme')
    // 确保主题属性正确应用到body
    document.body.setAttribute('data-theme', currentTheme.value)
  } else {
    document.body.classList.remove('tech-theme')
    document.body.removeAttribute('data-theme')
  }
}, { immediate: true })

// 监听主题变化，更新body属性
watch(currentTheme, (newTheme) => {
  if (isFrontendPage.value) {
    document.body.setAttribute('data-theme', newTheme)
  }
})

onMounted(() => {
  // 初始化主题
  initTheme()
  
  // 组件挂载时立即设置body类
  if (isFrontendPage.value) {
    document.body.classList.add('tech-theme')
    document.body.setAttribute('data-theme', currentTheme.value)
  }
})

onUnmounted(() => {
  // 组件卸载时清理body类
  document.body.classList.remove('tech-theme')
  document.body.removeAttribute('data-theme')
})
</script>

<template>
  <div class="app-container" :class="{ 'tech-theme': isFrontendPage }">
    <!-- 科技背景只在前台页面+科技主题下显示 -->
    <TechBackground v-if="shouldShowTechBackground" />
    <router-view></router-view>
    <!-- AI聊天窗口只在前台页面显示 -->
    <ChatWindow v-if="shouldShowChatWindow" />
  </div>
</template>

<style scoped>
.app-container {
  min-height: 100vh;
  position: relative;
  background: transparent;
}

/* 只有前台页面才应用科技风格 */
.app-container.tech-theme {
  /* 科技风格的全局覆盖 */
}

.app-container.tech-theme :deep(.el-card) {
  background: var(--surface-bg) !important;
  border: 1px solid var(--border-color) !important;
  border-radius: 12px !important;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3) !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
}

.app-container.tech-theme :deep(.el-card:hover) {
  border-color: var(--primary-color) !important;
  box-shadow: 0 12px 40px rgba(0, 212, 255, 0.2) !important;
  transform: translateY(-2px) !important;
}

.app-container.tech-theme :deep(.el-card__header) {
  background: transparent !important;
  border-bottom: 1px solid var(--border-color) !important;
  color: var(--text-primary) !important;
}

.app-container.tech-theme :deep(.el-card__body) {
  color: var(--text-primary) !important;
}

/* Element Plus 按钮科技风格 - 只在前台页面应用 */
.app-container.tech-theme :deep(.el-button--primary) {
  background: linear-gradient(45deg, var(--primary-color), var(--accent-color)) !important;
  border: none !important;
  color: var(--primary-bg) !important;
  font-weight: 600 !important;
  transition: all 0.3s ease !important;
  position: relative !important;
  overflow: hidden !important;
}

.app-container.tech-theme :deep(.el-button--primary:hover) {
  background: linear-gradient(45deg, var(--accent-color), var(--primary-color)) !important;
  transform: translateY(-2px) !important;
  box-shadow: 0 10px 25px rgba(0, 212, 255, 0.3) !important;
}

.app-container.tech-theme :deep(.el-button--primary:active) {
  transform: translateY(0) !important;
}

/* Element Plus 输入框科技风格 - 只在前台页面应用 */
.app-container.tech-theme :deep(.el-input__wrapper) {
  background: rgba(26, 26, 46, 0.6) !important;
  border: 1px solid var(--border-color) !important;
  border-radius: 8px !important;
  transition: all 0.3s ease !important;
  box-shadow: none !important;
}

.app-container.tech-theme :deep(.el-input__wrapper:hover),
.app-container.tech-theme :deep(.el-input__wrapper.is-focus) {
  border-color: var(--primary-color) !important;
  box-shadow: 0 0 0 2px rgba(0, 212, 255, 0.2) !important;
}

.app-container.tech-theme :deep(.el-input__inner) {
  color: var(--text-primary) !important;
  background: transparent !important;
}

.app-container.tech-theme :deep(.el-input__inner::placeholder) {
  color: var(--text-muted) !important;
}

/* Element Plus 标签科技风格 - 只在前台页面应用 */
.app-container.tech-theme :deep(.el-tag) {
  background: linear-gradient(45deg, rgba(0, 212, 255, 0.2), rgba(78, 205, 196, 0.2)) !important;
  border: 1px solid var(--primary-color) !important;
  color: var(--primary-color) !important;
  border-radius: 20px !important;
  font-weight: 500 !important;
}

.app-container.tech-theme :deep(.el-tag.el-tag--info) {
  background: linear-gradient(45deg, rgba(78, 205, 196, 0.2), rgba(0, 212, 255, 0.2)) !important;
  border-color: var(--accent-color) !important;
  color: var(--accent-color) !important;
}

/* Element Plus 消息组件科技风格 - 只在前台页面应用 */
.app-container.tech-theme :deep(.el-message) {
  background: var(--surface-bg) !important;
  border: 1px solid var(--border-color) !important;
  color: var(--text-primary) !important;
  backdrop-filter: blur(20px) !important;
  -webkit-backdrop-filter: blur(20px) !important;
}

.app-container.tech-theme :deep(.el-message--success) {
  border-color: var(--success-color) !important;
}

.app-container.tech-theme :deep(.el-message--error) {
  border-color: var(--error-color) !important;
}

/* Element Plus 主菜单科技风格 - 只在前台页面应用 */
.app-container.tech-theme :deep(.el-menu) {
  background: var(--glass-bg) !important;
  border: 1px solid var(--border-color) !important;
  backdrop-filter: blur(20px) !important;
  -webkit-backdrop-filter: blur(20px) !important;
}

.app-container.tech-theme :deep(.el-menu-item) {
  color: var(--text-secondary) !important;
  transition: all 0.3s ease !important;
}

.app-container.tech-theme :deep(.el-menu-item:hover),
.app-container.tech-theme :deep(.el-menu-item.is-active) {
  color: var(--primary-color) !important;
  background: rgba(0, 212, 255, 0.1) !important;
}

/* Element Plus 分页器科技风格 - 只在前台页面应用 */
.app-container.tech-theme :deep(.el-pagination) {
  color: var(--text-primary) !important;
}

.app-container.tech-theme :deep(.el-pagination .el-pager li) {
  background: var(--surface-bg) !important;
  border: 1px solid var(--border-color) !important;
  color: var(--text-primary) !important;
}

.app-container.tech-theme :deep(.el-pagination .el-pager li:hover),
.app-container.tech-theme :deep(.el-pagination .el-pager li.is-active) {
  background: var(--primary-color) !important;
  color: var(--primary-bg) !important;
}

/* Element Plus 加载器科技风格 - 只在前台页面应用 */
.app-container.tech-theme :deep(.el-loading-mask) {
  background: rgba(10, 10, 15, 0.8) !important;
  backdrop-filter: blur(10px) !important;
  -webkit-backdrop-filter: blur(10px) !important;
}

.app-container.tech-theme :deep(.el-loading-spinner .circular) {
  stroke: var(--primary-color) !important;
}

/* Element Plus 弹出框科技风格 - 只在前台页面应用 */
.app-container.tech-theme :deep(.el-dialog) {
  background: var(--surface-bg) !important;
  border: 1px solid var(--border-color) !important;
  backdrop-filter: blur(20px) !important;
  -webkit-backdrop-filter: blur(20px) !important;
}

.app-container.tech-theme :deep(.el-dialog__header) {
  background: transparent !important;
  border-bottom: 1px solid var(--border-color) !important;
  color: var(--text-primary) !important;
}

.app-container.tech-theme :deep(.el-dialog__body) {
  color: var(--text-primary) !important;
}

/* Element Plus 表格科技风格 - 只在前台页面应用 */
.app-container.tech-theme :deep(.el-table) {
  background: var(--surface-bg) !important;
  color: var(--text-primary) !important;
}

.app-container.tech-theme :deep(.el-table th.el-table__cell) {
  background: var(--accent-bg) !important;
  border-bottom: 1px solid var(--border-color) !important;
  color: var(--text-primary) !important;
}

.app-container.tech-theme :deep(.el-table td.el-table__cell) {
  border-bottom: 1px solid var(--border-color) !important;
}

.app-container.tech-theme :deep(.el-table__row:hover > td.el-table__cell) {
  background: rgba(0, 212, 255, 0.1) !important;
}
</style>
