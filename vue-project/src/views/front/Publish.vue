<script setup>

import {reactive, ref,shallowRef} from "vue";

import {Delete,UploadFilled} from "@element-plus/icons-vue";
import {serverHost} from "../../../config/config.default.js";
import {ElMessage, ElMessageBox} from "element-plus";
import {regionData} from "element-china-area-data";
import request from "@/utils/request.js";

import '@wangeditor/editor/dist/css/style.css'
import { Editor,Toolbar } from "@wangeditor/editor-for-vue";
import axios from "axios"

//定义富文本数据
const htmlContent=ref('')
const editorRefContent=shallowRef();

const form=ref({
    quality:'全新',
    shipment:'包邮'
})

//富文本自定义上传方法
const customUpload=(file,insertFn)=>{
  const formData=new FormData()
  formData.append('file',file)
  axios({
    url:`${serverHost}/web/upload`,
    method:'post',
    data:formData,
    headers:{'Content-Type':'multipart/form-data'}
  }).then(res=>{
    insertFn(res.data)
  }).catch((error)=>{
    console.log('上传失败:',error)
    ElMessage.error('上传失败')
  })
}

//wangEditor配置
const editorConfig={
  placeholder:'请输入内容...',
  MENU_CONF:{
    uploadImage:{
      customUpload:async (file,insertFn)=>{
        customUpload(file,insertFn)
      },
    },
    uploadVideo:{
      customUpload:async(file,insertFn)=>{
        customUpload(file,insertFn)
      },
    },
  }
}

//保存
const save=()=>{

  if(imgList.value.length>0){
    form.value.imgList=imgList.value.join(',');

  }else{
    form.value.imgList='';
  }

  form.value.content=htmlContent.value;
  if(Array.isArray(form.value.place)){
    form.value.place=form.value.place.join('/');
  }else{
    form.value.place=form.value.place || '';
  }

  request.post("/goods",form.value).then(res=>{
    if(res.code==='200'){
      ElMessage.success("保存成功")
    //   dialogFormVisible.value=false
    //   load()
    }else{
      ElMessage.error("保存失败")
    }
  })

  //跳转到首页，不要用router
  window.location.href='/front/home'

}


//图片上传成功处理
const  handleImgUploadSuccess=(res)=>{
  form.value.img=res
};

//多图列表
const imgList = ref([])

//多图片上传成功处理
const handleImgListUploadSuccess=(res)=>{
  imgList.value.push(res);
};

//删除已上传的图片
const removeImgList=(index)=>{
  imgList.value.splice(index,1);
};

const types = ref([])
const loadType = ()=>{
    request.get('/type').then(res=>{
        types.value = res.data
    })
}
loadType()

const users = ref([]) 
const loadUser = () => {
  request.get('/user').then(res => {
    users.value = res.data
  })
}
loadUser()

</script>

