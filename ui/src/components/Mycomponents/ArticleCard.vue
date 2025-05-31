<script setup lang="ts">
import { ref, defineProps, watch } from 'vue'
import { get, like, likeArticle} from '@/api/article'
import { convertDate } from '@/utils/DateUntil'
type dataform = {
  article_id: string,
  article_title: string,
  article_author: string,
  publish_time: string,
  article_tag: string,
  article_cover: string,
  like_count: number,
  is_liked: boolean
}
const data = ref<dataform[]>([])
const total = ref(0)
const currentPage1 = ref(1)
const pageSize1 = ref(5)

const refreshList = () => {
  get(currentPage1.value, pageSize1.value, 0, "", 5, 11).then((respon) => {
    data.value = respon.data.data.list
    total.value = respon.data.data.total
  })
}
//分页回调
const handleSizeChange = () => {
  if (props.searchData) {
    doSomething(props.searchData)
  } else {
    refreshList()
  }
}
const handleCurrentChange = () => {
  if (props.searchData) {
    doSomething(props.searchData)
  } else {
    refreshList()
  }
}

const handleLike = (item: dataform) => {
  likeArticle(parseInt(item.article_id)).then((res) => {
      if (res.data.msg == "取消点赞成功") {
        item.like_count -= 1;
      }else{
        item.like_count += 1;
      }
    });
  item.is_liked = !item.is_liked;
};
const PushTime = (time: string) => {
  // 将传入的时间字符串转换为 Date 对象
  const inputTime = new Date(time).getTime();
  // 获取当前时间
  const currentTime = new Date().getTime();
  // 计算两个时间的时间差（单位：毫秒）
  const diff = currentTime - inputTime;

  // 定义不同时间单位对应的毫秒数
  const second = 1000;
  const minute = second * 60;
  const hour = minute * 60;
  const day = hour * 24;
  const month = day * 30;
  const year = day * 365;

  if (diff < second) {
    return "刚刚";
  } else if (diff < minute) {
    // 计算相差的秒数
    const seconds = Math.floor(diff / second);
    return `${seconds} 秒前`;
  } else if (diff < hour) {
    // 计算相差的分钟数
    const minutes = Math.floor(diff / minute);
    return `${minutes} 分钟前`;
  } else if (diff < day) {
    // 计算相差的小时数
    const hours = Math.floor(diff / hour);
    return `${hours} 小时前`;
  } else if (diff < month) {
    // 计算相差的天数
    const days = Math.floor(diff / day);
    return `${days} 天前`;
  } else if (diff < year) {
    // 计算相差的月数
    const months = Math.floor(diff / month);
    return `${months} 个月前`;
  } else {
    // 计算相差的年数和剩余的月数
    const years = Math.floor(diff / year);
    const remainingMonths = Math.floor((diff % year) / month);
    if (remainingMonths > 0) {
      return `${years} 年 ${remainingMonths} 个月前`;
    } else {
      return `${years} 年前`;
    }
  }
}

const props = defineProps({
  searchData: {
    type: String,
    default: ''
  }
});

// 定义一个函数，当接收到的值变化时执行
const doSomething = (txt: string) => {
  likes(txt)
  // 这里可以添加更多的逻辑
};

// 监听 inputValue 的变化
watch(() => props.searchData, (newValue) => {
  if (newValue) {
    doSomething(newValue);
  } else {
    refreshList()
  }
});
//模糊查询
const likes = (txt: string) => {
  like(currentPage1.value, pageSize1.value, txt).then((respon) => {
    data.value = respon.data.data.list
    total.value = respon.data.data.total
  })
}

refreshList()
</script>

