import Request from '@/utils/Request'
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
export function get(pagenow:number,pageCount:number,type:number,inputData?:string,article_type?:number){
  let params = {}
  /*
{
      pagenow:pagenow,
      pageCount:pageCount,
      type:type,
      title:inputData,
      article_id:inputData,
      tag:inputData,
      auth_id:inputData,
      article_type:article_type
    }
  */
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
      type:type
    }
  }
  return Request({
    url: 'api/article/getAll',
    method: 'get',
    params:params
  })
}
export function delOne(id:number){
  return Request({
    url: 'api/article/delOne',
    method: 'get',
    params:{
      id:id
    }
  })
}
export function delArr(idArr:string){
  return Request({
    url:"api/article/delArr",
    method:"post",
    data:"idArr="+idArr
  })
}
export function findOne(id:number){
  return Request({
    url:"api/article/findOne",
    method:"get",
    params:{
      id:id
    }
  })
}
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
