<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useTransition } from '@vueuse/core'
import { getCount } from '@/api/home'
import { getECharts } from '@/api/article'
import * as echarts from 'echarts';
const user = ref(0)
const notice = ref(0)
const article = ref(0)
const goods = ref(0)
// 定义 ECharts 选项类型
type EChartsOption = echarts.EChartsOption;

// 定义图表 DOM 元素的引用
const chartDom = ref<HTMLDivElement | null>(null);

// 生成当前日期前七天的日期数组，格式为月/日
const getLastSevenDays = () => {
  const dates = [];
  const currentDate = new Date();
  for (let i = 6; i >= 0; i--) {
    const date = new Date(currentDate);
    date.setDate(date.getDate() - i);
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    dates.push(`${month}/${day}`);
  }
  return dates;
};



onMounted(async () => {
  try {
    getECharts().then((res) => {
      const datas = res.data.data;
      if (chartDom.value) {
        // 初始化 ECharts 实例
        const myChart = echarts.init(chartDom.value);
        // 生成日期数组
        const xAxisData = getLastSevenDays();
        // 定义 ECharts 配置选项
        const option: EChartsOption = {
          xAxis: {
            type: 'category',
            data: xAxisData
          },
          yAxis: {
            type: 'value',
            axisLabel: {
              formatter: '{value} 篇'
            }
          },
          series: [
            {
              data: datas,
              type: 'line',
              smooth: true
            }
          ]
        };
        // 使用 if 语句检查 option 是否存在并设置选项
        if (option) {
          myChart.setOption(option);
        }
      }
    });

  } catch (error) {
    console.error('数据获取出错:', error);
  }
});
const userValue = useTransition(user, {
  duration: 1000,
})
const noticeValue = useTransition(notice, {
  duration: 1000,
})
const articleValue = useTransition(article, {
  duration: 1000,
})
const infoValue = useTransition(goods, {
  duration: 1000,
})

getCount().then((respon) => {
  user.value = respon.data.data.user;
  notice.value = respon.data.data.notice;
  article.value = respon.data.data.article;
  goods.value = respon.data.data.goods;
}).catch((err) => {
  console.error('请求出错:', err);
});

</script>

<template>
  <div style="display: flex; justify-content: space-between;">
    <div>
      <el-card style="width: 280px" shadow="always">
        <template #header>
          <h2 style="text-align: center;">用户数</h2>
        </template>
        <el-statistic style="text-align: center;" :value="userValue" />
      </el-card>
    </div>

    <div>
      <el-card style="width: 280px" shadow="always">
        <template #header>
          <h2 style="text-align: center;">文章数</h2>
        </template>
        <el-statistic style="text-align: center;" :value="articleValue" />
      </el-card>
    </div>

    <div>
      <el-card style="width: 280px" shadow="always">
        <template #header>
          <h2 style="text-align: center;">留言数</h2>
        </template>
        <el-statistic style="text-align: center;" :value="infoValue" />
      </el-card>
    </div>

    <div>
      <el-card style="width: 280px" shadow="always">
        <template #header>
          <h2 style="text-align: center;">商品数</h2>
        </template>
        <el-statistic style="text-align: center;" :value="noticeValue" />
      </el-card>
    </div>
  </div>

  <div>
    <h1>文章新增统计</h1>
    <div ref="chartDom" style="width: 600px; height: 400px;"></div>
  </div>
</template>


<style scoped></style>
