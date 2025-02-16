<script lang="ts" setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { Clock } from '@element-plus/icons-vue'
import { findOne } from '@/api/article';
import { useRoute } from 'vue-router';
import { convertDate } from '@/utils/DateUntil'
// 获取动态路由参数中的 ID
const route = useRoute();
const articleId = route.params.id;
const texts = ref('')
const title = ref('')
const auth = ref('')
const upTime = ref('')
const type = ref()
const tags = ref([])
const handleCopyCodeSuccess = () => {
  ElMessage.success("复制成功")
}
const view = () => {

  findOne(parseInt(articleId + "")).then((respon) => {
    texts.value = respon.data.data.article_content
    title.value = respon.data.data.article_title
    auth.value = respon.data.data.article_author
    upTime.value = respon.data.data.publish_time
    type.value = respon.data.data.article_type
    tags.value = JSON.parse(respon.data.data.article_tag)
  })
}
view()

</script>

<template>
  <div class="wrappera">
    <div>
      <h1 class="title-size">{{ title }}</h1>
      <div style="background-color: rgb(248, 248, 248); border-radius: 10px;">
        <div style="padding: 10px;">
          <span>作者：</span>
          <span>{{ auth }}</span>

          <span>
            <el-icon>
              <Clock />
            </el-icon>
            于 {{ convertDate(upTime) }} 发布
          </span>
        </div>
        <div style="padding: 10px;">
          <span>分类专栏：</span>
          <el-tag type="primary">
            <template #default="">
              {{
                type === 1 ? '前端' :
                  type === 2 ? '后端' :
                    type === 3 ? '测试' :
                      type === 4 ? '运维' :
                        type === 5 ? '算法' :
                          type === 6 ? '数据库' :
                            type === 7 ? '工具' :
                              type === 8 ? '其他' :
                                type
              }}
            </template>
          </el-tag>
          <span>文章标签：</span>
          <span v-for="(item, index) in tags" :key="index">
            <el-tag type="primary" style="margin: 5px;">{{ item }}</el-tag>
          </span>
        </div>
      </div>

      <div>
        <v-md-preview :text="texts" @copy-code-success="handleCopyCodeSuccess"></v-md-preview>
      </div>
    </div>
  </div>

</template>
<style scoped>
.wrappera {
  height: auto;
  margin: 0 auto;
  max-width: 800px;
}

.title-size {
  font-size: 40px;
}

@media screen and (max-width: 600px) {
  .title-size {
    font-size: 20px;
  }

  .wrappera {
    padding: 10px;
  }
}
</style>
