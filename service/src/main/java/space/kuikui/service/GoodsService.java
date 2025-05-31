package space.kuikui.service;

import com.github.pagehelper.PageInfo;
import space.kuikui.pojo.Goods;

import java.util.List;

/**
 * @author kuikui
 * @date 2025/3/2 16:34
 */
public interface GoodsService {
    public int addGoods(Goods goods);
    public PageInfo<Goods> getGoodsAll(int pageNum, int pageSize);
    public List<Goods> selectAllGoodsByStatus(int status);
    public int updateGoods(int goods_id, int status);
    public int deleteGoodsById(int goods_id);
    public int buyGoods(int goods_sales, int goods_count,int goods_id);
    public Goods selectGoodsById(int goods_id);
    public int updateGoodsInfo(int goods_sales, int goods_count,int goods_id);
    int count();
}
