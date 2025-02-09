<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { uploadImg } from '@/api/home'
import { Plus } from '@element-plus/icons-vue'
import HotCover from '@/components/HotCover.vue'
import { useShareStore } from '@/stores/counter';
const sharedDataStore = useShareStore();
const text = ref('')
const inputTag = ref<string[]>()
const imageUrl = computed(() => sharedDataStore.HotCoverImgSrc);

const handleUploadImage = (event: Event, insertImage: (image: { url: string, desc: string, width: string, height: string }) => void, files: File[]) => {
  // 拿到 files 之后上传到文件服务器，然后向编辑框中插入对应的内容
  uploadImg(2, files[0]).then((respon) => {
    // 此处只做示例
    insertImage({
      url: 'http://localhost:5173/' + respon.data.msg,
      desc: '上传的图片',
      width: 'auto',
      height: 'auto',
    });
  })
};
const recover = () => {
  ElMessage.success("已恢复上次保存数据")
  const data = localStorage.getItem("saveData")
  if (data !== null) {
    text.value = data
  }

}
if (localStorage.getItem("saveData") !== null) {
  recover()
}

const toSave = (txt: string) => {
  localStorage.setItem("saveData", txt)
  ElMessage.success("保存成功")
}

const handleCopyCodeSuccess = () => {
  ElMessage.success("复制成功")
}

</script>

<template>
  <el-form>
    <el-form-item label="文章标题">
      <el-input />
    </el-form-item>
    <el-form-item label="文章标签">
      <el-input-tag v-model="inputTag" placeholder="回车结束标签" aria-label="Please click the Enter key after input" />
    </el-form-item>
    <el-form-item label="文章封面" style="display: flex;">
      <el-upload style="background: gray;" class="avatar-uploader"
        action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15" :show-file-list="false"
        :on-success="handleAvatarSuccess" :before-upload="beforeAvatarUpload">
        <img v-if="imageUrl" :src="imageUrl" class="avatar" width="200" height="150" />

        <el-icon v-else class="avatar-uploader-icon">
          <Plus />
        </el-icon>

      </el-upload>
      <HotCover></HotCover>
    </el-form-item>
    <el-form-item>
      <v-md-editor v-model="text" height="400px" :disabled-menus="[]" @upload-image="handleUploadImage" @save='toSave'
      @copy-code-success="handleCopyCodeSuccess"></v-md-editor>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" round style="margin: 0 auto;">发布文章</el-button>
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
