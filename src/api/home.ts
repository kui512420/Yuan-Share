import Request from '@/utils/Request'
export function getCount(){
  return Request({
    url: 'api/getCount',
    method: 'get'
  })
}
//上传图片
export function uploadImg(type:number,file:File,id?:number,article_id?:number) {
  const formdata = new FormData()
  if(type==0){
    formdata.append("id",id+"")
  }else if(type==1){
    formdata.append("id",article_id+"")
  }
  formdata.append("type",type+"")
  formdata.append("fileInput",file)
  return Request({
    url: 'api/upload-url',
    method: 'post',
    data:formdata
  })
}
