import { ref, computed, watch, onMounted } from 'vue'

export type ThemeType = 'tech' | 'light' | 'cartoon'

interface Theme {
  id: ThemeType
  name: string
  icon: string
  description: string
}

export const themes: Theme[] = [
  {
    id: 'tech',
    name: '科技风格',
    icon: '🚀',
    description: '深色科技主题，霓虹发光效果'
  },
  {
    id: 'light',
    name: '白色主题',
    icon: '☀️',
    description: '清新简洁的浅色主题'
  },
  {
    id: 'cartoon',
    name: '卡通风格',
    icon: '🎨',
    description: '彩色活泼的卡通主题'
  }
]

const THEME_KEY = 'user-theme-preference'
const currentTheme = ref<ThemeType>('tech')
const isThemeTransitioning = ref(false)

export const useTheme = () => {
  // 获取当前主题信息
  const getCurrentTheme = computed(() => {
    return themes.find(theme => theme.id === currentTheme.value) || themes[0]
  })

  // 应用主题到DOM（带过渡效果）
  const applyTheme = async (theme: ThemeType) => {
    try {
      isThemeTransitioning.value = true
      
      // 添加过渡效果
      document.documentElement.style.transition = 'all 0.3s ease'
      
      // 移除所有主题类
      document.documentElement.removeAttribute('data-theme')
      
      // 短暂延迟以确保过渡效果
      await new Promise(resolve => setTimeout(resolve, 50))
      
      // 应用新主题
      document.documentElement.setAttribute('data-theme', theme)
      
      // 更新body类（用于前台页面）
      if (document.body.classList.contains('tech-theme')) {
        // 如果是前台页面，确保主题属性正确应用
        document.body.setAttribute('data-theme', theme)
      }
      
      // 移除过渡效果
      setTimeout(() => {
        document.documentElement.style.transition = ''
        isThemeTransitioning.value = false
      }, 300)
      
    } catch (error) {
      console.error('主题切换失败:', error)
      isThemeTransitioning.value = false
    }
  }

  // 切换主题
  const setTheme = (theme: ThemeType) => {
    if (isThemeTransitioning.value) return // 防止快速重复切换
    
    currentTheme.value = theme
    applyTheme(theme)
    
    // 保存到localStorage
    try {
      localStorage.setItem(THEME_KEY, theme)
    } catch (error) {
      console.warn('无法保存主题偏好:', error)
    }
    
    // 触发自定义事件，通知其他组件主题已更改
    window.dispatchEvent(new CustomEvent('theme-changed', { 
      detail: { theme, themeInfo: getCurrentTheme.value }
    }))
  }

  // 获取下一个主题（用于快速切换）
  const getNextTheme = () => {
    const currentIndex = themes.findIndex(theme => theme.id === currentTheme.value)
    const nextIndex = (currentIndex + 1) % themes.length
    return themes[nextIndex].id
  }

  // 切换到下一个主题
  const toggleTheme = () => {
    if (isThemeTransitioning.value) return
    setTheme(getNextTheme())
  }

  // 初始化主题
  const initTheme = () => {
    try {
      // 从localStorage读取保存的主题
      const savedTheme = localStorage.getItem(THEME_KEY) as ThemeType
      
      if (savedTheme && themes.some(theme => theme.id === savedTheme)) {
        currentTheme.value = savedTheme
      } else {
        // 检测系统主题偏好
        const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
        currentTheme.value = prefersDark ? 'tech' : 'light'
      }
      
      applyTheme(currentTheme.value)
    } catch (error) {
      console.warn('主题初始化失败，使用默认主题:', error)
      currentTheme.value = 'tech'
      applyTheme('tech')
    }
  }

  // 监听系统主题变化
  const watchSystemTheme = () => {
    try {
      const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
      
      const handleChange = (e: MediaQueryListEvent) => {
        // 只有在用户没有手动设置主题时才跟随系统
        const hasManualTheme = localStorage.getItem(THEME_KEY)
        if (!hasManualTheme) {
          setTheme(e.matches ? 'tech' : 'light')
        }
      }
      
      mediaQuery.addEventListener('change', handleChange)
      
      // 返回清理函数
      return () => mediaQuery.removeEventListener('change', handleChange)
    } catch (error) {
      console.warn('无法监听系统主题变化:', error)
      return () => {}
    }
  }

  // 重置主题为默认
  const resetTheme = () => {
    try {
      localStorage.removeItem(THEME_KEY)
      setTheme('tech')
    } catch (error) {
      console.warn('重置主题失败:', error)
    }
  }

  // 获取主题对应的CSS变量
  const getThemeColors = computed(() => {
    try {
      const style = getComputedStyle(document.documentElement)
      return {
        primary: style.getPropertyValue('--primary-color').trim(),
        secondary: style.getPropertyValue('--secondary-color').trim(),
        accent: style.getPropertyValue('--accent-color').trim(),
        surface: style.getPropertyValue('--surface-bg').trim(),
        text: style.getPropertyValue('--text-primary').trim(),
      }
    } catch (error) {
      console.warn('获取主题颜色失败:', error)
      return {
        primary: '#00d4ff',
        secondary: '#ff6b6b',
        accent: '#4ecdc4',
        surface: 'rgba(26, 26, 46, 0.8)',
        text: '#ffffff',
      }
    }
  })

  // 检查是否支持某个主题
  const isThemeSupported = (theme: ThemeType) => {
    return themes.some(t => t.id === theme)
  }

  // 预加载主题（可选功能）
  const preloadTheme = (theme: ThemeType) => {
    if (!isThemeSupported(theme)) return
    
    // 创建临时元素来预加载主题样式
    const tempEl = document.createElement('div')
    tempEl.setAttribute('data-theme', theme)
    tempEl.style.position = 'absolute'
    tempEl.style.top = '-9999px'
    tempEl.style.opacity = '0'
    tempEl.style.pointerEvents = 'none'
    
    document.body.appendChild(tempEl)
    
    // 强制重绘
    getComputedStyle(tempEl).color
    
    // 移除临时元素
    setTimeout(() => {
      document.body.removeChild(tempEl)
    }, 100)
  }

  onMounted(() => {
    initTheme()
  })

  return {
    currentTheme: computed(() => currentTheme.value),
    getCurrentTheme,
    themes,
    setTheme,
    toggleTheme,
    getNextTheme,
    resetTheme,
    getThemeColors,
    initTheme,
    applyTheme,
    watchSystemTheme,
    isThemeSupported,
    preloadTheme,
    isThemeTransitioning: computed(() => isThemeTransitioning.value)
  }
} 