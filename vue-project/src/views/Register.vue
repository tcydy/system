<script setup>

import request from "@/utils/request.js";
import {useRouter} from "vue-router";
import {reactive,ref} from "vue";
import {User, Lock, Key, DataAnalysis} from '@element-plus/icons-vue'
import {ElMessage} from "element-plus";
import {projectName} from "../../config/config.default.js";

//路由实例
const router=useRouter()
const userFromInst=ref(null)

//角色选项
const roleOptions=[
  //系统角色
  {label:'普通用户' ,value:'ROLE_USER'},
  {label:'管理员',value:'ROLE_ADMIN'},

]


//注册表单
const registerForm=reactive({
  username: '',
  nickname:'',
  password:'',
  confirmPassword:'',
  role:'ROLE_USER'//默认选择普通用户
})

//是否同意
const isallow=ref(true)

//表单验证规则
const rules={
  username:[
    {required:true,message:'请输入用户名',trigger:'blur'},
    {min:3,max:10,message: '长度在3到10个字符',trigger: 'blur'}
  ],
  nickname:[
    {required:true,message:'请输入昵称',trigger:'blur'},
    {min:1,max:10,message: '长度在1到10个字符',trigger: 'blur'}
  ],
  password:[
    {required:true,message:'请输入密码',trigger:'blur'},
    {min:1,max:20,message:'长度在1到20个字符',trigger:'blur'}
  ],
  confirmPassword:[
    {required:true,message:'请输入密码',trigger:'blur'},
    {min:1,max:20,message:'长度在1到20个字符',trigger:'blur'}
  ],

  role:[
    {required:true,message:'请选择角色',trigger:'change'}
  ]
}

//注册方法
const register=()=>
{
  //表单校验合法
  userFromInst.value.validate((valid)=>{
    //若合法
    if(valid)
    {
      //检查两次密码是否一致
      if(registerForm.password!==registerForm.confirmPassword)
      {
        ElMessage.error('两次输入的密码不相同')
        return false
      }

      //创建一个不包含确认密码的对象
      const registerDate={
        username: registerForm.username,
        nickname: registerForm.nickname,
        password:registerForm.password,
        role:registerForm.role
      }
      request.post('/web/register',registerDate).then((res)=>{
        if(res.code==='200')
        {
          ElMessage.success('注册成功，请登录')
          router.push('/login')
        }
        else{
          ElMessage.error(res.msg||'注册失败')
        }
      }).catch(error=>{
        console.error('注册请求错误',error)
        ElMessage.error('注册失败，请稍后再试')
      })
    }
  })
}

</script>


<template>
  <div class="register-container">
    <div class="background-shapes">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
      <div class="shape shape-3"></div>
      <div class="shape shape-4"></div>
    </div>

    <div class="register-content">
      <div class="register-left">
        <div class="brand-logo">
          <div class="logo-circle">
            <img src="../../config/Logo.svg"alt="Logo" class="logo-image" />
            <Key class="logo-icon"/>
          </div>
          <h2 class ="brand-name">{{projectName}}</h2>
        </div>
        <div class="register-features">
          <div class="feature-item">
            <div class="feature-icon">
              <el-icon><User/></el-icon>
            </div>
            <div class="feature-text">
              <h3>用户管理</h3>
              <p>全面的用户权限管理系统</p>
            </div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <el-icon><Lock/></el-icon>
            </div>
            <div class="feature-text">
              <h3>安全可靠</h3>
              <p>多重安全防护，数据无忧</p>
            </div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <el-icon><data-analysis/></el-icon>
            </div>
            <div class="feature-text">
              <h3>数据融合</h3>
              <p>多方位数据存储功能融合</p>
            </div>
          </div>
        </div>
        <div class="register-footer">
          <p>{{new Date().getFullYear()}}{{projectName}}. 保留所有权利</p>
        </div>
      </div>

      <div class="register-right">
        <div class="register-form-container">
          <h1 class="register-title">创建账号</h1>
          <p class="register-subtitle">请填写以下信息完成注册</p>

          <el-form :model="registerForm":rules="rules" ref="userFromInst" class="register-form" @keydown-enter="register">
            <el-form-item prop="username">
              <el-input
                  placeholder="请输入用户名"
                  size="large"
                  :prefix-icon="User"
                  v-model="registerForm.username">
              </el-input>
            </el-form-item>

            <el-form-item prop="nickname">
              <el-input
                  placeholder="请输入昵称"
                  size="large"
                  :prefix-icon="User"
                  v-model="registerForm.nickname">
              </el-input>
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                  size="large"
                  :prefix-icon="Lock"
                  show-password
                  placeholder="请输入密码"
                  v-model="registerForm.password">
              </el-input>
            </el-form-item>

            <el-form-item prop="confirmPassword">
              <el-input
                  size="large"
                  :prefix-icon="Lock"
                  show-password
                  placeholder="请确认密码"
                  v-model="registerForm.confirmPassword">
              </el-input>
            </el-form-item>

            <el-form-item prop="role">
              <el-select
                  v-model="registerForm.role"
                  placeholder="请选择角色"
                  size="large"
                  style="width:100%">
                <el-option
                    v-for="item in roleOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>

            <el-form-item>
              <el-checkbox v-model="isallow" class="allow-warp">
                我已阅读并同意 <a href="#" class="link">《隐私政策》</a>和<a href="#" class="link">《服务条款》</a>
              </el-checkbox>
            </el-form-item>

            <el-form-item>
              <el-button
                  class="register-button"
                  type="primary"
                  size="large"
                  :disabled="!isallow"
                  @click="register">
                注册
              </el-button>
            </el-form-item>

            <div class="login-link">
              已有账号？<a @click="router.push('/login')" class="link">立即登录</a>
            </div>
          </el-form>
        </div>

      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.register-container{
  width:100%;
  height:100vh;
  display:flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg,#f0f4f8 0%,#d7e3f3 100%);
  position: relative;
  overflow: hidden;
}
.background-shapes{
  position: absolute;
  width:100%;
  height: 100%;
  z-index: 0;
  .shape{
    position: absolute;
    border-radius: 50%;

    &-1{
      width: 300px;
      height: 300px;
      /* 同步登录页浅黄透明渐变 */
      background: linear-gradient(45deg, rgba(255, 242, 153, 0.1) 0%, rgba(235, 221, 119, 0.05) 100%);
      top: -100px;
      left:-100px;
    }
    &-2 {
      width: 200px;
      height: 200px;
      background: linear-gradient(45deg, rgba(48, 50, 91, 0.1) 0%, rgba(48, 50, 91, 0.05) 100%);
      bottom: -50px;
      right: 10%;
    }

    &-3 {
      width: 150px;
      height: 150px;
      /* 同步登录页浅黄透明渐变 */
      background: linear-gradient(45deg, rgba(255, 242, 153, 0.1) 0%, rgba(235, 221, 119, 0.05) 100%);
      top: 20%;
      right: -50px;
    }

    &-4 {
      width: 250px;
      height: 250px;
      background: linear-gradient(45deg, rgba(48, 50, 91, 0.08) 0%, rgba(48, 50, 91, 0.04) 100%);
      bottom: 10%;
      left: 10%;
    }
  }
}

