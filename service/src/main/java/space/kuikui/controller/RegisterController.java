package space.kuikui.controller;

import org.springframework.data.redis.core.RedisTemplate;
import space.kuikui.utils.CaptchaEmailUtil;
import space.kuikui.utils.CaptchaUtil;
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/api/user")
public class RegisterController {

    //注入service
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private JwtLoginUtil jwtLoginUtil;
    @Autowired
    private ResultUtil resultUtil;
    @Autowired
    private CaptchaUtil captchaUtil;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private CaptchaEmailUtil captchaEmailUtil;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            HttpServletRequest request,
            @RequestBody Map<String,Object> mapData
            ) {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        // 用户名正则表达式
        String usernameRegex = "^[a-zA-Z0-9]{6,20}$";
        // 密码正则表达式
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{6,20}$";
        // 昵称正则表达式
        String nicknameRegex = "^[a-zA-Z0-9_\\u4e00-\\u9fa5]{1,10}$";

        Map<String, Object> result = new LinkedHashMap<>();
        User u = new User();
        String userName =  mapData.get("username").toString();
        String password =  mapData.get("password").toString();
        String nickName =  mapData.get("nickname").toString();
        u.setUsername(userName);
        u.setPassword(password);
        u.setNickname(nickName);
        boolean isUsernameValid = Pattern.matches(usernameRegex, userName);
        boolean isPasswordValid = Pattern.matches(passwordRegex, password);
        boolean isNicknameValid = Pattern.matches(nicknameRegex, nickName);
        if(!isUsernameValid){
            result = resultUtil.getResult(201,"账号由字母、数字组成，长度在 6 到 20 个字符之间",resultUtil.DATA_NOW);
            return ResponseEntity.ok(result);
        }else if(!isPasswordValid){
            result = resultUtil.getResult(201,"密码长度在 6 到 20 个字符之间，至少包含一个大写字母、一个小写字母和一个数字",resultUtil.DATA_NOW);
            return ResponseEntity.ok(result);
        }else if(!isNicknameValid){
            result = resultUtil.getResult(201,"名称由中文、字母、数字和下划线组成，长度在 2 到 15 个字符之间。",resultUtil.DATA_NOW);
            return ResponseEntity.ok(result);
        }


        u.setType(1);
        String email =  mapData.get("email").toString();
        u.setEmail(email);
        String captaCode = mapData.get("captaCode").toString();
        boolean isEmailCheak = false;
        try{
            isEmailCheak = captchaEmailUtil.cheak(email,captaCode);
        }catch (Exception e){
            result = resultUtil.getResult(202,"验证码过期",resultUtil.DATA_NOW);
            return ResponseEntity.ok(result);
        }
        u.setRegister_time(timestamp);
        boolean isRegister = userService.isExist(u.getUsername());
        if(isEmailCheak){
            if(isRegister){
                result = resultUtil.getResult(201,"账号已经存在",resultUtil.DATA_NOW);
            }else{
                JwtLoginUtil jl = new JwtLoginUtil();
                userService.insertUser(u);
                User uu = userService.queryByUsername(u.getUsername());
                String token = jl.jwtBd(uu);
                redisTemplate.opsForValue().set(userName+"-token",token);
                result = resultUtil.getResult(200,"注册成功",token);
            }
        }else{
            result = resultUtil.getResult(202,"验证码错误",resultUtil.DATA_NOW);
        }

        return ResponseEntity.ok(result);
    }

}
