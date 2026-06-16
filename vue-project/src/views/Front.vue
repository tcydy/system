<script setup>

//路由实例


import {useRoute, useRouter} from "vue-router";
import {projectName} from "../../config/config.default.js";
import {ElMessage} from "element-plus"
import { User, Lock, SwitchButton, Search, Plus, ChatDotRound, Document, Star,House } from '@element-plus/icons-vue';
import { computed, ref } from "vue";
import request from '@/utils/request.js';
import Type from "@/views/back/Type.vue";

const router=useRouter();
const route=useRoute();


//用户信息
const account=ref(
    localStorage.getItem('account')?JSON.parse(localStorage.getItem('account')):{}
)

//当前激活的菜单项
const activeMenu=computed(()=>route.path)
//退出登录
const logout=()=>{
  localStorage.removeItem('account')
  ElMessage.success('退出成功')
  router.push('/login')
}

const handleUpdateAccount=(updatedAccount)=>
{
  //更新组件中的用户信息
  account.value=updatedAccount
  localStorage.setItem('account',JSON.stringify(updatedAccount))
  ElMessage.success('用户信息已更新')
}
const keyword=ref('')
const toSearch=()=>{
  router.push('/front/search?typeId=0&keyword='+keyword.value)
}

const my = ref([])
const getAccount=()=>{
  request.get('/web/userInfo').then(res => {
    if(res.code==='200'&&res.data){
      my.value=res.data
      console.log(res.data)
      console.log(my.value)
    }else{
      ElMessage.error(res.msg)
    }
  })
}
getAccount()


</script>

<template>
  <div class="front-container">
  <!--顶部导航栏-->
    <header class="header-nav">
      <div class="header-left-wrap" @click="router.push('/front/home')">
        <div class="logo-wrap">
          <div class="logo">
            <img src="../../config/Logo.svg" alt="Logo"/>
          </div>
        </div>
      </div>

      <div style="height: 40px;width: 500px;background-color: white;border-radius: 40px;display: flex;align-items: center;justify-content: space-between" >


        <div style="margin-left: 20px">
          <input v-model="keyword" placeholder="搜索你想要的闲置商品" style="height: 40px;border:none;outline: none;background-color: transparent"/>
        </div>
        <div style="margin-right: 10px">
          <el-button :icon="Search" size="large" type="primary" style="border-radius: 20px;width: 100px;height: 20px" @click="toSearch">搜索</el-button>
        </div>

      </div>

      <div class="user-wrap">
  <!--      未登录状态显示登录注册按钮-->
        <template v-if="!account.id">
          <div class="btn-login">
            <el-button @click="router.push('/login')">登录</el-button>
          </div>
          <div class="btn-register" style="margin-left:10px">
            <el-button @click="router.push('/register')">注册</el-button>
          </div>
        </template>
        <!--一登录状态显示用户头像和下拉菜单-->
        <el-dropdown v-else class="custom-dropdown">
          <div class="user-avatar avatar-with-border">
            <img :src="account.avatarUrl || 'https://via.placeholder.com/40'" alt="avatar"/>
            //<img :src="account.avatarUrl || 'https://picsum.photos/40/40'" alt="avatar" />
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>{{account.nickname}}</el-dropdown-item>
              <el-dropdown-item>
                <router-link to="/front/person" class="dropdown-link">
                  <el-icon><User/></el-icon>
                  <span>个人信息</span>
                </router-link>
              </el-dropdown-item>
              <el-dropdown-item>
                <router-link to="/front/password" class="dropdown-link">
                  <el-icon><Lock/></el-icon>
                  <span>修改密码</span>
                </router-link>
              </el-dropdown-item>
              <el-dropdown-item>
                <div @click="logout"class="dropdown-link">
                  <el-icon><SwitchButton/></el-icon>
                  <span>退出登录</span>
                </div>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>
    <!--  主内容区域-->
    <div class="main-content">
      <router-view @update-account="handleUpdateAccount"></router-view>
    </div>
    <!--  页脚-->
    <footer class="front-footer">
      <p>@{{new Date().getFullYear()}}{{projectName}}.保留所有权利</p>
    </footer>
    <div style="width: 58px;background-color:#fff;position: fixed;right: 28px;top: 50%;transform: translateY(-50%);border-radius: 14px;padding: 0;box-shadow: 0 8px 24px rgba(0,0,0,0.16);overflow: hidden;z-index: 1700;">

      <div style="width: 58px;height: 58px;border-bottom:1px solid #eeeeee;padding: 0" class="side-bar-item" :class="{'is-active':route.path==='/front/publish'}" @click="router.push('/front/publish')"  >
        <div style="display: flex;justify-content: center;font-size: 20px">
          <el-icon><Plus/></el-icon>
        </div>
        <div style="display: flex;justify-content: center;font-size: 12px;margin-top: 4px">
          <span>发布</span>
        </div>
      </div>

      <div style="width: 58px;height: 58px;border-bottom:1px solid #eeeeee;padding: 0"class="side-bar-item" :class="{'is-active':route.path==='/front/message'}" @click="router.push('/front/message')">
        <div style="display: flex;justify-content: center;font-size: 20px">
          <el-icon><ChatDotRound/></el-icon>
        </div>
        <div style="display: flex;justify-content: center;font-size: 12px;margin-top: 4px">
          <span>消息</span>
        </div>
      </div>

      <div style="width: 58px;height: 58px;border-bottom:1px solid #eeeeee;padding: 0"class="side-bar-item" :class="{'is-active':route.path==='/front/orders'}" @click="router.push('/front/orders')">
        <div style="display: flex;justify-content: center;font-size: 20px">
          <el-icon><Document/></el-icon>
        </div>
        <div style="display: flex;justify-content: center;font-size: 12px;margin-top: 4px">
          <span>订单</span>
        </div>
      </div>

      <div style="width: 58px;height: 58px;border-bottom:1px solid #eeeeee;padding: 0"class="side-bar-item" :class="{'is-active':route.path==='/front/collect'}" @click="router.push('/front/collect')">
        <div style="display: flex;justify-content: center;font-size: 20px">
          <el-icon><Star/></el-icon>
        </div>
        <div style="display: flex;justify-content: center;font-size: 12px;margin-top: 4px">
          <span>收藏</span>
        </div>
      </div>

      <div style="width: 58px;height: 58px;border-bottom:none;padding: 0"class="side-bar-item" :class="{'is-active':route.path==='/front/user'}" @click="router.push('/front/user?id='+my.id)">
        <div style="display: flex;justify-content: center;font-size: 20px">
          <el-icon><User/></el-icon>
        </div>
        <div style="display: flex;justify-content: center;font-size: 12px;margin-top: 4px">
          <span>我的</span>
        </div>
      </div>

    </div>


  </div>


