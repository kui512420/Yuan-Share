<template>
  <div class="List-top">

    <div class="mt-4">
      <el-input v-model="searchData" style="max-width: 500px" placeholder="请输入数据" class="input-with-select">
        <template #prepend>
          <el-select v-model="select" placeholder="搜索条件" style="width: 115px">
            <el-option label="商品ID" value="1" />
            <el-option label="商品名称" value="2" />
          </el-select>
        </template>
        <template #append>
          <el-button :icon="Search" @click="refreshList()" />
        </template>
      </el-input>
    </div>

    <el-button type="danger" @click="delMore(selectionarr)">批量删除</el-button>
    <el-button type="info" @click="reset">重置</el-button>
    <el-button type="success" @click="goAdd">添加商品</el-button>
  </div>



  <el-table :data="tableData" style="width: 100%;height: 75vh;" :border=true @selection-change="selection">
    <el-table-column type="selection" width="55" />
    <el-table-column label="序号" width="50">
      <template #default="scope">
        <!-- 当前页码减 1 乘以每页条数，再加上当前行的索引加 1 -->
        {{ (currentPage1 - 1) * pageSize1 + scope.$index + 1 }}
      </template>
    </el-table-column>
    <el-table-column property="goods_id" label="商品ID" width="76" />
    <el-table-column property="goods_name" label="商品名称" />
    <el-table-column property="goods_price" label="商品价格" />
    <el-table-column property="goods_count" label="剩余数量" />
    <el-table-column property="goods_sales" label="商品销量" />
    <el-table-column property="goods_cover" label="商品封面" width="123">
      <template #default="scope">
        <div class="demo-image__preview">
          <el-image style="width: 100px; height: 100px;" :src="scope.row.goods_cover"
            :preview-src-list="[scope.row.goods_cover]" :zoom-rate="1.2" :max-scale="7" :min-scale="0.2"
            :initial-index="4" fit="cover" :preview-teleported=true />
        </div>
      </template>
    </el-table-column>
    <el-table-column property="goods_status" label="状态">
      <template #default="scope">
        <el-tag :type="scope.row.goods_status === 1 ? 'primary' : 'danger'">
          {{ scope.row.goods_status === 1 ? '上架' : '下架' }}
        </el-tag>
      </template>
    </el-table-column>

    <el-table-column property="goods_updatetime" label="修改时间" width="70">
      <template #default="scope">
        {{ convertDate(scope.row.goods_updatetime) }}
      </template>
    </el-table-column>

    <el-table-column property="goods_createtime" label="发布时间">
      <template #default="scope">
        {{ convertDate(scope.row.goods_createtime) }}
      </template>
    </el-table-column>
    <el-table-column property="option" label="操作" width="250">
      <template #default="scope">
        <el-button type="info" :icon="Edit" circle @click="goEdit(scope.row.article_id)"/>
        <el-button type="danger" :icon="Delete" circle @click="del(scope.row.goods_id)" />

        <el-dropdown style="margin-left: 5px;">
          <el-button size="small">
            更多<el-icon class="el-icon--right">
              <ArrowDown />
            </el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="updateSt(scope.row.goods_id,1)">上架</el-dropdown-item>
              <el-dropdown-item @click="updateSt(scope.row.goods_id,0)">下架</el-dropdown-item>
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
import { ArrowDown} from '@element-plus/icons-vue'
import { ref } from 'vue'
import {getGoodsAll,UpdateStatus,delByID} from '@/api/goods.ts'
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

const disabled = ref(false)
const selectionarr = ref<number[]>([])
const useShare = useShareStore()
interface editFormDatain {
  article_id: number
}

const goAdd= ()=>{
  router.push("/management/home/Goods/Add")
}
const goEdit= (id:number)=>{
  router.push("/management/home/Article/Edit")
  useShare.setArticle_id(id)
}


//刷新列表
const refreshList = () => {

  getGoodsAll(pageSize1.value,currentPage1.value ).then((respon) => {
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
      delByID(id).then((respon) => {
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
        /*
        const data = idArr.toString().replace("[", "").replace("]", "")

        delArr(data).then((respon) => {
          if (respon.data.data >= 1 && respon.data.data <100000) {
            ElMessage({
              type: 'success',
              message: '删除了' + respon.data.data + "条数据",
            })
            selectionarr.value = []
            refreshList()
          }
        })
          */
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
  refreshList()
}
//更改状态
const updateSt = (id:number,status:number)=>{

  UpdateStatus(id,status).then((respon)=>{
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
