<template>
  <div
    class="tech-ai-window"
    :class="{ 'minimized': isMinimized, 'dragging': isDragging, 'collapsed': isCollapsed }"
    :style="{ transform: `translate3d(${position.x}px, ${position.y}px, 0)` }"
    @mousedown="startDrag"
  >
    <!-- 收起后显示的科技标签 -->
    <div class="tech-collapsed-tab" v-if="isCollapsed" @click.stop="toggleCollapse">
      <div class="tab-glow"></div>
      <span class="tab-icon">🤖</span>
      <span class="tab-text">AI 图像生成</span>
      <div class="tab-pulse"></div>
    </div>
    
    <div class="tech-window-header" v-if="!isCollapsed">
      <div class="header-left">
        <div class="header-icon">🤖</div>
        <h3 class="header-title">AI 图像生成</h3>
        <div class="header-glow"></div>
      </div>
      <div class="header-controls">
        <button class="tech-control-btn minimize-btn" @click.stop="toggleMinimize">
          {{ isMinimized ? '□' : '－' }}
        </button>
        <button class="tech-control-btn collapse-btn" @click.stop="toggleCollapse">
          →
        </button>
      </div>
    </div>
    <div class="tech-window-content" @mousedown.stop v-if="!isCollapsed">
      <div class="tech-image-display" ref="imageDisplay" @mousedown.stop>
        <div v-if="generatedImage" class="tech-image-container">
          <img :src="generatedImage" alt="生成的图像" />
          <div class="tech-image-controls">
            <button class="tech-control-button" @click="downloadImage">
              <span class="control-icon">⬇️</span>
              <span>下载</span>
            </button>
            <button class="tech-control-button" @click="addToChatWindow" v-if="!isGenerating">
              <span class="control-icon">💬</span>
              <span>添加到聊天</span>
            </button>
            <button class="tech-control-button" @click="regenerateImage" v-if="!isGenerating">
              <span class="control-icon">🔄</span>
              <span>重新生成</span>
            </button>
          </div>
        </div>
        <div v-else-if="isGenerating" class="tech-loading-container">
          <div class="tech-loading-spinner"></div>
          <p class="loading-text">AI 正在绘制图像中...</p>
          <div class="loading-progress">
            <div class="progress-bar"></div>
          </div>
        </div>
        <div v-else-if="generateError" class="tech-error-container" :class="{'prohibited-error': generateError.includes('违禁')}">
          <div class="error-icon">{{ generateError.includes('违禁') ? '🚫' : '⚠️' }}</div>
          <p class="error-text">{{ generateError }}</p>
          <button class="tech-retry-btn" @click="regenerateImage">
            重试
          </button>
          <p v-if="generateError.includes('违禁')" class="prohibited-tip">
            请修改提示词，避免包含色情、暴力、政治敏感等内容
          </p>
        </div>
        <div v-else class="tech-placeholder">
          <div class="placeholder-icon">🖼️</div>
          <p class="placeholder-text">输入提示词生成图像</p>
          <div class="tech-credit-info">
            <div class="credit-item">
              <span class="credit-icon">💎</span>
              <span>生成每张图像需要消耗AI积分</span>
            </div>
            <div class="credit-item">
              <span class="credit-icon">📅</span>
              <span>每天签到可获取1~5积分</span>
            </div>
          </div>
        </div>
      </div>
      <div class="tech-prompt-settings">
        <div class="settings-row">
          <div class="tech-setting-item">
            <label class="setting-label">尺寸</label>
            <el-select v-model="imageSize" class="tech-select" :disabled="isGenerating" placeholder="请选择">
              <el-option label="小图 (512x512)" value="512*512"></el-option>
              <el-option label="中图 (768x768)" value="768*768"></el-option>
              <el-option label="大图 (1024x1024)" value="1024*1024"></el-option>
              <el-option label="正方形 (1080x1080)" value="1080*1080"></el-option>
            </el-select>
          </div>
        </div>
      </div>
      <div class="tech-prompt-input">
        <div class="input-wrapper">
          <textarea
            v-model="promptText"
            @keydown.ctrl.enter="generateImage"
            placeholder="描述你想要生成的图像..."
            rows="3"
            :disabled="isGenerating"
            class="tech-textarea"
          ></textarea>
          <div class="input-glow"></div>
        </div>
        <div class="tech-prompt-tips">
          <p class="tips-title">提示: 尝试添加更多细节可以获得更好的图片效果</p>
          <div class="tips-examples">
            <span @click="fillPromptExample('风景')" class="tech-tip-tag">风景</span>
            <span @click="fillPromptExample('人物')" class="tech-tip-tag">人物</span>
            <span @click="fillPromptExample('动漫')" class="tech-tip-tag">动漫</span>
            <span @click="fillPromptExample('写实')" class="tech-tip-tag">写实</span>
            <el-popover
              placement="top"
              title="高级提示技巧"
              :width="300"
              trigger="click"
            >
              <template #reference>
                <span class="tech-tip-tag more-tips">更多技巧</span>
              </template>
              <div class="tech-advanced-tips">
                <h4>提示词技巧:</h4>
                <ul>
                  <li>描述场景、光线和氛围，如 "黄昏阳光、柔和光线"</li>
                  <li>指定艺术风格，如 "水彩画、油画、照片级写实"</li>
                  <li>添加技术词汇，如 "高清细节、4K、景深效果"</li>
                  <li>描述色彩倾向，如 "暖色调、冷色系、饱和度高"</li>
                </ul>
              </div>
            </el-popover>
          </div>
        </div>
        <div class="tech-input-actions">
          <span class="tech-char-count" :class="{ 'limit-reached': promptText.length > 200 }">
            {{ promptText.length }}/200
          </span>
          <button 
            @click="generateImage" 
            :disabled="!promptText.trim() || isGenerating"
            class="tech-generate-btn"
          >
            <span v-if="isGenerating" class="btn-loading"></span>
            <span class="btn-icon">✨</span>
            {{ isGenerating ? '生成中...' : '生成图像' }}
            <div class="btn-glow"></div>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { generate } from '@/api/ai'
