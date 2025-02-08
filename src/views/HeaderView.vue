<template>
  <div class="wrapper">
    <div class="left">
      <strong>博客管理系统</strong>
      <el-icon size="30" style="margin-left: 90px;" @click="changec">
        <Fold />
      </el-icon>
    </div>
    <div class="center">
      云想衣裳花想容，春风拂槛露华浓。欢迎***
    </div>
  </div>
</template>

<script lang="ts" setup>
import { Fold } from '@element-plus/icons-vue';
import { useShareStore } from '@/stores/counter';

// 获取共享状态仓库实例
const shareStore = useShareStore();

// 定义切换侧边栏收缩状态的方法
const changec = () => {
  shareStore.changeCollapse();
};
</script>

<style scoped>
.wrapper {
  width: 100%;
  border-bottom: 1px solid gray;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
}

.left {
  display: flex;
  align-items: center;
}

.el-icon {
  cursor: pointer;
}
</style>
