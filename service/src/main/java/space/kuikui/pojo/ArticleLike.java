package space.kuikui.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author kuikui
 * @date 2025/3/13 1:18
 */
@Data
@TableName(value = "articlelike")
@AllArgsConstructor
@NoArgsConstructor
public class ArticleLike {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("articleId")
    private String articleId;
    @TableField("userId")
    private String userId;
    @TableField("likeTime")
    private LocalDateTime likeTime;
}
