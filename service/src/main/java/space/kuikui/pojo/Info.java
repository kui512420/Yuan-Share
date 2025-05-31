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

@Data
@TableName(value = "info")
@AllArgsConstructor
@NoArgsConstructor
public class Info{
    @TableId(value = "infoid", type = IdType.AUTO)
    private Integer infoid;
    @TableField("userid")
    private Integer userid;
    @TableField("nickname")
    private String nickname;
    @TableField("info")
    private String info;
    @TableField("time")
    private Timestamp time;
}
