<template>
  <div
    class="chat-window"
    :class="{ 'minimized': isMinimized }"
    :style="{ left: position.x + 'px', top: position.y + 'px' }"
    @mousedown="startDrag"
  >
    <div class="chat-header">
      <div class="header-left">
        <h3>聊天窗口</h3>
        <select v-model="selectedModel" class="model-select" @click.stop>
          <option v-for="model in availableModels" :key="model.model" :value="model.model">
            {{ model.name }} ({{ model.details.parameter_size }})
          </option>
        </select>
      </div>
      <button class="minimize-btn" @click.stop="toggleMinimize">
        {{ isMinimized ? '□' : '－' }}
      </button>
    </div>
    <div class="chat-content" @mousedown.stop>
      <div class="chat-messages" ref="messagesContainer" @mousedown.stop>
        <div v-for="(message, index) in messages" :key="index"
             :class="['message', message.role]">
          <div class="avatar">
            {{ message.role === 'user' ? '👤' : '🤖' }}
          </div>
          <div class="message-content">
            <pre v-if="isCode(message.content)"><code>{{ message.content }}</code></pre>
            <span v-else v-html="md.render(message.content)"></span>
          </div>
        </div>
      </div>
      <div class="chat-input">
        <textarea
          v-model="inputMessage"
          @keydown.enter.prevent="sendMessage"
          placeholder="输入消息..."
          rows="3"
        ></textarea>
        <button @click="sendMessage" :disabled="!inputMessage.trim()">
          发送
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { useScroll } from '@vueuse/core'
import { getModelsList,chat} from '@/api/ai'
import MarkdownIt from 'markdown-it';
const md = new MarkdownIt();

interface Message {
  role: 'user' | 'assistant'
  content: string
}

interface ModelDetails {
  parent_model: string;
  format: string;
  family: string;
  families: string[];
  parameter_size: string;
  quantization_level: string;
}

interface Model {
  name: string;
  model: string;
  modified_at: string;
  size: number;
  digest: string;
  details: ModelDetails;
}

const messages = ref<Message[]>([
  {
    role: 'assistant',
    content: '你好！我是AI助手，很高兴为你服务。'
  }
])

const inputMessage = ref('')
const messagesContainer = ref<HTMLElement | null>(null)
const availableModels = ref<Model[]>([])
const selectedModel = ref('')

const { y } = useScroll(messagesContainer)

const position = ref({ x: 1100, y: 200 }) // 初始位置
let isDragging = false
let startPos = { x: 0, y: 0 }
let startMousePos = { x: 0, y: 0 }

const isMinimized = ref(false)

const startDrag = (e: MouseEvent) => {
  isDragging = true
  console.log('mousedown event triggered, isDragging set to true');
  startPos = { x: position.value.x, y: position.value.y }
  startMousePos = { x: e.clientX, y: e.clientY }

  // 添加全局事件监听器
  document.addEventListener('mousemove', onDrag)
  document.addEventListener('mouseup', stopDrag)
}

const onDrag = (e: MouseEvent) => {
  console.log('mousemove event triggered');
  if (!isDragging) return

  const dx = e.clientX - startMousePos.x
  const dy = e.clientY - startMousePos.y

  position.value = {
    x: startPos.x + dx,
    y: startPos.y + dy
  }
}

const stopDrag = () => {
  console.log('mouseup event triggered, isDragging set to false');
  isDragging = false
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
}

const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

const sendMessage = async () => {
  if (!inputMessage.value.trim()) return

  // 添加用户消息
  messages.value.push({
    role: 'user',
    content: inputMessage.value
  });

  // 清空输入框
  const userMessage = inputMessage.value;
  inputMessage.value = '';

  // 调用chat方法
  chat(selectedModel.value, userMessage).then((res) => {
    messages.value.push({
      role: res.data.message.role,
      content: res.data.message.content
    });
    scrollToBottom();
  }).catch((error) => {
    alert('网络异常，请稍后重试');
    inputMessage.value = userMessage;
  });

  // 模拟AI响应
  // setTimeout(() => {
  //   const currentModel = availableModels.value.find(m => m.model === selectedModel.value);
  //   messages.value.push({
  //     role: 'assistant',
  //     content: `[${currentModel?.name || 'AI'}] 我收到了你的消息：${userMessage}`
  //   });
  //   scrollToBottom();
  // }, 1000);

  await scrollToBottom()
}

const toggleMinimize = (e: MouseEvent) => {
  e.stopPropagation()
  isMinimized.value = !isMinimized.value
}

onMounted(() => {
  // 获取模型列表
  getModelsList().then((res) => {
    availableModels.value = res.data.models;
    if (availableModels.value.length > 0) {
      selectedModel.value = availableModels.value[0].model;
    }
  });
  scrollToBottom()
})
// 在 <script setup lang="ts"> 中添加判断是否为代码的方法
const isCode = (content: string) => {
  // 简单示例：如果内容以 ` 开头和结尾，认为是代码
  return content.startsWith('```') && content.endsWith('```');
}
</script>

<style scoped>
.chat-window {
  position: fixed;
  width: 300px;
  height: 400px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: height 0.3s ease;
}

.chat-window.minimized {
  height: 44px;
  overflow: hidden;
}

.chat-header {
  background: #4a90e2;
  color: white;
  padding: 12px;
  cursor: move;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.minimize-btn {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  padding: 0 8px;
  font-size: 16px;
  line-height: 1;
  height: 20px;
  width: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
}

.minimize-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

.chat-header h3 {
  margin: 0;
  font-size: 16px;
}

.chat-content {
  padding: 16px;
  height: calc(100% - 44px);
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f5f5f5;
  border-radius: 10px;
  margin-bottom: 20px;
  width: 100%;
  box-sizing: border-box;
}

.message {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20px;
}

.message.user {
  flex-direction: row-reverse;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  margin: 0 10px;
  font-size: 20px;
}

.message-content {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 10px;
  background: #fff;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  word-wrap: break-word;
  word-break: break-word;
  white-space: pre-wrap;
}

.message.assistant .message-content {
  background: #e3f2fd;
}

.message.user .message-content {
  background: #dcf8c6;
}

.chat-input {
  display: flex;
  gap: 10px;
}

textarea {
  flex: 1;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  resize: none;
  font-size: 14px;
}

button {
  padding: 0 20px;
  background: #1976d2;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.3s;
}

button:hover {
  background: #1565c0;
}

button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.model-select {
  background: rgba(255, 255, 255, 0.1);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 4px;
  padding: 4px 8px;
  font-size: 12px;
  cursor: pointer;
  outline: none;
  transition: background 0.3s;
  min-width: 150px; /* 确保下拉框有足够宽度显示模型信息 */
}

.model-select:hover {
  background: rgba(255, 255, 255, 0.2);
}

.model-select option {
  background: #4a90e2;
  color: white;
}
</style>
