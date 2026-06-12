<script setup>
//表格数据
import {reactive, ref,shallowRef,onMounted} from "vue";
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import {Shop,Search} from "@element-plus/icons-vue";



const sideBar = ref('我卖出的')
const status = ref('全部')

//表格数据
const tableData=ref([])
const total=ref(0)
const pageNum=ref(1)
const pageSize=ref(10)

//搜索条件
const searchForm=reactive({
  keyword:'',
})

//加载数据
const load=()=>{
  request.get("/orders/front/page",{
    params:{
      pageNum:pageNum.value,
      pageSize:pageSize.value,
      keyword:searchForm.keyword,
      status:status.value,
      flag:sideBar.value,
    }
  }).then(res => {
    console.log('res:', res)  // 调试用
    if (res.code !== '200') {
        ElMessage.error(res.message)
        return
    }
    if(res.data){
        tableData.value = res.data.records
        total.value = res.data.total
        //console.log('加载订单数据:', tableData)  // 调试用
    }
  })
}

</script>

<template>
    <div style="width:100%;height:100vh;background-color: #f1f1f1;">
        <div style="width:80%;margin:0 auto;display: flex;gap:50px;">

            <div style="width:300px;">
                <el-card style="border-radius: 10px">
                    <div class="left-card-header">
                        <el-icon><Shop /></el-icon>
                        <h3>我的交易</h3>
                    </div>
                    
                    <div class="button-buy-sale":class="{'active': sideBar==='我卖出的'}" @click="sideBar='我卖出的'">
                        我卖出的
                    </div>

                    <div class="button-buy-sale":class="{'active': sideBar==='我买到的'}" @click="sideBar='我买到的'">
                        我买到的
                    </div>
                </el-card>
            </div>

            <div style="flex:1;display: flex;flex-direction: column;gap:10px;">
                <el-card class="search-card" >
                    <el-input class="search-input" v-model="searchForm.keyword"placeholder="请输入订单号" clear="filter-input":prefix-icon="Search" clearable/>
                    <el-button class="ml-10" plain type="primary"@click="load">搜索</el-button>
                </el-card>

                <el-card style="border-radius: 10px">
                    <el-tabs v-model="status" @tab-change="load">
                        <el-tab-pane label="全部" name="全部"></el-tab-pane>
                        <el-tab-pane label="待支付" name="待支付"></el-tab-pane>
                        <el-tab-pane label="待发货" name="待发货"></el-tab-pane>
                        <el-tab-pane label="待收货" name="待收货"></el-tab-pane>
                    </el-tabs>

                </el-card>
                <div>
                    <el-card style="border-radius: 10px">

                    </el-card>
                </div>
                <el-card style="border-radius: 10px">

                </el-card>

            </div>

        </div>
    </div>



</template>

<style scoped>
.active{
    background-color: #f1f1f1;
}
.button-buy-sale{
    width:100%;
    height:50px;
    display:flex;
    align-items: center;
    padding:10px;
    border-radius: 10px;
}
.left-card-header{
    display:flex;
    align-items: center;
    gap:10px;
    .el-icon{
        font-size: 22px;
    }
}
.search-input{
    width:300px;
}
.search-card{
    border-radius: 10px;
    padding:0px;
    .search-input{
        padding:0px;
        margin-right: 10px;
        width: 90%;
        height:40px;
    }
    .search-input :deep(.el-input__wrapper) {
    border-radius: 10px !important;
    }
}

</style>