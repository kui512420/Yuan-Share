<template>
  <div
    class="image-gen-window"
    :class="{ 'minimized': isMinimized, 'dragging': isDragging, 'collapsed': isCollapsed }"
    :style="{ transform: `translate3d(${position.x}px, ${position.y}px, 0)` }"
    @mousedown="startDrag"
  >
    <!-- 收起后显示的标签，点击展开 -->
    <div class="collapsed-tab" v-if="isCollapsed" @click.stop="toggleCollapse">
      <span class="tab-icon">🖼️</span>
      <span class="tab-text">AI 图像生成</span>
    </div>
    
    <div class="window-header" v-if="!isCollapsed">
      <div class="header-left">
        <h3>AI 图像生成</h3>
      </div>
      <div class="header-controls">
        <button class="minimize-btn" @click.stop="toggleMinimize">
          {{ isMinimized ? '□' : '－' }}
        </button>
        <button class="collapse-btn" @click.stop="toggleCollapse">
          →
        </button>
      </div>
    </div>
    <div class="window-content" @mousedown.stop v-if="!isCollapsed">
      <div class="image-display" ref="imageDisplay" @mousedown.stop>
        <div v-if="generatedImage" class="image-container">
          <img :src="generatedImage" alt="生成的图像" />
          <div class="image-controls">
            <button class="control-btn" @click="downloadImage">
              <span>下载</span>
            </button>
            <button class="control-btn" @click="addToChatWindow" v-if="!isGenerating">
              <span>添加到聊天</span>
            </button>
            <button class="control-btn" @click="regenerateImage" v-if="!isGenerating">
              <span>重新生成</span>
            </button>
          </div>
        </div>
        <div v-else-if="isGenerating" class="loading-container">
          <div class="loading-spinner"></div>
          <p>AI 正在绘制图像中...</p>
        </div>
        <div v-else-if="generateError" class="error-container" :class="{'prohibited-error': generateError.includes('违禁')}">
          <div class="error-icon">{{ generateError.includes('违禁') ? '🚫' : '⚠️' }}</div>
          <p>{{ generateError }}</p>
          <button class="retry-btn" @click="regenerateImage">
            重试
          </button>
          <p v-if="generateError.includes('违禁')" class="prohibited-tip">
            请修改提示词，避免包含色情、暴力、政治敏感等内容
          </p>
        </div>
        <div v-else class="placeholder">
          <div class="placeholder-icon">🖼️</div>
          <p>输入提示词生成图像</p>
          <div class="credit-info">
            <p>生成每张图像需要消耗AI积分</p>
            <p>每天签到可获取1~5积分</p>
          </div>
        </div>
      </div>
      <div class="prompt-settings">
        <div class="settings-row">
          <div class="setting-item">
            <label>尺寸</label>
            <el-select v-model="imageSize" class="setting-select" :disabled="isGenerating" placeholder="请选择">
              <el-option label="小图 (512x512)" value="512*512"></el-option>
              <el-option label="中图 (768x768)" value="768*768"></el-option>
              <el-option label="大图 (1024x1024)" value="1024*1024"></el-option>
              <el-option label="正方形 (1080x1080)" value="1080*1080"></el-option>
            </el-select>
          </div>

        </div>
      </div>
      <div class="prompt-input">
        <textarea
          v-model="promptText"
          @keydown.ctrl.enter="generateImage"
          placeholder="描述你想要生成的图像..."
          rows="3"
          :disabled="isGenerating"
        ></textarea>
        <div class="prompt-tips">
          <p>提示: 尝试添加更多细节可以获得更好的图片效果</p>
          <div class="tips-examples">
            <span @click="fillPromptExample('风景')">风景</span>
            <span @click="fillPromptExample('人物')">人物</span>
            <span @click="fillPromptExample('动漫')">动漫</span>
            <span @click="fillPromptExample('写实')">写实</span>
            <el-popover
              placement="top"
              title="高级提示技巧"
              :width="300"
              trigger="click"
            >
              <template #reference>
                <span class="more-tips">更多技巧</span>
              </template>
              <div class="advanced-tips">
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
        <div class="input-actions">
          <span class="char-count" :class="{ 'limit-reached': promptText.length > 200 }">
            {{ promptText.length }}/200
          </span>
          <button 
            @click="generateImage" 
            :disabled="!promptText.trim() || isGenerating"
            class="generate-btn"
          >
            <span v-if="isGenerating" class="btn-loading"></span>
            {{ isGenerating ? '生成中...' : '生成图像' }}
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
.image-gen-window {
  position: fixed;
  width: 400px;
  height: 600px;
  background: #fff;
  border-radius: 12px;
  z-index: 1000;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  will-change: transform;
  transform: translate3d(0, 0, 0);
  transition: transform 0.05s ease, height 0.3s ease, width 0.3s ease;
}

/* 折叠状态 */
.image-gen-window.collapsed {
  width: 40px;
  height: auto;
  background: transparent;
  box-shadow: none;
}

