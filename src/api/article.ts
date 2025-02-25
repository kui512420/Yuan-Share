import Request from '@/utils/Request'
//添加文章
export function add(article_title:string,article_cover:string,article_tag:string[],article_content:string,article_type:number){
  return Request({
    url: 'api/article/add',
    method: 'post',
    data:{
      article_title:article_title,
      article_cover:article_cover,
      article_tag:article_tag,
      article_content:article_content,
      article_type:article_type
    }
  })
}
//获取文章列表
export function get(pagenow:number,pageCount:number,type:number,inputData?:string,article_type?:number,article_status?:number){
  let params = {}
  if(type==1){
    params = {
      pagenow:pagenow,
      pageCount:pageCount,
      type:type,
      article_id:inputData
    }
  }else if(type==2){
    params = {
      pagenow:pagenow,
      pageCount:pageCount,
      type:type,
      article_title:inputData
    }
  }else if(type==3){
    params = {
      pagenow:pagenow,
      pageCount:pageCount,
      type:type,
      article_tag:inputData
    }
  }else if(type==4){
    params = {
      pagenow:pagenow,
      pageCount:pageCount,
      type:type,
      article_authorid:inputData
    }
  }else if(type==5){
    params = {
      pagenow:pagenow,
      pageCount:pageCount,
      type:type,
      article_type:article_type
    }
  }else{
    params = {
      pagenow:pagenow,
      pageCount:pageCount,
      type:type,
      article_status:article_status
    }
  }
  return Request({
    url: 'api/article/getAll',
    method: 'get',
    params:params
  })
}
//删除单个
export function delOne(id:number){
  return Request({
    url: 'api/article/delOne',
    method: 'get',
    params:{
      id:id
    }
  })
}
//批量删除文章
export function delArr(idArr:string){
  return Request({
    url:"api/article/delArr",
    method:"post",
    data:"idArr="+idArr
  })
}
//通过id查询文章
export function findOne(id:number){
  return Request({
    url:"api/article/findOne",
    method:"get",
    params:{
      id:id
    }
  })
}
//更新文章状态
export function updateStatus(id:number,status:number){

  return Request({
    url:"api/article/updateStatus",
    method:"get",
    params:{
      id:id,
      status:status
    }
  })
}
//评论
export function commentAdd(user_id:number,comment_content:string,article_id:number,){

  return Request({
    url:"api/comment/add",
    method:"post",
    data:{
      user_id:user_id,
      comment_content:comment_content,
      article_id:article_id
    }
  })
}
//获取评论
export function commentGet(article_id:number){

  return Request({
    url:"api/comment/getComment",
    method:"get",
    params:{
      article_id:article_id
    }
  })
}
//模糊查询
export function like(pagenow:number,pageCount:number,txt:string){

  return Request({
    url:"api/article/like",
    method:"get",
    params:{
      pagenow:pagenow,
      pageCount:pageCount,
      txt:txt
    }
  })
}
