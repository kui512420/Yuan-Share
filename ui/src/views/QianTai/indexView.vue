<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router';
import { signIns } from '@/api/users'
import { getTop } from "@/api/article"
import { convertDate } from '@/utils/DateUntil'
import { ElMessage } from 'element-plus'
import ArticleCard from '@/components/Mycomponents/ArticleCard.vue'
import MessageBoard from '@/components/Mycomponents/MessageBoard.vue'
import ToolsList from '@/components/Mycomponents/ToolsList.vue'
import ChatWindow from '@/components/Management/AiComponents/ChatWindow.vue'
import ThemeSwitcher from '@/components/ThemeSwitcher.vue'

const activeIndex = ref('1')
const router = useRouter();
const searchData = ref('')
const data = ref()
const signbtn = ref('签到')
const goMe = () => {
  router.push('/management')
}
const signInto = () => {
  signIns().then((respon) => {
    if (respon.data.code == "211") {
      ElMessage.success({
        message: respon.data.msg[0]
      })
    } else if (respon.data.code == "210") {
      ElMessage.error({
        message: respon.data.msg[0]
      })
      signbtn.value = respon.data.msg[0]
    } else {
      router.push('/management')
      ElMessage.error({
        message: "未登录,正在前往登录"
      })
    }
  })
}
const getTops = () => {
  getTop().then((res) => {
    data.value = res.data.data
  })
}
getTops()
const datato = () => {
  // 创建指定日期的 Date 对象
  const targetDate = new Date('2024-07-08');
  // 获取当前日期的 Date 对象
  const currentDate = new Date();
  // 获取两个日期的时间戳（毫秒数）
  const targetTimestamp = targetDate.getTime();
  const currentTimestamp = currentDate.getTime();
  // 计算时间差（毫秒数）
  const timeDifference = currentTimestamp - targetTimestamp;
  // 将时间差转换为天数
  const daysDifference = Math.floor(timeDifference / (24 * 60 * 60 * 1000));
  return daysDifference;
};
const dateInfo = computed(() => {
  const hour: number = new Date().getHours()
  if (hour >= 5 && hour <= 11) {
    return '早上好'
  } else if (hour > 11 && hour <= 13) {
    return '中午好'
  } else if (hour > 13 && hour <= 18) {
    return '下午好'
  } else if (hour > 18 && hour <= 24) {
    return '晚上好'
  } else {
    return '凌晨啦'
  }
})
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

</script>

