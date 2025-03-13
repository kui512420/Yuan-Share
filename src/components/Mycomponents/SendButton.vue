<script lang="ts" setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import {send} from '@/api/register'
const buttonName = ref("发送")
const isDisabled = ref(false)
const isLoading = ref(false)
const { to,RemainingTime } = defineProps({
  to: String,
  RemainingTime: {
    type: Number,
    default: 60000
  }
})
const sendCode = (event) => {
  event.stopPropagation();
  isDisabled.value = true
  isLoading.value = true
  send(to).then((res) => {
    if (res.data.isSend) {
      isLoading.value = false
      let time = RemainingTime
      buttonName.value = time/1000+"秒"
      const dsq = setInterval(() => {
        buttonName.value = (time-1000) / 1000 + "秒"
        if (time <= 0) {
          buttonName.value = "发送"
          isDisabled.value = false
          clearInterval(dsq)
        }
        time -= 1000
      }, 1000)
      ElMessage.success("发送成功")
    }else{
      ElMessage.error('发送验证码失败')
      isLoading.value = false
      isDisabled.value = false
    }
  }).catch((e)=>{
    isDisabled.value = false
    ElMessage.error('发送验证码时出错:' + e)
  })
}
</script>
<template>
  <el-button style="width: 50px;" @click="sendCode" :loading=isLoading :disabled=isDisabled>{{ buttonName }}</el-button>
</template>
