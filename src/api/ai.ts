import Request from '@/utils/Request'
//添加文章
export function getModelsList(){
  return Request({
    url: 'api/ai/getModelsList',
    method: 'get'
  })
}

export function chat(modelName:string,msg:string){
  return Request({
    url: 'api/ai/chat',
    method: 'post',
    data:{
      modelName:modelName,
      msg:msg
    }
  })
}
