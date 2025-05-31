package space.kuikui.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "login_log")
public class LoginLog {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String username;
    private String login_ip;
    private Timestamp login_time;
    private String login_agent;
}
