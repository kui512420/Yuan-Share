<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router';
import { signIn } from '@/api/users'
import { Search} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import ArticleCard from '@/components/Mycomponents/ArticleCard.vue'
import MessageBoard from '@/components/Mycomponents/MessageBoard.vue'
import ToolsList from '@/components/Mycomponents/ToolsList.vue'
const activeIndex = ref('1')
const currentPage1 = ref(1)
const pageSize1 = ref(5)
const router = useRouter();
const searchData = ref('')
const goMe = () => {
  router.push('/management')
}
const signInto = () => {
  signIn(currentPage1.value, pageSize1.value).then((respon) => {
    if (respon.data.code == "211") {
      ElMessage.success({
        message: respon.data.msg
      })
    } else if (respon.data.code == "210") {
      ElMessage.error({
        message: respon.data.msg
      })
    } else {
      router.push('/management')
      ElMessage.error({
        message: "未登录,正在前往登录"
      })
    }
  })
}
const dateInfo = computed(()=>{
  const hour:number = new Date().getHours()
  if(hour>=5 && hour<=11){
    return '早上好'
  }else if(hour>11 && hour<=13){
    return '中午好'
  }else if(hour>13 && hour<=18){
    return '下午好'
  }else if(hour>18 && hour<=24){
    return '晚上好'
  }else {
    return '凌晨啦'
  }
})

</script>

<template>
  <div style="background-color: rgb(242, 243, 245); height: 100vh;">
    <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal">
      <el-menu-item index="1" @click="activeIndex = '1'">首页</el-menu-item>
      <el-menu-item index="2" @click="activeIndex = '2'">留言</el-menu-item>
      <el-menu-item index="3" @click="activeIndex = '3'">工具</el-menu-item>
      <el-menu-item index="4" @click="goMe">后台</el-menu-item>
      <el-input
      size="small"
      v-model="searchData"
      style="width: 240px"
      placeholder="关键字搜索"
    /><el-button :icon="Search" style="height: 100%;" />
    </el-menu>
    <el-main>
      <div class="main-content">
        <ArticleCard v-if="activeIndex == '1'"></ArticleCard>
        <MessageBoard v-else-if="activeIndex == '2'"></MessageBoard>
        <ToolsList v-else-if="activeIndex == '3'"></ToolsList>
        <div class="main-right">
          <el-card style="margin-bottom: 30px;">
            <div>
              <h3>{{ dateInfo }}</h3>
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
