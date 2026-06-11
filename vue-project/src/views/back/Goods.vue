<script setup>

//表格数据
import {reactive, ref,shallowRef} from "vue";
import request from "@/utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";
import {Delete, Edit, Plus, Search, UploadFilled} from "@element-plus/icons-vue";
import {serverHost} from "../../../config/config.default.js";


//引入富文本组件
import '@wangeditor/editor/dist/css/style.css'
import { Editor,Toolbar } from '@wangeditor/editor-for-vue'

import axios from "axios"
import {regionData} from "element-china-area-data";


//定义富文本数据
const htmlContent=ref('')
const editorRefContent=shallowRef();

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

//表格数据
const tableData=ref([])
const total=ref(0)
const pageNum=ref(1)
const pageSize=ref(10)


//搜索条件
const searchForm=reactive({
  keyword:'',
})

//表单数据
const form=ref({})
const dialogFormVisible=ref(false)
const multipleSection=ref([])



//加载数据
const load=()=>{
  request.get("/goods/page",{
    params:{
      pageNum:pageNum.value,
      pageSize:pageSize.value,
      keyword:searchForm.keyword,
    }
  }).then(res=>{
    if(res.data){
      tableData.value=res.data.records
      total.value=res.data.total
    }
  })
}
load()

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
      dialogFormVisible.value=false
      load()
    }else{
      ElMessage.error("保存失败")
    }
  })
}

//添加
const handleAdd=(row)=>{
  form.value={}
  htmlContent.value='';
  imgList.value=[];

  dialogFormVisible.value=true
}

//编辑
const handleEdit=(row)=>{
  form.value=JSON.parse(JSON.stringify(row))
  if(form.value.imgList){
    imgList.value=form.value.imgList.split(',');

  }else{
    imgList.value=[];
  }

  htmlContent.value=form.value.content||'';
  if(form.value.place){
    form.value.place=form.value.place.split('/');
  }else{
    form.value.place=[];
  }
  dialogFormVisible.value=true
}

//删除
const del=(id)=>{
  request.delete("/goods/"+id).then(res=>{
    if(res.code==='200'){
      ElMessage.success("删除成功")
      load()
    }else{
      ElMessage.error("删除失败")
    }
  })
}

//批量删除
const delBatch=()=>{
  if(multipleSection.value.length===0){
    ElMessage.warning("请至少选择一条记录")
    return
  }
  const ids=multipleSection.value.map(v=>v.id)
  request.post("/goods/del/batch",ids).then(res=>{
    if(res.code=='200'){
      ElMessage.success("批量删除成功")
      load()
    }else{
      ElMessage.error("批量删除失败")
    }
  })
}

//重置搜索
const reset=()=>{
  searchForm.keyword=""
  load()
}

