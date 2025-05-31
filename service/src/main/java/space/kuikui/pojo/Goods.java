package space.kuikui.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

/**
 * @author kuikui
 * @date 2025/3/2 16:27
 */
@Data
@TableName(value = "goods")//标明实体类对应的表
@AllArgsConstructor
@NoArgsConstructor
public class Goods {

    @TableId(value = "goods_id", type = IdType.AUTO)
    private Integer goods_id;
    private String goods_name;
    private int goods_price;
    private String goods_desc;
    private Timestamp goods_updatetime;
    private Timestamp goods_createtime;
    private String goods_cover;
    private Integer goods_count;
    private Integer goods_sales;
    private Integer goods_status;
}
