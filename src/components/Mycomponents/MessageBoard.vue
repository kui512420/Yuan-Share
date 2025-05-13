<script lang="ts" setup>
  import {add,get} from '@/api/notes'
  import { ref, onBeforeUnmount, onMounted, nextTick } from 'vue'
  import {convertDate} from '@/utils/DateUntil'
  import { ElMessage, ElImageViewer } from 'element-plus';
  import { uploadImg } from '@/api/home'
  import { Plus, Message } from '@element-plus/icons-vue'
  import type { UploadProps } from 'element-plus'
  import { useRouter } from 'vue-router'

  // 定义消息项的接口
  interface MessageItem {
    infoid?: number;
    headsrc: string;
    nickname: string;
    time: string;
    userid?: number;
    info: string;
  }

  const router = useRouter()
  const txt = ref('')
  const timer = ref();
  const imageUrls = ref<string[]>([]) // 存储图片URL的数组
  const chatContainer = ref(null)
  const isUploading = ref(false)
  const msfObj = ref<MessageItem[]>([]) // 使用定义的接口类型
  
  // 图片预览相关变量
  const previewVisible = ref(false)
  const previewImages = ref<string[]>([])
  const initialIndex = ref(0)
  
  // 图片预览函数
  const showPreview = (url: string, urlList: string[]) => {
    previewImages.value = urlList
    initialIndex.value = urlList.indexOf(url)
    previewVisible.value = true
  }
  
  // 模拟数据，当API请求失败时使用
  const mockData: MessageItem[] = [
    {
      nickname: "用户",
      info: "你好",
      time: new Date().toString(),
      headsrc: ""
    }
  ]
  
  // 检查登录状态
  const checkLogin = () => {
    const token = window.localStorage.getItem('token')
    if (!token) {
      ElMessage.warning('请先登录后再操作')
      router.push('/management')
      return false
    }
    return true
  }
  
  // 发送消息
  const addmsg = () => {
    if (!checkLogin()) return
    
    if (!txt.value.trim() && imageUrls.value.length === 0) return
    
    // 如果有图片，将多张图片URL添加到消息中
    let message = txt.value
    
    if (imageUrls.value.length > 0) {
      imageUrls.value.forEach(url => {
        // 直接使用api/goodsCom/格式，不添加额外的斜杠
        message += `\n[图片:${url}]`
      })
    }
    
    add(message).then((res) => {
      if(res.data.code !== 500 && res.data.code !== 205){
        ElMessage.success("发布成功")
        clearAllImages() // 清空所有图片
        txt.value = ''
        // 获取最新消息
        getmsg()
      } else if (res.data.code === 205) {
        ElMessage.warning("请先登录后再操作")
        router.push('/management')
      }
    }).catch(error => {
      console.error('发送留言失败:', error)
      ElMessage.error('发送留言失败')
      checkLogin()
    })
  }
  
  // 获取消息
  const getmsg = () => {
    get().then((res) => {
      if (res.data && res.data.data) {
        // 确保响应数据是数组，然后反转
        const messageData = Array.isArray(res.data.data) ? res.data.data : [];
        msfObj.value = [...messageData].reverse() as MessageItem[];
        
        // 滚动到底部
        nextTick(() => {
          scrollToBottom()
        })
      }
    }).catch(error => {
      console.error('获取留言失败:', error)
      // 如果获取数据失败，使用模拟数据
      if (msfObj.value.length === 0) {
        msfObj.value = mockData
      }
    })
  }
  
  // 滚动到底部
  const scrollToBottom = () => {
    if (chatContainer.value) {
      const container = chatContainer.value as HTMLElement
      container.scrollTop = container.scrollHeight
    }
  }
  
  // 上传前的校验钩子
  const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
    if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png' && rawFile.type !== 'image/gif' && rawFile.type !== 'image/jpg') {
      ElMessage.error('只能上传【image/jpeg,image/png,image/gif,image/jpg】格式的图片')
      return false
    } else if (rawFile.size / 1024 / 1024 > 5) {
      ElMessage.error('最大 5MB!')
      return false
    }
    return true
  }
  
  // 上传图片
  const uploadIm = (params: { file: File; data: { type: number } }) => {
    if (!checkLogin()) return
    
    const file = params.file;
    const formData = new FormData();
    formData.append('type', params.data.type + "");
    formData.append('fileInput', file);
    
    // 设置上传中状态
    isUploading.value = true;
    
    uploadImg(formData).then((res) => {
      console.log('上传成功，响应数据:', res.data);
      isUploading.value = false;
      
      if (res.data && res.data.code === 200 && res.data.msg) {
        // 添加到图片数组
        imageUrls.value.push(res.data.msg);
        ElMessage.success("上传图片成功");
      } else if (res.data.code === 205) {
        ElMessage.warning("请先登录后再操作")
        router.push('/management')
      } else {
        ElMessage.warning("上传失败：" + res.data.msg);
      }
    }).catch(error => {
      console.error('上传图片失败:', error);
      ElMessage.error('上传图片失败，请重试');
      isUploading.value = false;
      checkLogin()
    });
  }
  
  // 删除单张图片
  const removeImage = (index: number) => {
    if (index < 0 || index >= imageUrls.value.length) return;
    imageUrls.value.splice(index, 1);
  }
  
  // 清除所有图片
  const clearAllImages = () => {
    imageUrls.value = [];
  }
  
  // 处理消息内容，检查是否包含图片
  const processMessageContent = (content: string | null | undefined) => {
    if (!content) return { text: '', imgUrls: [] };
    
    try {
      // 使用正则表达式匹配[图片:URL]格式
      const imgRegex = /\[图片:(.*?)\]/g;
      const matches = Array.from(content.matchAll(imgRegex));
      
      if (matches.length > 0) {
        // 提取所有图片URL
        const imgUrls = matches.map(match => match[1].trim());
        // 移除所有图片标记，保留纯文本
        const textContent = content.replace(imgRegex, '').trim();
        
        return { text: textContent, imgUrls };
      }
      
      return { text: content, imgUrls: [] };
    } catch (error) {
      console.error('处理消息内容出错:', error, content);
      return { text: content, imgUrls: [] };
    }
  }
  
  // 处理按键事件
  const handleKeydown = (event: KeyboardEvent) => {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault()
      addmsg()
    }
  }
  
  // 组件挂载和销毁
  getmsg()
  onMounted(()=>{
    timer.value = setInterval(()=>{
      getmsg()
    }, 5000) // 增加轮询间隔，减少服务器压力
    
    // 初始加载完成后滚动到底部
    nextTick(() => {
      scrollToBottom()
    })
  })

  onBeforeUnmount(()=>{
    clearInterval(timer.value);
    timer.value = null
  })