//表格选择变化
const handleSelectionChange=(val)=>{
  multipleSection.value=val;
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

//确认删除
const confirmDelete=(id)=>{
  ElMessageBox.confirm(
      '确定要删除这条数据吗？',
      '警告',
      {
        confirmButtonText:'确定',
        cancelButtonText:'取消',
        type:'warning',
      }
  )
      .then(()=>{
        del(id)
      })
}

//确认批量删除
const confirmBatchDelete=()=>{
  if(multipleSection.value.length===0){
    ElMessageBox.warning("请至少选择一条记录")
    return
  }
  ElMessageBox.confirm(
      '确定要批量删除这些数据吗？',
      '警告',
      {
        confirmButtonText:'确定',
        cancelButtonText:'取消',
        type:'warning',
      }
  )
      .then(()=>{
        delBatch()
      })
}

//图片上传成功处理
const  handleImgUploadSuccess=(res)=>{
  form.value.img=res
};
//图片上传失败处理
const  handleImgUploadFail=(error)=>{
  ElMessage.error('图片上传失败,请重试')
};


//多图列表
const imgList = ref([])
//多图片上传成功处理
const handleImgListUploadSuccess=(res)=>{
  imgList.value.push(res);
};
//多图片上传失败处理
const handleImgListUploadFail=(error)=>{
  ElMessage.error('图片上传失败,请重试')
};

//删除已上传的图片
const removeImgList=(index)=>{
  imgList.value.splice(index,1);
};

//获取表格中显示的图片列表
const getImageList=(imgString)=>{
  if(!imgString)return [];
  return imgString.split(',');

};

const getTypeName=(typeId)=>{
  const type=types.value.find(item=>Number(item.id)===Number(typeId));
  return type ? type.name : '';
};

const types=ref([])
const loadType=()=>{
  request.get('/type').then(res=>{
    types.value=res.data;
  })
}
loadType()


//定义富文本数据
const contentViewVisible=ref(false)
const currentViewContent=ref('')

const viewContent=(content)=>{
  currentViewContent.value=content||''
  contentViewVisible.value=true
}

const users=ref([])
const loadUser=()=>{
  request.get('/user').then(res=>{
    users.value=res.data;
  })
}
loadUser()

const getUserNickname=(userId)=>{
  const user=users.value.find(item=>Number(item.id)===Number(userId));
  return user ? user.nickname : '';
};

</script>

<template>
  <div class="content-container">

<!--    搜索区域-->
    <div class="header-section">
      <el-input v-model="searchForm.keyword"placeholder="请输入昵称" clear="filter-input":prefix-icon="Search" clearable/>
      <el-button class="ml-10" plain type="primary"@click="load">搜索</el-button>
      <el-button plain type="info" @click="reset">重置</el-button>
    </div>

<!--    操作按钮区域-->
    <div class="toolbar-section">
      <el-button plain type="primary"@click="handleAdd":icon="Plus">新增</el-button>
      <el-button plain type="danger"@click="confirmBatchDelete":icon="Delete">批量删除</el-button>
    </div>

<!--    表格区域-->
    <el-card>
      <el-table :data="tableData" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="60" align="center"/>
        <el-table-column prop="id" label="ID" width="80" align="center"/>
        <el-table-column prop="name" label="商品名称"/>

        <el-table-column label="图片" width="120" align="center">
          <template #default="scope">
            <el-image style="width: 80px; height: 80px" :src="scope.row.img" :preview-src-list="[scope.row.img]" :preview-teleported="true"></el-image>
          </template>
        </el-table-column>

        <el-table-column label="多张图片" width="120" align="center">
          <template #default="scope">
            <el-image
                style="width: 80px; height: 80px" :src="getImageList(scope.row.imgList)[0]" :preview-src-list="getImageList(scope.row.imgList)" :preview-teleported="true">
            </el-image>
          </template>
        </el-table-column>

        <el-table-column label="分类">
        <template #default="scope">
          <span>{{getTypeName(scope.row.typeId)}}</span>
        </template>
        </el-table-column>

        <el-table-column prop="price" label="售价"/>
        <el-table-column prop="rePrice" label="原价"/>

        <el-table-column prop="num" label="浏览量"/>

        <el-table-column prop="status" label="状态"/>

        <el-table-column label="详情" width="80">
          <template #default="scope">
            <el-button type="primary" @click="viewContent(scope.row.content)">查看</el-button>
          </template>
        </el-table-column>

        <el-table-column prop="place" label="所在地"/>、

        <el-table-column label="发货设置">
          <template #default="scope">
            <el-tag type="primary">{{scope.row.shipment}}</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="用户">
          <template #default="scope">
            <span>{{getUserNickname(scope.row.userId)}}</span>
          </template>
        </el-table-column>

        <el-table-column label="成色">
          <template #default="scope">
            <el-tag type="primary">{{scope.row.quality}}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="date" label="发布日期"/>、

        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="scope">
            <el-tooltip content="编辑" placement="top" :effect="'light'">
              <el-button circle type="primary" :icon="Edit" @click="handleEdit(scope.row)"/>
            </el-tooltip>
            <el-tooltip content="删除" placement="top" :effect="'light'">
              <el-button circle type="danger" :icon="Delete" @click="confirmDelete(scope.row.id)"/>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

<!--      分页区域-->
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10,20,50,100]"
          layout="total,sizes,prev,pager,next,jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"/>
      </div>
    </el-card>