import eventBus from '@/utils/EventBus'
import { ElMessage } from 'element-plus'

const promptText = ref('')
const imageDisplay = ref<HTMLElement | null>(null)
const generatedImage = ref<string | null>(null)
const isGenerating = ref(false)
const imageSize = ref('768*768')
const imageStyle = ref('realistic')
const generateError = ref<string | null>(null)

// 默认位置设置为页面右侧
const position = ref({ x: window.innerWidth - 50, y: 200 }) 
const isDragging = ref(false)
let startPos = { x: 0, y: 0 }
let startMousePos = { x: 0, y: 0 }

const isMinimized = ref(false)
const isCollapsed = ref(true) // 默认收起

// 切换展开/折叠状态
const toggleCollapse = (e?: MouseEvent) => {
  if (e) e.stopPropagation()
  isCollapsed.value = !isCollapsed.value
  
  // 当展开时调整位置，确保窗口在屏幕内
  if (!isCollapsed.value) {
    position.value.x = Math.min(position.value.x, window.innerWidth - 420)
    position.value.x = Math.max(position.value.x, 20)
  } else {
    // 折叠时移动到屏幕右侧
    position.value.x = window.innerWidth - 50
  }
}

const startDrag = (e: MouseEvent) => {
  // 如果是折叠状态或点击的是控制按钮，不启动拖拽
  if (isCollapsed.value || (e.target as HTMLElement).closest('.collapse-btn, .minimize-btn')) return
  
  // 只响应左键拖拽
  if (e.button !== 0) return
  
  isDragging.value = true
  startPos = { x: position.value.x, y: position.value.y }
  startMousePos = { x: e.clientX, y: e.clientY }

  // 添加全局事件监听器
  document.addEventListener('mousemove', onDrag, { passive: true })
  document.addEventListener('mouseup', stopDrag)
  
  // 防止选中文本
  e.preventDefault()
}

