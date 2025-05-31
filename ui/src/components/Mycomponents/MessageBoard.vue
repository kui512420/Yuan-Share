<script lang="ts" setup>
  import {add,get} from '@/api/notes'
  import { ref, onBeforeUnmount, onMounted, nextTick } from 'vue'
  import {convertDate} from '@/utils/DateUntil'
  import { ElMessage, ElImageViewer, ElDrawer, ElDropdown, ElDropdownMenu, ElDropdownItem } from 'element-plus';
  import { uploadImg, getFavoriteImages, addImageToFavorite, deleteFavoriteImage } from '@/api/home'
  import { Plus, Message, Collection, Refresh } from '@element-plus/icons-vue'
  import type { UploadProps } from 'element-plus'
  import { useRouter } from 'vue-router'
  import eventBus from '@/utils/EventBus'

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
  const currentPage = ref(1)
  const isLoadingMore = ref(false)
  const noMoreMessages = ref(false)
  
  // AI图片提示信息显示状态
  const showAiImageNotice = ref(true)
  
  // 图片预览相关变量
  const previewVisible = ref(false)
  const previewImages = ref<string[]>([])
  const initialIndex = ref(0)
  
  // 收藏图片相关变量
  const favoriteDrawerVisible = ref(false)
  const favoriteImages = ref<Array<{id: number, fileUrl: string}>>([])
  const loadingFavorites = ref(false)
  
  // 右键菜单相关变量
  const showContextMenu = ref(false)
  const contextMenuPosition = ref({ x: 0, y: 0 })
  const selectedImageUrl = ref('')
  
  // 是否应该滚动到底部的标志
  const shouldScrollToBottom = ref(true);
  
  // 是否禁用滚动
  const disableScroll = ref(false);
  
  // 用户上次滚动时间
  const lastUserScrollTime = ref(Date.now());
  
  // 用户是否正在查看历史消息（不在底部）
  const userViewingHistory = ref(false);
  
  // 是否正在初始化加载
  const isInitialLoading = ref(true);
  
  // 关闭AI图片提示信息
  const closeAiImageNotice = () => {
    showAiImageNotice.value = false
    // 可以选择将这个状态保存到localStorage中，这样用户下次访问时就不会再显示
    localStorage.setItem('hideAiImageNotice', 'true')
  }
  
  // 添加图片到收藏
  const addToFavorite = (imageUrl: string) => {
    if (!checkLogin()) return
    
    // 检查图片URL是否包含dashscope-result，这类图片是临时的AI生成图片
    if (imageUrl.includes('dashscope-result')) {
      ElMessage({
        message: '这是AI临时生成的图片，无法直接收藏。请先保存到本地，然后上传到服务器，发送到聊天页面后再收藏。',
        type: 'warning',
        duration: 5000,
        showClose: true,
      })
      return
    }
    
    console.log('收藏的图片地址:', imageUrl)
    // 直接使用API调用，现在API已经适配为POST方法
    addImageToFavorite(imageUrl).then((res) => {
      if (res.data) {
        ElMessage.success('添加到收藏成功')

        // 也可以使用ElMessage显示图片地址
        ElMessage({
          message: `收藏成功: ${imageUrl}`,
          type: 'success',
          duration: 3000,
          showClose: true,
        })
      } else if (res.data && res.data.code === 205) {
        ElMessage.warning("请先登录后再操作")
        router.push('/management')
      } else {
        ElMessage.warning("添加到收藏失败：" + (res.data ? res.data.msg : '未知错误'))
      }
    }).catch(error => {
      console.error('添加到收藏失败:', error)
      ElMessage.error('添加到收藏失败，请重试')
      checkLogin()
    })
  }
  
  // 处理右键点击图片事件
  const handleContextMenu = (event: MouseEvent, imageUrl: string) => {
    event.preventDefault()
    
    // 检查是否是AI临时生成的图片，如果是则显示特定的菜单选项
    const isAITempImage = imageUrl.includes('dashscope-result')
    
    showContextMenu.value = true
    selectedImageUrl.value = imageUrl
    contextMenuPosition.value = {
      x: event.clientX,
      y: event.clientY
    }
    
    // 添加全局点击监听，点击其他地方关闭菜单
    document.addEventListener('click', closeContextMenu)
  }
  
  // 关闭右键菜单
  const closeContextMenu = () => {
    showContextMenu.value = false
    document.removeEventListener('click', closeContextMenu)
  }
  
  // 获取收藏图片
  const getFavorites = () => {
    if (!checkLogin()) return
    
    loadingFavorites.value = true
    getFavoriteImages().then((res) => {
      console.log('获取收藏图片响应:', res); // 调试日志
      
      if (res.data) {
        // 检查返回的数据结构
        console.log('收藏图片数据:', res.data);
        
        try {
          if (Array.isArray(res.data)) {
            // 如果直接返回数组，包含完整对象（包含id和fileUrl）
            favoriteImages.value = res.data.filter((item: any) => {
              return item && item.fileUrl;
            });
          } else if (res.data.data && Array.isArray(res.data.data)) {
            // 如果是嵌套在data字段中的数组
            favoriteImages.value = res.data.data.filter((item: any) => {
              return item && item.fileUrl;
            });
          } else {
            favoriteImages.value = [];
            console.warn('收藏图片数据格式异常:', res.data);
          }
          
          if (favoriteImages.value.length === 0) {
            ElMessage.info('您还没有收藏任何图片')
          } else {
            console.log('处理后的收藏图片列表:', favoriteImages.value);
          }
        } catch (err) {
          console.error('处理收藏图片数据出错:', err);
          ElMessage.error('处理收藏图片数据出错');
          favoriteImages.value = [];
        }
      } else {
        favoriteImages.value = [];
        ElMessage.warning("获取收藏图片失败：没有返回数据");
      }
      
      loadingFavorites.value = false
    }).catch(error => {
      console.error('获取收藏图片失败:', error)
      ElMessage.error('获取收藏图片失败，请重试')
      loadingFavorites.value = false
      favoriteImages.value = []
      checkLogin()
    })
  }
  
  // 打开收藏图片抽屉
  const openFavoriteDrawer = () => {
    favoriteDrawerVisible.value = true
    getFavorites()
  }
  
  // 添加收藏图片到聊天框
  const addFavoriteImageToChat = (imageUrl: string) => {
    if (imageUrl) {
      // 检查是否是AI临时生成的图片
      if (imageUrl.includes('dashscope-result')) {
        ElMessage({
          message: '这是AI临时生成的图片，无法直接使用。请先保存到本地，然后上传到服务器。',
          type: 'warning',
          duration: 5000,
          showClose: true,
        })
        return
      }
      
      // 不对图片URL做任何修改，使用原始URL
      imageUrls.value.push(imageUrl)
      ElMessage.success('已添加收藏图片到聊天框')
      favoriteDrawerVisible.value = false
    }
  }
  
  // 删除收藏图片
  const deleteFavorite = (id: number, index: number) => {
    ElMessage.info({
      message: '正在删除收藏...',
      duration: 1000
    })
    
    deleteFavoriteImage(id).then(res => {
      if (res.data === true) {
        // 删除成功，从本地列表中移除
        favoriteImages.value.splice(index, 1)
        ElMessage.success('已删除收藏图片')
      } else {
        ElMessage.error('删除收藏图片失败')
      }
    }).catch(error => {
      console.error('删除收藏图片出错:', error)
      ElMessage.error('删除收藏图片失败，请重试')
      checkLogin()
    })
  }
  
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
      info: "模拟数据，当API请求失败时使用",
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
  
  // 获取最新消息ID辅助函数
  const getLatestMessageId = (messages: MessageItem[]): number => {
    if (!messages || messages.length === 0) return 0;
    
    // 过滤出有效的infoid并找出最大值
    const validIds = messages
      .map((m: MessageItem) => m.infoid)
      .filter((id): id is number => id !== undefined && id !== null);
    
    return validIds.length > 0 ? Math.max(...validIds) : 0;
  };
  
  // 按照信息ID或时间排序消息，确保消息按照时间顺序显示
  const sortMessages = (messages: MessageItem[]): MessageItem[] => {
    // 使用时间字段排序，较早的消息在前
    return [...messages].sort((a, b) => {
      // 优先使用infoid比较，较小的id通常是较早的消息
      if (a.infoid !== undefined && b.infoid !== undefined) {
        return a.infoid - b.infoid;
      }
      // 回退到时间比较
      return new Date(a.time).getTime() - new Date(b.time).getTime();
    });
  };
  
  // 预览收藏图片
  const previewFavoriteImage = (item: {id: number, fileUrl: string}, index: number) => {
    showPreview(item.fileUrl, favoriteImages.value.map(img => img.fileUrl));
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
        
        // 重新获取最新消息，确保显示刚发送的消息
        currentPage.value = 1; // 重置页码
        noMoreMessages.value = false; // 重置没有更多消息标志
        getmsg(false, false); // 获取第一页消息
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
  
  // 为了解决图片加载后可能导致的滚动问题，添加一个预加载图片的方法
  const preloadImages = (imageUrls: string[]): Promise<void[]> => {
    // 如果没有图片，直接返回空Promise数组
    if (!imageUrls || imageUrls.length === 0) {
      return Promise.resolve([]);
    }
    
    // 创建图片加载Promise
    const promises = imageUrls.map(url => {
      return new Promise<void>((resolve) => {
        const img = new Image();
        img.onload = () => resolve();
        img.onerror = () => resolve(); // 即使加载失败也继续
        img.src = url;
      });
    });
    
    return Promise.all(promises);
  };
  
  // 增强版滚动到底部的方法，确保图片加载后再滚动
  const enhancedScrollToBottom = async () => {
    // 如果处于初始化状态，禁用滚动事件触发
    if (isInitialLoading.value) {
      disableScroll.value = true;
    }
    
    // 收集所有消息中的图片URL
    const allImageUrls: string[] = [];
    msfObj.value.forEach(item => {
      const { imgUrls } = processMessageContent(item.info);
      if (imgUrls && imgUrls.length > 0) {
        allImageUrls.push(...imgUrls);
      }
    });
    
    // 预加载图片
    if (allImageUrls.length > 0) {
      console.log(`开始预加载${allImageUrls.length}张图片`);
      await preloadImages(allImageUrls);
      console.log(`图片预加载完成`);
    }
    
    // 执行滚动
    scrollToBottom();
    
    // 初始加载完成后延迟一段时间再解除禁用，避免初始滚动触发加载
    if (isInitialLoading.value) {
      setTimeout(() => {
        disableScroll.value = false;
      }, 800);
    }
  };
  
  // 监听滚动事件，检测何时需要加载更多消息
  const handleScroll = async () => {
    if (isInitialLoading.value || !chatContainer.value || isLoadingMore.value) {
      // 如果正在初始化，或容器不存在，或已在加载更多，则返回
      return;
    }

    const container = chatContainer.value as HTMLElement;

    // 更新用户滚动相关的状态
    lastUserScrollTime.value = Date.now();
    const isNearBottom = container.scrollHeight - container.scrollTop - container.clientHeight < 50;
    shouldScrollToBottom.value = isNearBottom;
    userViewingHistory.value = !isNearBottom;

    // 当滚动到顶部附近 (<50px)，且没有正在加载，且还有更多消息时，触发加载
    if (container.scrollTop < 50 && !noMoreMessages.value) {
      console.log('滚动到顶部，尝试加载更多消息...');
      isLoadingMore.value = true; // 设置为正在加载更多
      disableScroll.value = true;  // 禁用滚动事件，防止重复触发

      const oldHeight = container.scrollHeight;
      const oldScroll = container.scrollTop;

      try {
        // 调用 getmsg 获取更多消息，并等待其完成
        await getmsg(true, false); 

        // 等待 Vue DOM 更新
        await nextTick();

        if (chatContainer.value) {
          const newHeight = container.scrollHeight;
          // 关键：保持滚动位置
          // 临时禁用平滑滚动，以确保 scrollTop 的修改是即时的
          const originalScrollBehavior = container.style.scrollBehavior;
          container.style.scrollBehavior = 'auto';
          container.scrollTop = oldScroll + (newHeight - oldHeight);
          container.style.scrollBehavior = originalScrollBehavior; // 恢复原始设置
          
          console.log(`更多消息加载完成。旧滚动位置: ${oldScroll}, 新滚动位置: ${container.scrollTop}`);
        }
      } catch (error) {
        console.error('加载更多消息失败 (handleScroll):', error);
        // 此处可以添加用户提示，例如 ElMessage.error('加载历史消息失败');
      } finally {
        // 无论成功或失败，都重置加载状态和滚动禁用状态
        isLoadingMore.value = false;
        disableScroll.value = false;
        console.log('加载更多消息流程结束。');
      }
    }
  };
  
  // 获取消息
  const getmsg = (isLoadMore = false, isPoll = false): Promise<void> => {
    return new Promise((resolve, reject) => {
      // isLoadMore 和 disableScroll 由 handleScroll 管理，此处不再重复设置
      
      const pageNum = isLoadMore ? currentPage.value + 1 : 1;
      const pageSize = 10;

      if (isLoadMore) {
        console.log(`[getmsg] 准备获取更多消息，页码: ${pageNum}`);
      }

      const loadWithDelay = () => {
        get(pageSize, pageNum).then((res) => {
          console.log('[getmsg] API 响应:', res.data);
          if (res.data && res.data.code === "200" && res.data.data) {
            const responseData = res.data.data;
            let messageData = responseData.list || [];
            console.log(`[getmsg] 收到 ${messageData.length} 条消息。当前页: ${responseData.pageNum}, 总页数: ${responseData.pages}`);

            const totalPages = responseData.pages || 1;

            if (isLoadMore) {
              if (messageData.length === 0) {
                noMoreMessages.value = true;
                console.log('[getmsg] 没有更多历史消息了。');
              }
              if (pageNum >= totalPages) {
                 noMoreMessages.value = true;
                 console.log('[getmsg] 已到达历史消息的最后一页。');
              }
              if (messageData.length > 0) {
                const allMessages = [...messageData, ...msfObj.value];
                msfObj.value = sortMessages(allMessages) as MessageItem[];
                currentPage.value = pageNum;
                console.log(`[getmsg] 历史消息已预置。总数: ${msfObj.value.length}。当前页: ${currentPage.value}`);
              }
            } else if (isPoll) {
              const hasNewMessages = messageData.length > 0 && 
                (msfObj.value.length === 0 || 
                getLatestMessageId(messageData) > getLatestMessageId(msfObj.value));
              if (hasNewMessages) {
                let isAtBottom = shouldScrollToBottom.value && !userViewingHistory.value;
                const oldScrollPosition = chatContainer.value ? (chatContainer.value as HTMLElement).scrollTop : 0;
                const oldScrollHeight = chatContainer.value ? (chatContainer.value as HTMLElement).scrollHeight : 0;
                msfObj.value = sortMessages(messageData) as MessageItem[];
                console.log(`[getmsg] 轮询更新，有新消息。用户${isAtBottom ? '在底部' : '不在底部'}`);
                if (isAtBottom) {
                  nextTick(() => scrollToBottom());
                } else {
                  nextTick(() => {
                    if (chatContainer.value) {
                      const container = chatContainer.value as HTMLElement;
                      const newScrollHeight = container.scrollHeight;
                      if (newScrollHeight !== oldScrollHeight) {
                        const scrollDiff = newScrollHeight - oldScrollHeight;
                        container.scrollTop = oldScrollPosition + scrollDiff;
                      }
                    }
                  });
                }
              }
            } else { // 初始加载
              msfObj.value = sortMessages(messageData) as MessageItem[];
              currentPage.value = 1;
              noMoreMessages.value = pageNum >= totalPages;
              console.log(`[getmsg] 初始消息加载: ${msfObj.value.length} 条。`);
              if (!isPoll) { 
                  nextTick(async () => {
                    await enhancedScrollToBottom(); //确保预加载图片和滚动完成
                    shouldScrollToBottom.value = true;
                    userViewingHistory.value = false;
                  });
              }
            }
            resolve();
          } else {
            console.error('[getmsg] 获取消息失败或响应格式不正确:', res.data);
            if (!isLoadMore && !isPoll) ElMessage.error('获取消息失败');
            reject(new Error('获取消息失败或响应格式不正确'));
          }
        }).catch(error => {
          console.error('[getmsg] API调用失败:', error);
          if (msfObj.value.length === 0 && !isLoadMore && !isPoll) {
            msfObj.value = mockData; 
             nextTick(() => scrollToBottom());
          }
          reject(error);
        });
      };

      if (isLoadMore) {
        setTimeout(loadWithDelay, 500); 
      } else {
        loadWithDelay(); 
      }
    });
  };
  
  // 滚动到底部
  const scrollToBottom = () => {
    // 如果处于初始化状态，添加额外保护
    if (isInitialLoading.value) {
      disableScroll.value = true;
    }
    
    nextTick(() => {
      if (chatContainer.value) {
        const container = chatContainer.value as HTMLElement;
        container.scrollTop = container.scrollHeight;
        console.log('已滚动到底部:', container.scrollHeight);
      }
      
      // 初始化滚动完成后解除滚动禁用
      if (isInitialLoading.value) {
        setTimeout(() => {
          disableScroll.value = false;
        }, 800);
      }
    });
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
  
  // 添加消息到显示区域
  const addMessage = (message: MessageItem) => {
    // 检查当前是否在底部
    let isAtBottom = false;
    if (chatContainer.value) {
      const container = chatContainer.value as HTMLElement;
      isAtBottom = container.scrollHeight - container.scrollTop - container.clientHeight < 50;
    }
    
    // 添加消息
    msfObj.value.push(message);
    
    // 如果用户正在查看底部，则自动滚动
    if (isAtBottom) {
      nextTick(() => {
        scrollToBottom();
      });
    }
  };
  
  // 保存图片到本地
  const saveImageToLocal = (imageUrl: string) => {
    // 创建一个临时的a标签来触发下载
    const link = document.createElement('a')
    link.href = imageUrl
    
    // 从URL中提取文件名，或使用默认名称
    let filename = 'image.png'
    try {
      const urlParts = imageUrl.split('/')
      if (urlParts.length > 0) {
        const lastPart = urlParts[urlParts.length - 1]
        if (lastPart.includes('.')) {
          filename = lastPart.split('?')[0] // 移除URL参数
        }
      }
    } catch (e) {
      console.error('提取文件名失败:', e)
    }
    
    // 对AI生成的图片使用特殊名称
    if (imageUrl.includes('dashscope-result')) {
      const now = new Date()
      const timestamp = `${now.getFullYear()}${(now.getMonth()+1).toString().padStart(2, '0')}${now.getDate().toString().padStart(2, '0')}${now.getHours().toString().padStart(2, '0')}${now.getMinutes().toString().padStart(2, '0')}`
      filename = `AI生成图片_${timestamp}.png`
    }
    
    link.download = filename
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    ElMessage.success('图片下载已开始')
  }
  
  // 组件挂载和销毁
  onMounted(() => {
    // 检查用户是否之前关闭过AI图片提示
    const hideNotice = localStorage.getItem('hideAiImageNotice')
    if (hideNotice === 'true') {
      showAiImageNotice.value = false
    }

    isInitialLoading.value = true; 

    getmsg(false, false) 
      .catch(err => {
        console.error("[onMounted] 初始 getmsg 调用失败:", err);
      })

    timer.value = setInterval(() => {
      if (!isLoadingMore.value && !isInitialLoading.value) { // 仅在非加载状态时轮询
        getmsg(false, true).catch(err => console.error("[onMounted] 轮询 getmsg 失败:", err));
      }
    }, 5000);

    setTimeout(() => {
      if (chatContainer.value) {
        const container = chatContainer.value as HTMLElement;
        container.addEventListener('scroll', handleScroll);
        console.log('[onMounted] 滚动监听器已添加。');
      }
      isInitialLoading.value = false; 
      console.log('[onMounted] 初始化加载完成，isInitialLoading 设置为 false。');
    }, 1200); // 略微增加延迟，确保初始渲染和滚动调整完成

    eventBus.on('add-image-to-chat', (imageUrl: string) => {
      if (imageUrl) {
        imageUrls.value.push(imageUrl);
        ElMessage.success('AI生成的图片已添加到聊天输入框');
      }
    });
  });

  onBeforeUnmount(()=>{
    clearInterval(timer.value);
    timer.value = null;
    
    // 移除滚动事件监听
    if (chatContainer.value) {
      const container = chatContainer.value as HTMLElement;
      container.removeEventListener('scroll', handleScroll);
    }
    
    // 移除事件监听
    eventBus.off('add-image-to-chat');
    document.removeEventListener('click', closeContextMenu);
  });
</script>

<template>
  <div class="chat-container">
    <!-- AI图片有效期提示信息 -->
    <div class="ai-image-notice" v-if="showAiImageNotice">
      <el-alert
        title="AI生成的图片有效时长为24小时，如果你需要收藏，建议先下载下来，然后在点击输入框左侧的+号，上传到服务器，发送到聊天页面后再收藏"
        type="info"
        :closable="true"
        show-icon
        @close="closeAiImageNotice"
      />
    </div>
    
    <!-- 聊天记录区域 -->
    <div class="chat-messages" ref="chatContainer" @scroll="handleScroll">
      <!-- 显示初始加载状态 -->
      <div v-if="isInitialLoading" class="initial-loading">
        <div class="loading-spinner-small"></div>
        <span>正在加载消息...</span>
      </div>
      
      <!-- 加载更多提示 -->
      <div v-if="isLoadingMore" class="loading-more">
        <div class="loading-spinner-small"></div>
        <span>加载更多消息...</span>
      </div>
      
      <!-- 没有更多消息提示 -->
      <div v-if="noMoreMessages && msfObj.length > 0" class="no-more-messages">
        没有更多消息了
      </div>
      
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
              
              <!-- 消息中的图片支持点击预览和右键菜单 -->
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
                    @contextmenu="handleContextMenu($event, imgUrl)"
                    @load="index === msfObj.length - 1 && imgIndex === processMessageContent(item.info).imgUrls.length - 1 && shouldScrollToBottom && scrollToBottom()"
                  >
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 添加一个占位元素，确保滚动到底部时能看到最新消息 -->
      <div class="scroll-anchor"></div>
      
      <!-- 添加"返回底部"按钮 -->
      <div v-if="userViewingHistory" class="back-to-bottom" @click="scrollToBottom">
        <el-button type="primary" size="small" circle>
          <el-icon>
            <svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg" data-v-029747aa="">
              <path fill="currentColor" d="M831.872 340.864 512 652.672 192.128 340.864a30.592 30.592 0 0 0-42.752 0 29.12 29.12 0 0 0 0 41.6L489.664 714.24a32 32 0 0 0 44.672 0l340.288-331.712a29.12 29.12 0 0 0 0-41.728 30.592 30.592 0 0 0-42.752 0z"></path>
            </svg>
          </el-icon>
        </el-button>
      </div>
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
        
        <!-- 收藏图片按钮 -->
        <el-button 
          class="favorite-btn" 
          type="warning" 
          circle 
          @click="openFavoriteDrawer">
          <el-icon><Collection /></el-icon>
        </el-button>
        
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
    
    <!-- 收藏图片抽屉 -->
    <el-drawer
      v-model="favoriteDrawerVisible"
      title="我的收藏图片"
      direction="rtl"
      size="30%"
    >
      <div class="favorite-container">
        <div v-if="loadingFavorites" class="loading-favorites">
          <div class="loading-spinner-small"></div>
          <span>正在加载收藏图片...</span>
        </div>
        <div v-else-if="favoriteImages.length === 0" class="no-favorites">
          <span>暂无收藏图片</span>
        </div>
        <div v-else class="favorite-grid">
          <div 
            v-for="(item, index) in favoriteImages" 
            :key="item.id || index" 
            class="favorite-item"
          >
            <img 
              :src="item.fileUrl" 
              :alt="`收藏图片${index+1}`" 
              class="favorite-img"
              @click="addFavoriteImageToChat(item.fileUrl)"
            >
            <div class="favorite-hover">
              <span>点击添加到聊天</span>
            </div>
            <div class="favorite-actions">
              <el-button 
                type="primary" 
                circle 
                size="small"
                @click.stop="previewFavoriteImage(item, index)"
                title="预览图片"
                class="preview-btn"
              >
                <el-icon>
                  <svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg" data-v-029747aa="">
                    <path fill="currentColor" d="M512 160c320 0 512 352 512 352S832 864 512 864 0 512 0 512s192-352 512-352zm0 64c-225.28 0-384.128 208.064-436.8 288 52.608 79.872 211.456 288 436.8 288 225.28 0 384.128-208.064 436.8-288-52.608-79.872-211.456-288-436.8-288zm0 64a224 224 0 1 1 0 448 224 224 0 0 1 0-448zm0 64a160.192 160.192 0 0 0-160 160c0 88.192 71.744 160 160 160s160-71.808 160-160-71.744-160-160-160z"></path>
                  </svg>
                </el-icon>
              </el-button>
              <el-button 
                type="danger" 
                circle 
                size="small"
                @click.stop="deleteFavorite(item.id, index)"
                title="删除收藏"
                class="delete-btn"
              >
                <el-icon class="delete-icon">
                  <svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg" data-v-029747aa="">
                    <path fill="currentColor" d="M160 256H96a32 32 0 0 1 0-64h256V95.936a32 32 0 0 1 32-32h256a32 32 0 0 1 32 32V192h256a32 32 0 1 1 0 64h-64v672a32 32 0 0 1-32 32H192a32 32 0 0 1-32-32V256zm448-64v-64H416v64h192zM224 896h576V256H224v640zm192-128a32 32 0 0 1-32-32V416a32 32 0 0 1 64 0v320a32 32 0 0 1-32 32zm192 0a32 32 0 0 1-32-32V416a32 32 0 0 1 64 0v320a32 32 0 0 1-32 32z"></path>
                  </svg>
                </el-icon>
              </el-button>
            </div>
          </div>
        </div>
        
        <!-- 刷新按钮 -->
        <div class="refresh-btn-container">
          <el-button 
            type="primary" 
            :icon="Refresh" 
            circle 
            @click="getFavorites"
            :loading="loadingFavorites"
            title="刷新收藏列表"
          ></el-button>
        </div>
      </div>
    </el-drawer>
    
    <!-- 右键菜单 -->
    <div 
      v-show="showContextMenu" 
      class="context-menu"
      :style="{
        left: contextMenuPosition.x + 'px',
        top: contextMenuPosition.y + 'px'
      }"
    >
      <div 
        class="context-menu-item" 
        @click="addToFavorite(selectedImageUrl)"
        :class="{'disabled-menu-item': selectedImageUrl.includes('dashscope-result')}"
      >
        <el-icon><Collection /></el-icon>
        <span>添加到收藏</span>
        <span v-if="selectedImageUrl.includes('dashscope-result')" class="menu-item-note">
          (需先保存再上传)
        </span>
      </div>
      <div 
        class="context-menu-item" 
        @click="addFavoriteImageToChat(selectedImageUrl)"
        :class="{'disabled-menu-item': selectedImageUrl.includes('dashscope-result')}"
      >
        <el-icon><Plus /></el-icon>
        <span>添加到聊天框</span>
        <span v-if="selectedImageUrl.includes('dashscope-result')" class="menu-item-note">
          (需先保存再上传)
        </span>
      </div>
      <div class="context-menu-item" @click="saveImageToLocal(selectedImageUrl)">
        <el-icon>
          <svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg" data-v-029747aa="">
            <path fill="currentColor" d="M160 832h704a32 32 0 1 1 0 64H160a32 32 0 1 1 0-64zm384-253.696 236.288-236.352 45.248 45.248L508.8 704 320 515.2l45.248-45.248L508.8 578.304zM512 128a32 32 0 0 1 32 32v364.8l-64 64V160a32 32 0 0 1 32-32z"></path>
          </svg>
        </el-icon>
        <span>保存到本地</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 科技风格聊天容器 */
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  max-height: calc(100vh - 120px);
  background: var(--surface-bg);
  border: 2px solid var(--border-color);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 
    0 20px 40px rgba(0, 0, 0, 0.3),
    0 0 20px rgba(0, 212, 255, 0.2);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  position: relative;
}

