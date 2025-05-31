package space.kuikui.controller;

import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import space.kuikui.service.UserService;
import space.kuikui.utils.AiUtil;
import space.kuikui.utils.JwtLoginUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kuikui
 * @date 2025/3/10 22:10
 */
@RestController
@RequestMapping("/api/ai")
public class AiController {
    @Autowired
    private AiUtil aiUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtLoginUtil jwtLoginUtil;

    @GetMapping("/generate")
    public ResponseEntity<Map<String, Object>> chat(@RequestHeader(value = "token") String token,@RequestParam("prompt") String prompt,@RequestParam("size") String size) throws NoApiKeyException {
        ImageSynthesisResult result  = null;
        Map<String, Object> map = new HashMap<>();
        Integer id = null;
        try {
             id = (Integer) jwtLoginUtil.jwtPe(token).get("id");
            // 获取用户的余额
            Integer beans = userService.queryBean(id);
            // 余额充足
            if(beans<1){
                map.put("msg", "余额不足");
                return ResponseEntity.ok(map) ;
            }
            int last = beans-1;
            userService.updateBean(id,last);
            result = aiUtil.basicCall(prompt, id,size);
            map.put("msg", result);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(map) ;
    }

}
