import Request from '@/utils/Request'

//登录
export function login(formData: FormData) {
  return Request({
    url: 'api/user/login',
    method: 'post',
    data: formData,
  })
}
