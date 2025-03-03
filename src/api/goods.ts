import Request from '@/utils/Request'

//添加商品
export function postGoods(goods_name:string,goods_desc:string,goods_price:number,goods_count:number,goods_status:number,goods_cover:string){
  return Request({
    url: 'api/goods/postGoods',
    method: 'post',
    data:{
      goods_name:goods_name,
      goods_desc:goods_desc,
      goods_price:goods_price,
      goods_count:goods_count,
      goods_status:goods_status,
      goods_cover:goods_cover
    }
  })
}
//查询所有商品
export function getGoodsAll(pageNum:number,pageSize:number){
  return Request({
    url: 'api/goods/getGoodsAll',
    method: 'get',
    params:{
      pageNum:pageNum,
      pageSize:pageSize
    }
  })
}
//查询所有商品 状态
export function getGoodsByStatus(status:number){
  return Request({
    url: 'api/goods/getGoodsByStatus',
    method: 'get',
    params:{
      status:status
    }
  })
}
//修改状态
export function UpdateStatus(id:number,status:number){
  return Request({
    url: 'api/goods/updateStatus',
    method: 'post',
    data:{
      id:id,
      status:status
    }
  })
}
//删除通过id
export function delByID(id:number){
  return Request({
    url: 'api/goods/delByID',
    method: 'get',
    params:{
      id:id
    }
  })
}
//购买
export function buy(goods_id:number){
  return Request({
    url: 'api/goods/buy',
    method: 'post',
    data:{
      goods_id:goods_id
    }
  })
}
