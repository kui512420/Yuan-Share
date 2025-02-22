import Request from '@/utils/Request'
//登录日志
export function getLogin_logs(type:string,nowPage:number,pageSize:number,parm?:string){
  return Request({
    url:"api/users/getLoginLogs",
    method:"get",
    params:{
      type:type,
      parm:parm,
      pageNow:nowPage,
      pageSize:pageSize
    }
  })
}
