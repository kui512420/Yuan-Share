import Request from '@/utils/Request'
//查询部分用户
export function findAll(orderBy:string,pageNow:number,pageSize:number) {
  return Request({
    url: 'api/users/findAll',
    method: 'get',
    params:{
      orderBy:orderBy,
      pageNow:pageNow,
      pageSize:pageSize
    }
  })
}
//查询单个用户(包含密码)
export function findOne(id:number) {
  return Request({
    url: 'api/users/findOne',
    method: 'get',
    params:{
      id:id
    }
  })
}

//通过id查询用户
export function queryById(id:number) {
  return Request({
    url: 'api/users/queryById',
    method: 'get',
    params:{
      id:id
    }
  })
}
//通过username查询用户
export function queryByUsername(username:string) {
  return Request({
    url: 'api/users/queryByUsername',
    method: 'get',
    params:{
      username:username
    }
  })
}
//查询类型用户
export function findType(type:number,pageNow:number,pageSize:number) {
  return Request({
    url: 'api/users/findType',
    method: 'get',
    params:{
      type:type,
      pageNow:pageNow,
      pageSize:pageSize
    }
  })
}

//删除单个用户
export function delOne(id:number) {
  return Request({
    url: 'api/users/del',
    method: 'get',
    params:{
      id:id
    }
  })
}

//删除多个用户
export function delArr(idArr:string) {
  return Request({
    url: 'api/users/delArr',
    method: 'post',
    data:"idArr="+idArr
  })
}
//更新用户状态
export function updateStatus(id:number,username:string,status:number) {
  return Request({
    url: 'api/users/updateStatus',
    method: 'put',
    data:{
      id:id,
      username:username,
      status:status
    }
  })
}

//更新用户部分信息
export function updateInfo(id:number,username:string,status:number,type:number,password:string) {
  return Request({
    url: 'api/users/updateInfo',
    method: 'put',
    data:{
      id:id,
      username:username,
      status:status,
      type:type,
      password:password
    }
  })
}
//签到
export function signIn(pagenow:number,pagesize:number,userid?:string) {
  const params1={
    pagenow:pagenow,
    pagesize:pagesize,
  }
  const params2={
    pagenow:pagenow,
    pagesize:pagesize,
    userid:userid
  }

  return Request({
    url: 'api/user/signin',
    method: 'get',
    params:userid==undefined?params1:params2
  })
}
