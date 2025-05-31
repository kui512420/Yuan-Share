package space.kuikui.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import space.kuikui.pojo.Goods;

import java.util.List;

/**
 * @author kuikui
 * @date 2025/3/2 16:32
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    @Insert("INSERT INTO goods (good_name, good_desc, good_price, good_updatetime, good_createtime, good_cover, good_count, good_sales, good_status) " +
            "VALUES (#{goodName}, #{goodDesc}, #{goodPrice}, #{goodUpdatetime}, #{goodCreatetime}, #{goodCover}, #{goodCount}, #{goodSales}, #{goodStatus})")
    public int insertGoods(Goods goods);

    @Select("select * from goods")
    public List<Goods> selectAllGoods();

    @Select("select * from goods where goods_status=#{goods_status}")
    public List<Goods> selectAllGoodsByStatus(int goods_status);

    //改状态
    @Update("update goods set goods_status=#{goods_status} where goods_id =  #{goods_id}")
    public int updateGoods(int goods_id, int goods_status);
    //删除单个
    @Update("delete from goods where goods_id = #{goods_id}")
    public int delById(int goods_id);
    //查询余量数量
    @Select("select * from goods where goods_id = #{goods_id}")
    public int selectOneById(int goods_id);

    //更新商品信息
    @Update("update goods set goods_sales=#{goods_sales},goods_count=#{goods_count}  where goods_id =  #{goods_id}")
    public int updateGoodsInfo(int goods_sales, int goods_count,int goods_id);

    //查询余量数量
    @Select("select count(*) from goods")
    public int count();
}
