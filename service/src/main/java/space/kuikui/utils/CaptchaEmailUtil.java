package space.kuikui.utils;

import cn.hutool.core.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import space.kuikui.service.EmailService;

import java.util.concurrent.TimeUnit;

/**
 * @author kuikui
 * @date 2025/3/8 20:43
 */
@Component
public class CaptchaEmailUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private EmailService emailService;

    public String generateCaptcha(String email) {
        String code = RandomUtil.randomString(4);
        redisTemplate.opsForValue().set(email+"-code", code,60000, TimeUnit.MILLISECONDS);
        return code;
    }
    public boolean cheak(String email, String cheakCode) {
        String code = (String) redisTemplate.opsForValue().get(email+"-code");
        return cheakCode==null?false:code.equals(cheakCode);
    }
}