const onDrag = (e: MouseEvent) => {
  if (!isDragging.value) return
  
  requestAnimationFrame(() => {
    const dx = e.clientX - startMousePos.x
    const dy = e.clientY - startMousePos.y
    
    position.value = {
      x: startPos.x + dx,
      y: startPos.y + dy
    }
  })
}

const stopDrag = () => {
  isDragging.value = false
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
}

const generateImage = async () => {
  if (!promptText.value.trim() || isGenerating.value) return
  
  isGenerating.value = true
  generateError.value = null // 重置错误状态
  
  try {
    const response = await generate(
      promptText.value,
      imageSize.value
    )
    
    console.log('AI图片生成接口响应:', response);
    
    // 检查是否空响应 (表示包含违禁词)
    if (!response.data || Object.keys(response.data).length === 0) {
      generateError.value = "提示词包含违禁内容，请修改后重试"
      ElMessage({
        message: '您的提示词包含违禁内容，请修改后重试！',
        type: 'error',
        duration: 5000,
        showClose: true
      })
      return
    }
    
    // 检查是否余额不足
    if (response.data && response.data.msg === "余额不足") {
      generateError.value = "AI积分余额不足，每天签到可获取1~5积分"
      ElMessage({
        message: '您的AI积分余额不足，每天签到可以获取1~5积分哦！',
        type: 'warning',
        duration: 5000,
        showClose: true
      })
      return
    }
    
    // 处理API返回的数据结构
    if (response.data && response.data.msg && response.data.msg.output && 
        response.data.msg.output.results && response.data.msg.output.results.length > 0) {
      generatedImage.value = response.data.msg.output.results[0].url
    } else {
      // 生成失败但不是余额不足的情况
      generateError.value = "图像生成失败，请重试"
    }
  } catch (error) {
    console.error('生成图像失败:', error)
    generateError.value = "图像生成失败，请稍后重试"
  } finally {
    isGenerating.value = false
  }
}

const regenerateImage = () => {
  if (promptText.value.trim()) {
    generateImage()
  }
}

const downloadImage = () => {
  if (!generatedImage.value) return
  
  const link = document.createElement('a')
  link.href = generatedImage.value
  link.download = `AI生成图像_${new Date().getTime()}.png`
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

const addToChatWindow = () => {
  if (!generatedImage.value) return
  // 通过事件总线发送图片URL给MessageBoard组件
  eventBus.emit('add-image-to-chat', generatedImage.value)
}

// 示例提示词
const promptExamples = {
  '风景': '高清自然风景照片，雪山与湖泊倒影，金色晨光照射，晴朗湛蓝天空，松树环绕，绚丽多彩的日出，薄雾弥漫，4K超清画质，专业摄影作品',
  '人物': '年轻亚洲女性肖像，自然柔和光线，浅景深虚化背景，专业摄影棚拍摄，高清面部细节，微笑表情，精致妆容，穿着时尚服装，照片级真实感',
  '动漫': '动漫风格女孩角色，明亮鲜艳色彩，细节丰富的大眼睛，精致描绘的长发飘动，Studio Ghibli风格背景，温暖明亮的氛围，高清插画质量',
  '写实': '超写实主义肖像油画，年轻男性特写，精细刻画的面部细节和皮肤质感，自然光从左侧照射，深邃的眼神，情感丰富的表情，文艺复兴风格光影效果'
}

// 填充示例提示词
const fillPromptExample = (type: keyof typeof promptExamples) => {
  promptText.value = promptExamples[type]
}

const toggleMinimize = (e: MouseEvent) => {
  e.stopPropagation()
  isMinimized.value = !isMinimized.value
}

// 调整窗口大小时重新计算位置
const handleResize = () => {
  if (isCollapsed.value) {
    position.value.x = window.innerWidth - 50
  } else {
    // 确保窗口在可视区域内
    position.value.x = Math.min(position.value.x, window.innerWidth - 420)
    position.value.x = Math.max(position.value.x, 20)
  }
}

onMounted(() => {
  // 监听窗口大小变化
  window.addEventListener('resize', handleResize)
})

// 关闭时移除事件监听器
onMounted(() => {
  window.addEventListener('resize', handleResize)
  return () => {
    window.removeEventListener('resize', handleResize)
  }
})
</script>

<style scoped>
/* 科技风格AI窗口 */
.tech-ai-window {
  position: fixed;
  width: 420px;
  height: 600px;
  background: var(--surface-bg);
  border: 2px solid var(--border-color);
  border-radius: 16px;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  box-shadow: 
    0 20px 40px rgba(0, 0, 0, 0.3),
    0 0 20px rgba(0, 212, 255, 0.2);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 1000;
  cursor: move;
  overflow: hidden;
  color: var(--text-primary);
}

.tech-ai-window::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, 
    rgba(0, 212, 255, 0.05), 
    transparent, 
    rgba(78, 205, 196, 0.05)
  );
  border-radius: 16px;
  z-index: -1;
}

