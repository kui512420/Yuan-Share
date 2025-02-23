<template>
  <div class="List-top">
    <el-select v-model="userType" placeholder="用户类型" style="width: 120px" @change="refreshListByT">
      <el-option v-for="item in userTypes" :key="item" :label="item" :value="item" />
    </el-select>

    <div class="mt-4">
      <el-input v-model="input3" style="max-width: 500px" placeholder="请输入数据" class="input-with-select">
        <template #prepend>
          <el-select v-model="select" placeholder="搜索条件" style="width: 115px">
            <el-option label="账号" value="1" />
            <el-option label="ID" value="2" />
          </el-select>
        </template>
        <template #append>
          <el-button :icon="Search" @click="searchUser" />
        </template>
      </el-input>
    </div>

    <el-button type="danger" @click="delMore(selectionarr)">批量删除</el-button>
    <el-button type="info" @click="reset">重置</el-button>
  </div>



  <el-table :data="tableData" style="width: 100%;height: 75vh;" :border=true @selection-change="selection">
    <el-table-column type="selection" width="55" />
    <el-table-column label="序号" width="50">
      <template #default="scope">
        <!-- 当前页码减 1 乘以每页条数，再加上当前行的索引加 1 -->
        {{ (currentPage1 - 1) * pageSize1 + scope.$index + 1 }}
      </template>
    </el-table-column>
    <el-table-column property="id" label="用户ID" width="76" />
    <el-table-column property="username" label="账号" />
    <el-table-column property="nickname" label="名称" />
    <el-table-column property="headsrc" label="用户头像" width="123">
      <template #default="scope">
        <div class="demo-image__preview">
          <el-image style="width: 100px; height: 100px;border-radius: 50%;" :src="scope.row.headsrc"
            :zoom-rate="1.2" :max-scale="7" :min-scale="0.2" :preview-src-list="[scope.row.headsrc]"
            :initial-index="4" fit="cover" :preview-teleported=true />
        </div>
      </template>
    </el-table-column>
    <el-table-column property="type" label="用户类型">
      <template #default="scope">
        {{ scope.row.type === 1 ? '普通用户' : scope.row.type === 0 ? '管理员' : scope.row.type }}
      </template>
    </el-table-column>
    <el-table-column property="status" label="用户状态" width="88">
      <template #default="scope">
        <el-tag :type="scope.row.status === 1 ? 'primary' : 'danger'">
          {{ scope.row.status === 1 ? '正常' : '停用' }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column property="bean" label="币余额" width="70" />
    <el-table-column property="last_login" label="最后登录时间">
      <template #default="scope">
        {{ convertDate(scope.row.last_login) }}
      </template>
    </el-table-column>
    <el-table-column property="register_time" label="注册时间">
      <template #default="scope">
        {{ convertDate(scope.row.register_time) }}
      </template>
    </el-table-column>
    <el-table-column property="option" label="操作" width="210">
      <template #default="scope">
        <el-button type="info" :icon="Edit" @click="edit(scope.row.id)" circle />
        <el-button type="danger" :icon="Delete" @click="del(scope.row.id)" circle />
        <el-dropdown style="margin-left: 5px;">
          <el-button size="small">
            更多<el-icon class="el-icon--right">
              <ArrowDown />
            </el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="upStatus(scope.row.id, scope.row.username, 1)">启用</el-dropdown-item>
              <el-dropdown-item @click="upStatus(scope.row.id, scope.row.username, 0)">停用</el-dropdown-item>
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

  <el-drawer v-model="drawer" title="编辑用户" :direction="direction" :before-close="handleClose">
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
      <el-form-item label="状态">
        <el-switch v-model="editStatus" />
      </el-form-item>
      <el-form-item label="类型">
        <div class="mb-2 ml-4">
          <el-radio-group v-model="radio1">
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
  </el-drawer>
</template>

<script lang="ts" setup>
import { ElTable, ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import { reactive, ref } from 'vue'
import { findAll, delOne, findOne, delArr, updateStatus, findType, queryById, queryByUsername, updateInfo } from '@/api/users'
import { Search, Edit, Delete, Check, Plus } from '@element-plus/icons-vue'
import type { DrawerProps, UploadProps } from 'element-plus'
import { isNumber } from 'element-plus/es/utils/types.mjs'
import { uploadImg } from '@/api/home'
import { convertDate } from '@/utils/DateUntil'
// 定义用户信息接口

const tableData = ref([])
const total = ref(0)
const currentPage1 = ref(1)

const pageSize1 = ref(10)
const input3 = ref('')
const select = ref('')
const userType = ref('')
const userTypes = ["全部用户", "普通用户", "管理员"]
const disabled = ref(false)

const drawer = ref(false)
const direction = ref<DrawerProps['direction']>('rtl')
const editStatus = ref(true)
const selectionarr = ref<number[]>([])
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

interface editFormDatain {
  id: number,
  username: string,
  password: string,
  nickname:string
  headsrc: string,
  status: number,
  type: number,
  register_time: string,
  last_login: string,
  bean: number
}

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
  if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png' && rawFile.type !== 'image/gif' && rawFile.type !== 'image/jpg') {
    ElMessage.error('只能上传【image/jpeg,image/png,image/gif,image/jpg】格式的图片')
    return false
  } else if (rawFile.size / 1024 / 1024 > 10) {
    ElMessage.error('最大 10MB!')
    return false
  }
  return true
}

