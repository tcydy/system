<script setup>
//表格数据
import {reactive, ref,shallowRef,onMounted} from "vue";
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import { Shop, Search } from "@element-plus/icons-vue";
import { useRouter } from "vue-router";



const router=useRouter()
const sideBar = ref('我卖出的')

const changeSideBar = (item) => {
    sideBar.value = item;
    load()
}

const status = ref('全部')



//表格数据
const tableData=ref([])
const total=ref(0)
const pageNum=ref(1)
const pageSize = ref(10)
const form = ref({})
const dialogFormVisible=ref(false)

const users = ref([])
const loadUser = () => {
    request.get('/user').then(res => {
        users.value=res.data
    })
}

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

const getStatusClass = (status) => {
    const classes = {
        '交易关闭':'status-closed',
        '交易完成':'status-success',
        '待支付':'status-payment',
        '待发货':'status-shipment',
        '待收货':'status-receipt',
    }
    return classes[status]||''
}

const pay = (id) => {
    request.get('/orders/pay/' + id).then(res => {
        if (res.code === '200') {
            ElMessage.success('已支付')
        } else {
            ElMessage.error(res.msg)
        }
        load()
    })
}
const cancel = (id) => {
    request.get('/orders/cancel/' + id).then(res => {
        if (res.code === '200') {
            ElMessage.success('已取消')
        } else {
            ElMessage.error(res.msg)
        }
        load()
    })
}
const shipment = (id) => {
    request.get('/orders/shipment/' + id).then(res => {
        if (res.code === '200') {
            ElMessage.success('已发货')
        } else {
            ElMessage.error(res.msg)
        }
        load()
    })
}
const receipt = (id) => {
    request.get('/orders/receipt/' + id).then(res => {
        if (res.code === '200') {
            ElMessage.success('已收货')
        } else {
            ElMessage.error(res.msg)
        }
        load()
    })
}
//编辑
const handleEdit = (item) => {
    form.value = item
    dialogFormVisible.value=true
}
//保存
const save=()=>{
  request.post("/orders",form.value).then(res=>{
    if(res.code==='200'){
      ElMessage.success("保存成功")
      dialogFormVisible.value=false
      load()
    }else{
      ElMessage.error("保存失败")
    }
  })
}


onMounted(() => {
    load()
    loadUser()
})
</script>