<template>
  <div class="tech-container">
    <ChatWindow></ChatWindow>
    
    <!-- 科技风格导航栏 -->
    <div class="tech-header">
      <div class="tech-nav glass-morphism">
        <!-- 网站名称/Logo区域 -->
        <div class="site-brand">
          <div class="site-logo">
            <span class="logo-icon">🚀</span>
            <h1 class="site-name neon-text">KUIKUI SPACE</h1>
          </div>
          <div class="site-description">科技创新 · 共享未来</div>
        </div>
        
        <div class="nav-items">
          <div class="nav-item" :class="{ active: activeIndex === '1' }" @click="activeIndex = '1'">
            <span class="nav-icon">🏠</span>
            <span>首页</span>
          </div>
          <div class="nav-item" :class="{ active: activeIndex === '2' }" @click="activeIndex = '2'">
            <span class="nav-icon">💬</span>
            <span>留言</span>
          </div>
          <div class="nav-item" :class="{ active: activeIndex === '3' }" @click="activeIndex = '3'">
            <span class="nav-icon">🛒</span>
            <span>商城</span>
          </div>
          <div class="nav-item" @click="goMe">
            <span class="nav-icon">⚙️</span>
            <span>后台</span>
          </div>
        </div>
        <div class="nav-controls">
          <div class="search-container">
            <el-input 
              v-model="searchData" 
              class="tech-search"
              placeholder="搜索关键字..." 
              size="large"
              clearable
            >
              <template #prefix>
                <span class="search-icon">🔍</span>
              </template>
            </el-input>
          </div>
          
          <!-- 主题切换器 -->
          <ThemeSwitcher :show-quick-toggle="true" />
        </div>
      </div>
    </div>

    <!-- 主内容区域 -->
    <el-main class="tech-main">
      <div class="main-content">
        <!-- 左侧内容区 -->
        <div class="content-left">
          <!-- 置顶文章卡片 - 顶部区域 -->
          <div v-if="activeIndex == '1'" class="featured-card top-section">
            <div class="card-header">
              <h4 class="card-title">🔥 置顶文章</h4>
            </div>
            <div class="featured-articles-grid">
              <div v-for="(item, index) in data" :key="index" class="featured-article-horizontal">
                <div class="article-cover-featured">
                  <img :src="item.article_cover" alt="文章封面" />
                </div>
                <div class="article-content-featured">
                  <h5 class="article-title-featured">
                    <router-link :to="`/article/${item.article_id}`">
                      {{ item.article_title }}
                    </router-link>
                  </h5>
                  <div class="article-meta-featured">
                    <span class="article-author">{{ item.article_author }}</span>
                    <span class="article-time">{{ PushTime(convertDate(item.publish_time)) }}</span>
                  </div>
                  <div class="article-tags-featured">
                    <span v-for="(tag, tagIndex) in JSON.parse(item.article_tag)" :key="tagIndex" class="tech-tag">
                      {{ tag }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 普通文章内容 -->
          <ArticleCard v-if="activeIndex == '1'" :searchData="searchData" class="tech-card"></ArticleCard>
          <MessageBoard v-else-if="activeIndex == '2'" class="tech-card message-board"></MessageBoard>
          <ToolsList v-else-if="activeIndex == '3'" class="tech-card"></ToolsList>
        </div>
        
        <!-- 右侧边栏 -->
        <div class="main-right">
          <!-- 问候卡片 -->
          <div class="greeting-card tech-card">
            <div class="greeting-content">
              <div class="greeting-text">
                <h3 class="neon-text">{{ dateInfo }}</h3>
                <p class="greeting-subtitle">热爱，让我们一同去追寻吧</p>
              </div>
              <div class="greeting-action">
                <button class="tech-button pulse" @click="signInto">{{ signbtn }}</button>
              </div>
            </div>
          </div>

          <!-- 联系信息卡片 -->
          <div class="contact-card tech-card">
            <div class="card-header">
              <h4 class="card-title">联系方式</h4>
            </div>
            <div class="contact-list">
              <div class="contact-item">
                <span class="contact-icon">📧</span>
                <span class="contact-text">kui512420@163.com</span>
              </div>
              <div class="contact-item">
                <span class="contact-icon">🔗</span>
                <a href="https://github.com/kui512420/Vue-management" target="_blank" class="contact-link">
                  GitHub: kui512420
                </a>
              </div>
              <div class="contact-item">
                <span class="contact-icon">⚖️</span>
                <a href="http://oj.kuikui.space" target="_blank" class="contact-link">
                  KUIKUIOJ 判题平台
                </a>
              </div>
              <div class="contact-item">
                <span class="contact-icon">📊</span>
                <a href="http://zb.kuikui.space" target="_blank" class="contact-link">
                  KUIKUI 周报
                </a>
              </div>
              <div class="runtime-info">
                <span class="runtime-label">运行时间</span>
                <span class="runtime-value neon-text">{{ datato() }} 天</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-main>

    <!-- 科技风格页脚 -->
    <footer class="tech-footer">
      <div class="footer-content">
        <div class="footer-divider"></div>
        <div class="footer-text">
          <a href="https://beian.miit.gov.cn" target="_blank">豫ICP备2024077864号</a>
        </div>
      </div>
    </footer>
  </div>
</template>

<style scoped>
/* 整体容器 */
.tech-container {
  min-height: 100vh;
  background: transparent;
  color: var(--text-primary);
}

/* 科技风格导航栏 */
.tech-header {
  position: sticky;
  top: 0;
  z-index: 100;
  padding: 20px;
  backdrop-filter: blur(20px);
}

.tech-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  margin: 0 auto;
  max-width: 1400px;
  border: 1px solid var(--border-color);
  gap: 32px;
}

/* 网站品牌区域 */
.site-brand {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 200px;
}

