<script lang="ts" setup>
  import {add,get} from '@/api/notes'
import { ref, onBeforeUnmount, onMounted } from 'vue'
import {convertDate} from '@/utils/DateUntil'
import { ElMessage } from 'element-plus';
const txt = ref('')
const timer = ref();
type msg = {
  nickname:string,
  info:string,
  time:string,
  headsrc:string
}
const msfObj = ref<msg[]>([])
const addmsg = ()=>{
  add(txt.value).then(()=>{
    ElMessage.success("发布成功")
    txt.value = ''
  })
}
const getmsg = ()=>{
  get().then((res)=>{
    msfObj.value = res.data.data
  })
}
getmsg()
onMounted(()=>{
  timer.value = setInterval(()=>{
  getmsg()
},3000)

onBeforeUnmount(()=>{
    clearInterval(timer.value);
    timer.value = null
  })
})

</script>

<template>
  <el-timeline>
    <textarea v-model="txt" @keydown.enter="addmsg"></textarea> <el-button plain @click="addmsg">发言</el-button>
    <el-timeline-item v-for="(item,index) in msfObj" :key="index" :timestamp=convertDate(item.time) placement="top" >
      <el-card>
        <div><img style="border-radius: 50%;" width="32" height="32" :src=item.headsrc alt="无头像"> {{item.nickname}}：</div>
        <p>{{ item.info }}</p>
      </el-card>
    </el-timeline-item>
  </el-timeline>
</template>