<template>
  <div class="tech-article-container">
    <div class="article-header">
      <h3 class="section-title">📰 最新文章</h3>
      <div class="header-decoration"></div>
    </div>
    
    <div class="articles-list">
      <div v-for="(item, index) in data" :key="index" class="article-item tech-card">
        <div class="article-main">
          <div class="article-content">
            <h4 class="article-title">
              <router-link :to="`/article/${item.article_id}`" class="title-link">
                {{ item.article_title }}
              </router-link>
            </h4>
            
            <div class="article-meta">
              <div class="meta-left">
                <span class="article-author">
                  <span class="author-icon">👤</span>
                  {{ item.article_author }}
                </span>
                <span class="article-time">
                  <span class="time-icon">🕒</span>
                  {{ PushTime(convertDate(item.publish_time)) }}
                </span>
              </div>
              
              <div class="meta-right">
                <div class="article-tags">
                  <span v-for="(tag, tagIndex) in JSON.parse(item.article_tag)" :key="tagIndex" class="tech-tag">
                    {{ tag }}
                  </span>
                </div>
                
                <button 
                  class="like-button"
                  :class="{ 'liked': item.is_liked }"
                  @click="handleLike(item)"
                >
                  <span class="like-icon">♥</span>
                  <span class="like-count">{{ item.like_count }}</span>
                </button>
              </div>
            </div>
          </div>
          
          <div class="article-image">
            <img :src="item.article_cover" :alt="item.article_title" />
            <div class="image-overlay"></div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="pagination-container">
      <el-pagination 
        v-model:current-page="currentPage1" 
        v-model:page-size="pageSize1" 
        :background="true"
        layout="prev, pager, next" 
        :total="total" 
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        class="tech-pagination"
      />
    </div>
  </div>
</template>

<style scoped>
.tech-article-container {
  background: var(--surface-bg);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  padding: 24px;
  backdrop-filter: blur(5px);
  -webkit-backdrop-filter: blur(5px);
  color: var(--text-primary);
}

.article-header {
  position: relative;
  margin-bottom: 24px;
  padding-bottom: 16px;
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
  background: linear-gradient(45deg, var(--primary-color), var(--accent-color));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 0 20px var(--primary-color);
}

.header-decoration {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--primary-color), var(--accent-color), transparent);
  border-radius: 1px;
  animation: headerGlow 6s ease-in-out infinite;
}

@keyframes headerGlow {
  0%, 100% {
    opacity: 0.4;
    transform: scaleX(1);
  }
  50% {
    opacity: 0.8;
    transform: scaleX(1.01);
  }
}

.articles-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.article-item {
  background: rgba(26, 26, 46, 0.4);
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.article-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(0, 212, 255, 0.06), transparent);
  transition: left 0.8s;
}

.article-item:hover::before {
  left: 100%;
}

.article-item:hover {
  border-color: var(--primary-color);
  background: rgba(26, 26, 46, 0.6);
  transform: translateY(-1px);
  box-shadow: 
    0 6px 20px rgba(0, 212, 255, 0.15);
}

.article-main {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}

.article-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.article-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  line-height: 1.4;
}

