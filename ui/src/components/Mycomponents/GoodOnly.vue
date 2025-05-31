<script setup lang="ts">
import { ref } from 'vue';
import { getGoodsByStatus, buy } from '@/api/goods'
import { ElMessage, ElMessageBox } from 'element-plus';
interface Good {
  goods_name: string,
  goods_desc: string,
  goods_price: number,
  goods_cover: string,
  goods_count: number,
  goods_sales: number,
  goods_id: number
}
const good = ref<Good[]>([])
const getGoods = () => {
  getGoodsByStatus(1).then((res) => {
    good.value = res.data.data
  })
}

const toBuy = (goods_id: number) => {
  ElMessageBox.confirm(
    '您确定要购买此商品吗？',
    '提示',
    {
      confirmButtonText: '购买',
      cancelButtonText: '取消',
      type: 'warning',
      draggable: true,
    }
  )
    .then(() => {
      buy(goods_id).then((res) => {
        if (res.data.msg == "购买成功") {
          ElMessage({
            type: 'success',
            message: '购买成功',
          })
          getGoods()
        } else {
          ElMessage({
            type: 'error',
            message: res.data.msg,
          })
        }
      })


    })
    .catch(() => {
      ElMessage({
        type: 'error',
        message: '取消购买',
      })
    })
}

getGoods()
</script>

<template>
  <div v-for="(item, index) in good" :key="index">
    <img :src=item.goods_cover alt="无图片" width="95" height="80">
    <h2>{{ item.goods_name }}</h2>
    <p>{{ item.goods_desc }}</p>
    <p>价格：{{ item.goods_price }}</p>
    <p>库存：{{ item.goods_count }}</p>
    <p>已售:{{ item.goods_sales }}</p>
    <el-button @click="toBuy(item.goods_id)">购买</el-button>
  </div>
</template>
