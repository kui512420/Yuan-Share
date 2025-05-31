package space.kuikui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.kuikui.service.EmailService;
import space.kuikui.utils.CaptchaEmailUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kuikui
 * @date 2025/3/8 14:33
 */
@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;
    @Autowired
    private CaptchaEmailUtil captchaEmailUtil;

    @PostMapping("/send")
    public ResponseEntity<Map<String,Object>> sendEmail(@RequestBody Map<String, String> info) {
        String to = info.get("to");
        String subject = "【kuikui】验证码";
        String code = captchaEmailUtil.generateCaptcha(to);
        String content = "【kuikui】验证码: "+code+" 用于身份验证，1分钟内有效，请勿泄露和转发。如非本人操作，请忽略此短信。";
        boolean isSend = emailService.sendEmail(to,subject,content);
        Map<String,Object> map = new HashMap<>();
        map.put("isSend",isSend);
        return ResponseEntity.ok(map);
    }
}