.register-content{
  width:1000px;
  height:600px;
  display:flex;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 20px 20px rgba(0,0,0,0.1);
  position: relative;
  z-index: 1;
  background: rgba(255,255,255,0.9);
  backdrop-filter: blur(10px);
  border:1px solid rgba(255,255,255,0.5);
}
.register-left{
  width:40%;
  /* 和登录页完全一致奶黄渐变 */
  background:linear-gradient(135deg,#fff299 0%,#ebdd77 100%);
  color:#30325B;
  padding:40px;
  display: flex;
  flex-direction: column;
  position:relative;
  overflow: hidden;
}
.brand-logo{
  display: flex;
  align-items: center;
  margin-bottom: 60px;
  position:relative;
  z-index: 1;

  .logo-circle{
    width:50px;
    height: 50px;
    border-radius: 50%;
    background-color: #30325B;
    position:relative;
    margin-right: 12px;
    box-shadow: 0 4px 10px rgba(48, 50, 91, 0.2);

    .logo-image{
      width: 40px;
      height: 40px;
      object-fit: contain;
      position: absolute;
      top:50%;
      left:50%;
      transform: translate(-50%,-50%);
      color: #fff;
    }
  }

  .brand-name{
    font-size: 20px;
    font-weight: 600;
    color: #30325B;
  }
}

.register-features{
  flex:1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position:relative;
  z-index: 1;

  .feature-item{
    display: flex;
    align-items:center;
    margin: 30px;

    .feature-icon{
      width:48px;
      height:48px;
      border-radius: 12px;
      background-color: #30325B;
      display: flex;
      justify-content: center;
      align-items: center;
      margin-right: 16px;
      box-shadow: 0 4px 10px rgba(48, 50, 91, 0.2);

      .el-icon{
        font-size:24px;
        color: #fff;
      }
    }

    .feature-text{
      color: #30325B;
      h3{
        font-size:16px;
        margin:0 0 5px 0;
        font-weight: 600;
      }

      p{
        font-size: 14px;
        margin:0;
        opacity: 0.8;
      }
    }
  }
}

.register-footer{
  font-size:12px;
  opacity: 0.7;
  text-align: center;
  position:relative;
  z-index: 1;
  color: #30325B;
}

.register-right{
  width: 60%;
  background-color: transparent;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
}

.register-form-container
{
  width: 360px;
  position: relative;
  z-index: 1;
}

.register-title{
  font-size: 28px;
  font-weight: 600;
  color:#30325B;
  margin:0 0 8px 0;
}

.register-subtitle{
  font-size: 16px;
  color:#505280;
  margin:0 0  40px 0;
}

.register-form{
  .allow-warp{
    font-size: 14px;
    color:#505280;
  }
  .register-button{
    width: 100%;
    height: 48px;
    font-size: 16px;
    font-weight: 500;
    margin-top:10px;
    /* 按钮渐变同步登录页黄色 */
    background: linear-gradient(to right, #ffe618, #e9d866);
    border:none;
    color: #30325B;

    &:hover{
      background: linear-gradient(to right, #ffe618, #ddcc5c);
    }

    &:disabled{
      background: linear-gradient(to right, #fff2b0, #f3e699);
      color: #686a9c;
    }
  }
}

.register-link{
  text-align: center;
  margin-top:20px;
  font-size: 14px;
  color:#505280;
}

.link{
  color: #30325B;
  text-decoration: none;
  cursor:pointer;
  &:hover{
    text-decoration: underline;
    /* hover黄色统一登录页亮黄 */
    color: #ffe618;
  }
}
</style>