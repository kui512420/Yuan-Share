import Request from '@/utils/Request'
//添加留言
export function add(info:string){
  return Request({
    url:"api/info/add",
    method:"post",
    data:{
      info:info
    }
  })
}
//获取留言
export function get(){
  return Request({
    url:"api/info/get",
    method:"get"
  })
}
//留言列表
export function getAll(pageNow:number,pageSize:number){
  return Request({
    url:"api/info/getAll",
    method:"get",
    params:{
      pageNow:pageNow,
      pageSize:pageSize
    }
  })
}
