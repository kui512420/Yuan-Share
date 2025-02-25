<script setup lang="ts">
import { ref,defineProps, watch} from 'vue'
import { get,like } from '@/api/article'
import { convertDate } from '@/utils/DateUntil'
type dataform = {
  article_id:string,
  article_title:string,
  article_author:string,
  publish_time:string,
  article_tag:string,
  article_cover:string
}
const data = ref <dataform[]>([])
const total = ref(0)
const currentPage1 = ref(1)
const pageSize1 = ref(5)

const refreshList = () => {
  get(currentPage1.value, pageSize1.value, 0,"",5,11).then((respon) => {
    data.value = respon.data.data.list
    total.value = respon.data.data.total
  })
}
//分页回调
const handleSizeChange = () => {
  if(props.searchData){
    doSomething(props.searchData)
  }else{
    refreshList()
  }
}
const handleCurrentChange = () => {
  if(props.searchData){
    doSomething(props.searchData)
  }else{
    refreshList()
  }
}
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

const props = defineProps({
  searchData: {
    type: String,
    default: ''
  }
});

// 定义一个函数，当接收到的值变化时执行
const doSomething = (txt:string) => {
  likes(txt)
  // 这里可以添加更多的逻辑
};

// 监听 inputValue 的变化
watch(() => props.searchData, (newValue) => {
  if (newValue) {
    doSomething(newValue);
  }else{
    refreshList()
  }
});
const likes = (txt:string)=>{
  like(currentPage1.value, pageSize1.value, txt).then((respon) => {
    data.value = respon.data.data.list
    total.value = respon.data.data.total
  })
}
refreshList()
</script>

<template>
  <div style="flex-grow: 5; padding: 5px; background-color: #fff; ">
    <div v-for="(item, index) in data" :key="index"
      style="display: flex;border-bottom:1px solid rgba(228,230,235,0.5); justify-content: space-between;">
      <div style="display: flex; flex-direction: column; justify-content: space-around;">
        <h3 style="white-space:nowrap;overflow: hidden;text-overflow: ellipsis;"><router-link
            :to="`/article/${item.article_id}`" style="color: black;">{{ item.article_title }}</router-link></h3>
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
    <div class="demo-pagination-block" style="display: flex; justify-content: center; margin-top: 10px;">
      <el-pagination
      size="small"
      v-model:current-page="currentPage1"
      v-model:page-size="pageSize1"
      :background="true"
      layout="prev, pager, next"
      v-model:total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
    </div>
  </div>
</template>

<style scoped>
a {
  text-decoration: none;
}
.isShowTime{
  margin:0 10px;
}

@media screen and (max-width: 600px) {

  .isShowImg {
    display: none;
  }
}
</style>
