import Request from '@/utils/Request'

//注册
export function register(username:string,password:string,email:string,nickname:string,captaCode:string) {
  return Request({
    url: 'api/user/register',
    method: 'post',
    data: {
      username:username,
      password:password,
      email:email,
      captaCode:captaCode,
      nickname:nickname
    }
  })
}
//发送邮箱
export function send(to:string) {
  return Request({
    url: 'api/email/send',
    method: 'post',
    data: {
      to: to
    }
  })
}

