<script setup>
import {onMounted, ref} from 'vue'
import request from "@/utils/request.js";
import * as echarts from 'echarts';

const userCount = ref(0)
const goodsCount = ref([])

const loadUser = ()=>{
  request.get('/user').then(res=>{
    userCount.value=res.data.length
  })
}
loadUser()

const loadGoods = ()=>{
  request.get('/goods').then(res=>{
    goodsCount.value=res.data.length
  })
}
loadGoods()

onMounted(() => {
  const chartDom = document.getElementById('main');
  const myChart = echarts.init(chartDom);

  const option = {
  title: {
    text: '不同商品分类闲置商品数量统计',
    subtext: '饼图',
    left: 'center'
  },
  tooltip: {
    trigger: 'item'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [
    {
      name: '',
      type: 'pie',
      radius: '50%',
      data: [],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
  };
  request.get('/echarts/count').then(res => { 
    option.series[0].data=res.data
    option && myChart.setOption(option);
  })

  
})

</script>

<template>

<div style="padding: 10px">
    <div style="display: flex;gap: 20px">
        <el-card style="flex: 1">
            <h3>用户数量:{{userCount}}</h3>
        </el-card>
        <el-card style="flex: 1">
            <h3>闲置物品数量:{{goodsCount}}</h3>
        </el-card>
    </div>
</div>

<div style="width:100%;height: 600px;" id="main"></div>

</template>

<style scoped>
</style>