/* 折叠状态 */
.tech-ai-window.collapsed {
  width: 50px;
  height: auto;
  cursor: pointer;
  border-radius: 10px 0 0 10px;
}

/* 收起后显示的科技标签 */
.tech-collapsed-tab {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px 8px;
  background: linear-gradient(135deg, var(--primary-color), var(--accent-color));
  border-radius: 10px 0 0 10px;
  color: var(--primary-bg);
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  writing-mode: vertical-rl;
  text-orientation: mixed;
}

.tab-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  animation: tabGlow 3s ease-in-out infinite;
}

@keyframes tabGlow {
  0%, 100% { opacity: 0; }
  50% { opacity: 1; }
}

.tab-pulse {
  position: absolute;
  top: 2px;
  right: 2px;
  width: 8px;
  height: 8px;
  background: var(--success-color);
  border-radius: 50%;
  animation: pulse 2s infinite;
}

.tech-collapsed-tab:hover {
  transform: translateX(-5px);
  box-shadow: 
    0 10px 25px rgba(0, 212, 255, 0.4),
    -5px 0 15px rgba(0, 212, 255, 0.3);
}

.tab-icon {
  font-size: 20px;
  margin-bottom: 8px;
}

.tab-text {
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 1px;
}

.tech-ai-window.dragging {
  transition: none;
  cursor: grabbing;
}

/* 科技风格窗口头部 */
.tech-window-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.1), rgba(26, 26, 46, 0.8));
  border-bottom: 2px solid var(--border-color);
  border-radius: 16px 16px 0 0;
  cursor: move;
  position: relative;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
  position: relative;
}

.header-icon {
  font-size: 24px;
  animation: pulse 2s infinite;
}

.header-title {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  background: linear-gradient(45deg, var(--primary-color), var(--accent-color));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.header-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100px;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--primary-color), transparent);
  opacity: 0.3;
  animation: headerGlow 4s ease-in-out infinite;
}

@keyframes headerGlow {
  0%, 100% { width: 50px; opacity: 0.3; }
  50% { width: 150px; opacity: 0.8; }
}

.header-controls {
  display: flex;
  align-items: center;
  gap: 8px;
}

.tech-control-btn {
  background: rgba(26, 26, 46, 0.6);
  color: var(--text-primary);
  border: 1px solid var(--border-color);
  width: 32px;
  height: 32px;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  font-size: 14px;
  font-weight: 600;
}

.tech-control-btn:hover {
  background: var(--primary-color);
  border-color: var(--primary-color);
  color: var(--primary-bg);
  transform: scale(1.1);
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.5);
}

/* 科技风格窗口内容 */
.tech-window-content {
  height: calc(100% - 68px);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 科技风格图片显示区域 */
.tech-image-display {
  flex: 1;
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  position: relative;
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.02), rgba(26, 26, 46, 0.8));
  border-bottom: 1px solid var(--border-color);
}

.tech-image-container {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  border-radius: 12px;
  border: 1px solid var(--border-color);
  background: rgba(0, 0, 0, 0.2);
}

