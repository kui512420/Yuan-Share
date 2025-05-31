package space.kuikui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import space.kuikui.utils.CaptchaUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/api/code")
public class CaptchaController {

    @Autowired
    private CaptchaUtil captchaUtil;

    //显示验证码
    @RequestMapping("/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        captchaUtil.outCodeImg(request, response);
    }
    @RequestMapping("/check_captcha")
    public ResponseEntity<String> checkCaptchaInter(HttpServletRequest request, String input) {
        try {
            if (captchaUtil.validateCode(request, input)) {
                return ResponseEntity.ok("200");
            } else {
                return ResponseEntity.ok("验证失败~");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("异常！");
        }
    }
}
