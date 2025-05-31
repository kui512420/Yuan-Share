package space.kuikui.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author kuikui
 * @date 2025/5/14 18:51
 * @description 用户-收藏
 */
@Data
@TableName(value = "user_file")//标明实体类对应的表
public class UserFile {
    @TableId(type = IdType.AUTO) // 声明主键自增
    private Integer id; // 文件ID
    @TableField("user_id")
    private int userId;
    @TableField("file_url")
    private String fileUrl;
}