<template>
    <div class="all-card" style="width:100%;height:100%;background-color: #f1f1f1;">
        <div style="width:80%;margin:0 auto;display: flex;gap:50px;">

            <div style="width:300px;">
                <el-card style="border-radius: 10px">
                    <div class="left-card-header">
                        <el-icon><Shop /></el-icon>
                        <h3>我的交易</h3>
                    </div>
                    
                    <div class="button-buy-sale":class="{'active': sideBar==='我卖出的'}" @click="changeSideBar('我卖出的')">
                        我卖出的
                    </div>

                    <div class="button-buy-sale":class="{'active': sideBar==='我买到的'}" @click="changeSideBar('我买到的')">
                        我买到的
                    </div>
                </el-card>
            </div>

            <div style="flex:1;display: flex;flex-direction: column;gap:10px;">
                <el-card class="search-card" >
                    <el-input class="search-input" v-model="searchForm.keyword" placeholder="请输入订单号" clear="filter-input":prefix-icon="Search" clearable/>
                    <el-button class="ml-10" plain type="primary"@click="load">搜索</el-button>
                </el-card>

                <el-card style="border-radius: 10px">
                    <el-tabs v-model="status" @tab-change="load">
                        <el-tab-pane label="全部" name="全部"></el-tab-pane>
                        <el-tab-pane label="待支付" name="待支付"></el-tab-pane>
                        <el-tab-pane label="待发货" name="待发货"></el-tab-pane>
                        <el-tab-pane label="待收货" name="待收货"></el-tab-pane>
                        <el-tab-pane label="交易完成" name="交易完成"></el-tab-pane>
                    </el-tabs>
                </el-card>
                <div class="orders-card">
                    <el-card style="border-radius: 10px" v-for="order in tableData" :key="order.id">
                        <div style="display:flex;justify-content: space-between;">
                            <div>
                                <span style="font-size:12px;color:grey">订单号：{{order.no}}</span>
                            </div>
                            <div v-if="sideBar==='我卖出的'" style="display: flex;gap:10px;align-items: center;" @click="router.push('/front/user?id='+(users.find(item=>item.id===order.toId))?.id)">
                                <span style="font-weight: bolder;font-size: 16px;">买家：</span>
                                <el-avatar :src="(users.find(item=>item.id===order.toId))?.avatarUrl || ''" :size="30"></el-avatar>
                                <span>{{ (users.find(item=>item.id===order.toId))?.nickname || '未知用户'}}</span>
                            </div>
                            <div v-if="sideBar==='我买到的'" style="display: flex;gap:10px;align-items: center;" @click="router.push('/front/user?id='+(users.find(item=>item.id===order.fromId))?.id)">
                                <span style="font-weight: bolder;font-size: 16px;">卖家：</span>
                                <el-avatar :src="(users.find(item=>item.id===order.fromId))?.avatarUrl || ''" :size="30"></el-avatar>
                                <span>{{ (users.find(item=>item.id===order.fromId))?.nickname || '未知用户'}}</span>
                            </div>
                            
                        </div>
                            <!-- 分割线 -->
                            <el-divider></el-divider>
                            <div style="display: flex;gap:20px;" @click="router.push('/front/goodsDetail?id=' + order.itemId)">
                                <!-- 左边照片 -->
                                <div style="height: 100px;width:100px;">
                                    <el-image 
                                    style="width:100%;height:100%;object-fit:fill;border-radius:10px"
                                    :src="order.itemImg" 
                                    :preview-src-list="[order.itemImg]" 
                                    :preview-teleported="true">
                                    </el-image>
                                </div>
                                <!-- 照片右边的信息 -->
                                <div style="flex:1;display: flex;flex-direction: column;justify-content:space-between;">
                                    <div style="display: flex;flex-direction: column;gap:5px;">
                                        <span style="font-size: 16px;font-weight: bolder;">{{ order.itemName }}</span>
                                        <span style="font-size: 12px;color:grey;">下单时间{{ order.time }}</span>
                                    </div>
                                    <div>
                                        <span style="font-size: 20px;color:#e87f01;font-weight: bold; ">¥{{ order.price }}</span>
                                    </div>

                                </div>

                            </div>
                            <el-divider></el-divider>
                            <div style="display: flex;flex-direction:row;justify-content:space-between;">
                                <div><span :class="['orders-status',getStatusClass(order.status)]"><h4>{{ order.status }}</h4></span></div>
                                <div>
                                    <el-button type="primary" v-if="order.status==='待支付'&& sideBar==='我买到的'" @click="pay(order.id)">立即支付</el-button>
                                    <el-button type="danger" v-if="order.status==='待支付'" @click="cancel(order.id)">取消订单</el-button>
                                    <el-button type="success" v-if="order.status==='待发货'&& sideBar==='我卖出的'" @click="shipment(order.id)">立即发货</el-button>
                                    <el-button type="success" v-if="order.status==='待收货'&& sideBar==='我买到的'" @click="receipt(order.id)">确认收货</el-button>
                                    <el-button type="warning" v-if="order.status==='交易完成'&& sideBar==='我买到的' && !order.toReview" @click="handleEdit(order)">去评价</el-button>
                                </div>
                            </div>
                            
                    </el-card>
                </div>
                <el-card style="border-radius: 10px">
                    <div class="pagination-section">
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
                </el-card>

            </div>

        </div>
        <el-dialog v-model="dialogFormVisible" :title="填写评价" width="50%" destroy-on-close center>
            <el-form :model="form" label-width="120px">
                <el-form-item label="买家评分" required>
                <el-rate v-model="form.toRate" show-score text-color="#ff9900" />
                </el-form-item>
                <el-form-item label="买家评价" required>
                <el-input v-model="form.toReview" type="textarea" placeholder="请输入" />
                </el-form-item>
            </el-form>

            <template #footer>
                <div class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取消</el-button>
                <el-button type="primary" @click="save">确定</el-button>
                </div>
            </template>
        </el-dialog>
    </div>



</template>

<style scoped>
.all-card{
  --el-color-primary: #f9af32;
  --el-input-focus-border-color: #f9af32;
  padding: 50px;
}
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
:deep(.el-tabs__nav-wrap:after){
    background-color: white;

}
.el-button--primary.is-link, .el-button--primary.is-plain, .el-button--primary.is-text{
    height: 40px;
    width:8%;
    border-radius: 10px;
    font-size: 16px;
    color: black;
    background-color: rgb(255, 230, 24);
    border-color: white;
}
.pagination-section{
    display: flex;
    justify-content: space-around;
}
.orders-card{
    display: flex;
    flex-direction: column;
    gap:10px;
}


</style>