<template>
  <div class="mt-4" style="margin-bottom: 10px;">
    <el-input v-model="search" style="max-width: 500px" placeholder="请输入数据" class="input-with-select">
      <template #prepend>
        <el-select v-model="select" placeholder="搜索条件" style="width: 115px">
          <el-option label="用户ID" value="0" />
        </el-select>
      </template>
      <template #append>
        <el-button :icon="Search"/>
      </template>
    </el-input>
    <el-button type="info" @click="reset">重置</el-button>
  </div>
  <el-table :data="tableData" border style="width: 100%" :show-overflow-tooltip=true>
    <el-table-column prop="infoid" label="ID" width="80" />
    <el-table-column prop="userid" label="用户ID" width="80" />
    <el-table-column prop="nickname" label="名称" width="80" />
    <el-table-column prop="time" label="" width="180">
      <template #default="scope">
        {{ convertDate(scope.row.time) }}
      </template>
    </el-table-column>
    <el-table-column prop="info" label="消息"/>

  </el-table>

  <div class="demo-pagination-block">
    <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 30, 80, 100]"
      :disabled="disabled" :background="true" layout="total, sizes, prev, pager, next, jumper" v-model:total='total'
      @size-change="handleSizeChange" @current-change="handleCurrentChange" />
  </div>
</template>

<script lang="ts" setup>
import { Search } from '@element-plus/icons-vue'
import { ref } from 'vue'
import { getAll } from '@/api/notes'
import { convertDate } from '@/utils/DateUntil'
const search = ref('')
//1 账号 2 ID
const select = ref()
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const disabled = ref(false)
interface message {
  infoid: number,
  userid:number,
  nickname:string,
  info:string,
  time:string
}

const tableData = ref<message[]>([])


const getInfo = () => {

  if(select.value==0){
    getAll(currentPage.value,pageSize.value).then((respon)=>{
    total.value = respon.data.data.total
    tableData.value = respon.data.data.list
  })
  }else{
    getAll(currentPage.value,pageSize.value).then((respon)=>{
    total.value = respon.data.data.total
    tableData.value = respon.data.data.list
  })
  }
}

const reset = ()=>{
  select.value = "全部";
  search.value = ""
  getInfo()

}

getInfo()

const handleSizeChange = () => {
  getInfo()
}
const handleCurrentChange = () => {
  getInfo()
}


</script>

<style scoped>
.el-pagination {
  justify-content: center;
  margin-top: 20px;
}
</style>