.chat-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, 
    rgba(0, 212, 255, 0.03), 
    transparent, 
    rgba(78, 205, 196, 0.03)
  );
  border-radius: 16px;
  z-index: -1;
}

/* AI图片有效期提示信息样式 - 科技风格 */
.ai-image-notice {
  padding: 16px;
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.1), rgba(26, 26, 46, 0.8));
  border-bottom: 2px solid var(--border-color);
  position: relative;
}

.ai-image-notice::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--primary-color), var(--accent-color));
  animation: scanLine 3s ease-in-out infinite;
}

@keyframes scanLine {
  0%, 100% { opacity: 0.3; transform: scaleX(1); }
  50% { opacity: 1; transform: scaleX(1.02); }
}

/* Element Plus Alert 科技风格重写 */
:deep(.el-alert) {
  background: rgba(26, 26, 46, 0.6) !important;
  border: 1px solid var(--border-color) !important;
  border-radius: 12px !important;
  color: var(--text-primary) !important;
}

:deep(.el-alert__icon) {
  color: var(--primary-color) !important;
}

:deep(.el-alert__title) {
  color: var(--text-primary) !important;
  font-size: 14px !important;
}

/* 科技风格聊天消息区域 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  scroll-behavior: smooth;
  min-height: 300px;
  background: rgba(26, 26, 46, 0.3);
  position: relative;
}

.chat-messages::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    linear-gradient(rgba(0, 212, 255, 0.02) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 212, 255, 0.02) 1px, transparent 1px);
  background-size: 30px 30px;
  pointer-events: none;
  z-index: 0;
}

.scroll-anchor {
  height: 20px;
  margin-top: 20px;
  margin-bottom: 10px;
  visibility: hidden;
}

/* 科技风格消息包装器 */
.message-wrapper {
  margin-bottom: 20px;
  position: relative;
  z-index: 1;
}

