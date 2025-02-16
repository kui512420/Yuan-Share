<template>
  <div class="List-top">
    <el-select v-model="userType" placeholder="文章类型" style="width: 120px" @change="refreshList()">
      <el-option v-for="item in userTypes" :key="item" :label="item" :value="item" />
    </el-select>

    <div class="mt-4">
      <el-input v-model="searchData" style="max-width: 500px" placeholder="请输入数据" class="input-with-select">
        <template #prepend>
          <el-select v-model="select" placeholder="搜索条件" style="width: 115px">
            <el-option label="文章ID" value="1" />
            <el-option label="文章标题" value="2" />
            <el-option label="标签" value="3" />
            <el-option label="作者ID" value="4" />
          </el-select>
        </template>
        <template #append>
          <el-button :icon="Search" @click="refreshList(true)" />
        </template>
      </el-input>
    </div>

    <el-button type="danger" @click="delMore(selectionarr)">批量删除</el-button>
    <el-button type="info" @click="reset">重置</el-button>
    <el-button type="success" @click="goEdit">添加文章</el-button>
  </div>



  <el-table :data="tableData" style="width: 100%;height: 75vh;" :border=true @selection-change="selection">
    <el-table-column type="selection" width="55" />
    <el-table-column label="序号" width="50">
      <template #default="scope">
        <!-- 当前页码减 1 乘以每页条数，再加上当前行的索引加 1 -->
        {{ (currentPage1 - 1) * pageSize1 + scope.$index + 1 }}
      </template>
    </el-table-column>
    <el-table-column property="article_id" label="文章ID" width="76" />
    <el-table-column property="article_title" label="标题" />
    <el-table-column property="article_author" label="作者" />
    <el-table-column property="article_type" label="类型">
      <template #default="scope">
        {{
          scope.row.article_type === 1 ? '前端' :
            scope.row.article_type === 2 ? '后端' :
              scope.row.article_type === 3 ? '测试' :
                scope.row.article_type === 4 ? '运维' :
                  scope.row.article_type === 5 ? '算法' :
                    scope.row.article_type === 6 ? '数据库' :
                      scope.row.article_type === 7 ? '工具' :
                        scope.row.article_type === 8 ? '其他' :
                          scope.row.article_type
        }}
      </template>
    </el-table-column>
    <el-table-column property="article_tag" label="标签">
      <template #default="scope">
        <div style="display: flex; flex-wrap: wrap;" v-for="(item, index) in JSON.parse(scope.row.article_tag)"
          :key="index">
          <el-tag type="success">{{ item }}</el-tag>
        </div>
      </template>
    </el-table-column>
    <el-table-column property="article_cover" label="封面" width="123">
      <template #default="scope">
        <div class="demo-image__preview">
          <el-image style="width: 100px; height: 100px;" :src="scope.row.article_cover"
            :preview-src-list="[scope.row.article_cover]" :zoom-rate="1.2" :max-scale="7" :min-scale="0.2"
            :initial-index="4" fit="cover" :preview-teleported=true />
        </div>
      </template>
    </el-table-column>
    <el-table-column property="article_status" label="状态">
      <template #default="scope">
        <el-tag :type="scope.row.article_status === 1 ? 'primary' : 'danger'">
          {{ scope.row.article_status === 1 ? '上架' : '下架' }}
        </el-tag>
      </template>
    </el-table-column>

    <el-table-column property="last_time" label="修改时间" width="70">
      <template #default="scope">
        {{ convertDate(scope.row.last_time) }}
      </template>
    </el-table-column>

    <el-table-column property="publish_time" label="发布时间">
      <template #default="scope">
        {{ convertDate(scope.row.publish_time) }}
      </template>
    </el-table-column>
    <el-table-column property="option" label="操作" width="250">
      <template #default="scope">
        <el-button type="info" :icon="Edit" circle />
        <el-button type="danger" :icon="Delete" circle @click="del(scope.row.article_id)" />
        <el-button type="primary" :icon="View" circle @click="goView(scope.row.article_id)"/>

        <el-dropdown style="margin-left: 5px;">
          <el-button size="small">
            更多<el-icon class="el-icon--right">
              <ArrowDown />
            </el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="updateSt(scope.row.article_id,1)">上架</el-dropdown-item>
              <el-dropdown-item @click="updateSt(scope.row.article_id,0)">下架</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </template>

    </el-table-column>
  </el-table>

  <div class="demo-pagination-block">
    <el-pagination v-model:current-page="currentPage1" v-model:page-size="pageSize1" :page-sizes="[10, 30, 80, 100]"
      :disabled="disabled" :background="true" layout="total, sizes, prev, pager, next, jumper" v-model:total='total'
      @size-change="handleSizeChange" @current-change="handleCurrentChange" />
  </div>



</template>

