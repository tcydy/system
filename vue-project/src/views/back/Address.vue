<script setup>
import { reactive, ref, onMounted } from "vue";
import request from "@/utils/request.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { Delete, Edit, Plus, Search } from "@element-plus/icons-vue";
import { regionData } from "element-china-area-data";

// ===================== 基础表格&分页变量（和商品页架构一致） =====================
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

// 搜索条件（统一用 searchForm 对象）
const searchForm = reactive({
  keyword: ''
})

// 表单 & 弹窗
const form = ref({})
const dialogFormVisible = ref(false)

// 表格多选数据（统一命名 multipleSection）
const multipleSection = ref([])

// 省市区级联选中值
const selectedArea = ref([])

// 用户列表（用于下拉 & 昵称展示）
const users = ref([])

// ===================== 工具方法 =====================
// 根据地址字符串回填省市区级联
const setSelectedArea = (addressStr) => {
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

// 省市区选择变更
const handleAreaChange = (value) => {
  if (value && value.length) {
    const uniqueValue = [...new Set(value)]
    form.value.address = uniqueValue.join('')
  } else {
    form.value.address = ''
  }
}

// 根据用户ID获取昵称
const getUserNickname = (userId) => {
  const user = users.value.find(item => Number(item.id) === Number(userId));
  return user ? (user.nickname || user.username) : '未知用户';
};

// ===================== 数据加载 =====================
// 主列表加载（统一命名 load）
const load = () => {
  request.get("/address/page", {
    params: {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: searchForm.keyword,
    }
  }).then(res => {
    if (res.data) {
      tableData.value = res.data.records
      total.value = res.data.total
    }
  }).catch(() => {
    ElMessage.error('加载数据失败')
  })
}

// 加载用户列表
const loadUser = () => {
  request.get('/user').then(res => {
    users.value = Array.isArray(res.data) ? res.data : (res.data.records || [])
  })
}

// ===================== 增删改查逻辑（完全对标商品页函数名） =====================
// 新增
const handleAdd = () => {
  form.value = {}
  selectedArea.value = []
  dialogFormVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  form.value = JSON.parse(JSON.stringify(row))
  setSelectedArea(form.value.address || '')
  dialogFormVisible.value = true
}

// 保存
const save = () => {
  // 表单校验
  if (!form.value.userId) {
    ElMessage.warning('请选择用户')
    return
  }
  if (!form.value.name) {
    ElMessage.warning('请输入收货人')
    return
  }
  if (!form.value.phone) {
    ElMessage.warning('请输入联系电话')
    return
  }
  if (!form.value.address) {
    ElMessage.warning('请选择省市区')
    return
  }
  if (!form.value.info) {
    ElMessage.warning('请输入详细地址')
    return
  }

  request.post("/address", form.value).then(res => {
    if (res.code === '200') {
      ElMessage.success("保存成功")
      dialogFormVisible.value = false
      load()
    } else {
      ElMessage.error(res.msg || "保存失败")
    }
  }).catch(() => {
    ElMessage.error("保存失败")
  })
}

// 单条删除接口
const del = (id) => {
  request.delete("/address/" + id).then(res => {
    if (res.code === '200') {
      ElMessage.success("删除成功")
      load()
    } else {
      ElMessage.error("删除失败")
    }
  })
}

// 批量删除接口
const delBatch = () => {
  if (multipleSection.value.length === 0) {
    ElMessage.warning("请至少选择一条记录")
    return
  }
  const ids = multipleSection.value.map(v => v.id)
  request.post("/address/del/batch", ids).then(res => {
    if (res.code === '200') {
      ElMessage.success("批量删除成功")
      load()
    } else {
      ElMessage.error("批量删除失败")
    }
  })
}

// 重置搜索
const reset = () => {
  searchForm.keyword = ""
  load()
}

// 表格选中行变化
const handleSelectionChange = (val) => {
  multipleSection.value = val;
}

// 分页每页条数改变
const handleSizeChange = (size) => {
  pageSize.value = size
  load()
}

// 分页页码改变
const handleCurrentChange = (current) => {
  pageNum.value = current
  load()
}

// 单条删除确认弹窗
const confirmDelete = (id) => {
  ElMessageBox.confirm(
    '确定要删除这条数据吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    del(id)
  })
}

// 批量删除确认弹窗
const confirmBatchDelete = () => {
  if (multipleSection.value.length === 0) {
    ElMessage.warning("请至少选择一条记录")
    return
  }
  ElMessageBox.confirm(
    '确定要批量删除这些数据吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    delBatch()
  })
}

// 页面初始化
onMounted(() => {
  load()
  loadUser()
})
</script>

<template>
  <div class="content-container">
    <!-- 搜索区域（和商品页结构一致） -->
    <div class="header-section">
      <el-input
        v-model="searchForm.keyword"
        placeholder="请输入收货人姓名"
        class="filter-input"
        :prefix-icon="Search"
        clearable
        @keyup.enter="load"
      />
      <el-button class="ml-10" plain type="primary" @click="load">搜索</el-button>
      <el-button plain type="info" @click="reset">重置</el-button>
    </div>

    <!-- 操作按钮区域 -->
    <div class="toolbar-section">
      <el-button plain type="primary" @click="handleAdd" :icon="Plus">新增</el-button>
      <el-button plain type="danger" @click="confirmBatchDelete" :icon="Delete">批量删除</el-button>
    </div>

    <!-- 表格区域 -->
    <el-card>
      <el-table :data="tableData" @selection-change="handleSelectionChange" border stripe>
        <el-table-column type="selection" width="60" align="center" />
        <el-table-column prop="id" label="ID" width="80" align="center" />

        <el-table-column label="用户信息" align="center" width="150">
          <template #default="scope">
            <div>用户ID: {{ scope.row.userId }}</div>
            <div style="font-size: 12px; color: #666;">
              {{ getUserNickname(scope.row.userId) }}
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="name" label="收货人" align="center" />
        <el-table-column prop="phone" label="联系电话" align="center" />
        <el-table-column prop="address" label="省市区" align="center" />
        <el-table-column prop="info" label="详细地址" align="center" />

        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="scope">
            <el-tooltip content="编辑" placement="top" :effect="'light'">
              <el-button circle type="primary" :icon="Edit" @click="handleEdit(scope.row)" />
            </el-tooltip>
            <el-tooltip content="删除" placement="top" :effect="'light'">
              <el-button circle type="danger" :icon="Delete" @click="confirmDelete(scope.row.id)" />
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页区域（完全复用商品页分页逻辑&事件） -->
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10,20,50,100]"
          layout="total,sizes,prev,pager,next,jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogFormVisible"
      :title="form.id ? '编辑' : '新增'"
      width="50%"
      destroy-on-close
      center
    >
      <el-form :model="form" label-width="80px">
        <el-form-item label="选择用户" required>
          <el-select
            v-model="form.userId"
            placeholder="请选择用户"
            style="width: 100%"
            filterable
            clearable
          >
            <el-option
              v-for="user in users"
              :key="user.id"
              :label="`${user.nickname || user.username} (${user.username})`"
              :value="user.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="收货人" required>
          <el-input v-model="form.name" placeholder="请输入收货人" />
        </el-form-item>

        <el-form-item label="联系电话" required>
          <el-input v-model="form.phone" placeholder="请输入电话" />
        </el-form-item>

        <el-form-item label="省市区" required>
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

        <el-form-item label="详细地址" required>
          <el-input v-model="form.info" placeholder="请输入详细地址（街道、小区、门牌号等）" />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取消</el-button>
          <el-button type="primary" @click="save">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
/* 沿用商品页样式 */
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