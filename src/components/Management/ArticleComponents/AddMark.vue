<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage, ElNotification } from 'element-plus'
import { uploadImg } from '@/api/home'
import { Plus } from '@element-plus/icons-vue'
import HotCover from '@/components/Mycomponents/HotCover.vue'
import { useShareStore } from '@/stores/counter';
import { add } from '@/api/article'
import type { UploadProps } from 'element-plus'
const sharedDataStore = useShareStore();
const text = ref('')

const imageUrl = computed(() => sharedDataStore.HotCoverImgSrc);
const article_src = ref(imageUrl)
const inputTag = ref([])
const title = ref('')
const articleType = ref()
interface UploadParams {
  file: File;
  data: {
    type: number;
  };
}
type ImageInsertOptions = {
  url: string;
  desc: string;
  width: string;
  height: string;
};
type InsertImageFunction = (options: ImageInsertOptions) => void;


const handleCopyCodeSuccess = () => {
  ElMessage.success("复制成功")
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

const uploadArticle = (event: never, insertImage: InsertImageFunction, files: File[]) => {

  const formData = new FormData();
  formData.append('type', "2");
  formData.append('fileInput', files[0]);
  uploadImg(formData).then((respon) => {
    insertImage({
      url: "kuikui.space/"+respon.data.msg,
      desc: '图片',
      width: 'auto',
      height: 'auto',
    });

  })
}


const uploadIm = (params: UploadParams) => {
  const file = params.file;
  const formData = new FormData();
  formData.append('type', params.data.type + "");
  formData.append('fileInput', file);
  uploadImg(formData).then((res) => {
    sharedDataStore.HotCoverImgSrc = res.data.msg;
    ElMessage.success({
      message: "上传头像成功"
    })
  })
}
const addArticle = () => {
  // 去除首尾的方括号
  const trimmedStr = inputTag.value + "".slice(1, -1);
  // 使用逗号分割字符串
  const arr = trimmedStr.split(',');
  if (title.value == "") {
    ElNotification({
      title: 'Warning',
      message: '标题不能为空',
      type: 'warning',
    })
  } else if (inputTag.value.length == 0) {
    ElNotification({
      title: 'Warning',
      message: '标签不能为空',
      type: 'warning',
    })
  } else if (articleType.value == undefined) {
    ElNotification({
      title: 'Warning',
      message: '文章类型不能为空',
      type: 'warning',
    })
  } else if (text.value == "") {
    ElNotification({
      title: 'Warning',
      message: '文章内容不能为空',
      type: 'warning',
    })
  } else {
    add(title.value, article_src.value, arr, text.value, articleType.value).then((respon) => {
      ElMessage.success(respon.data.msg)
    })
  }


}
</script>

<template>
  <el-form>
    <el-form-item label="文章标题">
      <el-input v-model="title" />
    </el-form-item>
    <el-form-item label="文章标签">
      <el-input-tag v-model="inputTag" :max="3" placeholder="回车结束标签,最多3个"
        aria-label="Please click the Enter key after input" />
    </el-form-item>
    <el-form-item label="文章类型">
      <el-radio-group v-model="articleType">
        <el-radio value="1" border>前端</el-radio>
        <el-radio value="2" border>后端</el-radio>
        <el-radio value="3" border>测试</el-radio>
        <el-radio value="4" border>运维</el-radio>
        <el-radio value="5" border>算法</el-radio>
        <el-radio value="6" border>数据库</el-radio>
        <el-radio value="7" border>工具</el-radio>
        <el-radio value="8" border>其他</el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="文章封面" style="display: flex;">
      <el-upload style="background: gray;" class="avatar-uploader" :show-file-list="false" :http-request="uploadIm"
        :data="{ type: 1 }" name="fileInput" :before-upload="beforeAvatarUpload">
        <img v-if="imageUrl" :src="sharedDataStore.HotCoverImgSrc" class="avatar" width="200" height="150" />
        <el-icon v-else class="avatar-uploader-icon">
          <Plus />
        </el-icon>
      </el-upload>
      <HotCover></HotCover>
    </el-form-item>
    <el-form-item>
      <v-md-editor v-model="text" height="400px" :disabled-menus="[]" @upload-image="uploadArticle"
        @copy-code-success="handleCopyCodeSuccess"></v-md-editor>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" round style="margin: 0 auto;" @click="addArticle">发布文章</el-button>
    </el-form-item>
  </el-form>



</template>

<style scoped>
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}


.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
}
</style>
