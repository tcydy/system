<script setup>


import {reactive, ref} from "vue";
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import {serverHost} from "../../../config/config.default.js";
import {Plus} from "@element-plus/icons-vue";

//表单数据
const form=reactive({})
//用户信息
const account=ref(
    localStorage.getItem('account')?JSON.parse(localStorage.getItem('account')):{}
)

//添加上传请求头配置
const uploadHeader=reactive({
  token: account.value.token || ''
})

//获取用户信息
const getAccount=()=>{
  request.get('/web/userInfo').then(res=>{
    if(res.code==='200'&&res.data){
      Object.assign(form,res.data)
    }else{
      ElMessage.error(res.msg)
    }
  })
}
getAccount();


//定义要发出的事件
const emit=defineEmits(['refreshUser'])
//保存用户信息
const save=()=>{
  //根据角色选择不同的接口
  const apiUrl=account.value.role==='ROLE_ADMIN'?'/admin':'/user';

  request.post(apiUrl,form).then(res=>{
    if(res.code==='200'){
      ElMessage.success('保存成功')
      //只更新昵称和头像到account对象，其他属性保持不变
      if(form.nickname)account.value.nickname=form.nickname
      if(form.avatarUrl) account.value.avatarUrl=form.avatarUrl

      //更新浏览器存储的用户信息
      localStorage.setItem('account',JSON.stringify(account.value))

      //向父组件发送更新时间，传递更新后的用户信息
      emit('update-account',account.value)

    }else{
      ElMessage.error(res.msg||'保存失败')
    }
  })
}
//取消修改
const cancel=()=>{
  //重新获取用户信息，回复原始数据
  getAccount();
  ElMessage.info('已取消修改');
}

//退出
const back=()=>{
  window.history.back()
}

//头像上传成功处理
const  handleAvatarSuccess=(res)=>{
  console.log('上传返回:', res)
  form.avatarUrl=res
  ElMessage.success('头像上传成功,请点击"保存修改"以保存')
}

//头像上传失败处理
const handleAvatarError=(error)=>{
  console.error('上传失败:', error)
  ElMessage.error('头像上传失败,请重试')
}

//上传前的校验
const beforeAvatarUpload=(file)=>{
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}


</script>

<template>
  <div class="person-container">
    <el-card class="person-card">
      <h2 class="card-title">个人信息</h2>

      <el-form label-width="50px">
        <div class="avatar-container">
          <el-upload
              class="avatar-uploader"
              :action="serverHost+'/web/upload'"
              :headers="uploadHeader"
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :on-success="handleAvatarSuccess"
              :on-error="handleAvatarError"
              accept="image/*">
            <img v-if="form.avatarUrl" :src="form.avatarUrl" class="avatar">
            <el-icon v-else class="avatar-uploader-icon"><Plus/></el-icon>
          </el-upload>
          <div class="avatar-tip">点击上传头像 (支持jpg/png,最大2MB)</div>
        </div>

        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" autocomplete="off"></el-input>
        </el-form-item>

        <!-- 按钮区域：独立布局，不再嵌套到头像容器里 -->
        <el-form-item>
          <div class="form-footer">
            <el-button type="primary" @click="save">保存修改</el-button>
            <div class="right-btn-group">
              <el-button @click="cancel">取消</el-button>
              <el-button @click="back">返回</el-button>
            </div>
          </div>
        </el-form-item>

      </el-form>
    </el-card>
  </div>
</template>

<style lang="scss" scoped>
.person-container{
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px;
  

  .person-card{
    max-width: 600px;
    width: 100%;
    padding: 20px;

    .card-title{
      text-align: center;
      margin-top:0;
      margin-bottom: 20px;
      font-size: 22px;
      color:#333;
    }
    .avatar-container{
      display: flex;
      flex-direction: column;
      align-items: center;
      margin-bottom: 20px;

      .avatar-tip{
        margin-top: 8px;
        font-size: 12px;
        color:#909399;
      }
    }
  }
}

/* 按钮布局核心样式 */
.form-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}
.right-btn-group {
  display: flex;
  gap: 10px; /* 取消、返回两个按钮之间的间距 */
}

.avatar-uploader-icon{
  font-size: 28px;
  color:#8c939d;
  width: 138px;
  height: 138px;
  line-height: 138px;
  text-align: center;
  border: 2px dashed #d9d9d9;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s;
  pointer-events: none;
  
  &:hover{
    border-color:#409eff;
    color:#409eff;
  }
}

.avatar-uploader{
  display: inline-block;
  
  :deep(.el-upload){
    border-radius: 50%;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: all 0.3s;
    display: block;
    
    &:hover .avatar-uploader-icon{
      border-color:#409eff;
      color:#409eff;
    }
  }
}

.avatar{
  height: 138px;
  width: 138px;
  display: block;
  object-fit: cover;
  object-position: center;
  border-radius: 50%;
  border:2px solid #dcdfe6;
  cursor: pointer;
  transition: all 0.3s;

  &:hover{
    border-color:#409eff;
    transform: scale(1.05);
  }
}
</style>