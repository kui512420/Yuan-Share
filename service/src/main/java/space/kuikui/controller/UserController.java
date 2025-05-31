package space.kuikui.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import retrofit2.http.GET;
import space.kuikui.pojo.LoginLog;
import space.kuikui.pojo.User;
import space.kuikui.service.LoginLogService;
import space.kuikui.service.UserService;
import space.kuikui.utils.ExportUtil;
import space.kuikui.utils.JwtLoginUtil;
import space.kuikui.utils.ResultUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private ResultUtil resultUtil;
    @Autowired
    private JwtLoginUtil jwtLoginUtil;
    @Autowired
    private LoginLogService loginLogService;
    @Autowired
    private ExportUtil exportUtil;

    @PostMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        List<User> users1 = userService.queryUser();
        exportUtil.export(response,"xxx.xls",users1,User.class);
    }



    @GetMapping("/getUserInfo")
    public ResponseEntity<Map<String, Object>> findAll(
        @RequestHeader("token") String token
    ) {
        User u = new User();
        Map<Object, Object> map = jwtLoginUtil.jwtPe(token);
        u.setNickname(map.get("nickname").toString());
        u.setId((int)(map.get("id")));
        u.setUsername(map.get("username").toString());
        u.setHeadsrc(map.get("headsrc")+"");
        u.setType((int)map.get("type"));
        return ResponseEntity.ok(resultUtil.getResult(200,"获取信息成功",u));
    }

    @GetMapping("/findAll")
    public ResponseEntity<Map<String, Object>> findAll(
            @RequestParam(value = "orderBy",defaultValue = "register_time",required = false) String orderBy,
            @RequestParam(value = "pageNow",defaultValue = "1") Integer pageNow,
            @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize
    ) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("users", userService.findAll(orderBy, pageNow, pageSize));
        return ResponseEntity.ok(response);
    }


    @GetMapping("/del")
    public ResponseEntity<Map<String, Object>> findAll(
            @RequestParam(value = "id") Integer id
    ) {
        Map<String, Object> response = new LinkedHashMap<>();
        int data =  userService.delByID(id);
        response.put("code", 200);
        response.put("msg","删除成功");
        response.put("data",data);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/delArr")
    public ResponseEntity<Map<String, Object>> delAll(
            @RequestParam(value = "idArr") String[] idArr)
    {

        Map<String, Object> response = new LinkedHashMap<>();
        int data =  userService.delByIDArr(idArr);
        response.put("code", 200);
        response.put("msg","删除成功");
        response.put("data",data);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/findOne")
    public ResponseEntity<Map<String, Object>> findOne(
            @RequestParam(value = "id") Integer id
    ) {
        Map<String, Object> response = new LinkedHashMap<>();
        User data =  userService.findOne(id);
        response.put("code", 200);
        response.put("msg","查询成功");
        response.put("data",data);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/find")
    public ResponseEntity<Map<String, Object>> find(
            @RequestParam(value = "id") Integer id,
            @RequestHeader String token
    ) {
        Map<String, Object> response = new LinkedHashMap<>();
        try{
            if(jwtLoginUtil.jwtPe(token).get("id").equals(id)){
                User data =  userService.findOne(id);
                response.put("code", 200);
                response.put("msg","查询成功");
                response.put("data",data);
            }else{
                response.put("code", 200);
                response.put("msg","查询失败");
                response.put("data","");
            }
        }catch (Exception e){
            e.printStackTrace();
            User data =  userService.findOne(id);
            response.put("code", 200);
            response.put("msg","非法操作");
            response.put("data","");
        }
        return ResponseEntity.ok(response);
    }
    @GetMapping("/getLoginLogs")
    public ResponseEntity<Map<String, Object>> getLoginLogs(
            @RequestParam(value = "type",defaultValue = "3") String type,
            @RequestParam(value = "pageNow",defaultValue = "1") Integer pageNow,
            @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
            @RequestParam(value = "parm",required = false) String parm
    ) {
        Map<String, Object> response = new LinkedHashMap<>();
        PageInfo<LoginLog> data =  loginLogService.getLoginLog(type,parm,pageNow,pageSize);
        response.put("code", 200);
        response.put("msg","查询成功");
        response.put("data",data);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/findType")
    public ResponseEntity<Map<String, Object>> findType(
            @RequestParam(value = "type") Integer type,
            @RequestParam(value = "pageNow",defaultValue = "1") Integer pageNow,
            @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize
    ) {
        Map<String, Object> response = new LinkedHashMap<>();
        PageInfo<User> data =  userService.findType(type,pageNow,pageSize);
        response.put("code", 200);
        response.put("msg","查询成功");
        response.put("data",data);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/queryById")
    public ResponseEntity<Map<String, Object>> queryById(
            @RequestParam(value = "id") Integer id
    ) {
        Map<String, Object> response = new LinkedHashMap<>();
        User data =  userService.queryById(id);
        response.put("code", 200);
        response.put("msg","查询成功");
        response.put("data",data);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/queryByUsername")
    public ResponseEntity<Map<String, Object>> queryByUsername(
            @RequestParam(value = "username") String username
    ) {
        Map<String, Object> response = new LinkedHashMap<>();
        User data =  userService.queryByUsername(username);
        response.put("code", 200);
        response.put("msg","查询成功");
        response.put("data",data);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/updateStatus")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @RequestBody Map<String,Object> map
    ) {

        Map<String, Object> response = new LinkedHashMap<>();
        boolean data =  userService.updateStatus((int)map.get("id"),(String) map.get("username"),(int)map.get("status"));
        response.put("code", 200);
        response.put("msg","更新状态成功");
        response.put("data",data);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/updateInfo")
    public ResponseEntity<Map<String, Object>> updateInfo(
            @RequestBody Map<String,Object> map
    ) {
        Map<String, Object> response = new LinkedHashMap<>();
        boolean data =  userService.updateInfo((Integer) map.get("id"),(String) map.get("username") ,(Integer) map.get("type"), (Integer) map.get("status"), (String) map.get("password"),(String) map.get("nickname"));
        response.put("code", 200);
        response.put("msg","更新信息成功");
        response.put("data",data);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/updateInfo2")
    public ResponseEntity<Map<String, Object>> updateInfo2(
            @RequestBody Map<String,Object> map,
            @RequestHeader String token
    ) {
        Map<String, Object> response = new LinkedHashMap<>();
        try{
            if(jwtLoginUtil.jwtPe(token).get("id").equals(map.get("id"))){
                boolean data =  userService.updateInfo((Integer) map.get("id"),(String) map.get("username") ,(Integer) map.get("type"), (Integer) map.get("status"), (String) map.get("password"),(String) map.get("nickname"));
                response.put("code", 200);
                response.put("msg","更新信息成功");
                response.put("data",data);
            }else{
                response.put("code", 201);
                response.put("msg","非法操作");
                response.put("data","");
            }
        }catch (Exception e){
            e.printStackTrace();
            response.put("code", 201);
            response.put("msg","非法操作");
            response.put("data","");
            return ResponseEntity.ok(response);

        }


        return ResponseEntity.ok(response);
    }
}
