<template>
  <el-card style="width: auto; margin-left: 30px;" shadow="always" v-if="isVisible">
    <div style="display: flex; justify-content: space-between;">
      <div>热门封面</div>
      <div style="cursor: pointer;" @click="toUp">
        <el-icon><Close /></el-icon>
        </div>
    </div>
    <ul style="list-style: none; display: flex; width: 600px; overflow-x: scroll;">
      <li v-for="(image, index) in images" :key="index">
        <img :src="image" alt="" :class="{ selected: image === selectedImage }" @click="selectImage(image)" />
      </li>
    </ul>
  </el-card>

</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useShareStore } from '@/stores/counter';
import { Close } from '@element-plus/icons-vue'
const sharedDataStore = useShareStore();

// 定义图片数组
const images = [
  'api/articlecover/java.png',
  'api/articlecover/mysql.png',
  'api/articlecover/redis.png',
  'api/articlecover/springboot.png',
  'api/articlecover/算法.png',
  'api/articlecover/前端.png'
];

// 定义响应式变量来记录选中的图片
const selectedImage = ref('');
const isVisible = ref(true)
const toUp = ()=>{
  isVisible.value=false
}

// 定义点击事件处理函数
const selectImage = (image: string) => {
  selectedImage.value = image;
  sharedDataStore.postSrc(selectedImage.value)
};

</script>

<style scoped>
li img {
  width: 180px;
  height: 120px;
  margin: 10px;
  border: 2px solid transparent;
  /* 默认无边框 */
}

/* 选中图片的样式 */
.selected {
  border: 2px solid blue;
  /* 选中时添加蓝色边框 */
}
</style>