</template>



<style scoped lang="scss">
/*定义前台头部 背景 主题色*/
$front-back-color:#ffe618;

/*定义前台头部 字体 主题色*/
$front-font-color: #151111;

.side-bar-item{
  cursor: pointer;
  color:#666;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  transition: background-color 0.2s ease,color 0.2s ease;
}
.side-bar-item:hover,
.side-bar-item.is-active{
  background-color:#ffe618;
  color:#151111;
}

.side-bar-item .el-icon{
  color:#777;
}

.side-bar-item:hover .el-icon,
.side-bar-item.is-active .el-icon{
  color:#151111;
}

.el-button--primary{
  background: #ffe618;
  color: #151111;
  border: none;
}

.front-container{
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.header-nav{
  z-index: 1800;
  position: sticky;
  top:0;
  height: 70px;
  background-color: $front-back-color;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding:0 40px;
  box-shadow: 0 2px 10px 0 rgba(0, 0,0,0.1);
  overflow: visible;

  .header-left-wrap{
    display: flex;
    align-items: center;
    height: 100%;

    .logo-wrap{
      display: flex;
      align-items: center;
      margin-left: 20px;
      margin-right: 60px;

      .logo{
        width: 30px;
        height: 30px;
        margin-right: 20px;
        font-weight: bold;
        
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          filter: brightness(0.8) contrast(1.3);
          
        }
      }
      .login-content{
        font-size: 22px;
        font-weight: 500;
        color:$front-font-color;
      }
    }
    .header-navs{
      margin-left: 80px;
      height: 100%;

      .el-menu{
        background-color: $front-back-color !important;
        border:none !important;
        height: 70px !important;
      }
      .el-menu-item{
        height: 70px !important;
        line-height: 70px !important;
        border:none !important;
      }
      .el-menu-item:hover{
        color:$front-font-color !important;
        background-color: transparent !important;
      }
      .el-menu-item.is-active{
        color:$front-font-color !important;
        background-color: transparent !important;
        border:none !important;
      }
    }
  }
  .user-wrap{
    display: flex;
    align-items: center;
    margin-right: 20px;
    height: 100%;
    .btn-login{
      margin-top:0;
      .user-avatar{
        width: 40px;
        height: 40px;
        border-radius: 50%;
        overflow: hidden;
        border:1px solid $front-font-color;
        padding:2px;
        cursor: pointer;
        outline:none !important;
        img{
          width: 100%;
          height: 100%;
          object-fit: cover;
          border-radius: 50%;
        }
      }
      .dropdown-link{
        display: flex;
        align-items: center;
        color:inherit;
        text-decoration: none;
        .el-icon{
          margin-right: 8px;
        }
      }
    }
  }
}
.main-content{
  flex:1;
  background-color: #fff;
  padding-bottom: 50px;
}

.front-footer{
  padding: 16px 24px;
  text-align: center;
  background-color: #fff;
  color: #666;
  font-size: 12px;
  border-top: 1px solid #eee;
}
.avatar-with-border {
  width: 40px;
  height: 40px;
  border-radius: 50% !important;
  border: 2px solid $front-font-color !important;
  padding: 2px;
  overflow: hidden;
  //添加img标签样式
  img{
    width: 100%;
    height: 100%;
    object-fit: cover;
    object-position: center;
    border-radius: 50%;
  }
}
.brand-name{
  font-weight: bold;
  color:$front-font-color;
  font-size: 22px;
}
.nav-link {
  color: inherit;
  text-decoration: none;
  position: relative;

  &.router-link-active {
    color: #4084d9;

    // 创建下划线效果
    &::after {
      content: '';
      position: absolute;
      bottom: -5px;
      left: 0;
      right: 0;
      height: 2px;
      background-color: #4084d9;
    }
  }
}

</style>