<script lang="ts" setup>
import { ElTable, ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import { ArrowDown ,View} from '@element-plus/icons-vue'
import { ref } from 'vue'
import { get, delOne, delArr,updateStatus } from '@/api/article'
import { Search, Edit, Delete } from '@element-plus/icons-vue'
import { convertDate } from '@/utils/DateUntil'
import router from '@/router'
import  {useShareStore} from '@/stores/counter'
const tableData = ref([])
const total = ref(0)
const currentPage1 = ref(1)

const pageSize1 = ref(10)
const searchData = ref('')
const select = ref('')
const userType = ref('')
const userTypes = ["前端", "后端", '测试', '运维', "算法", "数据库", '工具', "其他"]
const disabled = ref(false)
const selectionarr = ref<number[]>([])
const useShare = useShareStore()
interface editFormDatain {
  article_id: number
}

const goEdit = ()=>{
  router.replace("/management/home/Article/Edit")
}
const goView = (id:number)=>{
  router.push('/management/home/Article/ViewArticle')
  useShare.setArticle_id(id)
}


//刷新列表
const refreshList = (isSearch?: boolean) => {
  let article_type1 = 0
  let Searchtype = 0
  // 1 前端 2 后端 3 测试 4 运维 5 算法 6 数据库 7 工具 8 其他

  if (userType.value == "前端") {
    article_type1 = 1
    Searchtype = 5
    console.log(Searchtype)
  } else if (userType.value == "后端") {
    article_type1 = 2
    Searchtype = 5
  } else if (userType.value == "测试") {
    article_type1 = 3
    Searchtype = 5
  } else if (userType.value == "运维") {
    article_type1 = 4
    Searchtype = 5
  } else if (userType.value == "算法") {
    article_type1 = 5
  } else if (userType.value == "数据库") {
    article_type1 = 6
    Searchtype = 5
  } else if (userType.value == "工具") {
    article_type1 = 7
    Searchtype = 5
  } else if (userType.value == "其他") {
    article_type1 = 8
    Searchtype = 5
  }

  if (isSearch == true) {
    Searchtype = parseInt(select.value)
  }
  get(currentPage1.value, pageSize1.value, Searchtype, searchData.value, article_type1).then((respon) => {
    const JsonArticle = respon.data.data
    tableData.value = JsonArticle.list
    total.value = parseInt(JsonArticle.total)
  })

}

refreshList()
//分页回调
const handleSizeChange = () => {
  refreshList()
}
const handleCurrentChange = () => {
  refreshList()
}

//删除单个文章
const del = (id: number) => {
  ElMessageBox.confirm(
    '确认删除？',
    '提示',
    {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning',
      draggable: true
    }
  )
    .then(() => {
      delOne(id).then((respon) => {
        if (respon.data.data == '1') {
          ElMessage({
            type: 'success',
            message: '删除成功',
          })
          refreshList()
        }
      })

    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '取消删除',
      })
    })
}

//删除多个用户
const delMore = (idArr: Array<number>) => {

  if (idArr.length == 0) {
    ElNotification({
      title: '警告',
      message: '请选择后在尝试',
      type: 'warning',
      duration: 1000
    })
  } else {
    ElMessageBox.confirm(
      '确认删除？',
      '提示',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning',
        draggable: true
      }
    )
      .then(() => {
        const data = idArr.toString().replace("[", "").replace("]", "")
        delArr(data).then((respon) => {
          if (respon.data.data >= 1) {
            ElMessage({
              type: 'success',
              message: '删除了' + respon.data.data + "条数据",
            })
            selectionarr.value = []
            refreshList()
          }
        })
      })
      .catch(() => {
        ElMessage({
          type: 'info',
          message: '取消删除',
        })
      })
  }
}


//获取所选项
const selection = (val: editFormDatain[]) => {

  selectionarr.value = []
  val.forEach(element => {
    selectionarr.value.push(element.article_id)
  });
}

//重置查询
const reset = () => {
  select.value = "1"
  userType.value = "全部"
  refreshList()
}
//更改状态
const updateSt = (id:number,status:number)=>{

  updateStatus(id,status).then((respon)=>{
    ElMessage.success(respon.data.msg)
    refreshList()
  })

}

</script>

<style scoped>
.demo-pagination-block+.demo-pagination-block {
  margin-top: 10px;
}

.demo-pagination-block .demonstration {
  margin-bottom: 16px;
}

.el-pagination {
  justify-content: center;
  margin-top: 10px;
}


.el-main {
  padding-top: 0;
}

.List-top {
  display: flex;
  width: 100%;
}

.demo-image__error .image-slot {
  font-size: 30px;
}

.demo-image__error .image-slot .el-icon {
  font-size: 30px;
}

.demo-image__error .el-image {
  width: 100%;
  height: 200px;
}

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
