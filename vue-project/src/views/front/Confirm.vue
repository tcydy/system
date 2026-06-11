<script setup>
import { useRoute, useRouter } from "vue-router";
import { ref } from "vue";
import request from "@/utils/request.js";
import {Location} from '@element-plus/icons-vue';


const route = useRoute()
const router = useRouter()

const address = ref([])
const selectedAddressId = ref(null)
const storedAccount = localStorage.getItem('account')
const account = ref(storedAccount ? JSON.parse(storedAccount) : {})

const id = ref(route.query.id)
const goods = ref()

const load = ()=>{
  request.get('/goods/' + id.value).then(res=>{
    goods.value=res.data
  })
}
// const loadGoods = ()=>{
//   request.get('/goods/' + id.value).then(res=>{
//     goods.value=res.data
//   })
// }
// loadGoods()

// 加载收货地址
const loadAddress = ()=>{
  request.get('/address').then(res=>{
    address.value = res.data
    if (address.data.length > 0&& !selectedAddressId.value) {
      selectedAddressId.value = res.data[0].id
    }
  })
}

//确认订单
const confirmOrder = () => {
  if(account.value==null){
    ElMessage.warning('请先登录')
    return
  }
  if(selectedAddressId.value==''){
    ElMessage.warning('请选择您的收货地址')
    return
  }
  if(goods.value.status!=='已上架'){
    ElMessage.warning('商品未上架或已卖出，请联系卖家确认')
    return
  }
  request.post('/orders',{
    goodsId: id,
    addressId: selectedAddressId.value
  }).then(res=>{
    if (res.code === '200') {
      ElMessage.success('已下单，请及时支付订单！')
      router.push('/front/orders')
    } else {
      ElMessage.error('res.msg')
    }
  })
}

const changeAddress = (id)=>{
  selectedAddressId.value=id
}

onMounted(()=>{
  load()
  loadAddress()
})

</script>

<template>
<div style="width: 100%;height: 100vh;background-color: #f1f1f1">
  <div style="display: flex;gap: 20px;width: 75%;margin: 0 auto;padding: 20px">
    <div style="flex: 1">
      <!-- 地址卡片 -->
      <el-card style="border-radius: 10px">
        <div style="display:flex;justify-content: space-between;align-items: center">
          <div>
            <h3>收货地址</h3>
          </div>
          <div>
            <span style="font-size: 14px;color: grey" @click="router.push('/front/address')">管理地址</span>
          </div>
        </div>

        <div style="margin-top: 10px;display: grid;grid-template-columns:repeat(3,1fr);gap:10px">

          <div class="address-card" :class="{'active' :item.id===selectedAddressId}" 
                v-for="item in address" :key="item.id"@click="changeAddress(item.id)">
            <div style="font-size: 24px">
                <el-icon><Location/></el-icon>
            </div>


            <div>
                <div style="margin-bottom: 6px;">
                    <span style="font-weight: bold; font-size: 15px;">{{item.address}}</span>
                </div>
                <div style="font-size:12px;margin-bottom: 6px;">
                    <span>{{item.info}}</span>
                </div>
                <div style="font-size:12px;color:#999;display: flex;gap: 5px">
                    <span>{{item.name}}</span>
                    <span>{{item.phone}}</span>
                </div>
            </div>



          </div>

        </div>

      </el-card>

      <!-- 商品信息卡片 -->
      <el-card style="border-radius: 10px;margin-top: 20px">
        <h3>订单信息</h3>
        <div style="display: flex;gap: 10px">
          <div style="width: 120px;height: 120px">
            <img :src="goods.img" style="width: 100%;height: 100%;object-fit: fill;border-radius: 10px" alt="">
          </div>
          <div style="flex: 1;display: flex;flex-direction: column;justify-content: space-between">
            <div style="display: flex;gap: 10px;align-items: center">
              <span style="font-size: 18px;font-weight: bolder;">{{goods.name}}</span>
              <el-tag>{{goods.shipment}}</el-tag>
            </div>
            <div>
              <span style="color: #1a4f94;font-size: 16px">{{goods.place}}</span>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 右侧价格明细卡片 -->
    <div style="width: 300px">
      <el-card style="border-radius: 10px">
        <h3>价格明细</h3>

        <div style="display: flex;justify-content: space-between">
          <div>
            <span style="font-size: 12px;color: grey">商品总价</span>
          </div>
          <div>
            <span style="font-size: 16px;font-weight: bolder">¥{{goods.price}}</span>
          </div>
        </div>

        <el-divider></el-divider>

        <div style="display: flex;justify-content: space-between">
          <div>
            <span style="font-size: 14px">合计</span>
          </div>
          <div>
            <span style="font-size: 24px;font-weight: bolder;color: orangered">¥{{goods.price}}</span>
          </div>
        </div>
        <button class="confirm-btn"  size="large" @click="confirmOrder()">确认购买</button>
      </el-card>
    </div>
  </div>
</div>
</template>

<style scoped lang="scss">

.address-card{
    height:100px;
    width: 100%;
    border: 1px solid #e6e6e6;
    border-radius: 10px;
    padding: 20px;
    display: flex;
    gap: 10px;

    &.active{
        border: 1px solid orangered;
        background-color: rgba(255, 69, 0, 0.1);
    }
}
.confirm-btn{
    width:80%;
    border-radius: 50px;
    background-color: orangered;
    color: white;font-size: 16px;
    padding: 10px 0;
    border: none;
    cursor: pointer;
}



</style>