import Request from '@/utils/Request'

//注册
export function register(username:string,password:string,nickname:string,captaCode:string) {
  return Request({
    url: 'api/user/register',
    method: 'post',
    data: {
      username:username,
      password:password,
      captaCode:captaCode,
      nickname:nickname
    }
  })
}
