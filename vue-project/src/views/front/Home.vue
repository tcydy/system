<script setup>
import {ref} from 'vue'
import request from '@/utils/request.js';
import {useRouter} from "vue-router";
import {MuteNotification} from "@element-plus/icons-vue";

const router=useRouter()

const notices = ref([])
const loadNotice = ()=>{
  request.get('/notice').then(res=>{
    notices.value = res.data
  })
}
loadNotice()

const showNotice = ref(true)
const closeNotice =()=>{
  showNotice.value=false
}

const types = ref([])
const loadType =()=>{
  request.get('/type').then(res=>{
    types.value = res.data
  })
}
loadType()

const hotTypes = ref([])
const loadHotType =()=>{
  request.get('/type/hot').then(res=>{
    hotTypes.value = res.data
  })
}
loadHotType()

const banners = ref([])
const loadBanner =()=>{
  request.get('/banner').then(res=>{
    banners.value = res.data
  })
}
loadBanner()

const categoryColors = [
  'linear-gradient(135deg, #ff6b35 0%, #ff8c42 100%)',
  'linear-gradient(135deg, #ffd93d 0%, #ffe66d 100%)',
  'linear-gradient(135deg, #6bcf7f 0%, #8fe99e 100%)',
  'linear-gradient(135deg, #ff6b9d 0%, #ffa5c5 100%)'
]

const goods=ref([])
const loadGoods=()=>{
  request.get('/goods').then(res=>{
    goods.value=res.data
  })
}
loadGoods();

const getProvine=(place)=>{
  return place?place.split('/')[0]:'';
}


const users=ref([])
const getUser=(userId)=>{
  return users.value.find(user=>Number(user.id)===Number(userId))||{}
}

const loadUser=()=>{
  request.get('/user').then(res=>{
    users.value=res.data;
  })
}
loadUser()

</script>

<template>
  <div style="width:100%;min-height:300px;background-color: #f3f3f3;padding:10px">

    <div style="width:85%;margin:0 auto;height:100%;">

      <div style="width: 100%;height: 50px;padding-left: 10px;padding-right: 10px;cursor:pointer" v-if="showNotice">

        <el-carousel height="50px" direction="vertical" :interval="3000" indicator-position="none">
          <el-carousel-item v-for="notice in notices" :key="notice.id">
            <div style="display:flex;margin-top: 20px;display: flex;justify-content: space-between;">
               <span style="color: #606266;">{{ notice.name }}:{{ notice.info }}</span>
               <span style="font-size:18px;color: grey" @click="closeNotice">X</span>
            </div>
          </el-carousel-item>
        </el-carousel>

      </div>


      
      <div style="display: flex;gap:20px;height: 430px;padding: 10px">
        <!-- 左边分类 -->
        <div style="height: 100%;flex:1;padding: 10px;background-color: white;border:1px solid #dfdfdf;border-radius: 10px;">
          <div style="width:100%;height: 100%;display:flex;flex-direction: column;gap:18px;justify-content: center;align-items: center;">
            <div v-for="item in types":key="item.id" style="display: flex;" class="type-item">
              <div id="card-jump" style="display:flex;gap:10px;" @click="router.push('/front/search?typeId='+item.id)+'&keyword='+null">
                  <img :src="item.icon" style="width:20px;height: 20px;object-fit: fill;" alt="">
                  <span style="font-weight: 500">{{ item.name }}</span>
              </div>
            </div>
          </div>
        </div>
        <!-- 中间轮播提示条 -->
        <div style="height: 100%;flex:2;border-radius: 10px;">
          <el-carousel height="410px">
            <el-carousel-item v-for="banner in banners" :key="banner.id">
             <div style="height: 100%;width: 100%;position: relative">
                <div style="position: absolute;top:50%;left:10%">
                  <span style="font-size: 20px;color:white;font-weight: bolder">{{ banner.name }}</span>
                </div>
                <img :src="banner.img" style="height: 100%;width: 100%;object-fit: fill;border-radius: 10px;" alt="">
             </div>
            </el-carousel-item>
          </el-carousel>
        </div>
        <!-- 右边热门分类 -->
        <div style="height: 100%;flex:5;display: grid;grid-template-columns: repeat(2,1fr);gap:20px">
          <div v-for="(item,index) in hotTypes":key="item.id" class="hotType-item":style="{background : categoryColors[index]}">
            
            <div style width="180px">
              <div>
                <span style="color: white;font-size: 16px;font-weight: 500;">{{ item.name }}</span>
              </div>
              <div>
                <span style="color:white;font-size: 12px;">{{ item.info }}</span>
              </div>
              <div style="margin-top:20px">
                <img :src="item.img" style="height:80px;width:80px;object-fit:cover" alt="">
              </div>
            </div>

            <div style="height: 160px;background:rgba(255,255,255,0.8);width:70%;border-radius: 20px;padding:10px;display:flex;gap:10px">
              <div v-for="item in 3":key="item" style="flex:1"  class="hotType-item-goods">
                <div style="height:90px;width:100%">
                  <img src="" style="width:100%;height: 100%;object-fit: cover;border-radius: 10px;" alt="">
                </div>
                <div style="text-align: center;margin-top: 20px;">
                  <span style="color: orangered;font-size: 16px;font-weight: 1000;">$100</span>
                </div>
              </div>
            </div>

          </div>

        </div>
      </div>

    </div>
  </div>
  <div>

    <div style="width:80%;margin: 0 auto;height: 100%;">

      <div style="display: flex;justify-content: space-between;margin-top: 50px;margin-bottom: 20px">
        <div>
          <span style="font-size: 40px;font-weight: bolder">精选好物</span>
        </div>
        <div>
          <span style="color:#3a76c4">查看全部</span>
        </div>
      </div>

      <div style="
        display: grid;
        grid-template-columns:repeat(4,1fr);
        gap:20px;
        grid-auto-flow: row;

      ">
        <div style="height: 380px;width: 100%;background-color: white;overflow: hidden;border:1px solid #e5e5e5;border-radius: 8px;box-shadow: 0 2px 8px rgba(0,0,0,0.04)" v-for="item in goods" :key="item.id" @click="router.push('/front/goodsDetail?id=' + item.id)">
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
            <el-divider style="margin: 15px;"></el-divider>

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
  </div>

</template>

<style scoped>

#card-jump:hover{
  background-color: #c8c8c8;
  border-radius: 5px;
}

.hotType-item{
  height: 195px;width:100%;
  border-radius: 10px;
  padding:10px;
  display: flex;
  gap:20px;
  transition: all 0.2s ease;
}

.hotType-item:hover{
  transform: translateY(-5px);
}

.hotType-item-goods{
  transition: all 0.2s ease;
}

.hotType-item-goods:hover{
  scale:1.05;
}

</style>
