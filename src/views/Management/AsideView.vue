<template>

  <div class="wrapper">

    <el-menu default-active="2" class="el-menu-vertical-demo" :collapse="isCollapse" @open="handleOpen"
      @close="handleClose" :unique-opened=true>

      <el-menu-item index="7" @click="navigateTo('management/home/index')">
        <el-icon>
          <House />
        </el-icon>
        <template #title>

          <span>首页</span>
        </template>
      </el-menu-item>

      <el-sub-menu index="1">
        <template #title>
          <el-icon>
            <User />
          </el-icon>
          <span>用户管理</span>
        </template>
        <el-menu-item-group>
          <el-menu-item index="1-1" @click="navigateTo('management/home/User/UserList')">用户列表</el-menu-item>
        </el-menu-item-group>
      </el-sub-menu>

      <el-sub-menu index="2">
        <template #title>
          <el-icon>
            <Memo />
          </el-icon>
          <span>文章管理</span>
        </template>
        <el-menu-item-group>
          <el-menu-item index="2-1" @click="navigateTo('management/home/Article/ArticleList')">文章列表</el-menu-item>
        </el-menu-item-group>
      </el-sub-menu>

      <el-sub-menu index="3">
        <template #title>
          <el-icon>
            <Document />
          </el-icon>
          <span>日志管理</span>
        </template>
        <el-menu-item-group>
          <el-menu-item index="3-1" @click="navigateTo('management/home/LoginLog')">登录日志</el-menu-item>
          <el-menu-item index="3-2" @click="navigateTo('management/home/SigninLog')">签到日志</el-menu-item>
        </el-menu-item-group>
      </el-sub-menu>
      <el-menu-item index="4">
        <el-icon>
          <Message />
        </el-icon>
        <template #title>
          留言管理
        </template>
      </el-menu-item>

      <el-menu-item index="5">
        <el-icon>
          <Bell />
        </el-icon>
        <template #title>
          公告管理
        </template>
      </el-menu-item>

      <el-menu-item index="6">
        <el-icon>
          <Setting />
        </el-icon>
        <template #title>
          设置
        </template>
      </el-menu-item>

    </el-menu>

  </div>

</template>

<script lang="ts" setup>
import { computed } from 'vue'
import {
  Setting,
  Bell,
  User,
  Memo,
  Message,
  Document,
  House,

} from '@element-plus/icons-vue'
import { useRouter } from 'vue-router';
import { useShareStore } from '@/stores/counter';
const sharedDataStore = useShareStore();

const isCollapse = computed(() => sharedDataStore.isCollapse);
const router = useRouter();
/**
 *
 *
 *
 *
 *
 */
const handleOpen = () => {

}
const handleClose = () => {

}

const navigateTo = (path: string) => {
  router.push(`/${path}`);
};

</script>

<style scoped>
.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 200px;
  min-height: 400px;
}

.el-menu--collapse {
  width: 60px;
  /* 设置收缩状态下侧边栏的宽度 */
}
</style>
