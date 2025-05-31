package space.kuikui.pojo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data//自动生成构造器和get/set方法
@TableName(value = "user")//标明实体类对应的表
@AllArgsConstructor
@NoArgsConstructor
public class User{
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("username")
    private String username;
    @TableField("email")
    private String email;
    @TableField("nickname")
    private String nickname;
    @TableField("password")
    private String password;
    @TableField("register_time")
    private Timestamp register_time;
    @TableField("headsrc")
    private String headsrc;
    @TableField("last_login")
    private Timestamp last_login;
    @TableField("bean")
    private Integer bean;
    @TableField("type")
    private Integer type;
    @TableField("status")
    private Integer status;
}