.site-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.site-logo:hover {
  transform: translateY(-1px);
}

.logo-icon {
  font-size: 28px;
  filter: drop-shadow(0 0 10px var(--primary-color));
  animation: logoGlow 3s ease-in-out infinite;
}

@keyframes logoGlow {
  0%, 100% { 
    filter: drop-shadow(0 0 10px var(--primary-color)); 
    transform: scale(1);
  }
  50% { 
    filter: drop-shadow(0 0 20px var(--primary-color)); 
    transform: scale(1.05);
  }
}

.site-name {
  font-size: 22px;
  font-weight: 700;
  margin: 0;
  background: linear-gradient(45deg, var(--primary-color), var(--accent-color));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 0 20px rgba(0, 212, 255, 0.5);
  font-family: var(--font-mono);
  letter-spacing: 1px;
}

.site-description {
  color: var(--text-secondary);
  font-size: 12px;
  margin-left: 40px;
  font-family: var(--font-mono);
  opacity: 0.8;
  position: relative;
}

.site-description::before {
  content: '>';
  color: var(--primary-color);
  margin-right: 6px;
  animation: blink 2s infinite;
}

@keyframes blink {
  0%, 50% { opacity: 1; }
  51%, 100% { opacity: 0; }
}

.nav-items {
  display: flex;
  gap: 32px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.nav-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(0, 212, 255, 0.1), transparent);
  transition: left 0.5s;
}

.nav-item:hover::before,
.nav-item.active::before {
  left: 100%;
}

.nav-item:hover,
.nav-item.active {
  background: rgba(0, 212, 255, 0.1);
  color: var(--primary-color);
  border: 1px solid var(--border-color);
}

.nav-icon {
  font-size: 18px;
}

.nav-controls {
  display: flex;
  align-items: center;
  gap: 16px;
}

.search-container {
  position: relative;
}

.tech-search {
  width: 280px;
}

.tech-search :deep(.el-input__wrapper) {
  background: rgba(26, 26, 46, 0.6);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  transition: all 0.3s ease;
}

.tech-search :deep(.el-input__wrapper:hover),
.tech-search :deep(.el-input__wrapper.is-focus) {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(0, 212, 255, 0.2);
}

.tech-search :deep(.el-input__inner) {
  color: var(--text-primary);
  font-family: var(--font-mono);
}

.search-icon {
  color: var(--text-secondary);
  font-size: 16px;
}

/* 主内容区域 */
.tech-main {
  padding: 0 20px 40px;
  max-width: 1400px;
  margin: 0 auto;
}

.main-content {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.content-left {
  flex: 2;
  min-width: 0;
}

.main-right {
  flex: 1;
  min-width: 320px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 问候卡片 */
.greeting-card {
  padding: 24px;
}

.greeting-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.greeting-text h3 {
  font-size: 24px;
  margin-bottom: 8px;
}

.greeting-subtitle {
  color: var(--text-secondary);
  font-size: 14px;
}

.greeting-action {
  margin-left: 20px;
}

/* 联系信息卡片 */
.contact-card {
  padding: 24px;
}

.card-header {
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--border-color);
}

.card-title {
  color: var(--text-primary);
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.contact-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}

.contact-icon {
  font-size: 16px;
  width: 20px;
  text-align: center;
}

.contact-text {
  color: var(--text-secondary);
  font-size: 14px;
}

.contact-link {
  color: var(--primary-color);
  font-size: 14px;
  transition: all 0.3s ease;
}

.contact-link:hover {
  color: var(--accent-color);
  text-shadow: 0 0 8px var(--accent-color);
}

.runtime-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: rgba(0, 212, 255, 0.05);
  border-radius: 8px;
  border: 1px solid var(--border-color);
  margin-top: 8px;
}

.runtime-label {
  color: var(--text-secondary);
  font-size: 14px;
}

.runtime-value {
  font-size: 16px;
  font-weight: 600;
}

/* 置顶文章卡片 - 顶部区域 */
.top-section {
  margin-bottom: 32px;
  position: relative;
  background: linear-gradient(135deg, 
    rgba(0, 212, 255, 0.03), 
    rgba(26, 26, 46, 0.8), 
    rgba(78, 205, 196, 0.03)
  );
  border: 2px solid rgba(0, 212, 255, 0.3);
  overflow: hidden;
  will-change: auto;
}

