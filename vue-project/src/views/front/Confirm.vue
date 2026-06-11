<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import request from "@/utils/request.js";
import { Location } from '@element-plus/icons-vue';

const route = useRoute()
const router = useRouter()

// 收货地址
const address = ref([])
const selectedAddressId = ref(null)

// 登录账号
const storedAccount = localStorage.getItem('account')
const account = ref(storedAccount ? JSON.parse(storedAccount) : {})

// 商品ID、商品信息
const id = ref(route.query.id ? Number(route.query.id) : null)//接收的是字符串，但后端的itemId是数字，所以这里转换成数字类型
const goods = ref({})

// 加载商品详情
const load = async () => {
  if (!id.value) return
  try {
    const res = await request.get('/goods/' + id.value)
    goods.value = res.data || {}
  } catch (err) {
    ElMessage.error('商品加载失败')
    console.error(err)
  }
}

// 加载收货地址
const loadAddress = async () => {
  try {
    const res = await request.get('/address', {
      params: {
        userId: account.value.id
      }
    })
    //console.log('地址列表响应:', res)  // 调试用
    //console.log('当前用户:', account.value)  // 调试用
    address.value = res.data || []
    if (address.value.length > 0 && !selectedAddressId.value) {
      selectedAddressId.value = address.value[0].id
    }
  } catch (err) {
    ElMessage.error('地址加载失败')
    console.error(err)
  }
}

// 切换选中地址
const changeAddress = (addrId) => {
  selectedAddressId.value = addrId
}

// 确认下单
const confirmOrder = async () => {
  //test
  // console.log('路由原始query:', route.query)
  // console.log('id.value =', id.value, '类型:', typeof id.value)
  // console.log('goods.status =', goods.value?.status)
  // console.log('goods.value.status =', goods.value.status)

  if (!account.value || Object.keys(account.value).length === 0) {
    ElMessage.warning('请先登录')
    return
  }
  if (!selectedAddressId.value) {
    ElMessage.warning('请选择您的收货地址')
    return
  }
  if (goods.value.status !== '上架') {
    ElMessage.warning('商品未上架或已卖出，请联系卖家确认')
    return
  }

  try {
    const res = await request.post('/orders', {
      itemId: id.value,
      addressId: selectedAddressId.value
    })
    if (res.code === '200') {
      ElMessage.success('已下单，请及时支付订单！')
      router.push('/front/orders')
    } else {
      ElMessage.error(res.msg || '下单失败')
    }
  } catch (err) {
    ElMessage.error('请求异常，下单失败')
    console.error(err)
  }
}

onMounted(() => {
  load()
  loadAddress()
})
</script>

<template>
  <div class="page-wrap">
    <div class="main-container">
      <div class="left-content">
        <!-- 地址卡片 -->
        <el-card class="card">
          <div class="addr-head">
            <h3>收货地址</h3>
            <span class="manage-addr" @click="router.push('/front/address')">管理地址</span>
          </div>

          <div class="addr-list">
            <div 
              class="address-card" 
              :class="{'active' : item.id === selectedAddressId}" 
              v-for="item in address" 
              :key="item.id"
              @click="changeAddress(item.id)"
            >
              <div class="addr-icon">
                <el-icon><Location/></el-icon>
              </div>
              <div class="addr-info">
                <div class="addr-text">{{item.address}}</div>
                <div class="addr-desc">{{item.info}}</div>
                <div class="addr-contact">
                  <span>{{item.name}}</span>
                  <span>{{item.phone}}</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 商品信息卡片 -->
        <el-card class="card goods-card">
          <h3>订单信息</h3>
          <div class="goods-box">
            <div class="goods-img">
              <img :src="goods.img" alt="">
            </div>
            <div class="goods-detail">
              <div class="goods-title">
                <span>{{goods.name}}</span>
                <el-tag>{{goods.shipment}}</el-tag>
              </div>
              <div class="goods-place">{{goods.place}}</div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 右侧价格明细卡片 -->
      <div class="right-content">
        <el-card class="card">
          <h3>价格明细</h3>
          <div class="price-row">
            <span class="price-label">商品总价</span>
            <span class="price-num">¥{{goods.price}}</span>
          </div>

          <el-divider></el-divider>

          <div class="price-row">
            <span class="total-label">合计</span>
            <span class="total-num">¥{{goods.price}}</span>
          </div>

          <el-button 
            class="confirm-btn" 
            size="large" 
            type="danger"
            @click="confirmOrder()"
          >确认购买</el-button>
        </el-card>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
// 页面最外层
.page-wrap {
  width: 100%;
  height: 100vh;
  background-color: #f1f1f1;
}

// 主容器
.main-container {
  display: flex;
  gap: 20px;
  width: 75%;
  margin: 0 auto;
  padding: 20px;
}

.left-content {
  flex: 1;
}

.right-content {
  width: 300px;
}

// 通用卡片样式
.card {
  border-radius: 10px;
}

// 地址头部
.addr-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

// 管理地址文字
.manage-addr {
  font-size: 14px;
  color: grey;
  cursor: pointer;
}

// 地址列表容器
.addr-list {
  margin-top: 10px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
}

// 地址卡片
.address-card {
  height: 100px;
  width: 100%;
  border: 1px solid #e6e6e6;
  border-radius: 10px;
  padding: 10px;
  display: flex;
  gap: 10px;

  &.active {
    border: 1px solid orangered;
    background-color: rgba(255, 69, 0, 0.1);
  }
}

.addr-icon {
  font-size: 20px;
}

.addr-text {
  margin-bottom: 2px;
  font-weight: bold;
  font-size: 15px;
}

.addr-desc {
  font-size: 12px;
  margin-bottom: 2px;
}

.addr-contact {
  font-size: 12px;
  color: #999;
  display: flex;
  gap: 5px;
}

// 商品卡片间距
.goods-card {
  margin-top: 20px;
}

// 商品容器
.goods-box {
  display: flex;
  gap: 10px;
}

.goods-img {
  width: 120px;
  height: 120px;

  img {
    width: 100%;
    height: 100%;
    object-fit: fill;
    border-radius: 10px;
  }
}

.goods-detail {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.goods-title {
  display: flex;
  gap: 10px;
  align-items: center;

  span {
    font-size: 18px;
    font-weight: bolder;
  }
}

.goods-place {
  color: #1a4f94;
  font-size: 16px;
}

// 价格行
.price-row {
  display: flex;
  justify-content: space-between;
}

.price-label {
  font-size: 12px;
  color: grey;
}

.price-num {
  font-size: 16px;
  font-weight: bolder;
}

.total-label {
  font-size: 14px;
}

.total-num {
  font-size: 24px;
  font-weight: bolder;
  color: orangered;
}

// 确认按钮
.confirm-btn {
  width: 80%;
  border-radius: 50px;
  font-size: 16px;
  padding: 10px 0;
}
</style>