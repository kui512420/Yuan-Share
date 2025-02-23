<script lang="ts" setup>
import { ref, reactive } from 'vue'
import { User, Lock, CircleCheck } from '@element-plus/icons-vue'
import { ElMessage, ElLoading } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { login } from '@/api/login'
import { getUserInfo } from '@/api/home'
import router from '@/router/index'
//方便获取到表单实例
const ruleFormRef = ref<FormInstance>()
//定义了一个表单接口规范
interface RuleForm {
  username: string,
  password: string,
  code: string
}

const goRegister = ()=>{
  router.push('/management/register')
}

const imgSrc = ref('api/code/captcha?t=')
const RuleForm = reactive<RuleForm>({
  username: '',
  password: '',
  code: ''
})

const cheakRules = reactive<FormRules<RuleForm>>(
  {
    username: [
      {
        required: true,
        message: '请输入账号',
        trigger: 'change'
      }
    ],
    password: [
      {
        required: true,
        message: '请输入密码',
        trigger: 'change'
      }
    ],
    code: [
      {
        required: true,
        message: '请输入验证码',
        trigger: 'change'
      }
    ],
  }
)

//******************************************方法*********************************************
/*
切换验证码图片
*/
function changeImg() {
  const timestamp = new Date().getTime();
  imgSrc.value = 'api/code/captcha?t=' + timestamp
}

const checkStatus= ()=>{
  getUserInfo().then((respon)=>{
    if(respon.data.code==200){
      router.push("/management/home/index")
    }
  })
}
checkStatus()
/*
提交表单登录
*/

const submitForm = async (form1: FormInstance | undefined) => {
  if(form1){
    await form1.validate((valid) => {
    if (valid) {
      const loading = ElLoading.service({
        text: "加载中",
        background: 'rgba(0, 0, 0, 0.7)',
      })
      const formdata = new FormData()
      formdata.append("username", RuleForm.username)
      formdata.append("password", RuleForm.password)
      formdata.append("captaCode", RuleForm.code)
      setTimeout(() => {

        login(formdata).then((respon) => {
          const result = respon.data
          loading.close()
          if (result.msg === "登录成功") {
            ElMessage({
              message: respon.data.msg,
              type: 'success',
            })
            //设置token
            window.sessionStorage.removeItem("token")
            window.sessionStorage.setItem("token",respon.data.data)
            router.push("/")
          } else {
            changeImg()
            ElMessage({
              message: respon.data.msg,
              type: 'error',
            })
          }
        })

      }, 800)

    } else {
      ElMessage.error('缺失参数')
    }
  })
  }else{
    ElMessage.error('表单验证失败')
  }


}

</script>

<template>
  <div class="wrapper">
    <el-form ref="ruleFormRef" :model="RuleForm" :rules="cheakRules" class="login-form" label-width="auto"
      style="max-width: 600px">
      <h2 class="login-title">登录</h2>
      <el-form-item prop="username">

        <el-input class="login-imput" v-model="RuleForm.username" style="width: 240px" placeholder="请输入账号"
          :prefix-icon="User" />

      </el-form-item>

      <el-form-item prop="password">
        <el-input v-model="RuleForm.password" style="width: 240px" type="password" placeholder="请输入密码" show-password
          :prefix-icon="Lock" />
      </el-form-item>
      <el-form-item prop="code">
        <div class="code-area">
          <el-input v-model="RuleForm.code" style="width: 160px" placeholder="请输入验证码" :prefix-icon="CircleCheck" />
          <img :src="imgSrc" alt="验证码加载失败" class="codeClass"  width="50" height="30" @click="changeImg">
        </div>

      </el-form-item>

      <el-button class="login-btn" type="primary" :plain="true" @click="submitForm(ruleFormRef)">登录</el-button>
      <el-button  class="login-btn reg" type="primary" :plain="true" @click="goRegister">去注册</el-button>
    </el-form>
  </div>
</template>

<style scoped>
.wrapper {
  width: 100%;
  height: calc(100vh);
  background-image: url(../../assets/login-bj.jpg);
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
}
.reg{
  margin-left: 0px;
}
.login-title {
  margin-bottom: 20px;
  /**禁止用户选择文字 */
  user-select: none;
}
.codeClass{
  border: 1px solid blue;
  margin-left: 5px;
}


.login-form {
  display: flex;
  width: 280px;
  background-color: #fff;
  border-radius: 10px;
  justify-content: center;
  align-content: space-around;
  flex-direction: column;
  flex-wrap: wrap;
  opacity: 0.8;
  position: absolute;
  right: 10%;
  top: 30%;
  padding: 20px 0;
}

.login-btn {
  width: 240px;
  margin-top: 10px;
}

.code-area {
  display: flex;
  justify-content: space-between;
}
</style>