/* 科技风格时间显示 */
.message-time {
  text-align: center;
  margin: 12px 0;
  font-size: 12px;
  color: var(--text-muted);
  font-family: var(--font-mono);
  background: rgba(26, 26, 46, 0.6);
  padding: 6px 16px;
  border-radius: 20px;
  border: 1px solid var(--border-color);
  display: inline-block;
  left: 50%;
  transform: translateX(-50%);
  position: relative;
}

/* 科技风格消息项 */
.message-item {
  display: flex;
  margin-bottom: 12px;
  align-items: flex-start;
  animation: messageSlideIn 0.3s ease-out;
}

@keyframes messageSlideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.my-message {
  flex-direction: row-reverse;
}

/* 科技风格头像 */
.avatar {
  margin: 0 12px;
  flex-shrink: 0;
  width: 44px;
  height: 44px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid var(--border-color);
  background: var(--surface-bg);
  position: relative;
}

.avatar::before {
  content: '';
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  background: linear-gradient(45deg, var(--primary-color), var(--accent-color));
  border-radius: 50%;
  z-index: -1;
  animation: avatarGlow 3s ease-in-out infinite;
}

@keyframes avatarGlow {
  0%, 100% { opacity: 0.5; }
  50% { opacity: 1; }
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

/* 科技风格消息气泡 */
.message-bubble {
  max-width: 70%;
  border-radius: 16px;
  padding: 16px 20px;
  background: var(--surface-bg);
  border: 1px solid var(--border-color);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  position: relative;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease;
}

.message-bubble::before {
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

.message-bubble:hover {
  border-color: var(--primary-color);
  box-shadow: 0 6px 25px rgba(0, 212, 255, 0.2);
  transform: translateY(-2px);
}

.my-message .message-bubble {
  background: linear-gradient(135deg, 
    rgba(0, 212, 255, 0.2), 
    rgba(78, 205, 196, 0.2)
  );
  border-color: var(--primary-color);
}

/* 科技风格发送者名称 */
.message-sender {
  font-size: 12px;
  color: var(--text-muted);
  margin-bottom: 8px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 1px;
  font-family: var(--font-mono);
}

.my-message .message-sender {
  text-align: right;
  color: var(--primary-color);
}

/* 科技风格消息内容 */
.message-content {
  word-break: break-word;
  color: var(--text-primary);
}

.message-content p {
  margin: 0;
  line-height: 1.6;
  font-size: 14px;
}

/* 科技风格消息图片 */
.message-images {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 12px;
}

.message-image {
  position: relative;
  cursor: pointer;
  margin: 6px;
  overflow: hidden;
  border-radius: 12px;
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
}

.message-image:hover {
  border-color: var(--primary-color);
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.4);
  transform: scale(1.02);
}

.chat-img {
  max-width: 200px;
  max-height: 200px;
  border-radius: 12px;
  transition: transform 0.3s;
  display: block;
}

.chat-img:hover {
  transform: scale(1.05);
}

/* 科技风格输入区域 */
.chat-input-area {
  background: linear-gradient(135deg, rgba(26, 26, 46, 0.8), rgba(0, 212, 255, 0.05));
  padding: 20px;
  border-top: 2px solid var(--border-color);
  position: relative;
}

.chat-input-area::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--primary-color), transparent);
  animation: inputGlow 4s ease-in-out infinite;
}

