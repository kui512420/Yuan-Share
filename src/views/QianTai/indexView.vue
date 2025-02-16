<script setup lang="ts">
import { ref } from 'vue'
import router from '@/router'
import { get } from '@/api/article'
import { signIn } from '@/api/users'
import { ElMessage } from 'element-plus'
import ArticleCard from '@/components/Mycomponents/ArticleCard.vue'
const activeIndex = ref('1')
const data = ref([])
const total = ref(0)
const currentPage1 = ref(1)
const pageSize1 = ref(5)
const goMe = () => {
  router.push('/management')
}
const refreshList = () => {
  get(currentPage1.value, pageSize1.value, 0).then((respon) => {
    data.value = respon.data.data.list
    total.value = respon.data.data.total
  })
}

const signInto = () => {
  signIn().then((respon) => {
    if (respon.data.code == "211") {
      ElMessage.success({
        message: respon.data.msg
      })
    } else {
      ElMessage.error({
        message: respon.data.msg
      })
    }
  })
}
refreshList()
</script>

<template>
  <div style="background-color: rgb(242, 243, 245);">
    <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal" @select="handleSelect">
      <el-menu-item index="1">首页</el-menu-item>
      <el-menu-item index="3">留言</el-menu-item>
      <el-menu-item index="4">分类</el-menu-item>
      <el-menu-item index="2" @click="goMe">后台</el-menu-item>
    </el-menu>
    <el-main>
      <div class="main-content">
        <ArticleCard></ArticleCard>
        <div class="main-right">
          <el-card>
            <div>
              <h3>晚上好</h3>
              <span>点亮在社区的每一天</span>
            </div>
            <el-button type="primary" plain @click="signInto">签到</el-button>
          </el-card>
          <el-card>
            <div>
              <span>邮箱：kui512420@163.com</span>
            </div>
            <div>
              <span><a href="https://github.com/kui512420/Vue-management">github：kui512420</a></span>
            </div>
            <div>
              <a href="https://beian.miit.gov.cn">豫ICP备2024077864号</a>
            </div>
          </el-card>
        </div>
      </div>
    </el-main>
  </div>

</template>

<style scoped>
.main-content {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.main-right {
  flex-grow: 1;
  border: 1px solid red;
  padding: 20px;
}

.demo-pagination-block {
  margin-top: 10px;
}

@media screen and (max-width: 600px) {
  .main-content {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }

  .isShowTime {
    display: none;
  }

  .isShowImg {
    display: none;
  }
}
</style>
