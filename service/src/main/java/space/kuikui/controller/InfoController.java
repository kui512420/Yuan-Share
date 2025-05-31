package space.kuikui.controller;


import space.kuikui.utils.JwtLoginUtil;
import space.kuikui.pojo.Info;
import space.kuikui.service.impl.InfoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/info")
public class InfoController {
    @Autowired
    private InfoServiceImpl infoService;
    @GetMapping("/get")
    public ResponseEntity<Map<String, Object>> getinfo() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("code", "200");
        response.put("msg","获取成功");
        response.put("data", infoService.getInfo());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAll")
    public ResponseEntity<Map<String, Object>> getinfoAll(@RequestParam(value = "pageNow",defaultValue = "1") Integer pageNow,
            @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("code", "200");
        response.put("msg","获取成功");
        response.put("data", infoService.getInfoAll(pageNow,pageSize));
        return ResponseEntity.ok(response);
    }
    @GetMapping("/del")
    public ResponseEntity<Map<String, Object>> delinfo() {

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("code", "200");
        response.put("msg", infoService.delInfo());
        response.put("date", new Date().getTime());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addinfo(@RequestBody Map<String,String> map,@RequestHeader(value = "token") String token) {
        JwtLoginUtil jwtLoginUtil = new JwtLoginUtil();
        int userid1 = Integer.parseInt(jwtLoginUtil.jwtPe(token).get("id").toString());
        //获取发布者的username
        String nickname = jwtLoginUtil.jwtPe(token).get("nickname").toString();
        Timestamp timestamp = new Timestamp(new Date().getTime());
        Info info1 = new Info();
        info1.setUserid(userid1);
        info1.setNickname(nickname);
        info1.setTime(timestamp);
        info1.setInfo(map.get("info"));
        boolean isSet = infoService.setInfo(info1);
        Map<String, Object> response = new LinkedHashMap<>();
        if(isSet){
            response.put("code", "200");
            response.put("msg", "添加成功");
        }else{
            response.put("code", "500");
            response.put("msg", "添加失败");
        }
        response.put("date", new Date().getTime());
        return ResponseEntity.ok(response);
    }

}
