
<script lang="ts" setup>
import {  ElMessage} from 'element-plus'
import { reactive, ref } from 'vue'
import {  find, updateInfo2 } from '@/api/users'
import {  Check, Plus } from '@element-plus/icons-vue'
import type {  UploadProps } from 'element-plus'
import { uploadImg,getUserInfo } from '@/api/home'
// 定义用户信息接口





const editStatus = ref(true)
const editFormData = reactive({
  id: "",
  username: "",
  nickname: "",
  password: "",
  headsrc: "",
  status: "",
  type: "",
  register_time: "",
  last_login: "",
  bean: ""
})
interface UploadParams {
  file: File;
  data: {
    id: number;
    type: number;
  };
}
const radio1 = ref('1')



const uploadIm = (params: UploadParams) => {
  const file = params.file;
  const formData = new FormData();
  formData.append('id', params.data.id + "");
  formData.append('type', params.data.type + "");
  formData.append('fileInput', file);
  uploadImg(formData).then(() => {
    editFormData.headsrc = URL.createObjectURL(file)
    ElMessage.success({
      message: "上传封面成功"
    })
  })
}


//上传前的校验钩子
const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
  if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png'  && rawFile.type !== 'image/jpg') {
    ElMessage.error('只能上传【image/jpeg,image/png,image/jpg】格式的图片')
    return false
  } else if (rawFile.size / 1024 / 1024 > 10) {
    ElMessage.error('最大 10MB!')
    return false
  }
  return true
}


//编辑单个用户
const edit = (id: number) => {



  find(id).then((respon) => {
    const data = respon.data.data
    editFormData.id = data.id
    editFormData.username = data.username
    editFormData.nickname = data.nickname
    editFormData.password = data.password
    editFormData.headsrc = data.headsrc
    editFormData.status = data.status
    editFormData.type = data.type
    editFormData.register_time = data.register_time
    editFormData.last_login = data.last_login
    editFormData.bean = data.bean
    setTimeout(() => {
      //状态开关
      if (editFormData.status == "1") {
        editStatus.value = true
      } else {
        editStatus.value = false
      }
      //身份
      radio1.value = editFormData.type + ""
    }, 300)
  })
}
const getInfo = ()=>{
  getUserInfo().then((res)=>{
    edit(res.data.data.id)
  })
}

getInfo()
//修改部分信息
const updateInfos = () => {
  let statusnum = 8
  if (editStatus.value) {
    statusnum = 1
  } else {
    statusnum = 0
  }
  updateInfo2(parseInt(editFormData.id), editFormData.username, statusnum, parseInt(radio1.value), editFormData.password,editFormData.nickname).then((respon) => {
    if (respon.data.data == true) {
      ElMessage.success({
        message: "修改信息成功"
      })
    }
  })

}
</script>

<template>
  <div>
    <el-form :model="editFormData">
      <el-form-item label="用户ID">
        <el-input v-model="editFormData.id" disabled />
      </el-form-item>
      <el-form-item label="用户账号">
        <el-input v-model="editFormData.username" disabled />
      </el-form-item>
      <el-form-item label="名称">
        <el-input v-model="editFormData.nickname" />
      </el-form-item>
      <el-form-item label="用户密码">
        <el-input v-model="editFormData.password" />
      </el-form-item>
      <el-form-item label="头像">
        <el-upload class="avatar-uploader" :http-request="uploadIm" :show-file-list="false"
          :before-upload="beforeAvatarUpload" :data="{ id: editFormData.id, type: 0 }" name="fileInput">
          <img class="avatar" />
          <div class="demo-image__preview">
            <el-image style="width: 100px; height: 100px;border-radius: 50%;" v-if="editFormData.headsrc"
              :src="editFormData.headsrc" :zoom-rate="1.2" :max-scale="7" :min-scale="0.2"
              :preview-src-list="[editFormData.headsrc]" :initial-index="4" fit="cover" :preview-teleported=true />
            <el-icon v-else class="avatar-uploader-icon">
              切换头像
              <Plus />
            </el-icon>
          </div>

        </el-upload>
      </el-form-item>
      <el-form-item label="状态" >
        <el-switch v-model="editStatus" disabled/>
      </el-form-item>
      <el-form-item label="类型">
        <div class="mb-2 ml-4">
          <el-radio-group v-model="radio1" disabled>
            <el-radio value="0" size="large">管理员</el-radio>
            <el-radio value="1" size="large">普通用户</el-radio>
          </el-radio-group>
        </div>
      </el-form-item>

      <el-form-item label="币余额">
        <el-input v-model="editFormData.bean" disabled />
      </el-form-item>

      <el-form-item label="最后登录时间">
        <el-input v-model="editFormData.last_login" disabled />
      </el-form-item>

      <el-form-item label="注册时间">
        <el-input v-model="editFormData.register_time" disabled />
      </el-form-item>

      <el-button type="primary" :icon="Check" @click="updateInfos">保存</el-button>
    </el-form>
  </div>
</template>
