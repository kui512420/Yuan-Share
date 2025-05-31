<template>
  <div class="theme-switcher">
    <!-- 主题切换按钮 -->
    <el-dropdown trigger="click" @command="handleThemeChange">
      <button class="theme-toggle-btn tech-button" :title="`当前主题: ${getCurrentTheme.name}`">
        <span class="theme-icon">{{ getCurrentTheme.icon }}</span>
        <span class="theme-text">{{ getCurrentTheme.name }}</span>
        <span class="dropdown-arrow">▼</span>
      </button>
      
      <template #dropdown>
        <el-dropdown-menu class="theme-dropdown">
          <div class="theme-dropdown-header">
            <span class="header-icon">🎨</span>
            <span class="header-text">选择主题</span>
          </div>
          
          <el-dropdown-item 
            v-for="theme in themes" 
            :key="theme.id"
            :command="theme.id"
            :class="{ 'active-theme': currentTheme === theme.id }"
            class="theme-option"
          >
            <div class="theme-option-content">
              <div class="theme-main">
                <span class="theme-icon-large">{{ theme.icon }}</span>
                <div class="theme-info">
                  <span class="theme-name">{{ theme.name }}</span>
                  <span class="theme-desc">{{ theme.description }}</span>
                </div>
              </div>
              <div v-if="currentTheme === theme.id" class="theme-check">✓</div>
            </div>
          </el-dropdown-item>
          
          <div class="theme-dropdown-footer">
            <el-button 
              type="text" 
              size="small" 
              @click="handleReset"
              class="reset-btn"
            >
              <span class="reset-icon">🔄</span>
              重置为默认
            </el-button>
          </div>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
    
    <!-- 快速切换按钮（可选） -->
    <button 
      v-if="showQuickToggle" 
      class="quick-toggle-btn tech-button"
      @click="handleQuickToggle"
      :title="`快速切换到: ${getNextThemeInfo.name}`"
    >
      {{ getNextThemeInfo.icon }}
    </button>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useTheme, type ThemeType } from '@/composables/useTheme'

interface Props {
  showQuickToggle?: boolean
  size?: 'small' | 'medium' | 'large'
}

const props = withDefaults(defineProps<Props>(), {
  showQuickToggle: false,
  size: 'medium'
})

const { 
  currentTheme, 
  getCurrentTheme, 
  themes, 
  setTheme, 
  toggleTheme, 
  getNextTheme,
  resetTheme 
} = useTheme()

// 获取下一个主题信息
const getNextThemeInfo = computed(() => {
  const nextThemeId = getNextTheme()
  return themes.find(theme => theme.id === nextThemeId) || themes[0]
})

// 处理主题切换
const handleThemeChange = (themeId: ThemeType) => {
  setTheme(themeId)
  
  const themeInfo = themes.find(theme => theme.id === themeId)
  ElMessage({
    message: `已切换到 ${themeInfo?.name} 主题`,
    type: 'success',
    duration: 2000,
    showClose: false
  })
}

// 快速切换主题
const handleQuickToggle = () => {
  const nextTheme = getNextTheme()
  const nextThemeInfo = themes.find(theme => theme.id === nextTheme)
  
  toggleTheme()
  
  ElMessage({
    message: `已切换到 ${nextThemeInfo?.name} 主题`,
    type: 'success',
    duration: 2000,
    showClose: false
  })
}

// 重置主题
const handleReset = () => {
  resetTheme()
  ElMessage({
    message: '已重置为默认主题',
    type: 'info',
    duration: 2000,
    showClose: false
  })
}
</script>

<style scoped>
.theme-switcher {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 主题切换按钮 */
.theme-toggle-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 8px;
  transition: all 0.3s ease;
  font-size: 14px;
  min-width: 120px;
  position: relative;
}

.theme-toggle-btn:hover {
  transform: translateY(-1px);
}

.theme-icon {
  font-size: 16px;
}

.theme-text {
  font-weight: 500;
}

.dropdown-arrow {
  font-size: 10px;
  transition: transform 0.3s ease;
  margin-left: auto;
}

.theme-toggle-btn:hover .dropdown-arrow {
  transform: rotate(180deg);
}

/* 快速切换按钮 */
.quick-toggle-btn {
  padding: 8px;
  border-radius: 50%;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  transition: all 0.3s ease;
}

.quick-toggle-btn:hover {
  transform: scale(1.1) rotate(15deg);
}

/* 下拉菜单样式 */
:deep(.theme-dropdown) {
  background: var(--surface-bg) !important;
  border: 2px solid var(--border-color) !important;
  border-radius: 12px !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3) !important;
  backdrop-filter: blur(20px) !important;
  -webkit-backdrop-filter: blur(20px) !important;
  min-width: 280px !important;
  padding: 8px 0 !important;
}

.theme-dropdown-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  border-bottom: 1px solid var(--border-color);
  margin-bottom: 4px;
  color: var(--text-primary);
  font-weight: 600;
  font-size: 14px;
}

.header-icon {
  font-size: 16px;
}

/* 主题选项 */
:deep(.theme-option) {
  padding: 0 !important;
  margin: 2px 8px !important;
  border-radius: 8px !important;
  transition: all 0.3s ease !important;
  border: 1px solid transparent !important;
}

:deep(.theme-option:hover) {
  background: rgba(var(--primary-color-rgb, 0, 212, 255), 0.1) !important;
  border-color: var(--primary-color) !important;
}

:deep(.theme-option.active-theme) {
  background: rgba(var(--primary-color-rgb, 0, 212, 255), 0.15) !important;
  border-color: var(--primary-color) !important;
}

.theme-option-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  width: 100%;
}

.theme-main {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.theme-icon-large {
  font-size: 20px;
  width: 24px;
  text-align: center;
}

.theme-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.theme-name {
  color: var(--text-primary);
  font-weight: 500;
  font-size: 14px;
}

.theme-desc {
  color: var(--text-muted);
  font-size: 12px;
}

.theme-check {
  color: var(--primary-color);
  font-weight: bold;
  font-size: 16px;
}

/* 下拉菜单底部 */
.theme-dropdown-footer {
  padding: 8px 16px;
  border-top: 1px solid var(--border-color);
  margin-top: 4px;
  display: flex;
  justify-content: center;
}

.reset-btn {
  color: var(--text-muted) !important;
  font-size: 12px !important;
  display: flex !important;
  align-items: center !important;
  gap: 4px !important;
  transition: all 0.3s ease !important;
}

.reset-btn:hover {
  color: var(--primary-color) !important;
}

.reset-icon {
  font-size: 12px;
}

/* 不同尺寸适配 */
.theme-switcher.size-small .theme-toggle-btn {
  padding: 6px 12px;
  font-size: 12px;
  min-width: 100px;
}

.theme-switcher.size-large .theme-toggle-btn {
  padding: 10px 20px;
  font-size: 16px;
  min-width: 140px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .theme-toggle-btn {
    min-width: 100px;
    padding: 6px 12px;
    font-size: 12px;
  }
  
  .theme-text {
    display: none;
  }
  
  :deep(.theme-dropdown) {
    min-width: 240px !important;
  }
  
  .theme-name {
    font-size: 13px;
  }
  
  .theme-desc {
    font-size: 11px;
  }
}

@media (max-width: 480px) {
  .quick-toggle-btn {
    width: 32px;
    height: 32px;
    font-size: 14px;
  }
}
</style> 