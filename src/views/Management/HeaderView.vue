<template>
  <div class="wrapper">
    <div class="left">
      <strong>博客管理系统</strong>
      <a href="/" style="text-decoration: none;">去前台</a>
      <el-icon size="30" style="margin-left: 90px;" @click="changec">
        <Fold />
      </el-icon>
      <el-breadcrumb separator="/">
      <el-breadcrumb-item v-for="(item, index) in breadcrumbList" :key="index" :to="item.to">
        {{ item.name }}
      </el-breadcrumb-item>
    </el-breadcrumb>
    </div>
    <div class="center">
      <span>云想衣裳花想容，春风拂槛露华浓。欢迎</span>
      <span>|<strong>{{ nickname }}</strong></span>
      <span>|<strong>类型：{{ type }}</strong></span>
      <el-avatar :src="headImg" />
      <el-switch v-model="isDark" inline-prompt active-text="dark" size="large" inactive-text="light"
        @change="toggleDark"></el-switch>
        <el-button @click="outLogin">退出登录</el-button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref,watchEffect  } from 'vue'
import { useRoute } from 'vue-router';
import { Fold } from '@element-plus/icons-vue';
import { useShareStore } from '@/stores/counter';
import { getUserInfo } from '@/api/home'
import { useDark, useToggle } from '@vueuse/core'
import breadcrumbConfig from '@/router/breadcrumbConfig'
const isDark = useDark();
const toggleDark = useToggle(isDark);

const nickname = ref('')
const type = ref('')
const headImg = ref('')
const route = useRoute();
// 获取共享状态仓库实例
const shareStore = useShareStore();

// 定义切换侧边栏收缩状态的方法
const changec = () => {
  shareStore.changeCollapse();
};
const outLogin = ()=>{
  window.localStorage.removeItem('token')
  window.location.reload()
}
const getUserIn = () => {
  getUserInfo().then((respon) => {
    nickname.value = respon.data.data.nickname
    headImg.value = respon.data.data.headsrc + "?t=" + new Date().getTime()
    if (respon.data.data.type == 0) {
      type.value = "管理员"
    } else {
      type.value = "普通用户"
    }
  })
}
getUserIn()
const breadcrumbList = ref<{ name: string; to?: { path: string } }[]>([]);
watchEffect(() => {
  const currentPath = route.path;
  breadcrumbList.value = breadcrumbConfig[currentPath]  || [];
});
</script>

<style scoped>
.wrapper {
  width: 100%;
  border-bottom: 1px solid gray;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  padding-bottom: 0;
}

.left {
  display: flex;
  align-items: center;
}

.el-icon {
  cursor: pointer;
}
</style>
