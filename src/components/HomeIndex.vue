<script lang="ts" setup>
import { ref } from 'vue'
import { useTransition } from '@vueuse/core'
import { getCount } from '@/api/home'
const user = ref(0)
const notice = ref(0)
const article = ref(0)
const info = ref(0)

const userValue = useTransition(user, {
  duration: 1000,
})
const noticeValue = useTransition(notice, {
  duration: 1000,
})
const articleValue = useTransition(article, {
  duration: 1000,
})
const infoValue = useTransition(info, {
  duration: 1000,
})

getCount().then((respon) => {
  user.value = respon.data.data.user
  notice.value = respon.data.data.notice
  article.value = respon.data.data.article
  info.value = respon.data.data.info
})

</script>

<template>
  <div style="display: flex; justify-content: space-between;">
    <div>
      <el-card style="width: 280px" shadow="always">
        <template #header>
          <h2 style="text-align: center;">用户数</h2>
        </template>
        <el-statistic style="text-align: center;" :value="userValue" />
      </el-card>
    </div>

    <div>
      <el-card style="width: 280px" shadow="always">
        <template #header>
          <h2 style="text-align: center;">文章数</h2>
        </template>
        <el-statistic style="text-align: center;" :value="articleValue" />
      </el-card>
    </div>

    <div>
      <el-card style="width: 280px" shadow="always">
        <template #header>
          <h2 style="text-align: center;">留言数</h2>
        </template>
        <el-statistic style="text-align: center;" :value="infoValue" />
      </el-card>
    </div>

    <div>
      <el-card style="width: 280px" shadow="always">
        <template #header>
          <h2 style="text-align: center;">公告数</h2>
        </template>
        <el-statistic style="text-align: center;" :value="noticeValue" />
      </el-card>
    </div>
  </div>


  <div>
    <el-card style="width: 280px" shadow="always">
      <template #header>
        <h2 style="text-align: center;">相关技术栈</h2>
      </template>
      <ul>
        <li>前端框架：<a target="_blank" href="https://cn.vuejs.org/">Vue3</a></li>
        <li>后端框架：<a target="_blank" href="https://spring.io/projects/spring-boot/">Springboot</a></li>
        <li>数据库：
          <a target="_blank" href="https://jwt.io/">Mysql</a>、
          <a target="_blank" href="https://jwt.io/">Redis</a>
        </li>
        <li>markdown解析：<a target="_blank" href="https://code-farmer-i.github.io/vue-markdown-editor/zh/">v-md-editor</a></li>
        <li>UI库：<a target="_blank" href="https://element-plus.org/zh-CN/">element-plus</a></li>
        <li>身份校验：<a target="_blank" href="https://jwt.io/">JWT</a></li>
      </ul>
    </el-card>
  </div>

</template>


<style scoped></style>
o
