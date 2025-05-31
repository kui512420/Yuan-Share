package space.kuikui.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import space.kuikui.pojo.Sign;
import space.kuikui.pojo.User;
import space.kuikui.service.SignService;
import space.kuikui.service.impl.SignServiceImpl;
import space.kuikui.utils.CaptchaUtil;
import space.kuikui.utils.JwtLoginUtil;
import space.kuikui.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import space.kuikui.utils.ResultUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

@RestController
@RequestMapping("/api/user")
public class HomeController {

    //注入service
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private SignServiceImpl signService;
    @Autowired
    private ResultUtil resultUtil;

    @PostMapping("/getinfo")
    public ResponseEntity<Map<String, Object>> getinfo(
            @RequestParam("token") String token) {

        JwtLoginUtil jwtLoginUtil = new JwtLoginUtil();
        Map<String, Object> response = new LinkedHashMap<>();
        Map<String, Object> info = new LinkedHashMap<>();
        response.put("code", "200");
        int userid = (int) jwtLoginUtil.jwtPe(token).get("id");
        User user = userService.queryById(userid);
        info.put("id",user.getId());
        info.put("username",user.getUsername());
        info.put("imgsrc",user.getHeadsrc());

        response.put("msg", info);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getinfoByid")
    public ResponseEntity<Map<String, Object>> getinfoByid(
            @RequestParam("userid") Integer userid) {
        System.out.println(userid);
        User u = userService.queryById(userid);
        Map<String, Object> response = new LinkedHashMap<>();
        List<String> li = new ArrayList<>();

        li.add(u.getUsername());
        li.add(u.getHeadsrc());

        response.put("code", "200");
        response.put("msg",li);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/signinInfo")
    public ResponseEntity<Map<String, Object>> signIn(
            @RequestHeader(value = "Token") String token) {
        Map<String, Object> response = new LinkedHashMap<>();
        int userid = (int) new JwtLoginUtil().jwtPe(token).get("id");
        List<String> li = new ArrayList<>();

        boolean isSignIn = signService.cheak(userid);
        if (isSignIn) {
            response.put("code", "210");
            li.add("已经签到获得："+signService.findSignByUserid(userid+"").getBean());
        }else{
            Sign sign = new Sign();
            Integer count = userService.queryBean(userid);
            //随机生成数字代表今天获得的币数
            int randomBean = (int) Math.floor(Math.random()*5) + 1;
            sign.setBean(randomBean);
            sign.setTime(new Timestamp(new Date().getTime()));
            sign.setUserid(userid);
            signService.insertInfo(sign);
            if(count==null){
                count = 0;
            }
            int endCount = count+randomBean;
            userService.updateBean(userid,endCount);
            response.put("code", 211);
            li.add("签到成功，获得："+randomBean+"个币");
        }
        response.put("msg", li);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/signin")
    public ResponseEntity<Map<String, Object>> GetSigninInfo(
        @RequestParam("pagenow") Integer pagenow,
        @RequestParam("pagesize") Integer pagesize,
            @RequestParam(value = "userid",required = false) String userid
    ){
        Map<String, Object> response = new LinkedHashMap<>();
        PageHelper.startPage(pagenow,pagesize);
        List<Sign> signs = null;
        if(userid==null){
            signs = signService.getSigns();
        }else{
            signs = signService.findSignsByUserid(userid);
        }

        PageInfo<Sign> pageInfo = new PageInfo<>(signs);
        response = resultUtil.getResult(200,"获取成功",pageInfo);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/queryBean")
    @CrossOrigin//允许跨域请求
    public ResponseEntity<Map<String, Object>> queryBean(
            @RequestParam(value = "token") String token) {
        Map<String, Object> response = new LinkedHashMap<>();
        int userid = (int) new JwtLoginUtil().jwtPe(token).get("id");
        List<String> li = new ArrayList<>();
        Integer count = userService.queryBean(userid);

        li.add(String.valueOf(count));

        response.put("code", "200");
        response.put("msg", li);

        return ResponseEntity.ok(response);
    }
}
