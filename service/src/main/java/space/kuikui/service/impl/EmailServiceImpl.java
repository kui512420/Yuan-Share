package space.kuikui.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import space.kuikui.service.EmailService;

/**
 * @author kuikui
 * @date 2025/3/8 14:27
 */
@Component
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;
    /**
     *
     * @param to 发送给谁
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    @Override
    public boolean sendEmail(String to, String subject, String content) {

        try{
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(to);
            email.setSubject(subject);
            email.setFrom(from);
            email.setText(content);
            mailSender.send(email);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
}
