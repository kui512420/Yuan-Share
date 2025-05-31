package space.kuikui.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@TableName(value = "notice")
@AllArgsConstructor
@NoArgsConstructor
public class Notice {
    @TableId(value = "notice_id", type = IdType.AUTO)
    private Integer notice_id;
    @TableField(value = "notice_title")
    private String notice_title;
    @TableField(value = "notice_issuer")
    private Integer notice_issuer;
    @TableField(value = "notice_content")
    private String notice_content;
    @TableField(value = "notice_createtime")
    private Timestamp notice_createtime;
}
