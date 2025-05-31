package space.kuikui.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author kuikui
 * @date 2025/3/2 17:41
 */
@Data
@AllArgsConstructor
public class GoodsVo {


    private String goods_name;
    private int goods_price;
    private String goods_img;
    private String goods_desc;
    private Timestamp goods_updatetime;
    private Timestamp goods_createtime;
    private String goods_cover;
    private Integer goods_count;
    private Integer goods_sales;
    private Integer goods_status;
}
