<script lang="ts" setup>
import { ElMessage } from 'element-plus';
import { Download } from '@element-plus/icons-vue'
import { exportlist } from '@/api/users'
import { exportArticleList } from '@/api/article'
const { fileName } = defineProps({
  ObjList: {
    type: Array
  },
  fileName: {
    type: String,
    default: "xxx.xls"
  },
  name: {
    type: String,
    default: "导出"
  }
})
const exportFile = () => {
  if (fileName.includes("用户")) {
    exportlist().then((res) => {
      const blob = new Blob([res.data], { type: 'application/vnd.ms-excel;charset=UTF-8' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = fileName;
      a.click();
      window.URL.revokeObjectURL(url);
    }).catch((e) => {
      ElMessage.error('导出数据时出错:' + e)
    });
  }else if(fileName.includes("文章")){
    exportArticleList().then((res) => {
      const blob = new Blob([res.data], { type: 'application/vnd.ms-excel;charset=UTF-8' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = fileName;
      a.click();
      window.URL.revokeObjectURL(url);
    }).catch((e) => {
      ElMessage.error('导出数据时出错:' + e)
    });
  }

}
</script>
<template>
  <el-button type="warning" :icon="Download" @click="exportFile">{{ name }}</el-button>
</template>