@keyframes inputGlow {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 1; }
}

/* 科技风格图片预览区域 */
.preview-area {
  padding: 16px;
  background: rgba(26, 26, 46, 0.6);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  margin-bottom: 16px;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.image-item {
  position: relative;
  width: 80px;
  height: 80px;
  border: 2px solid var(--border-color);
  border-radius: 12px;
  padding: 4px;
  background: var(--surface-bg);
  transition: all 0.3s ease;
}

.image-item:hover {
  border-color: var(--primary-color);
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.3);
}

.preview-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
  cursor: pointer;
}

.remove-image {
  position: absolute;
  top: -12px;
  right: -12px;
  width: 24px;
  height: 24px;
  background: linear-gradient(45deg, var(--error-color), var(--secondary-color));
  color: white;
  border-radius: 50%;
  text-align: center;
  line-height: 22px;
  cursor: pointer;
  font-size: 14px;
  font-weight: bold;
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
  border: 2px solid var(--primary-bg);
  transition: all 0.3s ease;
}

.remove-image:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.5);
}

/* 科技风格输入控制 */
.input-controls {
  display: flex;
  align-items: center;
  gap: 12px;
}

.text-input-wrapper {
  flex: 1;
  position: relative;
}

.text-input-wrapper::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 24px;
  background: linear-gradient(45deg, transparent, rgba(0, 212, 255, 0.1), transparent);
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
}

