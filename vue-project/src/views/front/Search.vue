<script setup>
import {useRouter,useRoute} from "vue-router";
import {ref,onMounted} from "vue";
import request from "@/utils/request.js";
import Goods from "../back/Goods.vue";

const router=useRouter()

const route = useRoute()

const typeId =ref(route.query.typeId)
const keyword =ref(route.query.keyword)

//表格数据
const tableData=ref([])
const total=ref(0)
const pageNum=ref(1)
const pageSize=ref(10)

const sortBy = ref('all')

//加载数据
const load=()=>{
  request.get("/goods/front/page",{
    params:{
      pageNum:pageNum.value,
      pageSize:pageSize.value,
      typeId:typeId.value,
      sortBy:sortBy.value,
      keyword:keyword.value,
    }
  }).then(res=>{
    if(res.data){
      tableData.value=res.data.records
      total.value = res.data.total
      console.log(res)
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

const types = ref([])
const loadType =()=>{
  request.get('/type').then(res=>{
    types.value = res.data
    types.value.unshift({
        id:0,
        name:'全部分类'
    })
  })
}

const changeType = (id) =>{
  typeId.value = id
  load()
}

const changeSortBy = (item) =>{
    sortBy.value = item
    load()
}

const getProvine=(place)=>{
  if(!place) return ''
  return place?place.split('/')[0]:'';
}

const users = ref([])
const getUser=(userId)=>{
  return users.value.find(user=>Number(user.id)===Number(userId))||{}
}
const loadUser=()=>{
    request.get('/user').then(res=>{
        users.value = res.data
    })
}

onMounted(() => {
  load()
  loadUser()
  loadType()
})

</script>

<template>
  <div style="width:80%;margin:0 auto">
    <div style="display:flex;gap:5px;margin-top:70px;padding:20px">
      <div v-for="item in types" :key="item.id" style="height:50px;">
        <el-card style="border-radius:50px;cursor: pointer;" @click="changeType(item.id)" :class="{'type-active':item.id == Number(typeId)}">
          <span>{{ item.name }}</span>
        </el-card>
      </div>
    </div>

    <div style="width:100%;height:80px;background-color: #f4f4f4;border-radius: 10px;padding:20px;display: flex;align-items:center;gap:10px">
      <span style="font-size:18px">排序方式:</span>
      <button class="btn" :class="{'active':sortBy==='all'}" @click="changeSortBy('all')">综合排序</button>
      <button class="btn" :class="{'active':sortBy==='date'}" @click="changeSortBy('date')">最新发布</button>
      <button class="btn" :class="{'active':sortBy==='price'}" @click="changeSortBy('price')">价格优先</button>
    </div>

    <el-divider></el-divider>

    <div>
      <div style="display: grid;grid-template-columns:repeat(6,1fr);gap:20px;overflow: hidden">
        <!-- 信息卡片 -->
        <div 
          style="height: 400px;width: 100%;background-color: white;overflow: hidden;border:1px solid #e5e5e5;border-radius: 8px;box-shadow: 0 2px 8px rgba(0,0,0,0.04);cursor: pointer;" 
          v-for="item in tableData" :key="item.id" @click="router.push('/front/goodsDetail?id=' + item.id)"
        >
          <div style="height: 240px;width: 100%">
            <img :src="item.img" alt="" style="height:100%;width:100%;object-fit:cover;overflow: hidden">
          </div>
          <div style="padding: 10px;width: 100%;min-height: 100px">
            <div style="display: flex;gap:5px;overflow: hidden">
              <div style="width: 40px;text-decoration: underline;text-decoration-color: #ffe618;text-decoration-thickness: 8px;text-underline-offset: -4px;">
                <span style="font-weight: bolder;white-space:nowrap;font-size: 16px">包邮</span>
              </div>
              <div style="line-height: 1.4;overflow: hidden;overflow: hidden;text-overflow: ellipsis;white-space: nowrap">
                <span style="font-size: 16px;font-weight: bolder">{{item.name}}</span>
              </div>
            </div>
            <div style="display: flex;justify-content: space-between">
              <div>
                <span style="color: orangered">￥{{item.price}}</span>
              </div>
              <div>
                <el-tag style="color: #409eff" type="place" effect="plain">{{getProvine(item.place)}}</el-tag>
              </div>
            </div>
            <el-divider></el-divider>
            <div style="display: flex;gap: 5px;align-items: center">
              <div>
                <el-avatar :src="getUser(item.userId).avatarUrl" :size="30"></el-avatar>
              </div>
              <div>
                <span style="font-size: 14px;color: gray;">{{getUser(item.userId).nickname || getUser(item.userId).username || '未知用户'}}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div style="display: flex;justify-content: space-around;padding-top: 30px;">
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
```

<style scoped lang="scss">



:deep(.el-card__body){
    padding:10px;
}

.type-active{
    background-color: #ffe610;
    color:#151111
}

.btn{
    width: 80px;
    height: 40px;
    border:none;
    border-radius: 10px;
    background-color: white;
    color:#151111;
    cursor: pointer;

    &.active{
      background-color: chocolate;
      color:white;        
    }
}



</style>