.title-link {
  color: var(--text-primary);
  text-decoration: none;
  transition: all 0.3s ease;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.title-link:hover {
  color: var(--primary-color);
  text-shadow: 0 0 8px var(--primary-color);
}

.article-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.meta-left {
  display: flex;
  gap: 20px;
  align-items: center;
}

.meta-right {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.article-author,
.article-time {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: var(--text-secondary);
}

.author-icon,
.time-icon {
  font-size: 12px;
  opacity: 0.8;
}

.article-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.like-button {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: rgba(26, 26, 46, 0.6);
  border: 1px solid var(--border-color);
  border-radius: 20px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
}

.like-button:hover {
  border-color: var(--primary-color);
  background: rgba(0, 212, 255, 0.1);
  color: var(--primary-color);
  transform: scale(1.05);
}

.like-button.liked {
  background: linear-gradient(45deg, var(--secondary-color), var(--error-color));
  border-color: var(--secondary-color);
  color: white;
}

.like-button.liked:hover {
  background: linear-gradient(45deg, var(--error-color), var(--secondary-color));
  transform: scale(1.05);
}

.like-icon {
  font-size: 16px;
  transition: transform 0.3s ease;
}

.like-button:hover .like-icon {
  transform: scale(1.2);
}

.like-button.liked .like-icon {
  /* 移除心跳动画以提升性能 */
}

.article-image {
  flex-shrink: 0;
  width: 120px;
  height: 90px;
  border-radius: 8px;
  overflow: hidden;
  position: relative;
  border: 1px solid var(--border-color);
}

.article-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 212, 255, 0.1);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.article-item:hover .article-image img {
  transform: scale(1.1);
}

.article-item:hover .image-overlay {
  opacity: 1;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid var(--border-color);
}

/* 分页器科技风格 */
:deep(.tech-pagination) {
  --el-pagination-bg-color: var(--surface-bg);
  --el-pagination-text-color: var(--text-primary);
  --el-pagination-border-radius: 8px;
}

:deep(.tech-pagination .el-pager li) {
  background: var(--surface-bg) !important;
  border: 1px solid var(--border-color) !important;
  color: var(--text-primary) !important;
  margin: 0 4px !important;
  border-radius: 8px !important;
  min-width: 36px !important;
  height: 36px !important;
  transition: all 0.3s ease !important;
}

:deep(.tech-pagination .el-pager li:hover),
:deep(.tech-pagination .el-pager li.is-active) {
  background: linear-gradient(45deg, var(--primary-color), var(--accent-color)) !important;
  border-color: var(--primary-color) !important;
  color: var(--primary-bg) !important;
  transform: translateY(-2px) !important;
  box-shadow: 0 4px 12px rgba(0, 212, 255, 0.3) !important;
}

:deep(.tech-pagination .btn-prev),
:deep(.tech-pagination .btn-next) {
  background: var(--surface-bg) !important;
  border: 1px solid var(--border-color) !important;
  color: var(--text-primary) !important;
  border-radius: 8px !important;
  transition: all 0.3s ease !important;
}

:deep(.tech-pagination .btn-prev:hover),
:deep(.tech-pagination .btn-next:hover) {
  background: var(--primary-color) !important;
  border-color: var(--primary-color) !important;
  color: var(--primary-bg) !important;
  transform: translateY(-2px) !important;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .tech-article-container {
    width: 100%;
    max-width: 100%;
    padding: 12px;
    margin: 0;
    box-sizing: border-box;
  }
  
  .articles-list {
    width: 100%;
  }
  
  .article-item {
    width: 100%;
    padding: 12px;
    box-sizing: border-box;
  }
  
  .article-main {
    flex-direction: column;
    gap: 16px;
    width: 100%;
  }
  
  .article-content {
    width: 100%;
  }
  
  .article-image {
    width: 100%;
    height: 150px;
  }
  
  .article-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
    width: 100%;
  }
  
  .meta-left {
    gap: 16px;
    width: 100%;
  }
  
  .meta-right {
    gap: 8px;
    width: 100%;
    justify-content: flex-start;
  }
  
  .article-tags {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .tech-article-container {
    padding: 8px;
    border-radius: 8px;
    margin: 0;
    width: 100%;
    min-width: 0;
  }
  
  .article-item {
    padding: 12px;
    border-radius: 8px;
    width: 100%;
    min-width: 0;
  }
  
  .section-title {
    font-size: 16px;
  }
  
  .article-title {
    font-size: 14px;
    line-height: 1.3;
  }
  
  .title-link {
    font-size: 14px;
  }
  
  .meta-left {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
    width: 100%;
  }
  
  .article-author,
  .article-time {
    font-size: 12px;
    width: 100%;
  }
  
  .article-tags {
    width: 100%;
    margin-top: 8px;
  }
  
  .like-button {
    font-size: 12px;
    padding: 4px 8px;
  }
}

@media (max-width: 360px) {
  .tech-article-container {
    padding: 6px;
    margin: 0;
  }
  
  .article-item {
    padding: 8px;
    margin: 0;
  }
  
  .section-title {
    font-size: 14px;
  }
  
  .article-title {
    font-size: 13px;
  }
  
  .article-meta {
    gap: 8px;
  }
}
</style>
