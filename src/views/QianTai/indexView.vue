<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router';
import { signIns } from '@/api/users'
import { getTop } from "@/api/article"
import { convertDate } from '@/utils/DateUntil'
import { ElMessage } from 'element-plus'
import ArticleCard from '@/components/Mycomponents/ArticleCard.vue'
import MessageBoard from '@/components/Mycomponents/MessageBoard.vue'
import ToolsList from '@/components/Mycomponents/ToolsList.vue'
import ChatWindow from '@/components/Management/AiComponents/ChatWindow.vue'
const activeIndex = ref('1')
const router = useRouter();
const searchData = ref('')
const data = ref()
const signbtn = ref('签到')
const goMe = () => {
  router.push('/management')
}
const signInto = () => {
  signIns().then((respon) => {
    if (respon.data.code == "211") {
      ElMessage.success({
        message: respon.data.msg[0]
      })
    } else if (respon.data.code == "210") {
      ElMessage.error({
        message: respon.data.msg[0]
      })
      signbtn.value = respon.data.msg[0]
    } else {
      router.push('/management')
      ElMessage.error({
        message: "未登录,正在前往登录"
      })
    }
  })
}
const getTops = () => {
  getTop().then((res) => {
    data.value = res.data.data
  })
}
getTops()
const datato = () => {
  // 创建指定日期的 Date 对象
  const targetDate = new Date('2024-07-08');
  // 获取当前日期的 Date 对象
  const currentDate = new Date();
  // 获取两个日期的时间戳（毫秒数）
  const targetTimestamp = targetDate.getTime();
  const currentTimestamp = currentDate.getTime();
  // 计算时间差（毫秒数）
  const timeDifference = currentTimestamp - targetTimestamp;
  // 将时间差转换为天数
  const daysDifference = Math.floor(timeDifference / (24 * 60 * 60 * 1000));
  return daysDifference;
};
const dateInfo = computed(() => {
  const hour: number = new Date().getHours()
  if (hour >= 5 && hour <= 11) {
    return '早上好'
  } else if (hour > 11 && hour <= 13) {
    return '中午好'
  } else if (hour > 13 && hour <= 18) {
    return '下午好'
  } else if (hour > 18 && hour <= 24) {
    return '晚上好'
  } else {
    return '凌晨啦'
  }
})
const PushTime = (time: string) => {
  // 将传入的时间字符串转换为 Date 对象
  const inputTime = new Date(time).getTime();
  // 获取当前时间
  const currentTime = new Date().getTime();
  // 计算两个时间的时间差（单位：毫秒）
  const diff = currentTime - inputTime;

  // 定义不同时间单位对应的毫秒数
  const second = 1000;
  const minute = second * 60;
  const hour = minute * 60;
  const day = hour * 24;
  const month = day * 30;
  const year = day * 365;

  if (diff < second) {
    return "刚刚";
  } else if (diff < minute) {
    // 计算相差的秒数
    const seconds = Math.floor(diff / second);
    return `${seconds} 秒前`;
  } else if (diff < hour) {
    // 计算相差的分钟数
    const minutes = Math.floor(diff / minute);
    return `${minutes} 分钟前`;
  } else if (diff < day) {
    // 计算相差的小时数
    const hours = Math.floor(diff / hour);
    return `${hours} 小时前`;
  } else if (diff < month) {
    // 计算相差的天数
    const days = Math.floor(diff / day);
    return `${days} 天前`;
  } else if (diff < year) {
    // 计算相差的月数
    const months = Math.floor(diff / month);
    return `${months} 个月前`;
  } else {
    // 计算相差的年数和剩余的月数
    const years = Math.floor(diff / year);
    const remainingMonths = Math.floor((diff % year) / month);
    if (remainingMonths > 0) {
      return `${years} 年 ${remainingMonths} 个月前`;
    } else {
      return `${years} 年前`;
    }
  }
}

</script>

<template>
  <div>
    <ChatWindow></ChatWindow>
    <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal">
      <el-menu-item index="1" @click="activeIndex = '1'">首页</el-menu-item>
      <el-menu-item index="2" @click="activeIndex = '2'">留言</el-menu-item>
      <el-menu-item index="3" @click="activeIndex = '3'">商城</el-menu-item>
      <el-menu-item index="4" @click="goMe">后台</el-menu-item>
      <el-input size="small" v-model="searchData" style="width: 240px" placeholder="关键字搜索" />
    </el-menu>
    <el-main>
      <div class="main-content">
        <ArticleCard v-if="activeIndex == '1'" :searchData="searchData"></ArticleCard>
        <MessageBoard v-else-if="activeIndex == '2'" style="flex-grow: 2; max-height: 100vh;overflow-y: scroll;">
        </MessageBoard>
        <ToolsList v-else-if="activeIndex == '3'"></ToolsList>
        <div class="main-right">
          <el-card style="margin-bottom: 30px;">
            <div>
              <h3>{{ dateInfo }}</h3>
              <span>热爱不是天赋的赠品，是凌晨四点琴房里，手指磨破仍按不准的和弦。</span>
            </div>
            <el-button type="primary" plain @click="signInto">{{ signbtn }}</el-button>
          </el-card>
          <el-card>
            <div>
              <span>邮箱：kui512420@163.com</span>
            </div>
            <div>
              <span><a href="https://github.com/kui512420/Vue-management">github：kui512420</a></span>
            </div>
            <div>
              本站已经运行：{{ datato() }} 天
            </div>
            <div>
              <a href="https://beian.miit.gov.cn">豫ICP备2024077864号</a>
            </div>

          </el-card>
          <el-card style="margin-bottom: 30px; margin-top: 10px;">
            <h1>置顶文章</h1>
            <div v-for="(item, index) in data" :key="index"
              style="display: flex;border-bottom:1px solid rgba(228,230,235,0.5); justify-content: space-between;">
              <div style="display: flex; flex-direction: column; justify-content: space-around;">
                <h3 style="white-space:nowrap;overflow: hidden;text-overflow: ellipsis;"><router-link
                    :to="`/article/${item.article_id}`" style="color: black;">{{ item.article_title }}</router-link>
                </h3>
                <div style="display: flex; justify-content: space-between;">
                  <span style="font-size: 15px;">{{ item.article_author }}</span>
                  <span class="isShowTime">
                    {{ PushTime(convertDate(item.publish_time)) }}
                  </span>
                  <span v-for="(item2, index2) in JSON.parse(item.article_tag)" :key="index2">
                    <el-tag type="info">{{ item2 }}</el-tag>
                  </span>
                </div>
              </div>
              <img class="isShowImg" style="padding: 5px;" :src=item.article_cover alt="" width="80px" height="80">
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
