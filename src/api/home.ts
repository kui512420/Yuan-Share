import Request from '@/utils/Request'
//获取后台管理的统计数量
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

/**
 * 获取用户收藏的图片
 * @returns 用户收藏的图片列表
 */
export function getFavoriteImages(){
  return Request({
    url: 'api/userFile/getFavoriteImages',
    method: 'get',
  })
}

/**
 * 添加图片到收藏
 * @param imageUrl 图片URL
 * @returns 添加结果
 */
export function addImageToFavorite(imageUrl: string){
  return Request({
    url: 'api/userFile/addFavoriteImage',
    method: 'post',
    data:{
      url:imageUrl
    }
  })
}

/**
 * 删除收藏的图片
 * @param id 收藏图片的ID
 * @returns 删除结果
 */
export function deleteFavoriteImage(id: number){
  return Request({
    url: 'api/userFile/del',
    method: 'get',
    params: {
      id: id
    }
  })
}
