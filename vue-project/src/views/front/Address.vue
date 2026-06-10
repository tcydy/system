<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request.js'
import { regionData } from 'element-china-area-data'
import { Location } from '@element-plus/icons-vue'

// 数据
const keyword = ref('')
const tableData = ref([])

// 弹窗
const dialogVisible = ref(false)
const dialogTitle = ref('新增地址')

// 表单
const form = reactive({
  id: null,
  name: '',
  phone: '',
  address: '',
  info: ''
})

// 省市区
const selectedArea = ref([])

// 加载地址列表
const load = () => {
  request.get("/address/page", {
    params: {
      pageNum: 1,
      pageSize: 999,
      keyword: keyword.value,
    }
  }).then(res => {
    if (res.code === '200' && res.data) {
      tableData.value = res.data.records || []
    }
  }).catch(error => {
    console.error('加载地址列表失败:', error)
    ElMessage.error('加载数据失败')
  })
}

// 省市区选择变化
 const handleAreaChange = (value) => {
   console.log('选中的值:', value)  // 调试用
   if (value && value.length > 0) {
     // 用斜杠连接
     form.address = value.join('/')
     console.log('拼接后:', form.address)  // 调试用
   } else {
     form.address = ''
   }
 }

// 设置省市区回显（支持斜杠分隔的地址）
const setSelectedArea = (addressStr) => {
  if (!addressStr) {
    selectedArea.value = []
    return
  }

  // 如果地址包含斜杠，先按斜杠分割
  let parts = addressStr.includes('/') ? addressStr.split('/') : []

  // 如果没有斜杠，尝试按原逻辑匹配
  if (parts.length === 0) {
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
  } else {
    // 直接用分割后的部分匹配
    selectedArea.value = parts
  }
}

// 重置表单
const resetForm = () => {
  form.id = null
  form.name = ''
  form.phone = ''
  form.address = ''
  form.info = ''
  selectedArea.value = []
}

// 新增
const openAdd = () => {
  dialogTitle.value = '新增地址'
  resetForm()
  dialogVisible.value = true
}

// 编辑
const openEdit = (row) => {
  dialogTitle.value = '编辑地址'
  form.id = row.id
  form.name = row.name
  form.phone = row.phone
  form.address = row.address || ''
  form.info = row.info
  setSelectedArea(form.address)
  dialogVisible.value = true
}

// 保存
const saveForm = async () => {
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
      load()
    } else {
      ElMessage.error(res.msg || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  }
}

// 删除
const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除该地址？', '提示', { type: 'warning' })
  try {
    await request.delete(`/address/${id}`)
    ElMessage.success('删除成功')
    load()
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  load()
})
</script>

<template>
  <div style="width: 60%; margin: 0 auto; min-height: 100vh; padding: 20px; background-color: #efefef">
    <el-card style="border-radius: 20px">
      <div style="display: flex; justify-content: space-between; align-items: center;">
        <div>
          <h1 style="margin: 0;">我的收货地址</h1>
        </div>
        <div>
          <el-button type="primary" size="large" @click="openAdd">新增地址</el-button>
        </div>
      </div>
    </el-card>

    <!-- 地址列表 - 卡片形式 -->
    <div style="margin-top: 20px; display: flex; flex-direction: column; gap: 20px">

      <el-card v-for="item in tableData" :key="item.id" style="border-radius: 20px">

        <div style="display: flex; justify-content: space-between; align-items: center;">
          <div style="display: flex; flex-direction: column; gap: 8px; flex: 1;">
            <div style="display: flex; gap: 20px; align-items: baseline;">
              <span style="font-weight: bold; font-size: 16px;">{{ item.name }}</span>
              <span style="color: #666; font-size: 14px;">{{ item.phone }}</span>
            </div>

            <div style="color: #333; font-size: 14px; display: flex; align-items: center;">
              <el-icon style="margin-right: 4px; color: orangered;"><Location /></el-icon>
              <span>{{ item.address }}</span>
              <span style="margin: 0 5px;"></span>
              <span>{{ item.info }}</span>
            </div>
          </div>
          <div style="display: flex; gap: 10px; margin-left: 20px;">
            <el-button type="primary" plain size="small" @click="openEdit(item)">编辑</el-button>
            <el-button type="danger" plain size="small" @click="handleDelete(item.id)">删除</el-button>
          </div>
        </div>
      </el-card>

      <!-- 空状态 -->
      <el-card v-if="tableData.length === 0" style="border-radius: 20px; text-align: center; padding: 40px;">
        <div style="color: #999;">
          <div style="font-size: 48px; margin-bottom: 16px;">📭</div>
          <div>暂无收货地址</div>
          <div style="font-size: 12px; margin-top: 8px;">点击「新增地址」添加你的收货地址</div>
        </div>
      </el-card>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" @close="resetForm">
      <el-form :model="form" label-width="80px">
        <el-form-item label="收货人" required>
          <el-input v-model="form.name" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" required>
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="省市区" required>
          <el-cascader
            v-model="selectedArea"
            :options="regionData"
            :props="{ value: 'label' }"
            placeholder="请选择省/市/区"
            clearable
            style="width:100%"
            @change="handleAreaChange"
          />
        </el-form-item>
        <el-form-item label="详细地址" required>
          <el-input v-model="form.info" placeholder="请输入街道、小区、门牌号等" />
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

</style>