<template>
    <div style="width:100%;height:100%;background-color:#f1f1f1;padding:50px">
        <div style="width: 80%;margin: 0 auto;min-height: 200px;">
            <el-card style="border-radius: 20px;">

                <h1>发布闲置</h1>

                <h3>基础信息</h3>
                <el-divider></el-divider>

                      <el-form  :model="form" label-width="100px">

                        <el-form-item label="商品主图" required>
                            <div class="upload-container">
                            <el-avatar v-if="form.img" :src="form.img" :size="300" style="border-radius: 0;"/>
                            <el-upload :action="`${serverHost}/web/upload`" :on-success="handleImgUploadSuccess" :show-file-list="false" >
                                <el-button type="primary" :icon="UploadFilled">{{ form.img ? '更换图片' : '上传图片' }}</el-button>
                            </el-upload>
                            </div>
                        </el-form-item>

                        <!--        多图组件上传-->
                        <el-form-item label="多张图片">
                        <div class="upload-container">
                            <div class="image-list" v-if="imgList.length> 0">
                            <div v-for="(img,index) in imgList":key="index" class="image-item">
                                <el-avatar :src="img" :size="80"/>
                                <el-button type="danger" circle:icon="Delete" class="delete-btn"@click="removeImgList(index)"></el-button>
                            </div>
                            </div>
                            <el-upload :action="`${serverHost}/web/upload`" :on-success="handleImgListUploadSuccess" :show-file-list="false" multiple>
                            <el-button type="primary":icon="UploadFilled">上传图片</el-button>
                            </el-upload>

                        </div>
                        </el-form-item>

                        <el-form-item label="商品名称"required>
                            <el-input v-model="form.name" placeholder="简介清晰的标题会吸引更多人" :maxlength="100" show-word-limit/>
                        </el-form-item>

                        <el-form-item label="分类"required>
                        <el-select v-model="form.typeId" placeholder="请选择分类" style="width: 100%">
                            <el-option
                                v-for="item in types"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id"
                        />
                        </el-select >
                        </el-form-item>

                        <el-form-item label="成色">
                        <el-radio-group v-model="form.quality">
                            <el-radio value="全新" border>全新</el-radio>
                            <el-radio value="九成新" border>九成新</el-radio>
                            <el-radio value="八成新" border>八成新</el-radio>
                            <el-radio value="七成新" border>七成新</el-radio>
                            <el-radio value="六成新及以下" border>六成新及以下</el-radio>
                        </el-radio-group>
                        </el-form-item>                        

                        <!--        富文本编辑-->
                        <el-form-item label="商品详情描述">
                        <div style="border:1px solid #ccc;z-index:100">
                            <Toolbar style="border-bottom:1px solid #ccc" :editor="editorRefContent" :default-config="editorConfig" mode="default"/>
                            <Editor style="min-height:50px; overflow-y: hidden;border-radius: 10px;" v-model="htmlContent" :default-config="editorConfig" mode="default" @onCreated="editorRefContent=$event"/>
                        </div>
                        </el-form-item>

                        <el-form-item label="所在城市">
                        <el-cascader
                            v-model="form.place"
                            :options="regionData"
                            :props="{value:'label'}"
                            placeholder="请选择省市区"
                            clearable
                            style="width:100%">
                        </el-cascader>
                        </el-form-item>



                        <el-form-item label="发货设置">
                        <el-radio-group v-model="form.shipment">
                            <el-radio value="包邮" border>包邮</el-radio>
                            <el-radio value="不包邮" border>不包邮</el-radio>
                        </el-radio-group>
                        </el-form-item>

                        <h3>价格</h3>

                        <el-divider></el-divider>
                        <div style="display:flex;gap:50px">

                            <div style="flex: 1">
                                <div style="display: flex;gap:5px;margin-bottom: 10px;">
                                    <span>售价</span>
                                    <span style="color:red">*</span>
                                </div>
                                <el-input v-model="form.price" type="number" placeholder="请输入" style="width:100%"/>
                            </div>
                            <div style="flex: 1">
                                <div style="display: flex;gap:5px;margin-bottom: 10px;">
                                    <span>原价</span>
                                    <span style="color:red">*</span>
                                </div>
                                <el-input v-model="form.rePrice" type="number" placeholder="请输入" style="width:100%"/>
                            </div>
                            
                        </div>

                    </el-form>

                    <div style="display: flex;justify-content: space-around;margin-top: 100px">
                        <button @click="save" style="background-color: orange;color:white;width: 400px;height:50px;border:none;border-radius: 50px;">
                          发布
                        </button>

                    </div>

            </el-card>
        </div>
    </div>

</template>


<style scoped>

:deep(.el-card__body){
    padding:30px;
}

.image-list{
  display: flex;
  flex-wrap: wrap;
  gap:10px;
  margin-bottom: 10px;
}
.image-item{
  position: relative;
}
.delete-btn{
  position: absolute;
  top:-8px;
  right: -8px;
  transform:scale(0.8);
}

</style>