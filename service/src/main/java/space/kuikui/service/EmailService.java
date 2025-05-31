package space.kuikui.service;

/**
 * @author kuikui
 * @date 2025/3/8 14:26
 */
public interface EmailService {

    public boolean sendEmail(String to, String subject, String content);

}
