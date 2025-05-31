package space.kuikui.controller;

import com.github.pagehelper.PageInfo;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import space.kuikui.pojo.Goods;
import space.kuikui.service.GoodsService;
import space.kuikui.service.UserService;
import space.kuikui.utils.JwtLoginUtil;
import space.kuikui.utils.ResultUtil;
import sun.dc.pr.PRError;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author kuikui
 * @date 2025/3/2 16:36
 */
@RestController
@RequestMapping("/api/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private ResultUtil resultUtil;
    @Autowired
    private JwtLoginUtil jwtLoginUtil;
    @Autowired
    private UserService userService;

    @PostMapping("/postGoods")
    public ResponseEntity<Map<String,Object>> addGoods(@RequestBody Map<String,Object> body) {
        Goods goods = new Goods();

        goods.setGoods_name(body.get("goods_name").toString());
        goods.setGoods_desc(body.get("goods_desc").toString());
        goods.setGoods_cover(body.get("goods_cover").toString());
        goods.setGoods_count(Integer.parseInt(body.get("goods_count").toString()));
        goods.setGoods_price(Integer.parseInt(body.get("goods_price").toString()));
        goods.setGoods_status(Integer.parseInt(body.get("goods_status").toString()));
        goods.setGoods_createtime(new Timestamp(new Date().getTime()));
        goods.setGoods_updatetime(new Timestamp(new Date().getTime())) ;
        goods.setGoods_sales(0) ;

        int count = goodsService.addGoods(goods);
        return  ResponseEntity.ok(resultUtil.getResult(200,"添加商品成功",count));
    }

    @GetMapping("/getGoodsAll")
    public ResponseEntity<Map<String,Object>> getGoods(
            @RequestParam("pageNum") int pageNum,
            @RequestParam("pageSize") int pageSize
    ) {
        System.out.println(1111);
        PageInfo<Goods>  goodsPageInfo =  goodsService.getGoodsAll(pageNum,pageSize);
        return ResponseEntity.ok(resultUtil.getResult(200,"获取商品信息成功",goodsPageInfo));
    }
    @GetMapping("/getGoodsByStatus")
    public ResponseEntity<Map<String,Object>> getGoodsByStatus(
            @RequestParam("status") int status
    ) {
        List<Goods> goodsPageInfo =  goodsService.selectAllGoodsByStatus(status);
        return ResponseEntity.ok(resultUtil.getResult(200,"获取商品信息成功",goodsPageInfo));
    }
    @PostMapping("/updateStatus")
    public ResponseEntity<Map<String,Object>> updateStatus(@RequestBody Map<String,Object> body) {
        int count = goodsService.updateGoods(Integer.parseInt(body.get("id").toString()), Integer.parseInt(body.get("status").toString()));
        return  ResponseEntity.ok(resultUtil.getResult(200,"修改状态成功",count));
    }
    @GetMapping("/delByID")
    public ResponseEntity<Map<String,Object>> delByID(@RequestParam("id") int id) {
        int count = goodsService.deleteGoodsById(id);
        return  ResponseEntity.ok(resultUtil.getResult(200,"删除成功",count));
    }

    @PostMapping("/buy")
    public ResponseEntity<Map<String,Object>> buy(@RequestBody Map<String,Object> body,@RequestHeader("token") String token) {
        Map<Object, Object> map = jwtLoginUtil.jwtPe(token);
        Integer id = Integer.parseInt(map.get("id").toString());
        Integer goods_id = Integer.parseInt(body.get("goods_id").toString());
        int beanCount = userService.queryBean(id);
        Goods goods = goodsService.selectGoodsById(goods_id);
        int goods_price = goods.getGoods_price();
        int goods_count = goods.getGoods_count();
        int goods_sales = goods.getGoods_sales();
        //判断库存
        if(goods_count<=0){
            return  ResponseEntity.ok(resultUtil.getResult(200,"购买失败：库存不足",resultUtil.DATA_NOW));
        }else{
            //判断余额
            if(beanCount<goods_price){
                return  ResponseEntity.ok(resultUtil.getResult(200,"购买失败：余额不足",resultUtil.DATA_NOW));
            }else{
                //购买后的余额
                int last_bean = beanCount-goods_price;
                userService.updateBean(id,last_bean);
                //购买后的数量
                goods_count = goods_count-1;
                //购买后的销量
                goods_sales = goods_sales+1;

                goodsService.updateGoodsInfo(goods_sales,goods_count,goods_id);

                return  ResponseEntity.ok(resultUtil.getResult(200,"购买成功",resultUtil.DATA_NOW));
            }
        }


    }
}
