<script setup>

import {reactive, ref} from "vue";
import request from "@/utils/request.js";

//搜索条件
const searchForm=reactive({
  keyword:'',
})

//表格数据
const tableData=ref([])
const total=ref(0)
const pageNum=ref(1)
const pageSize=ref(10)

//加载数据
const load=()=>{
  request.get("/goods/collect/page",{
    params:{
      pageNum:pageNum.value,
      pageSize:pageSize.value,
      keyword:searchForm.keyword,
    }
  }).then(res=>{
    if(res.data){
      tableData.value=res.data.records
      total.value=res.data.total
    }
  })
}
load()

//分页大小变化
const handleSizeChange=(size)=>{
  pageSize.value=size
  load()
}

//页码变化
const handleCurrentChange=(current)=>{
  pageNum.value=current
  load()
}

</script>

<template>
  <!-- 最外层容器：居中 + 限制宽度 -->
  <div style="max-width: 1000px; margin: 0 auto; padding: 20px;">

    <!-- 我的收藏 → 左上角 -->
    <div style="font-size: 24px; font-weight: bold; margin-bottom: 15px;">
      我的收藏
    </div>

    <!-- 搜索框 -->
    <el-input
      v-model="searchForm.keyword"
      placeholder="请输入商品名称"
      clearable
      :prefix-icon="Search"
      style="margin-bottom: 15px;"
    />

    <el-divider></el-divider>

    <!-- 商品列表 -->
    <el-row :gutter="20">
      <el-col :span="6" v-for="item in tableData" :key="item.id">
        <el-card style="border-radius: 10px">
          <div style="width: 100%; height: 220px" v-if="item.status!=='已售出'">
            <img :src="item.img" alt="" style="width: 100%; height: 100%; object-fit: fill;">
          </div>
          <div style="width: 100%; height: 220px;position:relative" v-if="item.status==='已售出'">
            <div class="overlay"></div>
            <img src="../../assets/已售出.png" alt="" class="overlay-img">
            <img :src="item.img" alt="" style="width: 100%; height: 100%; object-fit: fill;">
          </div>
          <div style="padding: 10px;">
            <div>{{ item.name }}</div>
            <div style="color: orangered; margin-top: 5px;">¥{{ item.price }}</div>
          </div>
        </el-card>
      </el-col>

      <div v-if="tableData.length === 0" style="width: 100%; text-align: center; padding: 40px 0; color: #999;">
        暂无收藏商品
      </div>
    </el-row>

    <!-- 分页居中 -->
    <div style="display: flex; justify-content: center; margin-top: 30px;">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :page-sizes="[10,20,50,100]"
        layout="total,sizes,prev,pager,next,jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<style scoped>

:deep(.el-card__body){
    padding: 0;
}

.overlay{
    position: absolute;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
    height:100%;
    width: 100%;
    z-index:1;
    background-color:white;
    opacity: 0.5;
}

.overlay-img{
    position: absolute;
    top: 25%;
    right: 0;
    bottom: 0;
    left: 25%;
    height:70%;
    width: 70%;
    z-index:2;
    object-fit:contain;
}

</style>