.text-input-wrapper:focus-within::before {
  opacity: 1;
}

.chat-textarea {
  width: 100%;
  min-height: 44px;
  max-height: 120px;
  padding: 12px 20px;
  border: 2px solid var(--border-color);
  border-radius: 24px;
  resize: none;
  font-size: 14px;
  line-height: 1.6;
  background: var(--surface-bg);
  color: var(--text-primary);
  font-family: var(--font-primary);
  transition: all 0.3s ease;
  outline: none;
}

.chat-textarea:focus {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(0, 212, 255, 0.2);
}

.chat-textarea::placeholder {
  color: var(--text-muted);
}

/* Element Plus 按钮科技风格重写 */
:deep(.upload-btn .el-button),
:deep(.send-btn .el-button),
:deep(.favorite-btn .el-button) {
  background: linear-gradient(45deg, var(--primary-color), var(--accent-color)) !important;
  border: none !important;
  color: var(--primary-bg) !important;
  border-radius: 12px !important;
  padding: 12px 16px !important;
  font-weight: 600 !important;
  transition: all 0.3s ease !important;
  position: relative !important;
  overflow: hidden !important;
}

:deep(.upload-btn .el-button:hover),
:deep(.send-btn .el-button:hover),
:deep(.favorite-btn .el-button:hover) {
  transform: translateY(-2px) !important;
  box-shadow: 0 8px 25px rgba(0, 212, 255, 0.4) !important;
}

