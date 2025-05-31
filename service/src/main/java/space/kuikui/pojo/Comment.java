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
@TableName(value = "comment")
public class Comment {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer user_id;
    private Integer article_id;
    private String comment_content;
    private Timestamp comment_date;
}
