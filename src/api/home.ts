import Request from '@/utils/Request'
export function getCount(){
  return Request({
    url: 'api/getCount',
    method: 'get',
  })
}
//上传图片
export function uploadImg(formdata:FormData) {
  return Request({
    url: 'api/upload-url',
    method: 'post',
    data:formdata,
    headers:{
      "Content-Type":"multipart/form-data"
    }
  })
}
/**
 *
 * @returns 当前登录用户的信息
 */
export function getUserInfo(){
  return Request({
    url: 'api/users/getUserInfo',
    method: 'get',
  })
}