:deep(.upload-btn .el-button::before),
:deep(.send-btn .el-button::before),
:deep(.favorite-btn .el-button::before) {
  content: '' !important;
  position: absolute !important;
  top: 0 !important;
  left: -100% !important;
  width: 100% !important;
  height: 100% !important;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent) !important;
  transition: left 0.6s !important;
}

:deep(.upload-btn .el-button:hover::before),
:deep(.send-btn .el-button:hover::before),
:deep(.favorite-btn .el-button:hover::before) {
  left: 100% !important;
}

/* 科技风格滚动条 */
.chat-messages::-webkit-scrollbar {
  width: 8px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: linear-gradient(45deg, var(--primary-color), var(--accent-color));
  border-radius: 4px;
}

.chat-messages::-webkit-scrollbar-track {
  background: rgba(26, 26, 46, 0.3);
  border-radius: 4px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(45deg, var(--accent-color), var(--primary-color));
}

/* 科技风格收藏图片抽屉 */
:deep(.el-drawer) {
  background: var(--surface-bg) !important;
  border-left: 2px solid var(--border-color) !important;
}

:deep(.el-drawer__header) {
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.1), rgba(26, 26, 46, 0.8)) !important;
  border-bottom: 2px solid var(--border-color) !important;
  color: var(--text-primary) !important;
  padding: 20px !important;
}

