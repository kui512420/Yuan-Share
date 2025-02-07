<script lang="ts" setup>
import Aside from '@/views/AsideView.vue'
import Header from '@/views/HeaderView.vue'
import Main from '@/views/MainView.vue'
</script>

<template>
  <div class="common-layout">
    <el-container>
      <el-header><Header></Header></el-header>
      <el-container>
        <el-aside width="200px"><Aside></Aside></el-aside>
        <el-main><Main></Main></el-main>
      </el-container>
    </el-container>
  </div>
</template>


<style scoped>
.el-header{
  --el-header-height: 40px;
}
.el-main{
  --el-main-padding: 10px;
}
</style>
