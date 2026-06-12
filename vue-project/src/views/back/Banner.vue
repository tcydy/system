<script setup>

//表格数据
import {reactive, ref} from "vue";
import request from "@/utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";
import {Delete, Edit, Plus, Search, UploadFilled} from "@element-plus/icons-vue";
import {serverHost} from "../../../config/config.default.js";

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
  request.get("/banner/page",{
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
  request.post("/banner",form.value).then(res=>{
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
  dialogFormVisible.value=true
}

//编辑
const handleEdit=(row)=>{
  form.value=JSON.parse(JSON.stringify(row))
  dialogFormVisible.value=true
}

//删除
const del=(id)=>{
  request.delete("/banner/"+id).then(res=>{
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
  request.post("/banner/del/batch",ids).then(res=>{
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
    ElMessage.warning("请至少选择一条记录")
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
}

</script>
<template>
  <div class="content-container">

<!--    搜索区域-->
    <div class="header-section">
      <el-input v-model="searchForm.keyword"placeholder="请输入说明" clear="filter-input":prefix-icon="Search" clearable/>
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
        <el-table-column prop="name" label="说明"/>

        <el-table-column label="图片" width="120" align="center">
          <template #default="scope">
            <el-image style="width: 80px; height: 80px" :src="scope.row.img" :preview-src-list="[scope.row.img]" :preview-teleported="true"></el-image>
          </template>
        </el-table-column>

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


          <el-form-item label="说明"required>
            <el-input v-model="form.name" placeholder="请输入"/>
          </el-form-item>

          <el-form-item label="图片上传">
            <div class="upload-container">
              <el-avatar v-if="form.img" :src="form.img" :size="80" />
              <el-upload :action="`${serverHost}/web/upload`" :on-success="handleImgUploadSuccess" :show-file-list="false">
                <el-button type="primary" :icon="UploadFilled">{{ form.img ? '更换图片' : '上传图片' }}</el-button>
              </el-upload>
            </div>
          </el-form-item>

      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible=false">取消</el-button>
          <el-button type="primary" @click="save">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.content-container {
  padding: 20px;
}

/* 搜索栏布局与间距 */
.header-section {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.header-section .el-input {
  width: 280px;
}

/* 顶部功能按钮栏 */
.toolbar-section {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

/* 分页靠右 */
.pagination-section {
  display: flex;
  justify-content: flex-end;
  margin-top: 15px;
}

/* 上传区域排版 */
.upload-container {
  display: flex;
  align-items: center;
  gap: 15px;
}

/* 弹窗底部按钮居中+间距 */
.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 20px;
}
</style>