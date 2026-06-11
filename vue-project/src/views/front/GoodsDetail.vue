<script setup>
import {useRoute,useRouter} from "vue-router";
import request from "@/utils/request.js"
import {ref} from 'vue'
import {Star,StarFilled} from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus'
import 'element-plus/dist/index.css'

const route = useRoute()
const router = useRouter()

const id = ref(route.query.id)

const img = ref('')

const imgList = ref([])

const user = ref({})
const goods = ref({})
const loadGoods = ()=>{
    request.get('/goods/'+id.value).then(res=>{
        goods.value=res.data

        img.value = goods.value.img

        imgList.value = goods.value.imgList.split(',')
        imgList.value.unshift(goods.value.img)

        request.get('/user/'+goods.value.userId).then(res=>{
            user.value=res.data
        })
    })
}
loadGoods()
request.post('/goods/addView/' + id.value)

const changeImg = (item) =>{
    img.value=item
}

const collect = () => {
    if (goods.value.isCollected) {
        request.delete('/collect/' + id.value).then(res => {
            if (res.code === '200') {
                goods.value.isCollected = false
                ElMessage.success('取消收藏成功')
            } else {
                ElMessage.error(res.msg)
            }
        })
    } else {
        request.post('/collect', { itemId: id.value }).then(res => {
            if (res.code === '200') {
                goods.value.isCollected = true
                ElMessage.success('收藏成功')
            } else {
                ElMessage.error(res.msg)
            }
        })
    }
}

// 跳转到下单确认页
const toConfirm = () => {
  router.push({
    path: '/front/confirm',
    query: {
      id: id.value  // 把商品id带过去
    }
  })
}

</script>

<template>

  <div style="width:80%;margin: 0 auto;min-height: 300px;padding:30px;background-color: #f4f4f4;">
    <el-card style="border-radius: 10px;">
        <div style="display: flex;gap:10px">
            <div>
                <el-avatar :src="user.avatarUrl" :size="50"></el-avatar>
            </div>
            <div>
                <div>
                    <span style="font-size: 20px;font-weight: bolder;">{{ user.nickname }}</span>
                </div>
                <div style="margin-top: 3px;">
                    <span style="font-size: 16px;">{{ goods.place }}</span>
                </div>
            </div>
        </div>
    </el-card>


    <div style="display: flex;gap:20px;margin-top:20px">
        <el-card style="border-radius: 10px;flex:3">
            <div style="display:flex;gap:10px">
                <div style="width:120px;padding:10px">

                    <div v-for="item in imgList" style="height:100px;width:100px;margin-top:20px" @click="changeImg(item)">
                        <img :src="item" alt="" style="width:100%;height:100%;object-fit:fill">

                    </div>

                </div>

                <div style="flex:1;height:500px">
                    <img :src="img" alt="" style="width: 100%;height: 100%;object-fit: fill;border-radius: 10px;">

                </div>
            </div>
        </el-card>

        <el-card style="border-radius: 10px;flex:2">

            <div style="display: flex;justify-content: space-between;">
                <div>
                    <span style="color: orangered;font-size: 30px;">￥</span>
                    <span style="color: orangered;font-size: 70px;">{{ goods.price }}</span>
                    <el-text style="font-size: 20px;" tag="del">原价{{ goods.rePrice }}</el-text>
                </div>
                <div>
                    <el-tag  type="danger" size="large">{{ goods.shipment }}</el-tag>
                </div>

            </div>

            <div>
                <span style="color:grey">{{ goods.num }}浏览</span>
            </div>

            <el-divider></el-divider>

            <h4>{{ goods.name }}</h4>

            <div v-html="goods.content">

            </div>

            <el-divider></el-divider>

            <div style="display: flex;gap: 50px;">
                <span>地区:</span>
                <span>{{ goods.place }}</span>
            </div>

            <el-divider></el-divider>

            <div style="display: flex;gap: 50px;">
                <span>成色:</span>
                <span>{{ goods.quality }}</span>
            </div>

            <el-divider></el-divider>

            <div class="buttons" style="display:flex;gap: 10px;">

                <button >
                    聊一聊
                </button>
                <button @click="toConfirm" >
                    立即购买
                </button>                
                <button style="" @click="collect">
                    <el-icon v-if="goods.isCollected"><StarFilled/></el-icon>
                    <el-icon v-else><Star/></el-icon>
                    {{ goods.isCollected ?'已收藏':'收藏' }}
                </button>

            </div>

        </el-card>
    </div>


  </div>


</template>

<style scoped>
.buttons{
    button{
        font-size: 16px;
        height: 50px;
        min-width: 100px;
        text-align: center;
        border: none;
        border-radius: 5px;
    }
    button:hover{
        opacity: 0.8;
        background-color: rgb(255, 230, 24);
    }
}

</style>