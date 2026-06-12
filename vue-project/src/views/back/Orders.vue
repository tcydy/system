<script setup>

//表格数据
import {reactive, ref,shallowRef,onMounted} from "vue";
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
  request.get("/orders",{
    params:{
      pageNum:pageNum.value,
      pageSize:pageSize.value,
      keyword:searchForm.keyword,
    }
  }).then(res => {
    console.log('res:', res)  // 调试用
    if (res.code !== '200') {
        ElMessage.error(res.message)
        return
    }
    if(res.data){
        tableData.value = res.data
        total.value = res.data.total
        //console.log('加载订单数据:', tableData)  // 调试用
    }
  })
}


//保存
const save=()=>{

  if(imgList.value.length>0){
    form.value.imgList=imgList.value.join(',');

  }else{
    form.value.imgList='';
  }

  form.value.content=htmlContent.value;
  if(Array.isArray(form.value.address)){
    form.value.address=form.value.address.join('/');
  }else{
    form.value.place=form.value.place || '';
  }

  request.post("/orders",form.value).then(res=>{
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

  dialogFormVisible.value=true
}

//编辑
const handleEdit=(row)=>{
  form.value=JSON.parse(JSON.stringify(row))

  htmlContent.value=form.value.content||'';
  if(form.value.address){
    form.value.address=form.value.address.split('/');
  }else{
    form.value.address=[];
  }
  dialogFormVisible.value=true
}

//删除
const del=(id)=>{
  request.delete("/orders/"+id).then(res=>{
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
  request.post("/orders/del/batch",ids).then(res=>{
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

//删除已上传的图片
const removeImgList=(index)=>{
  imgList.value.splice(index,1);
};

//加载商品
const goods=ref([])
const loadGoods=()=>{
  request.get('/goods').then(res=>{
    goods.value=res.data;
  })
}

//加载用户
const users=ref([])
const loadUser=()=>{
  request.get('/user').then(res=>{
    users.value=res.data;
  })
}
const getUserNickname=(userId)=>{
  const user=users.value.find(item=>Number(item.id)===Number(userId));
  return user ? user.nickname : '';
};

//设置评分
const setScore = (score) => { 
    return score ? score : '0';
}

//定义富文本数据
const contentViewVisible=ref(false)
const currentViewContent=ref('')

const viewContent=(content)=>{
  currentViewContent.value=content||''
  contentViewVisible.value=true
}


// 选中回调：val = 当前选中的 商品id
const handleSelect = (val) => {
  //根据id在商品列表中找到对应商品
  const target = goods.value.find( item => item.id === val )
  if (target) {
    form.value.itemId = target.id    // 赋值ID
    form.value.itemName = target.name// 赋值名称
  }
}



onMounted(() => { 
    load()
    loadGoods()
    loadUser()
})

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
        <el-table-column prop="no" label="订单号" width="300" align="center"/>
        <el-table-column prop="itemName" label="商品名称"/>

        <el-table-column label="商品图片" width="120" align="center">
          <template #default="scope">
                <el-image 
                style="width: 80px; height: 80px" 
                :src="scope.row.itemImg" 
                :preview-src-list="[scope.row.itemImg]" 
                :preview-teleported="true">
                </el-image>
          </template>
        </el-table-column>

        <el-table-column label="卖家">
          <template #default="scope">
            <span>{{getUserNickname(scope.row.fromId)}}</span>
          </template>
        </el-table-column>
        <el-table-column label="买家">
          <template #default="scope">
            <span>{{getUserNickname(scope.row.toId)}}</span>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格"/>
        <el-table-column prop="time" label="下单时间"/>
        <el-table-column prop="status" label="状态"/>
        <el-table-column label="买家评分" width="120" align="center">
            <template #default="scope">
                <el-rate 
                    v-model="scope.row.toRate" 
                    disabled 
                    show-score 
                    text-color="#ff9900"
                />
            </template>
        </el-table-column>
        <el-table-column prop="toReview" label="买家评价"/>
        <el-table-column prop="address" label="省市区"/>
        <el-table-column prop="info" label="详细地址"/>
        <el-table-column prop="name" label="收货人姓名"/>
        <el-table-column prop="phone" label="收货人联系方式"/>

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
    <el-dialog v-model="dialogFormVisible" :title="form.id ? '编辑' : '新增'" width="50%" destroy-on-close center>
      <el-form  :model="form" label-width="120px" padding="20px">
        <el-form-item label="订单号"required>
            <el-input v-model="form.no" placeholder="请输入"/>
        </el-form-item>

        <el-form-item label="商品"required>
          <el-select v-model="form.itemName" :placeholder="请选择" style="width: 240px" @change="handleSelect">
            <el-option
                v-for="item in goods"
                :key="item.id"
                :label="item.name"
                :value="item.id"/>
          </el-select >
        </el-form-item>

        <el-form-item label="卖家"required>
          <el-select v-model="form.fromId" :placeholder="请选择" style="width: 240px" @change="handleSelect">
            <el-option
                v-for="item in users"
                :key="item.id"
                :label="item.nickname"
                :value="item.id"/>
          </el-select >
        </el-form-item>

        <el-form-item label="买家"required>
          <el-select v-model="form.toId" :placeholder="请选择" style="width: 240px" @change="handleSelect">
            <el-option
                v-for="item in users"
                :key="item.id"
                :label="item.nickname"
                :value="item.id"/>
          </el-select >
        </el-form-item>

        <el-form-item label="价格"required>
          <el-input v-model="form.price" type="number" placeholder="请输入"/>
        </el-form-item>

        <el-form-item label="支付时间"required>
          <el-date-picker
              v-model="form.time"
              type="datetime"
              placeholder="选择日期时间"
              style="width: 240px"
          />
        </el-form-item>

        <el-form-item label="买家评分">
          <el-rate v-model="form.toRate" show-score text-color="#ff9900"/>
        </el-form-item>

        <el-form-item label="买家评价"required>
          <el-input v-model="form.toReview" type="textarea" placeholder="请输入"/>
        </el-form-item>

        <el-form-item label="省市区"required>
          <el-cascader
            v-model="form.address"
            :options="regionData"
            :props="{value:'label'}"
            placeholder="请选择"
            clearable
            style="width:100%">
          </el-cascader>
        </el-form-item>
        
        <el-form-item label="详细地址"required> 
            <el-input v-model="form.info" type="textarea" placeholder="请输入"/>
        </el-form-item>

        <el-form-item label="收货人姓名"required> 
            <el-input v-model="form.name" type="text" placeholder="请输入"/>
        </el-form-item>

        <el-form-item label="收货人联系方式"required> 
            <el-input v-model="form.phone" type="text" placeholder="请输入"/>
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
/* 搜索栏布局 */
.header-section {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.header-section .el-input {
  width: 280px;
}
</style>
