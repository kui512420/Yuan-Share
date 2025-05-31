package space.kuikui.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@TableName(value = "article")
@AllArgsConstructor
@NoArgsConstructor
public class Article{
    @TableId(value = "article_id", type = IdType.AUTO)
    private Integer article_id;
    @TableField("article_title")
    private String article_title;
    @TableField("article_tag")
    private String article_tag;
    @TableField("article_cover")
    private String article_cover;
    @TableField("article_content")
    private String article_content;
    @TableField("article_authorid")
    private int article_authorid;
    @TableField("publish_time")
    private Timestamp publish_time;
    @TableField("last_time")
    private Timestamp last_time;
    @TableField("article_type")
    private int article_type;
    @TableField("like_count")
    private int like_count;
    @TableField("article_status")
    private int article_status;
    @TableField("article_author")
    private String article_author;
    @TableField("top")
    private int top;
}
