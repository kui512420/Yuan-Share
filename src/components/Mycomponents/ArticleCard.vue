<script setup lang="ts">
import { ref } from 'vue';
import { View } from '@element-plus/icons-vue'
const title = ref('用 vue 给女主播写了个工具，榜一大哥爱上了她，她爱上了我')
const tagArray = ref(["springboot", "redis", "vue"])
const imgSrc = ref('')
const isShow = ref(true)
</script>

<template>
  <div>
    <el-card  shadow="hover">
      <h2>{{ title }}</h2>
      <div style="display: flex; justify-content: space-between; align-items: end;">
        <div style="justify-items: left;">
          <div>
            <span>魁魁</span>
            <span style="padding: 5px;">|</span>
            <span>6个小时前</span>
            <span style="padding: 5px;">|</span>
            <span>
              <el-icon>
                <View />
              </el-icon>
              <span style="padding: 5px;">2</span>
            </span>
          </div>
        </div>

        <div style="display: flex; align-items: end;">
          <div v-for="(item, index) in tagArray" :key="index">
            <el-tag type="info">{{ item }}</el-tag>
          </div>
          <img v-show="isShow" :src=imgSrc alt="" width="100" height="80">
        </div>

      </div>

    </el-card>
  </div>
</template>

<style scoped></style>