.top-section::before {
  content: '';
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  background: linear-gradient(45deg, 
    var(--primary-color), 
    var(--accent-color), 
    var(--primary-color)
  );
  z-index: -1;
  border-radius: 12px;
  animation: borderGlow 6s ease-in-out infinite;
}

@keyframes borderGlow {
  0%, 100% {
    opacity: 0.2;
  }
  50% {
    opacity: 0.5;
  }
}

.top-section .card-header {
  background: rgba(0, 212, 255, 0.08);
  margin: -24px -24px 20px -24px;
  padding: 20px 24px;
  border-bottom: 2px solid var(--border-color);
  position: relative;
}

.top-section .card-title {
  font-size: 20px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 1px;
  background: linear-gradient(45deg, var(--primary-color), var(--accent-color));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.featured-card {
  padding: 24px;
}

.featured-articles-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
}

.featured-article-horizontal {
  display: flex;
  gap: 16px;
  padding: 20px;
  background: rgba(26, 26, 46, 0.4);
  border-radius: 12px;
  border: 1px solid rgba(0, 212, 255, 0.2);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.featured-article-horizontal::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(0, 212, 255, 0.08), transparent);
  transition: left 0.8s;
}

.featured-article-horizontal:hover::before {
  left: 100%;
}

.featured-article-horizontal:hover {
  border-color: var(--primary-color);
  background: rgba(26, 26, 46, 0.6);
  transform: translateY(-2px);
  box-shadow: 
    0 8px 24px rgba(0, 212, 255, 0.15);
}

.article-cover-featured {
  flex-shrink: 0;
  width: 120px;
  height: 90px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid var(--border-color);
}

.article-cover-featured img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.featured-article-horizontal:hover .article-cover-featured img {
  transform: scale(1.1);
}

.article-content-featured {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.article-title-featured {
  margin: 0;
}

.article-title-featured a {
  color: var(--text-primary);
  font-size: 18px;
  font-weight: 600;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: all 0.3s ease;
}

.article-title-featured a:hover {
  color: var(--primary-color);
  text-shadow: 0 0 8px var(--primary-color);
}

.article-meta-featured {
  display: flex;
  gap: 16px;
  align-items: center;
  font-size: 13px;
  color: var(--text-muted);
}

.article-tags-featured {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 4px;
}

/* 留言板样式 */
.message-board {
  max-height: 80vh;
  overflow-y: auto;
  padding: 24px;
}

/* 页脚 */
.tech-footer {
  margin-top: 40px;
  padding: 24px 20px;
}

.footer-content {
  max-width: 1400px;
  margin: 0 auto;
  text-align: center;
}

.footer-divider {
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--border-color), transparent);
  margin-bottom: 20px;
}

.footer-text {
  color: var(--text-muted);
  font-size: 14px;
}

.footer-text a {
  color: var(--primary-color);
  transition: all 0.3s ease;
}

.footer-text a:hover {
  color: var(--accent-color);
}

/* 响应式设计 */
@media (max-width: 900px) {
  .main-content {
    flex-direction: column;
    gap: 20px;
  }
  
  .content-left {
    width: 100%;
    min-width: 0;
    flex: none;
  }
  
  .main-right {
    width: 100%;
    min-width: auto;
    flex: none;
  }
  
  /* 导航栏调整 */
  .tech-nav {
    gap: 20px;
  }
  
  .site-brand {
    min-width: 160px;
  }
  
  .site-name {
    font-size: 18px;
  }
  
  .site-description {
    font-size: 11px;
  }
  
  .nav-controls {
    gap: 12px;
  }
  
  .tech-search {
    width: 220px;
  }
}