//编辑抽屉关闭方法
const handleClose = (done: () => void) => {
  ElMessageBox.confirm('确认要关闭？', '提示', {
    confirmButtonText: '是，关闭',
    cancelButtonText: '否，不关闭',
    type: 'warning'
  })
    .then(() => {
      done()
      refreshList()
    })
    .catch(() => {
      // catch error
    })
}

//刷新列表
const refreshList = () => {
  findAll("register_time", currentPage1.value, pageSize1.value).then((respon) => {
    if (JSON.stringify(respon.data.users) !== undefined) {
      const JsonUser = JSON.parse(JSON.stringify(respon.data.users))
      tableData.value = JsonUser.list
      total.value = parseInt(JsonUser.total)
    }
  })
}
//刷新列表-Type
const refreshListByType = (type: number) => {
  findType(type, currentPage1.value, pageSize1.value).then((respon) => {
    const JsonUser = JSON.parse(JSON.stringify(respon.data.data))
    tableData.value = JsonUser.list
    total.value = parseInt(JsonUser.total)
  })
}
refreshList()
//分页回调
const handleSizeChange = () => {
  if (userType.value == "普通用户" || userType.value == "管理员") {
    if (userType.value == "普通用户") {
      refreshListByType(1)
    } else if (userType.value == "管理员") {
      refreshListByType(0)
    }
  } else {
    refreshList()
  }
}
const handleCurrentChange = () => {
  if (userType.value == "普通用户" || userType.value == "管理员") {
    if (userType.value == "普通用户") {
      refreshListByType(1)
    } else if (userType.value == "管理员") {
      refreshListByType(0)
    }
  } else {
    refreshList()
  }
}


//删除单个用户
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
//编辑单个用户
const edit = (id: number) => {



  findOne(id).then((respon) => {
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
      //打开抽屉
      drawer.value = true
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
    selectionarr.value.push(element.id)
  });
}
//更新状态
const upStatus = (id: number, username: string, status: number) => {
  updateStatus(id, username, status).then((respon) => {
    if (status == 1) {
      if (respon.data.data == true) {
        ElMessage({
          message: "启用成功",
          type: 'success',
        })
      } else {
        ElMessage({
          message: "启用失败",
          type: 'error',
        })
      }

    } else {
      if (respon.data.data == true) {
        ElMessage({
          message: "停用成功",
          type: 'success',
        })
      } else {
        ElMessage({
          message: "停用失败",
          type: 'error',
        })
      }
    }
    if (userType.value == "普通用户" || userType.value == "管理员") {
      if (userType.value == "普通用户") {
        refreshListByType(1)
      } else if (userType.value == "管理员") {
        refreshListByType(0)
      }
    } else {
      refreshList()
    }
  })
}
//刷新列表-类型
const refreshListByT = (newValue: string) => {
  if (newValue == "全部用户") {
    refreshList()
  } else if (newValue == "普通用户") {
    refreshListByType(1)
  } else if (newValue == "管理员") {
    refreshListByType(0)
  }
}
//搜索
const searchUser = () => {
  if (input3.value == "") {
    ElMessage({
      type: "warning",
      message: "请输入数据"
    })
  } else {
    userType.value = "全部用户"
    if (select.value == "1") {
      queryByUsername(input3.value).then((respon) => {
        const JsonUser = JSON.parse(JSON.stringify(respon.data.data))
        if (JsonUser + "" == "null") {
          ElMessage({
            type: "warning",
            message: "用户不存在"
          })
        } else {
          tableData.value = [JsonUser as never]
          ElMessage({
            type: "success",
            message: "查询成功"
          })
        }
      })
    } else if (select.value == "2") {
      if (isNumber(input3.value)) {
        queryById(parseInt(input3.value)).then((respon) => {
          const JsonUser = JSON.parse(JSON.stringify(respon.data.data))
          if (JsonUser + "" == "null") {
            ElMessage({
              type: "warning",
              message: "用户不存在"
            })
          } else {
            tableData.value = [JsonUser as never]
            ElMessage({
              type: "success",
              message: "查询成功"
            })
          }
        })
      } else {
        ElMessage({
          type: "error",
          message: "请输入数字"
        })
      }

    }
  }

}
//重置查询
const reset = () => {
  input3.value = ""
  select.value = "1"
  userType.value = "全部用户"
  refreshList()
}
//修改部分信息
const updateInfos = () => {
  let statusnum = 8
  if (editStatus.value) {
    statusnum = 1
  } else {
    statusnum = 0
  }
  updateInfo(parseInt(editFormData.id), editFormData.username, statusnum, parseInt(radio1.value), editFormData.password,editFormData.nickname).then((respon) => {

    if (respon.data.data == true) {
      ElMessage.success({
        message: "修改信息成功"
      })
      drawer.value = false
      setTimeout(() => {
        refreshList()
      }, 1000)

    }
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
