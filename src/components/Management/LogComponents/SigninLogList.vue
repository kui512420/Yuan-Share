<template>
  <div class="mt-4" style="margin-bottom: 10px;">
    <el-input v-model="search" style="max-width: 500px" placeholder="请输入数据" class="input-with-select">
      <template #prepend>
        <el-select v-model="select" placeholder="搜索条件" style="width: 115px">
          <el-option label="全部" value="0" />
          <el-option label="账号" value="1" />
          <el-option label="ID" value="2" />
        </el-select>
      </template>
      <template #append>
        <el-button :icon="Search" @click="searchUser" />
      </template>
    </el-input>
    <el-button type="info" @click="reset">重置</el-button>
  </div>
  <el-table :data="tableData" border style="width: 100%" :show-overflow-tooltip=true>
    <el-table-column prop="id" label="ID" width="80" />
    <el-table-column prop="username" label="账号" width="180" />
    <el-table-column prop="signin_time" label="签到时间" width="180">
      <template #default="scope">
        {{ convertDate(scope.row.login_time) }}
      </template>
    </el-table-column>
    <el-table-column prop="award" label="获得奖励"/>
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
import { getLogin_logs } from '@/api/log'
import { convertDate } from '@/utils/DateUntil'
const search = ref('')
//1 账号 2 ID
const select = ref()
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const disabled = ref(false)
interface LoginLog {
  id: number,
  username: string,
  login_time: string,
  login_ip: string,
  login_agent: string
}

const tableData = ref<LoginLog[]>([])



const searchUser = () => {
  getLogin_logs(select.value, currentPage.value, pageSize.value, search.value).then((respon) => {
    total.value = respon.data.data.total
    tableData.value = respon.data.data.list
  })
}

const reset = ()=>{
  select.value = "全部";
  search.value = ""
  searchUser()

}

searchUser()

const handleSizeChange = () => {
  searchUser()
}
const handleCurrentChange = () => {
  searchUser()
}


</script>

<style scoped>
.el-pagination {
  justify-content: center;
  margin-top: 20px;
}
</style>
