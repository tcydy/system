<template>
  <div class="container">
    <!-- 搜索栏 -->
    <div class="search-box">
      <el-input
        v-model="keyword"
        placeholder="搜索收货人姓名"
        clearable
        style="width: 240px"
        @keyup.enter="getList"
      />
      <el-button type="primary" @click="getList">搜索</el-button>
      <el-button @click="openAdd">新增地址</el-button>
      <el-button type="danger" @click="batchDelete" :disabled="!selectedIds.length">
        批量删除
      </el-button>
    </div>

    <!-- 表格 -->
    <el-table
      :data="tableData"
      border
      stripe
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="id" label="ID" width="70" align="center" />
      <el-table-column prop="name" label="收货人" align="center" />
      <el-table-column prop="phone" label="联系电话" align="center" />
      <el-table-column prop="address" label="省市区" align="center" />
      <el-table-column prop="info" label="详细地址" align="center" />
      <el-table-column label="操作" align="center" width="180">
        <template #default="scope">
          <el-button link type="primary" @click="openEdit(scope.row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div style="margin-top:10px;text-align:right">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :page-sizes="[5,10,20,50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="getList"
        @current-change="getList"
      />
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" title="地址编辑" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="收货人" prop="name" required>
          <el-input v-model="form.name" placeholder="请输入收货人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone" required>
          <el-input v-model="form.phone" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="省市区" prop="address" required>
          <el-input v-model="form.address" placeholder="如：广东省广州市天河区" />
        </el-form-item>
        <el-form-item label="详细地址" prop="info" required>
          <el-input v-model="form.info" placeholder="请输入详细地址" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

// 分页参数
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const keyword = ref('')
const tableData = ref([])
const selectedIds = ref([])

// 弹窗与表单
const dialogVisible = ref(false)
const form = reactive({
  id: null,
  name: '',
  phone: '',
  address: '',
  info: ''
})

// 列表查询
async function getList() {
  const res = await axios.get('/address/page', {
    params: {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value
    }
  })
  tableData.value = res.data.data.records
  total.value = res.data.data.total
}

// 新增
function openAdd() {
  form.id = null
  form.name = ''
  form.phone = ''
  form.address = ''
  form.info = ''
  dialogVisible.value = true
}

// 编辑
function openEdit(row) {
  form.id = row.id
  form.name = row.name
  form.phone = row.phone
  form.address = row.address
  form.info = row.info
  dialogVisible.value = true
}

// 保存
async function saveForm() {
  await axios.post('/address', form)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  getList()
}

// 单选删除
async function handleDelete(id) {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await axios.delete(`/address/${id}`)
  ElMessage.success('删除成功')
  getList()
}

// 多选
function handleSelectionChange(val) {
  selectedIds.value = val.map(item => item.id)
}

// 批量删除
async function batchDelete() {
  await ElMessageBox.confirm('确定批量删除？', '提示', { type: 'warning' })
  await axios.post('/address/del/batch', selectedIds.value)
  ElMessage.success('批量删除成功')
  getList()
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.container {
  padding: 20px;
}
.search-box {
  margin-bottom: 15px;
}
.search-box .el-input {
  margin-right: 10px;
}
</style>