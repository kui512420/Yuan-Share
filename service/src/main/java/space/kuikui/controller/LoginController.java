package space.kuikui.controller;

import org.springframework.data.redis.core.RedisTemplate;
import space.kuikui.pojo.LoginLog;
import space.kuikui.service.LoginLogService;
import space.kuikui.utils.CaptchaUtil;
import space.kuikui.utils.IpUtil;
import space.kuikui.utils.JwtLoginUtil;
import space.kuikui.pojo.User;
import space.kuikui.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import space.kuikui.utils.ResultUtil;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import static space.kuikui.utils.IpUtil.getIpAddress;

@Controller
@RequestMapping("/api/user")
public class LoginController {
    //注入service
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private CaptchaUtil captchaUtil;
    @Autowired
    private ResultUtil resultUtil;
    @Autowired
    private IpUtil ipUtil;
    @Autowired
    private LoginLogService loginLogService;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            HttpServletRequest request,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("captaCode") String captaCode) {
        Map<String, Object> response = null;
        //验证验证码是否正确
        boolean validateCode = captchaUtil.validateCode(request,captaCode);
        //验证用户是否正常登录成功
        User user = userService.verifyLogin(username,password);

        if(!validateCode){
            response = resultUtil.getResult(202,"验证码错误",resultUtil.DATA_NOW);
        }else if(user==null){
            response = resultUtil.getResult(203,"账号不存在",resultUtil.DATA_NOW);
        }else if(user!=null){
            //用户状态是否正常
            int status = userService.getStatus(username);
            if(status==0){
                response = resultUtil.getResult(204,"账号被停用，请联系管理员",resultUtil.DATA_NOW);
            }else{
                JwtLoginUtil jl = new JwtLoginUtil();
                String token = jl.jwtBd(user);
                //把token缓存到redis中
                redisTemplate.opsForValue().set(username+"-token",token);
                response = resultUtil.getResult(200,"登录成功",token);
                //更新用户最新登录时间
                Timestamp timestamp = new Timestamp(new Date().getTime());
                LoginLog loginLog = new LoginLog();
                loginLog.setUsername(username);
                loginLog.setLogin_agent(request.getHeader("User-Agent"));
                loginLog.setLogin_time(timestamp);
                loginLog.setLogin_ip(getIpAddress(request));
                loginLogService.addLoginLog(loginLog);
                userService.updateLoginTime(username,timestamp);
            }
        }else{
            response = resultUtil.getResult(201,"登录失败,账号或密码错误",resultUtil.DATA_NOW);
        }
        return ResponseEntity.ok(response);
    }

}