:deep(.el-drawer__title) {
  color: var(--primary-color) !important;
  font-weight: 700 !important;
  font-size: 18px !important;
}

:deep(.el-drawer__close-btn) {
  color: var(--text-primary) !important;
  background: rgba(26, 26, 46, 0.6) !important;
  border: 1px solid var(--border-color) !important;
  border-radius: 8px !important;
  padding: 8px !important;
  transition: all 0.3s ease !important;
}

:deep(.el-drawer__close-btn:hover) {
  background: var(--primary-color) !important;
  color: var(--primary-bg) !important;
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.5) !important;
}

.favorite-container {
  height: 100%;
  padding: 20px;
  background: var(--surface-bg);
}

.loading-favorites,
.no-favorites {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 120px;
  color: var(--text-secondary);
  background: rgba(26, 26, 46, 0.4);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  font-size: 14px;
}

.favorite-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  padding: 20px;
}

.favorite-item {
  position: relative;
  cursor: pointer;
  border-radius: 12px;
  overflow: hidden;
  border: 2px solid var(--border-color);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);
  aspect-ratio: 1 / 1;
  transition: all 0.3s ease;
  background: var(--surface-bg);
}

.favorite-item:hover {
  border-color: var(--primary-color);
  box-shadow: 0 12px 35px rgba(0, 212, 255, 0.3);
  transform: translateY(-4px);
}

.favorite-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.favorite-hover {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.8) 0%, transparent 100%);
  color: white;
  padding: 12px;
  font-size: 12px;
  text-align: center;
  opacity: 0;
  transition: opacity 0.3s;
  font-weight: 600;
}

.favorite-item:hover .favorite-img {
  transform: scale(1.1);
}

.favorite-item:hover .favorite-hover {
  opacity: 1;
}

