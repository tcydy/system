<script setup>
import { ref,watch, onMounted } from 'vue'
import { useRoute,useRouter } from 'vue-router';
import request from '@/utils/request.js';

const route = useRoute()
const router=useRouter()
const id = ref(route.query.id)
const user = ref({})
const tab = ref('宝贝')


const loadUser = () => {
    request.get('/user/' + id.value).then(res => {
        user.value = res.data
    })
}

const goods=ref([])
const loadGoods = () => {
    request.get('/goods/user/' + id.value).then(res => {
        goods.value = res.data
        console.log(res)
    })
}

const orders=ref([])
const loadOrders = () => {
    request.get('/orders/user/' + id.value).then(res => {
        orders.value = res.data
        console.log(res)
    })
}

const users = ref([])
const loadUsers = () => {
    request.get('/user').then(res => {
        users.value=res.data
    })
}

const gotoUser = (userId) => {
    if (!userId) return // 加个判空，防止 undefined 跳转
    router.push(`/front/user?id=${userId}`)
}

const loadUserData = () => {
  const userId = route.query.id
  if (!userId) return
  // 这里写你的请求逻辑
  request.get(`/user/${userId}`).then(res => {
    user.value = res.data
  })
}

onMounted(() => {
    loadUser()
    loadGoods()
    loadOrders()
    loadUsers()
})

watch(
  () => route.query.id,
  (newId) => {
    if (newId) {
      loadUserData()
    }
  }
)

</script>

<template>
<div style="width:80%;height: 100vh;margin: 0 auto;background-color: #f1f1f1;padding: 20px;">
    <el-card style="border-radius:20px;">
        <div style="display: flex;justify-content: space-between;">
            <div style="display: flex;align-items: center;gap:10px;">
                <el-avatar :src="user.avatarUrl":size="100"></el-avatar>
                <h2>{{ user.nickname }}</h2>
            </div>
            <div style="display: flex;align-items:center;">
                <el-button type="primary" size="large">去私聊</el-button>
            </div>
        </div>
    </el-card>
    <el-card style="margin-top: 20px;border-radius: 10px;">
        <div style="display: flex;gap:20px;">
            <div @click="tab='宝贝'" style="cursor: pointer;">
                <span :class="{'option':tab==='宝贝'}">宝贝</span>
                <span style="margin-left: 5px;">{{ goods.length }}</span>
            </div>
            <div @click="tab='历史评价'" style="cursor: pointer;">
                <span :class="{'option':tab==='历史评价'}">历史评价</span>
                <span style="margin-left: 5px;">{{ orders.length }}</span>
            </div>
        </div>
    </el-card>
    <div>
        <div style="margin-top: 10px;display: grid;grid-template-columns: repeat(5,1fr);gap:10px;" v-if="tab==='宝贝'">
            <div style="border-radius: 10px;background-color: white;cursor: pointer;" v-for="item in goods" :key="item.id" @click="router.push('/front/goodsDetail?id=' + item.id)">
                <div style="width: 100%;height: 300px;">
                    <img :src="item.img" style="width: 100%;height: 100%;object-fit: fill;border-radius: 10px 10px 0 0;" alt="">
                </div>
                <div style="width: 100%;height: 100px;padding: 10px;">
                    <div style="display: flex;gap:5px;">
                        <span class="option">{{ item.shipment }}</span>
                        <span class="title">{{ item.name }}</span>
                    </div>
                    <div style="display:flex;gap:10px;align-items: center;">
                        <span style="font-size: 16px;font-weight: bolder;color: red;">¥{{ item.price }}</span>
                        <span style="font-size: 12px;color: grey;text-decoration: line-through;">¥{{ item.rePrice }}</span>
                    </div>
                    <div style="display: flex;gap:5px;">
                        <el-avatar :src="user.avatarUrl" :size="20"></el-avatar>
                        <span>{{ user.nickname }}</span>
                    </div>
                </div>
            </div>
        </div>
        

        <div style="display: flex;flex-direction: column;gap:20px;width:100%;margin-top: 20px;" v-if="tab==='历史评价'">
            <el-card style="border-radius: 10px;width: 100%;" v-for="order in orders":key="order.id">
                <div style="border: 1px solid #ececec;min-height: 80px;width: 100%;padding: 20px;">
                    <div style="display: flex; flex-direction: column; gap: 20px;">
                    <!-- 顶部信息行：头像、昵称、时间、评分 -->
                    <div style="display: flex; justify-content: space-between; align-items: center; gap: 16px;">
                        <!-- 左侧用户信息：头像 + 昵称/时间 -->
                        <div style="display: flex; align-items: center; gap: 12px;cursor: pointer;" @click="gotoUser(users.find(item=>item.id===order.toId)?.id)">
                            <el-avatar 
                                :src="users.find(item=>item.id===order.toId)?.avatarUrl" 
                                :size="40"
                            />
                            <div style="display: flex; flex-direction: column; gap: 4px;">
                                <span style="font-size: 14px; font-weight: 500;">
                                {{ users.find(item=>item.id===order.toId)?.nickname }}
                                </span>
                                <span style="font-size: 12px; color: #909399;">
                                {{ order.time }}
                                </span>
                            </div>
                        </div>

                        <!-- 右侧评分 -->
                        <el-rate 
                        v-model="order.toRate" 
                        disabled 
                        :size="16"
                        text-color="#ff9900"
                        />
                    </div>

                    <!-- 底部评价内容 -->
                    <div style="padding: 0 4px;">
                        <p style="margin: 0; font-size: 14px; line-height: 1.6; color: #303133;">
                        {{ order.toReview }}
                        </p>
                    </div>
                    </div>
                </div>
            </el-card>
        </div>
    </div>
</div>
</template>

<style scoped>
.option{
    font-size: 18px;
    font-weight: bold;
    text-decoration: underline;
    text-decoration-color: #ffe610;
    text-decoration-thickness: 8px;
    text-underline-offset: -4px;
    min-width: 50px;
}
.title{
    font-size: 14px;
    line-height: 2;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
}
</style>