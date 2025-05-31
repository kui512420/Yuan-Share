package space.kuikui.service.impl;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.kuikui.mapper.GoodsMapper;
import space.kuikui.pojo.Goods;
import space.kuikui.service.GoodsService;

import java.util.Collections;
import java.util.List;

/**
 * @author kuikui
 * @date 2025/3/2 16:34
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public int addGoods(Goods goods) {
        return goodsMapper.insert(goods);
    }

    @Override
    public PageInfo<Goods> getGoodsAll(int pageNum, int pageSize) {
        List<Goods> goods = goodsMapper.selectAllGoods();
        PageInfo<Goods> pageInfo = new PageInfo<>(goods);
        return pageInfo;
    }

    @Override
    public List<Goods> selectAllGoodsByStatus(int status) {
        return goodsMapper.selectAllGoodsByStatus(status);
    }

    @Override
    public int updateGoods(int goods_id, int status) {
        return goodsMapper.updateGoods(goods_id, status);
    }

    @Override
    public int deleteGoodsById(int goods_id) {
        return goodsMapper.delById(goods_id);
    }

    @Override
    public int buyGoods(int goods_sales, int goods_count, int goods_id) {
        return 0;
    }

    @Override
    public Goods selectGoodsById(int goods_id) {
        return goodsMapper.selectById(goods_id);
    }

    @Override
    public int updateGoodsInfo(int goods_sales, int goods_count, int goods_id) {
        return goodsMapper.updateGoodsInfo(goods_sales, goods_count, goods_id);
    }

    @Override
    public int count() {
        return goodsMapper.count();
    }
}