.tech-image-container img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  border-radius: 12px;
  transition: transform 0.3s ease;
}

.tech-image-controls {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: center;
  gap: 8px;
  padding: 16px;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.8) 0%, transparent 100%);
  opacity: 0;
  transition: opacity 0.3s;
}

.tech-image-container:hover .tech-image-controls {
  opacity: 1;
}

.tech-control-button {
  background: var(--surface-bg);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 8px 12px;
  color: var(--text-primary);
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
  backdrop-filter: blur(10px);
}

.tech-control-button:hover {
  background: var(--primary-color);
  border-color: var(--primary-color);
  color: var(--primary-bg);
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 212, 255, 0.3);
}

.control-icon {
  font-size: 14px;
}

/* 科技风格加载容器 */
.tech-loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--text-primary);
}

.tech-loading-spinner {
  width: 50px;
  height: 50px;
  border: 3px solid rgba(0, 212, 255, 0.3);
  border-top: 3px solid var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
  position: relative;
}

.tech-loading-spinner::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 20px;
  height: 20px;
  background: var(--primary-color);
  border-radius: 50%;
  animation: innerPulse 2s ease-in-out infinite;
}

@keyframes innerPulse {
  0%, 100% { opacity: 0.3; transform: translate(-50%, -50%) scale(0.8); }
  50% { opacity: 1; transform: translate(-50%, -50%) scale(1.2); }
}

.loading-text {
  color: var(--text-primary);
  font-size: 14px;
  margin-bottom: 16px;
}

.loading-progress {
  width: 200px;
  height: 4px;
  background: rgba(0, 212, 255, 0.2);
  border-radius: 2px;
  overflow: hidden;
}

.progress-bar {
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, var(--primary-color), var(--accent-color));
  transform: translateX(-100%);
  animation: progressMove 2s ease-in-out infinite;
}

@keyframes progressMove {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

/* 科技风格错误容器 */
.tech-error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--error-color);
  text-align: center;
  padding: 20px;
  border: 1px solid var(--error-color);
  border-radius: 12px;
  background: rgba(255, 107, 107, 0.05);
}

.error-icon {
  font-size: 48px;
  margin-bottom: 12px;
  animation: errorPulse 2s ease-in-out infinite;
}

@keyframes errorPulse {
  0%, 100% { opacity: 0.7; }
  50% { opacity: 1; }
}

.error-text {
  margin: 0 0 16px 0;
  font-size: 14px;
}

.tech-retry-btn {
  background: linear-gradient(45deg, var(--error-color), var(--secondary-color));
  color: white;
  border: none;
  border-radius: 8px;
  padding: 10px 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 600;
}

.tech-retry-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(255, 107, 107, 0.3);
}

.prohibited-tip {
  margin-top: 12px;
  font-size: 12px;
  background: rgba(255, 107, 107, 0.1);
  padding: 8px 12px;
  border-radius: 6px;
  border: 1px solid rgba(255, 107, 107, 0.3);
  max-width: 90%;
}

/* 科技风格占位符 */
.tech-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--text-secondary);
  text-align: center;
}

.placeholder-icon {
  font-size: 64px;
  margin-bottom: 16px;
  opacity: 0.6;
  animation: float 4s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.placeholder-text {
  font-size: 16px;
  margin-bottom: 20px;
  color: var(--text-primary);
}

.tech-credit-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.credit-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: var(--text-secondary);
}

.credit-icon {
  font-size: 14px;
}

/* 科技风格设置区域 */
.tech-prompt-settings {
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-color);
  background: rgba(26, 26, 46, 0.4);
}

.settings-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.tech-setting-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.setting-label {
  font-size: 14px;
  color: var(--text-primary);
  font-weight: 500;
}

/* Element Plus 选择器科技风格 */
:deep(.tech-select .el-select__wrapper) {
  background: var(--surface-bg);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.tech-select .el-select__wrapper:hover),
:deep(.tech-select .el-select__wrapper.is-focused) {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(0, 212, 255, 0.2);
}