/* 折叠后显示的标签 */
.collapsed-tab {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #409EFF;
  color: white;
  padding: 15px 10px;
  border-radius: 10px 0 0 10px;
  cursor: pointer;
  box-shadow: -2px 0 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.collapsed-tab:hover {
  background-color: #66b1ff;
  transform: translateX(-5px);
}

.tab-icon {
  font-size: 20px;
  margin-bottom: 5px;
}

.tab-text {
  writing-mode: vertical-lr;
  text-orientation: upright;
  letter-spacing: 2px;
  font-size: 14px;
  font-weight: bold;
}

.image-gen-window.dragging {
  transition: none;
  cursor: grabbing;
}

.window-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #409EFF;
  color: #fff;
  border-radius: 12px 12px 0 0;
  cursor: move;
}

.header-left h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.header-controls {
  display: flex;
  align-items: center;
}

.minimize-btn, .collapse-btn {
  background: transparent;
  color: white;
  border: none;
  width: 28px;
  height: 28px;
  border-radius: 4px;
  margin-left: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.2s;
}

.minimize-btn:hover, .collapse-btn:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.window-content {
  height: calc(100% - 52px);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.image-display {
  flex: 1;
  padding: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  position: relative;
  background-color: #f5f7fa;
}

.image-container {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.image-container img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.image-controls {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: center;
  padding: 12px;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.7) 0%, rgba(0, 0, 0, 0) 100%);
  opacity: 0;
  transition: opacity 0.2s;
}

.image-container:hover .image-controls {
  opacity: 1;
}

.control-btn {
  background: white;
  border: none;
  border-radius: 20px;
  padding: 6px 12px;
  margin: 0 4px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
}

.control-btn:hover {
  background: #ecf5ff;
  color: #409EFF;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #409EFF;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 12px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
}

.placeholder-icon {
  font-size: 48px;
  margin-bottom: 12px;
  opacity: 0.5;
}

.prompt-settings {
  padding: 12px 16px;
  border-top: 1px solid #ebeef5;
  background-color: #f8f9fb;
}

.settings-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.setting-item {
  display: flex;
  align-items: center;
}

.setting-item label {
  margin-right: 8px;
  font-size: 14px;
  color: #606266;
}

.setting-select {
  width: 150px;
}

.prompt-input {
  padding: 16px;
  border-top: 1px solid #ebeef5;
}

.prompt-input textarea {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  resize: none;
  font-size: 14px;
  line-height: 1.5;
  transition: border-color 0.2s;
}

.prompt-input textarea:focus {
  outline: none;
  border-color: #409EFF;
}

.prompt-input textarea:disabled {
  background-color: #f5f7fa;
  cursor: not-allowed;
}

.prompt-tips {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}

.tips-examples {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}

.tips-examples span {
  padding: 2px 8px;
  background-color: #f0f9eb;
  color: #67c23a;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.tips-examples span:hover {
  background-color: #e1f3d8;
}

.more-tips {
  color: #409EFF !important;
  background-color: #ecf5ff !important;
}

.more-tips:hover {
  background-color: #d9ecff !important;
}

.advanced-tips h4 {
  margin-top: 0;
  margin-bottom: 8px;
}

.advanced-tips ul {
  padding-left: 16px;
  margin: 0;
}

.advanced-tips li {
  margin-bottom: 4px;
}

.input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}

.char-count {
  color: #909399;
  font-size: 12px;
}

.char-count.limit-reached {
  color: #f56c6c;
}

.generate-btn {
  background-color: #409EFF;
  color: white;
  border: none;
  border-radius: 20px;
  padding: 8px 16px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
}

.generate-btn:hover:not(:disabled) {
  background-color: #66b1ff;
}

.generate-btn:disabled {
  background-color: #a0cfff;
  cursor: not-allowed;
}

.btn-loading {
  display: inline-block;
  width: 12px;
  height: 12px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: #fff;
  animation: spin 1s linear infinite;
  margin-right: 6px;
}

.image-gen-window.minimized {
  height: 52px;
}

.image-gen-window.minimized .window-content {
  display: none;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .image-gen-window {
    width: 320px;
  }
  
  .setting-select {
    width: 120px;
  }
}

@media (max-width: 480px) {
  .image-gen-window {
    width: 100%;
    height: 100%;
    border-radius: 0;
    top: 0 !important;
    left: 0 !important;
    transform: none !important;
  }
  
  .image-gen-window.collapsed {
    width: 40px;
    height: auto;
    top: 20% !important;
    left: auto !important;
    right: 0 !important;
    transform: none !important;
    border-radius: 10px 0 0 10px;
  }
}

.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #f56c6c;
  text-align: center;
  padding: 20px;
}

.prohibited-error {
  background-color: #fef0f0;
  border: 1px solid #fbc4c4;
  border-radius: 8px;
}

.prohibited-tip {
  margin-top: 12px;
  font-size: 12px;
  color: #f56c6c;
  background-color: #fef0f0;
  padding: 8px 12px;
  border-radius: 4px;
  max-width: 90%;
}

.error-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.retry-btn {
  margin-top: 15px;
  background: #f56c6c;
  color: white;
  border: none;
  border-radius: 20px;
  padding: 8px 16px;
  cursor: pointer;
  transition: all 0.2s;
}

.retry-btn:hover {
  background: #f78989;
}

.credit-info {
  margin-top: 15px;
  font-size: 12px;
  color: #909399;
  text-align: center;
}
</style>
