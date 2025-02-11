<script lang="ts" setup>
import Aside from '@/views/Management/AsideView.vue'
import Header from '@/views/Management/HeaderView.vue'
import Main from '@/views/Management/MainView.vue'
import { useShareStore } from '@/stores/counter';
import { computed } from 'vue';

// 获取共享状态仓库实例
const shareStore = useShareStore();

// 通过计算属性获取侧边栏的收缩状态
const isCollapse = computed(() => shareStore.isCollapse);
</script>

<template>
  <div class="common-layout" style="height: calc(100vh); overflow: hidden;">
    <el-container>
      <el-header>
        <Header></Header>
      </el-header>
      <el-container>
        <el-aside :width="isCollapse ? '60px' : '200px'">
          <Aside></Aside>
        </el-aside>
        <el-main>
          <Main></Main>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>


<style scoped>
.el-main {
  --el-main-padding: 30px;
}

.el-aside {
  transition: width 0.3s ease;
  padding: 10px;
  overflow: hidden
}

/* 自定义滚动条样式 */
.el-main::-webkit-scrollbar {
  width: 8px;
  /* 滚动条宽度 */
}

/* 滚动条轨道 */
.el-main::-webkit-scrollbar-track {
  background: #f1f1f1;
  /* 轨道背景颜色 */
  border-radius: 4px;
  /* 轨道圆角 */
}

/* 滚动条滑块 */
.el-main::-webkit-scrollbar-thumb {
  background: #888;
  /* 滑块背景颜色 */
  border-radius: 4px;
  /* 滑块圆角 */
}

/* 鼠标悬停在滑块上的样式 */
.el-main::-webkit-scrollbar-thumb:hover {
  background: #555;
  /* 悬停时滑块背景颜色 */
}

/* Firefox 滚动条样式 */
.el-main {
  scrollbar-width: thin;
  /* 滚动条宽度：thin 细，auto 正常 */
  scrollbar-color: #888 #f1f1f1;
  /* 滑块颜色 轨道颜色 */
}
</style>