:deep(.tech-select .el-input__inner) {
  color: var(--text-primary);
}

/* 科技风格输入区域 */
.tech-prompt-input {
  padding: 20px;
  background: rgba(26, 26, 46, 0.4);
}

.input-wrapper {
  position: relative;
  margin-bottom: 16px;
}

.tech-textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid var(--border-color);
  border-radius: 12px;
  resize: none;
  font-size: 14px;
  line-height: 1.6;
  background: var(--surface-bg);
  color: var(--text-primary);
  font-family: var(--font-primary);
  transition: all 0.3s ease;
}

.tech-textarea:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(0, 212, 255, 0.2);
}

.tech-textarea:disabled {
  background-color: rgba(26, 26, 46, 0.3);
  cursor: not-allowed;
  opacity: 0.6;
}

.input-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 12px;
  background: linear-gradient(45deg, transparent, rgba(0, 212, 255, 0.1), transparent);
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
}

.tech-textarea:focus + .input-glow {
  opacity: 1;
}

/* 科技风格提示 */
.tech-prompt-tips {
  margin-bottom: 16px;
}

.tips-title {
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.tips-examples {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tech-tip-tag {
  padding: 6px 12px;
  background: linear-gradient(45deg, rgba(0, 212, 255, 0.1), rgba(78, 205, 196, 0.1));
  border: 1px solid var(--border-color);
  color: var(--primary-color);
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 12px;
  font-weight: 500;
}

.tech-tip-tag:hover {
  background: linear-gradient(45deg, var(--primary-color), var(--accent-color));
  color: var(--primary-bg);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 212, 255, 0.3);
}

.more-tips {
  background: linear-gradient(45deg, var(--accent-color), var(--primary-color)) !important;
  color: var(--primary-bg) !important;
}

/* 科技风格高级提示 */
:deep(.tech-advanced-tips) {
  background: var(--surface-bg);
  color: var(--text-primary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 16px;
}

:deep(.tech-advanced-tips h4) {
  margin-top: 0;
  margin-bottom: 12px;
  color: var(--primary-color);
}

:deep(.tech-advanced-tips ul) {
  padding-left: 16px;
  margin: 0;
}

:deep(.tech-advanced-tips li) {
  margin-bottom: 6px;
  color: var(--text-secondary);
}

/* 科技风格输入操作 */
.tech-input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tech-char-count {
  color: var(--text-secondary);
  font-size: 12px;
  font-family: var(--font-mono);
}

.tech-char-count.limit-reached {
  color: var(--error-color);
  animation: pulse 1s infinite;
}

.tech-generate-btn {
  background: linear-gradient(45deg, var(--primary-color), var(--accent-color));
  color: var(--primary-bg);
  border: none;
  border-radius: 12px;
  padding: 12px 20px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  position: relative;
  overflow: hidden;
}

.tech-generate-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 212, 255, 0.4);
}

.tech-generate-btn:disabled {
  background: rgba(0, 212, 255, 0.3);
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.btn-icon {
  font-size: 16px;
}

.btn-glow {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.6s;
}

.tech-generate-btn:hover .btn-glow {
  left: 100%;
}

.btn-loading {
  display: inline-block;
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: #fff;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 最小化状态 */
.tech-ai-window.minimized {
  height: 68px;
}

.tech-ai-window.minimized .tech-window-content {
  display: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .tech-ai-window {
    width: 360px;
  }
  
  :deep(.tech-select) {
    width: 120px;
  }
}

@media (max-width: 480px) {
  .tech-ai-window {
    width: 100%;
    height: 100%;
    border-radius: 0;
    top: 0 !important;
    left: 0 !important;
    transform: none !important;
  }
  
  .tech-ai-window.collapsed {
    width: 50px;
    height: auto;
    top: 20% !important;
    left: auto !important;
    right: 0 !important;
    transform: none !important;
    border-radius: 10px 0 0 10px;
  }
  
  .tech-prompt-input,
  .tech-prompt-settings {
    padding: 16px;
  }
}
</style>
