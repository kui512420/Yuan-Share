<script setup lang="ts">
import { ElMessage} from 'element-plus';
import { ref } from 'vue';
import { Plus } from '@element-plus/icons-vue'
import {uploadImg} from '@/api/home'
import {postGoods} from '@/api/goods'
import type { UploadProps } from 'element-plus'
const name = ref('')
const desc = ref('')
const count = ref(0)
const price = ref(0)
const status = ref(0)
const imageUrl = ref('')
interface UploadParams {
  file: File;
  data: {
    type: number;
  };
}
//上传前的校验钩子
const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
  if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png' && rawFile.type !== 'image/gif' && rawFile.type !== 'image/jpg') {
    ElMessage.error('只能上传【image/jpeg,image/png,image/gif,image/jpg】格式的图片')
    return false
  } else if (rawFile.size / 1024 / 1024 > 10) {
    ElMessage.error('最大 10MB!')
    return false
  }
  return true
}
const uploadIm = (params: UploadParams) => {
  const file = params.file;
  const formData = new FormData();
  formData.append('type', params.data.type + "");
  formData.append('fileInput', file);
  uploadImg(formData).then((res) => {
    //"https://kuikui.space/" +
    imageUrl.value= res.data.msg;
    ElMessage.success({
      message: "上传头像成功"
    })
  })
}
const addGoods = ()=>{
  let goodsStatus = 1
  if(!status.value){
    goodsStatus=0
  }
  postGoods(name.value,desc.value,price.value,count.value,goodsStatus,imageUrl.value).then((res)=>{
    if(res.data.code==200){
      ElMessage.success("商品发布成功！")
    }
  })
}
</script>

<template>
  <div>
    <el-form>
      <el-form-item label="商品名称">
        <el-input v-model="name" />
      </el-form-item>

      <el-form-item label="商品描述">
        <el-input v-model="desc" />
      </el-form-item>

      <el-form-item label="商品价格">
        <el-input v-model="price" />
      </el-form-item>

      <el-form-item label="商品数量">
        <el-input v-model="count" />
      </el-form-item>

      <el-form-item label="商品封面" style="display: flex;">
      <el-upload style="background: gray;" class="avatar-uploader" :show-file-list="false" :http-request="uploadIm"
        :data="{ type: 3 }" name="fileInput" :before-upload="beforeAvatarUpload">
        <img v-if="imageUrl" :src=imageUrl class="avatar" width="200" height="150" />
        <el-icon v-else class="avatar-uploader-icon">
          <Plus />
        </el-icon>
      </el-upload>
    </el-form-item>

      <el-form-item label="商品状态">
        <el-switch v-model="status" />
      </el-form-item>
    </el-form>
    <el-form-item>
      <el-button type="primary" round style="margin: 0 auto;" @click="addGoods">发布商品</el-button>
    </el-form-item>
  </div>
</template>