</script>

<template>
  <div class="chat-container">
    <!-- 聊天记录区域 -->
    <div class="chat-messages" ref="chatContainer">
      <div 
        v-for="(item, index) in msfObj" 
        :key="index" 
        class="message-wrapper"
      >
        <div class="message-time">{{ item.time ? convertDate(item.time) : '' }}</div>
        <div class="message-item" :class="{'my-message': item.nickname === '我'}">
          <div class="avatar">
            <img :src="item.headsrc || ''" alt="头像" class="avatar-image">
          </div>
          <div class="message-bubble">
            <div class="message-sender">{{ item.nickname || '匿名用户' }}</div>
            <div class="message-content">
              <p v-if="processMessageContent(item.info).text">{{ processMessageContent(item.info).text }}</p>
              
              <!-- 消息中的图片支持点击预览 -->
              <div class="message-images" v-if="processMessageContent(item.info).imgUrls.length > 0">
                <div 
                  v-for="(imgUrl, imgIndex) in processMessageContent(item.info).imgUrls" 
                  :key="imgIndex" 
                  class="message-image"
                >
                  <img 
                    :src="imgUrl" 
                    alt="留言图片" 
                    class="chat-img"
                    @click="showPreview(imgUrl, processMessageContent(item.info).imgUrls)"
                  >
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- 添加一个占位元素，确保滚动到底部时能看到最新消息 -->
      <div class="scroll-anchor"></div>
    </div>
    
    <!-- 输入区域 -->
    <div class="chat-input-area">
      <!-- 图片预览区 -->
      <div class="preview-area" v-if="imageUrls.length > 0">
        <div class="image-list">
          <div v-for="(url, index) in imageUrls" :key="index" class="image-item">
            <img 
              :src="`${url}`" 
              alt="图片预览" 
              class="preview-img"
              @click="showPreview(`${url}`, imageUrls.map(u => `${u}`))"
            >
            <span class="remove-image" @click="removeImage(index)">×</span>
          </div>
        </div>
      </div>
      
      <div class="input-controls">
        <el-upload 
          class="image-uploader" 
          :show-file-list="false" 
          :http-request="uploadIm"
          :data="{ type: 4 }" 
          :multiple="true"
          name="fileInput" 
          :before-upload="beforeAvatarUpload">
          <el-button 
            class="upload-btn" 
            type="primary" 
            circle 
            :loading="isUploading">
            <el-icon><Plus /></el-icon>
          </el-button>
        </el-upload>
        
        <div class="text-input-wrapper">
          <textarea 
            v-model="txt" 
            placeholder="请输入留言内容..." 
            @keydown="handleKeydown"
            class="chat-textarea"
          ></textarea>
        </div>
        
        <el-button 
          class="send-btn" 
          type="primary" 
          circle 
          @click="addmsg"
          :disabled="(!txt.trim() && imageUrls.length === 0) || isUploading">
          <el-icon><Message /></el-icon>
        </el-button>
      </div>
    </div>
    
    <!-- 图片预览组件 -->
    <el-image-viewer
      v-if="previewVisible"
      :url-list="previewImages"
      :initial-index="initialIndex"
      :z-index="2000"
      hide-on-click-modal
      @close="previewVisible = false"
    />
  </div>
