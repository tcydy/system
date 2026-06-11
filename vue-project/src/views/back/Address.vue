<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request.js'
import { regionData } from 'element-china-area-data'

// 分页参数
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const keyword = ref('')
const tableData = ref([])
const selectedIds = ref([])

// 用户列表
const userList = ref([])
const userMap = ref({})

// 弹窗与表单
const dialogVisible = ref(false)
const form = reactive({
  id: null,
  userId: null,
  name: '',
  phone: '',
  address: '',
  info: ''
})

// 省市区相关
const selectedArea = ref([])

// 根据用户ID获取昵称
function getUserNickname(row) {
  const userId = row.userId
  if (!userId) return '未知用户'
  const user = userMap.value[userId]
  if (user) {
    return user.nickname || user.username || `用户${userId}`
  }
  return `用户${userId}`
}

// 加载用户列表
async function loadUserList() {
  try {
    const res = await request.get('/user')
    let users = []
    if (res.data && res.data.data) {
      users = res.data.data
    } else if (Array.isArray(res.data)) {
      users = res.data
    } else if (res.data.records) {
      users = res.data.records
    }

    userList.value = users
    // 构建 id -> user 的映射表
    users.forEach(user => {
      userMap.value[user.id] = user
    })

    console.log('用户列表加载成功:', userList.value.length, '个用户')
  } catch (error) {
    console.error('加载用户列表失败', error)
  }
}

// 省市区选择变化
function handleAreaChange(value) {
  if (value && value.length) {
    const uniqueValue = [...new Set(value)]
    form.address = uniqueValue.join('')
  } else {
    form.address = ''
  }
}

// 根据地址字符串设置级联选择器的值
function setSelectedArea(addressStr) {
  if (!addressStr) {
    selectedArea.value = []
    return
  }

  for (const province of regionData) {
    if (addressStr.includes(province.label)) {
      for (const city of province.children) {
        if (addressStr.includes(city.label)) {
          for (const district of city.children) {
            if (addressStr.includes(district.label)) {
              selectedArea.value = [province.label, city.label, district.label]
              return
            }
          }
          selectedArea.value = [province.label, city.label]
          return
        }
      }
      selectedArea.value = [province.label]
      return
    }
  }

  selectedArea.value = []
}

// 列表查询
async function getList() {
  try {
    const res = await request.get('/address/page', {
      params: {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        keyword: keyword.value
      }
    })

    if (res.code === '200') {
      const pageData = res.data
      if (pageData && pageData.records) {
        tableData.value = pageData.records
        total.value = pageData.total
        console.log('成功加载地址数据:', tableData.value.length, '条')
      } else {
        tableData.value = []
        total.value = 0
      }
    }
  } catch (error) {
    console.error('请求失败:', error)
    ElMessage.error('加载数据失败')
  }
}

const reset = () => {
  keyword.value = ''
  getList()
}

// 新增
function openAdd() {
  form.id = null
  form.userId = null
  form.name = ''
  form.phone = ''
  form.address = ''
  form.info = ''
  selectedArea.value = []
  dialogVisible.value = true
}

// 编辑
function openEdit(row) {
  form.id = row.id
  form.userId = row.userId
  form.name = row.name
  form.phone = row.phone
  form.address = row.address || ''
  form.info = row.info

  setSelectedArea(form.address)
  dialogVisible.value = true
}

// 保存
async function saveForm() {
  if (!form.userId) {
    ElMessage.warning('请登录')
    return
  }
  if (!form.name) {
    ElMessage.warning('请输入收货人')
    return
  }
  if (!form.phone) {
    ElMessage.warning('请输入联系电话')
    return
  }
  if (!form.address) {
    ElMessage.warning('请选择省市区')
    return
  }
  if (!form.info) {
    ElMessage.warning('请输入详细地址')
    return
  }

  try {
    const res = await request.post('/address', form)
    if (res.code === '200') {
      ElMessage.success('保存成功')
      dialogVisible.value = false
      getList()
    } else {
      ElMessage.error(res.msg || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  }
}

// 单选删除
async function handleDelete(id) {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await request.delete(`/address/${id}`)
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
  await request.post('/address/del/batch', selectedIds.value)
  ElMessage.success('批量删除成功')
  getList()
}

onMounted(() => {
  loadUserList()
  getList()
})
</script>

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
      <el-button plain type="info" @click="reset">重置</el-button>
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

      <el-table-column label="用户信息" align="center" width="150">
        <template #default="scope">
          <div>用户ID: {{ scope.row.userId }}</div>
          <div style="font-size: 12px; color: #666;">
            {{ getUserNickname(scope.row) }}
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="name" label="收货人" align="center" />
      <el-table-column prop="phone" label="联系电话" align="center" />
      <el-table-column prop="address" label="省市区" align="center" />
      <el-table-column prop="info" label="详细地址" align="center" />


      
      <el-table-column label="操作" align="center" width="180">
        <template #default="scope">
          <el-button link type="primary" class="table-btn" @click="openEdit(scope.row)" >编辑</el-button>
          <el-button link type="danger" class="table-btn" @click="handleDelete(scope.row.id)">删除</el-button>
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
        <el-form-item label="选择用户" prop="userId" required>
          <el-select
            v-model="form.userId"
            placeholder="请选择用户"
            style="width: 100%"
            filterable
            clearable
          >
            <el-option
              v-for="user in userList"
              :key="user.id"
              :label="`${user.nickname || user.username} (${user.username})`"
              :value="user.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="收货人" prop="name" required>
          <el-input v-model="form.name" placeholder="请输入收货人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone" required>
          <el-input v-model="form.phone" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="省市区" prop="address" required>
          <el-cascader
            v-model="selectedArea"
            :options="regionData"
            :props="{ value: 'label' }"
            placeholder="请选择省市区"
            clearable
            style="width:100%"
            @change="handleAreaChange"
          />
        </el-form-item>
        <el-form-item label="详细地址" prop="info" required>
          <el-input v-model="form.info" placeholder="请输入详细地址（街道、小区、门牌号等）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>



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
.table-btn {
  font-size: 16px;
  padding:4px;
  background:#4084d91a;
  border-color:#4084d91a;
}
</style>