<!--    表单对话框-->
    <el-dialog v-model="dialogFormVisible" :title="form.id ? '编辑' : '新增'" width="30%" destroy-on-close center>
      <el-form  :model="form" label-width="100px">


          <el-form-item label="商品名称"required>
            <el-input v-model="form.name" placeholder="请输入"/>
          </el-form-item>

          <el-form-item label="图片上传">
            <div class="upload-container">
              <el-avatar v-if="form.img" :src="form.img" :size="80" />
              <el-upload :action="`${serverHost}/web/upload`" :on-success="handleImgUploadSuccess":on-error="handleImgUploadFail" :show-file-list="false">
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
            <el-upload :action="`${serverHost}/web/upload`" :on-success="handleImgListUploadSuccess":on-error="handleImgListUploadFail" :show-file-list="false" multiple>
              <el-button type="primary":icon="UploadFilled">上传图片</el-button>
            </el-upload>

          </div>
        </el-form-item>
        <el-form-item label="分类"required>
          <el-select v-model="form.typeId" placeholder="Select" style="width: 240px">
            <el-option
                v-for="item in types"
                :key="item.id"
                :label="item.name"
                :value="item.id"
          />
          </el-select >
        </el-form-item>

        <el-form-item label="售价"required>
          <el-input v-model="form.price" type="number" placeholder="请输入"/>
        </el-form-item>
        <el-form-item label="原价"required>
          <el-input v-model="form.rePrice" type="number" placeholder="请输入"/>
        </el-form-item>

<!--        富文本编辑-->
        <el-form-item label="详情">
          <div style="border:1px solid #ccc;z-index:100">
            <Toolbar style="border-bottom:1px solid #ccc" :editor="editorRefContent" :default-config="editorConfig" mode="default"/>
            <Editor style="height:300px; overflow-y: hidden;" v-model="htmlContent" :default-config="editorConfig" mode="default" @onCreated="editorRefContent=$event"/>
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
            <el-radio value="包邮">包邮</el-radio>
            <el-radio value="不包邮">不包邮</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="售价"required>
          <el-input v-model="form.price" type="number" placeholder="请输入"/>
        </el-form-item>

        <el-form-item label="用户" required>
          <el-select v-model="form.userId" placeholder="Select" style="width: 240px">
            <el-option
              v-for="item in users"
              :key="item.id"
              :label="item.nickname"
              :value="item.id"
              />
          </el-select>
        </el-form-item>

        <el-form-item label="浏览量"required>
          <el-input v-model="form.num" type="number" placeholder="请输入"/>
        </el-form-item>

        <el-form-item label="状态"required>
          <el-input v-model="form.status" placeholder="请输入"/>
        </el-form-item>

        <el-form-item label="成色">
          <el-radio-group v-model="form.quality">
            <el-radio value="全新">全新</el-radio>
            <el-radio value="九成新">九成新</el-radio>
            <el-radio value="八成新">八成新</el-radio>
            <el-radio value="七成新">七成新</el-radio>
            <el-radio value="六成新及以下">六成新及以下</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="日期">
          <el-date-picker v-model="form.date" type="date" value-format="YYYY-MM-DD" placeholder="选择日期"></el-date-picker>
        </el-form-item>

      </el-form>


      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible=false">取消</el-button>
          <el-button type="primary" @click="save">确定</el-button>
        </div>
      </template>
    </el-dialog>
<!-- 表单中的展示-->
    <el-dialog v-model="contentViewVisible" title="详情" width="40%" center>
      <div v-html="currentViewContent"></div>
    </el-dialog>


  </div>
</template>

<style scoped>

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