</template>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  max-height: calc(100vh - 120px);
  background-color: #f5f5f5;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  scroll-behavior: smooth;
}

.scroll-anchor {
  height: 1px;
  margin-top: 10px;
}

.message-wrapper {
  margin-bottom: 16px;
}

.message-time {
  text-align: center;
  margin: 8px 0;
  font-size: 12px;
  color: #999;
}

.message-item {
  display: flex;
  margin-bottom: 8px;
  align-items: flex-start;
}

.my-message {
  flex-direction: row-reverse;
}

.avatar {
  margin: 0 8px;
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  background-color: #eee;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%; /* 确保头像是圆形的 */
}

.message-bubble {
  max-width: 70%;
  border-radius: 18px;
  padding: 10px 16px;
  background-color: white;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  position: relative;
}

.my-message .message-bubble {
  background-color: #95ec69;
}

.message-sender {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.my-message .message-sender {
  text-align: right;
}

.message-content {
  word-break: break-word;
}

.message-content p {
  margin: 0;
  line-height: 1.5;
}

.message-images {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}

.message-image {
  position: relative;
  cursor: pointer;
  margin: 5px;
  overflow: hidden;
}

.chat-img {
  max-width: 200px;
  max-height: 200px;
  border-radius: 8px;
  transition: transform 0.3s;
}

.chat-img:hover {
  transform: scale(1.02);
}

.chat-input-area {
  background-color: white;
  padding: 12px;
  border-top: 1px solid #eaeaea;
}

.preview-area {
  padding: 8px;
  background-color: #f9f9f9;
  border-radius: 8px;
  margin-bottom: 10px;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.image-item {
  position: relative;
  width: 80px;
  height: 80px;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  padding: 3px;
  background-color: white;
}

.preview-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 6px;
  cursor: pointer;
}

.remove-image {
  position: absolute;
  top: -10px;
  right: -10px;
  width: 22px;
  height: 22px;
  background-color: #f56c6c;
  color: white;
  border-radius: 50%;
  text-align: center;
  line-height: 20px;
  cursor: pointer;
  font-size: 14px;
  font-weight: bold;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
  border: 1px solid white;
}

.input-controls {
  display: flex;
  align-items: center;
}

.text-input-wrapper {
  flex: 1;
  margin: 0 10px;
}

.chat-textarea {
  width: 100%;
  min-height: 40px;
  max-height: 120px;
  padding: 10px 16px;
  border: 1px solid #dcdfe6;
  border-radius: 20px;
  resize: none;
  font-size: 14px;
  line-height: 1.5;
  transition: border-color 0.2s;
  outline: none;
}

.chat-textarea:focus {
  border-color: #409eff;
}

.upload-btn,
.send-btn {
  flex-shrink: 0;
}

/* 滚动条样式 */
.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background-color: #c1c1c1;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-track {
  background-color: transparent;
}

.image-uploader {
  margin-right: 5px;
}
</style>