@media (max-width: 650px) {
  .tech-main {
    padding: 0 12px 24px;
  }
  
  .main-content {
    gap: 16px;
  }
  
  .content-left {
    width: 100%;
    padding: 0;
    margin: 0;
  }
  
  .tech-nav {
    flex-direction: column;
    gap: 16px;
    padding: 16px;
  }
  
  /* 网站名称在小屏幕上的优化 */
  .site-brand {
    min-width: auto;
    align-items: center;
    text-align: center;
  }
  
  .site-logo {
    justify-content: center;
  }
  
  .site-name {
    font-size: 20px;
  }
  
  .site-description {
    margin-left: 0;
    text-align: center;
  }
  
  .nav-items {
    gap: 16px;
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .nav-controls {
    width: 100%;
    justify-content: center;
    gap: 12px;
  }
  
  .tech-search {
    width: 100%;
    max-width: 240px;
  }
  
  .greeting-content {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  
  .greeting-action {
    margin-left: 0;
  }
  
  /* 置顶文章区域响应式 */
  .top-section {
    margin-bottom: 20px;
    padding: 16px;
  }
  
  .featured-articles-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .featured-article-horizontal {
    flex-direction: column;
    padding: 16px;
  }
  
  .article-cover-featured {
    width: 100%;
    height: 150px;
    margin-bottom: 8px;
  }
  
  .top-section .card-header {
    margin: -16px -16px 16px -16px;
    padding: 16px;
  }
  
  .top-section .card-title {
    font-size: 18px;
  }
}

@media (max-width: 420px) {
  .tech-header {
    padding: 12px;
  }
  
  .tech-main {
    padding: 0 8px 20px;
  }
  
  .main-content {
    gap: 12px;
  }
  
  .content-left {
    width: 100%;
    min-width: 0;
  }
  
  /* 超小屏幕网站名称优化 */
  .site-name {
    font-size: 16px;
    letter-spacing: 0.5px;
  }
  
  .site-description {
    font-size: 10px;
  }
  
  .logo-icon {
    font-size: 24px;
  }
  
  .nav-items {
    gap: 8px;
  }
  
  .nav-item {
    padding: 8px 12px;
    font-size: 14px;
  }
  
  .nav-icon {
    font-size: 16px;
  }
  
  /* 小屏幕置顶文章优化 */
  .top-section {
    margin-bottom: 24px;
  }
  
  .featured-article-horizontal {
    padding: 12px;
  }
  
  .article-title-featured a {
    font-size: 16px;
  }
  
  .article-meta-featured {
    font-size: 12px;
    gap: 12px;
  }
}

@media (max-width: 360px) {
  .tech-header {
    padding: 8px;
  }
  
  .tech-main {
    padding: 0 4px 16px;
  }
  
  .main-content {
    gap: 8px;
  }
  
  .content-left {
    width: 100%;
    min-width: 0;
    padding: 0;
    margin: 0;
  }
  
  .site-name {
    font-size: 14px;
    letter-spacing: 0.3px;
  }
  
  .site-description {
    font-size: 9px;
  }
  
  .logo-icon {
    font-size: 20px;
  }
  
  .nav-items {
    gap: 6px;
  }
  
  .nav-item {
    padding: 6px 10px;
    font-size: 13px;
  }
  
  .nav-icon {
    font-size: 14px;
  }
  
  .top-section {
    margin-bottom: 16px;
    padding: 12px;
  }
  
  .top-section .card-header {
    margin: -12px -12px 12px -12px;
    padding: 12px;
  }
  
  .top-section .card-title {
    font-size: 16px;
  }
  
  .featured-article-horizontal {
    padding: 8px;
  }
  
  .article-title-featured a {
    font-size: 14px;
  }
  
  .article-meta-featured {
    font-size: 11px;
    gap: 8px;
  }
}

/* 为中等屏幕添加更细致的控制 */
@media (min-width: 901px) and (max-width: 1100px) {
  .main-right {
    min-width: 280px;
  }
  
  .tech-search {
    width: 250px;
  }
  
  .featured-articles-grid {
    grid-template-columns: 1fr;
  }
}

/* 确保大屏幕下的最佳体验 */
@media (min-width: 1400px) {
  .main-content {
    max-width: 1600px;
  }
  
  .main-right {
    min-width: 350px;
  }
  
  .featured-articles-grid {
    grid-template-columns: repeat(auto-fit, minmax(450px, 1fr));
  }
}
</style>
