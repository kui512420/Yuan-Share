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
  const handleScroll = () => {
    // 如果是初始化过程中的滚动，则忽略
    if (isInitialLoading.value || !chatContainer.value || disableScroll.value) return;
    
    const container = chatContainer.value as HTMLElement;
    
    // 记录用户滚动时间，用于判断是否是用户主动滚动还是自动滚动
    lastUserScrollTime.value = Date.now();
    
    // 检查用户是否在底部附近
    const isNearBottom = container.scrollHeight - container.scrollTop - container.clientHeight < 50;
    
    // 更新状态标志
    shouldScrollToBottom.value = isNearBottom;
    userViewingHistory.value = !isNearBottom;
    
    // 当滚动到顶部附近时加载更多（懒加载方式，每次只加载下10条）
    if (container.scrollTop < 50 && !isLoadingMore.value && !noMoreMessages.value) {
      // 记录当前滚动位置和内容高度
      const oldHeight = container.scrollHeight;
      const oldScroll = container.scrollTop;
      
      console.log(`加载更多消息 - 当前页: ${currentPage.value}, 下一页: ${currentPage.value + 1}`);
      
      // 禁用滚动，防止用户继续滑动
      disableScroll.value = true;
      
      // 加载更多消息（只加载下一页的10条）
      getmsg(true, false);
      
      // 在新内容加载后保持滚动位置
      nextTick(() => {
        if (chatContainer.value) {
          const newHeight = container.scrollHeight;
          container.scrollTop = oldScroll + (newHeight - oldHeight);
          console.log(`加载完成 - 新高度: ${newHeight}, 旧高度: ${oldHeight}, 滚动位置调整为: ${container.scrollTop}`);
          
          // 添加短暂延迟后再启用滚动，确保渲染完成
          setTimeout(() => {
            disableScroll.value = false;
          }, 300); // 300毫秒延迟，可根据需要调整
        }
      });
    }
  };
  
  // 获取消息
  const getmsg = (isLoadMore = false, isPoll = false) => {
    // 如果已经在加载中，不重复加载
    if (isLoadingMore.value && isLoadMore) return;
    
    // 如果是加载更多，则页码加1
    const pageNum = isLoadMore ? currentPage.value + 1 : 1;
    const pageSize = 10;
    
    if (isLoadMore) {
      isLoadingMore.value = true;
      // 禁用滚动
      disableScroll.value = true;
      console.log(`开始加载第${pageNum}页数据，每页${pageSize}条`);
    }
    
    // 添加延迟，让用户感知到正在加载，减慢加载速度
    const loadWithDelay = () => {
      get(pageSize, pageNum).then((res) => {
        console.log('获取消息响应:', res.data);
        
        if (res.data && res.data.code === "200" && res.data.data) {
          const responseData = res.data.data;
          
          // 从data.list中获取消息列表
          let messageData = responseData.list || [];
          
          console.log(`获取到${messageData.length}条消息，当前页${responseData.pageNum}，总页数${responseData.pages}`);
          
          // 检查是否有更多消息
          if (messageData.length === 0) {
            noMoreMessages.value = true;
            isLoadingMore.value = false;
            console.log('没有更多消息了');
            
            // 启用滚动
            setTimeout(() => {
              disableScroll.value = false;
            }, 300);
            return;
          }
          
          // 保存总页数信息
          const totalPages = responseData.pages || 1;
          
          // 检查是否已经到最后一页
          if (pageNum >= totalPages) {
            noMoreMessages.value = true;
            console.log('已到达最后一页，没有更多消息了');
          }
          
          // 如果是加载更多，将新消息添加到现有消息之前
          if (isLoadMore) {
            // 合并并排序所有消息
            const allMessages = [...messageData, ...msfObj.value];
            msfObj.value = sortMessages(allMessages) as MessageItem[];
            console.log(`追加加载了${messageData.length}条历史消息，总共${msfObj.value.length}条`);
            
            // 更新当前页码
            currentPage.value = pageNum;
            
            // 加载完成后启用滚动
            setTimeout(() => {
              disableScroll.value = false;
            }, 300);
          } else if (isPoll) {
            // 如果是轮询获取的新消息
            // 检查是否有新消息 (比较最新消息的ID)
            const hasNewMessages = messageData.length > 0 && 
              (msfObj.value.length === 0 || 
              getLatestMessageId(messageData) > getLatestMessageId(msfObj.value));
            
            if (hasNewMessages) {
              // 记录轮询获取新消息前的状态
              const oldScrollPosition = chatContainer.value ? (chatContainer.value as HTMLElement).scrollTop : 0;
              const oldScrollHeight = chatContainer.value ? (chatContainer.value as HTMLElement).scrollHeight : 0;
              const oldClientHeight = chatContainer.value ? (chatContainer.value as HTMLElement).clientHeight : 0;
              
              // 检查用户是否在查看底部
              const wasAtBottom = shouldScrollToBottom.value && !userViewingHistory.value;
              
              // 更新消息并确保排序正确
              msfObj.value = sortMessages(messageData) as MessageItem[];
              console.log(`轮询更新消息，有新消息，用户${wasAtBottom ? '在底部' : '不在底部'}`);
              
              // 只有当用户在底部时才自动滚动到底部
              if (wasAtBottom) {
                nextTick(() => {
                  scrollToBottom();
                });
              } else {
                // 如果用户不在底部，保持当前的滚动位置
                nextTick(() => {
                  if (chatContainer.value) {
                    const container = chatContainer.value as HTMLElement;
                    const newScrollHeight = container.scrollHeight;
                    
                    // 如果内容高度变化了，调整滚动位置以保持相对位置
                    if (newScrollHeight !== oldScrollHeight) {
                      const scrollDiff = newScrollHeight - oldScrollHeight;
                      container.scrollTop = oldScrollPosition + scrollDiff;
                      console.log('保持相对滚动位置:', oldScrollPosition, '->', container.scrollTop, '差值:', scrollDiff);
                    }
                  }
                });
              }
              
              // 如果有新消息且用户不在底部，显示新消息提示
              if (!wasAtBottom && messageData.length > msfObj.value.length) {
                ElMessage({
                  message: '有新消息，滚动到底部查看',
                  type: 'info',
                  duration: 2000,
                  showClose: true
                });
              }
            }
          } else {
            // 第一次加载或主动刷新，直接替换
            msfObj.value = sortMessages(messageData) as MessageItem[];
            console.log(`初始加载${messageData.length}条消息`);
            currentPage.value = 1;
            noMoreMessages.value = pageNum >= totalPages;
            
            // 使用增强版滚动到底部方法 - 首次加载或刷新时总是滚动到底部
            if (!isPoll && !isLoadMore) {
              nextTick(() => {
                enhancedScrollToBottom();
                // 重置状态
                shouldScrollToBottom.value = true;
                userViewingHistory.value = false;
              });
            }
          }
        } else {
          console.error('获取消息失败，响应格式不正确:', res.data);
          if (!isLoadMore && !isPoll) {
            ElMessage.error('获取消息失败');
          }
          
          // 启用滚动
          if (isLoadMore) {
            setTimeout(() => {
              disableScroll.value = false;
            }, 300);
          }
        }
        
        isLoadingMore.value = false;
      }).catch(error => {
        console.error('获取留言失败:', error);
        isLoadingMore.value = false;
        
        // 启用滚动
        if (isLoadMore) {
          setTimeout(() => {
            disableScroll.value = false;
          }, 300);
        }
        
        // 如果获取数据失败且没有消息，使用模拟数据
        if (msfObj.value.length === 0 && !isLoadMore && !isPoll) {
          msfObj.value = mockData;
          // 滚动到底部
          nextTick(() => {
            scrollToBottom();
          });
        }
      });
    };
    
    // 如果是加载更多消息，添加延迟让用户感知到加载过程
    if (isLoadMore) {
      setTimeout(loadWithDelay, 500); // 添加500ms延迟
    } else {
      loadWithDelay();
    }
  }
  
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
  onMounted(()=>{
    // 设置初始化标志
    isInitialLoading.value = true;
    
    // 初始加载最新10条消息，加载完成后自动滚动到底部
    getmsg(false, false);
    
    timer.value = setInterval(()=>{
      // 轮询获取最新消息时，只获取第一页，并传入isPoll=true
      getmsg(false, true);
    }, 5000); // 增加轮询间隔，减少服务器压力
    
    // 添加滚动事件监听 - 延迟添加，避免初始化滚动触发
    setTimeout(() => {
      if (chatContainer.value) {
        const container = chatContainer.value as HTMLElement;
        container.addEventListener('scroll', handleScroll);
      }
      
      // 初始化完成
      isInitialLoading.value = false;
    }, 1000);
    
    // 监听来自ChatWindow的图片添加事件
    eventBus.on('add-image-to-chat', (imageUrl: string) => {
      if (imageUrl) {
        // 将图片URL添加到图片数组
        imageUrls.value.push(imageUrl);
        ElMessage.success('AI生成的图片已添加到聊天输入框');
      }
    });
    
    // 确保首次加载完成后滚动到底部 - 使用增强版滚动方法
    setTimeout(() => {
      enhancedScrollToBottom();
    }, 500);
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
    <div class="ai-image-notice">
      <el-alert
        title="AI生成的图片有效时长为24小时，如果你需要收藏，建议先下载下来，然后在点击输入框左侧的+号，上传到服务器，发送到聊天页面后再收藏"
        type="info"
        :closable="false"
        show-icon
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

/* AI图片有效期提示信息样式 */
.ai-image-notice {
  padding: 10px;
  background-color: #f0f9eb;
  border-bottom: 1px solid #e8f4d9;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  scroll-behavior: smooth;
  min-height: 300px; /* 确保即使没有内容也有滚动条 */
}

.scroll-anchor {
  height: 20px; /* 增加高度确保底部留有空间 */
  margin-top: 20px;
  margin-bottom: 10px; /* 添加底部边距 */
  visibility: hidden; /* 保持不可见 */
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
.send-btn,
.favorite-btn {
  flex-shrink: 0;
}

.favorite-btn {
  margin-left: 5px;
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

/* 收藏图片抽屉内容样式 */
.favorite-container {
  height: 100%;
  padding: 10px;
}

.loading-favorites,
.no-favorites {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100px;
  color: #909399;
}

.favorite-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
  padding: 10px;
}

.favorite-item {
  position: relative;
  cursor: pointer;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  aspect-ratio: 1 / 1;
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
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  padding: 8px;
  font-size: 12px;
  text-align: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.favorite-item:hover .favorite-img {
  transform: scale(1.05);
}

.favorite-item:hover .favorite-hover {
  opacity: 1;
}

.favorite-actions {
  position: absolute;
  top: 5px;
  right: 5px;
  opacity: 0;
  transition: opacity 0.3s;
  display: flex;
  gap: 5px;
}

.favorite-item:hover .favorite-actions {
  opacity: 1;
}

.favorite-actions .el-button {
  background-color: rgba(255, 255, 255, 0.9);
  border: none;
}

.preview-btn {
  background-color: rgba(64, 158, 255, 0.9);
  color: white;
}

.delete-btn {
  background-color: #f56c6c;
  color: white;
  border: none;
}

.delete-btn:hover {
  background-color: #f78989;
}

.delete-icon {
  color: white !important;
}

/* 刷新按钮容器 */
.refresh-btn-container {
  position: absolute;
  bottom: 20px;
  right: 20px;
}

/* 加载中动画 - 小尺寸 */
.loading-favorites .loading-spinner-small {
  width: 16px;
  height: 16px;
  border: 2px solid #f3f3f3;
  border-top: 2px solid #409EFF;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-right: 8px;
}

/* 右键菜单样式 */
.context-menu {
  position: fixed;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 5px 0;
  z-index: 2500;
  width: 220px;
}

.context-menu-item {
  padding: 8px 16px;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: background-color 0.2s;
}

.context-menu-item:hover {
  background-color: #f5f7fa;
}

.context-menu-item .el-icon {
  margin-right: 8px;
  font-size: 16px;
}

.disabled-menu-item {
  opacity: 0.6;
  cursor: not-allowed;
}

.disabled-menu-item:hover {
  background-color: white;
}

.menu-item-note {
  font-size: 11px;
  color: #909399;
  margin-left: 5px;
}

/* 加载更多和没有更多消息提示样式 */
.loading-more, .no-more-messages {
  text-align: center;
  padding: 10px;
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 8px 0;
}

.loading-more {
  background-color: #f0f9ff;
  border-radius: 4px;
}

.no-more-messages {
  background-color: #f0f0f0;
  border-radius: 4px;
  padding: 8px;
}

.loading-spinner-small {
  width: 16px;
  height: 16px;
  border: 2px solid #f3f3f3;
  border-top: 2px solid #409EFF;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-right: 8px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 返回底部按钮样式 */
.back-to-bottom {
  position: absolute;
  right: 20px;
  bottom: 80px;
  z-index: 10;
  transition: opacity 0.3s;
  cursor: pointer;
}

.back-to-bottom .el-button {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.2);
}

/* 初始加载状态 */
.initial-loading {
  text-align: center;
  padding: 10px;
  font-size: 14px;
  color: #409EFF;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 8px 0;
  background-color: #ecf5ff;
  border-radius: 4px;
}
</style>
