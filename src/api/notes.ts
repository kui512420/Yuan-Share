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

/**
 * 获取聊天消息
 * @param pageSize 每页消息数量，默认10
 * @param pageNum 页码，默认1
 * @returns 消息列表
 */
export function get(pageSize: number = 10, pageNow: number = 1) {
  return Request({
    url: 'api/info/getAll',
    method: 'get',
    params: {
      pageSize,
      pageNow
    }
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
