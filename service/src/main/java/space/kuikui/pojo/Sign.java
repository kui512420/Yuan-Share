package space.kuikui.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@TableName("sign")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sign {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("userid")
    private Integer userid;
    @TableField("time")
    private Timestamp time;
    @TableField("bean")
    private Integer bean;
}