.favorite-actions {
  position: absolute;
  top: 8px;
  right: 8px;
  opacity: 0;
  transition: opacity 0.3s;
  display: flex;
  gap: 6px;
}

.favorite-item:hover .favorite-actions {
  opacity: 1;
}

:deep(.favorite-actions .el-button) {
  background: rgba(26, 26, 46, 0.9) !important;
  border: 1px solid var(--border-color) !important;
  border-radius: 8px !important;
  padding: 8px !important;
  backdrop-filter: blur(10px) !important;
}

:deep(.preview-btn) {
  background: rgba(0, 212, 255, 0.9) !important;
  color: white !important;
  border-color: var(--primary-color) !important;
}

:deep(.delete-btn) {
  background: rgba(255, 107, 107, 0.9) !important;
  color: white !important;
  border-color: var(--error-color) !important;
}

:deep(.delete-btn:hover) {
  background: var(--error-color) !important;
  box-shadow: 0 0 15px rgba(255, 107, 107, 0.5) !important;
}

.delete-icon {
  color: white !important;
}

/* 科技风格刷新按钮 */
.refresh-btn-container {
  position: absolute;
  bottom: 20px;
  right: 20px;
}

:deep(.refresh-btn-container .el-button) {
  background: linear-gradient(45deg, var(--primary-color), var(--accent-color)) !important;
  border: none !important;
  border-radius: 50% !important;
  width: 48px !important;
  height: 48px !important;
  box-shadow: 0 4px 20px rgba(0, 212, 255, 0.3) !important;
  transition: all 0.3s ease !important;
}

:deep(.refresh-btn-container .el-button:hover) {
  transform: translateY(-2px) !important;
  box-shadow: 0 8px 30px rgba(0, 212, 255, 0.5) !important;
}

/* 科技风格加载动画 */
.loading-favorites .loading-spinner-small {
  width: 20px;
  height: 20px;
  border: 3px solid rgba(0, 212, 255, 0.3);
  border-top: 3px solid var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-right: 12px;
}

/* 科技风格右键菜单 */
.context-menu {
  position: fixed;
  background: var(--surface-bg);
  border: 2px solid var(--border-color);
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.5);
  padding: 8px 0;
  z-index: 2500;
  width: 240px;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
}

.context-menu::before {
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
  border-radius: 12px;
  z-index: -1;
}

.context-menu-item {
  padding: 12px 20px;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
  color: var(--text-primary);
  position: relative;
}

.context-menu-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(90deg, transparent, rgba(0, 212, 255, 0.1), transparent);
  opacity: 0;
  transition: opacity 0.3s;
}

.context-menu-item:hover::before {
  opacity: 1;
}

.context-menu-item:hover {
  background: rgba(0, 212, 255, 0.1);
  color: var(--primary-color);
}

:deep(.context-menu-item .el-icon) {
  margin-right: 12px !important;
  font-size: 16px !important;
  color: var(--primary-color) !important;
}

.disabled-menu-item {
  opacity: 0.5;
  cursor: not-allowed;
}

.disabled-menu-item:hover {
  background: transparent;
  color: var(--text-primary);
}

.disabled-menu-item:hover::before {
  opacity: 0;
}

.menu-item-note {
  font-size: 11px;
  color: var(--text-muted);
  margin-left: 8px;
  font-style: italic;
}

/* 科技风格加载提示 */
.loading-more, .no-more-messages {
  text-align: center;
  padding: 12px 16px;
  font-size: 12px;
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 12px 0;
  border-radius: 8px;
  border: 1px solid var(--border-color);
  background: rgba(26, 26, 46, 0.4);
  font-family: var(--font-mono);
}

.loading-more {
  background: rgba(0, 212, 255, 0.1);
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.no-more-messages {
  background: rgba(26, 26, 46, 0.6);
  color: var(--text-muted);
}

.loading-spinner-small {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(0, 212, 255, 0.3);
  border-top: 2px solid var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-right: 8px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 科技风格返回底部按钮 */
.back-to-bottom {
  position: absolute;
  right: 20px;
  bottom: 100px;
  z-index: 10;
  transition: all 0.3s ease;
  cursor: pointer;
}

:deep(.back-to-bottom .el-button) {
  background: linear-gradient(45deg, var(--primary-color), var(--accent-color)) !important;
  border: none !important;
  border-radius: 50% !important;
  width: 48px !important;
  height: 48px !important;
  box-shadow: 0 6px 25px rgba(0, 212, 255, 0.4) !important;
  color: var(--primary-bg) !important;
  transition: all 0.3s ease !important;
}

:deep(.back-to-bottom .el-button:hover) {
  transform: translateY(-3px) !important;
  box-shadow: 0 10px 35px rgba(0, 212, 255, 0.6) !important;
}

/* 科技风格初始加载状态 */
.initial-loading {
  text-align: center;
  padding: 16px;
  font-size: 14px;
  color: var(--primary-color);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 12px 0;
  background: rgba(0, 212, 255, 0.1);
  border: 1px solid var(--primary-color);
  border-radius: 8px;
  font-family: var(--font-mono);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .chat-container {
    border-radius: 12px;
  }
  
  .message-bubble {
    max-width: 85%;
    padding: 12px 16px;
  }
  
  .favorite-grid {
    grid-template-columns: 1fr;
    gap: 16px;
    padding: 16px;
  }
  
  .context-menu {
    width: 200px;
  }
}

@media (max-width: 480px) {
  .chat-messages {
    padding: 16px;
  }
  
  .chat-input-area {
    padding: 16px;
  }
  
  .input-controls {
    flex-direction: column;
    gap: 12px;
  }
  
  .avatar {
    width: 36px;
    height: 36px;
    margin: 0 8px;
  }
  
  .message-bubble {
    max-width: 90%;
    padding: 10px 14px;
  }
}
</style>
