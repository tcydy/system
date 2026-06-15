<script setup>
import { ref, onMounted, shallowRef, reactive, computed, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from "element-plus";
import request from '@/utils/request.js';
import { regionData } from "element-china-area-data";
import { Delete, Edit, UploadFilled } from "@element-plus/icons-vue";
import { Editor,Toolbar } from "@wangeditor/editor-for-vue";
// 关键：导入富文本基础样式，解决高度计算错乱
import '@wangeditor/editor/dist/css/style.css'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const id = ref(route.query.id || '')
const user = ref({})
const tab = ref('宝贝')
const dialogFormVisible = ref(false)

// 富文本
const htmlContent = ref('')
const editorRefContent = shallowRef();

// 统一接口域名（根据你项目实际地址修改）
const serverHost = import.meta.env.VITE_APP_BASE_API || ''

// 富文本上传
const customUpload = (file, insertFn) => {
  const formData = new FormData()
  formData.append('file', file)
  axios({
    url: `${serverHost}/web/upload`,
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  }).then(res => {
    insertFn(res.data)
  }).catch(() => {
    ElMessage.error('上传失败')
  })
}

const editorConfig = {
  placeholder: '请输入内容...',
  MENU_CONF: {
    uploadImage: {
      customUpload: (file, insertFn) => {
        customUpload(file, insertFn)
      },
    },
    uploadVideo: {
      customUpload: (file, insertFn) => {
        customUpload(file, insertFn)
      },
    },
  }
}

// 数据列表
const goods = ref([])
const orders = ref([])
const users = ref([])
const types = ref([])
// 当前登录用户信息
const person = reactive({})

// 加载用户信息
const loadUser = async () => {
  const res = await request.get('/user/' + id.value)
  user.value = res.data
}

// 加载商品
const loadGoods = async () => {
  const res = await request.get('/goods/user/' + id.value)
  goods.value = res.data
}

// 加载订单/评价
const loadOrders = async () => {
  const res = await request.get('/orders/user/' + id.value)
  orders.value = res.data
}

// 所有用户
const loadUsers = async () => {
  const res = await request.get('/user')
  users.value = res.data
}

// 商品分类
const loadType = async () => {
  const res = await request.get('/type')
  types.value = res.data
}

// 当前登录人信息
const getAccount = async () => {
  const res = await request.get('/web/userInfo')
  if (res.code === '200' && res.data) {
    Object.assign(person, res.data)
  } else {
    ElMessage.error(res.msg || '获取用户信息失败')
  }
}

// 切换查看其他用户
const gotoUser = async (userId) => {
  id.value = userId
  // 重新加载所有数据
  await Promise.all([loadUser(), loadGoods(), loadOrders()])
  // 路由只在地址不同时跳转
  if (route.query.id != userId) {
    router.push({ path: '/front/user', query: { id: userId } })
  }
}

// 编辑弹窗相关
const form = ref({})
// 单图、多图
const imgList = ref([])

// 编辑（增加nextTick刷新编辑器，解决弹窗渲染高度计算异常）
const handleEdit = async (item) => {
    form.value = item;

    // 多图列表处理
    if (form.value.imgList) {
        imgList.value = typeof form.value.imgList === 'string' 
        ? form.value.imgList.split(',') 
        : [];
    } else {
        imgList.value = [];
    }

    htmlContent.value = form.value.content || '';

    // 重点修复：place 字段的处理
    if (form.value.place) {
        if (typeof form.value.place === 'string') {
        form.value.place = form.value.place.split('/');
        } else if (Array.isArray(form.value.place)) {
        form.value.place = form.value.place;
        } else {
        form.value.place = [];
        }
    } else {
        form.value.place = [];
    }

    dialogFormVisible.value = true;
    // 等待弹窗DOM渲染完成，刷新编辑器尺寸
    await nextTick()
    if (editorRefContent.value) {
      editorRefContent.value.refresh()
    }
};

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
      loadGoods()
    }else{
      ElMessage.error("保存失败")
    }
  })
}

// 删除商品
const confirmDelete =async (goodsId,item) => {
    const res = await request.get('/orders/usergoods/' + goodsId)
    if (res.data[0]) {
        const orderStatus = res.data[0].status
        if (orderStatus !== "交易完成") {
            console.log(orderStatus)
            ElMessage.warning('交易进行中，不可删除！')
            return;
        }
    }
    ElMessageBox.confirm(
        '确定要删除这条数据吗？',
        '警告',
        {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        }
    ).then(() => {
        del(goodsId)
    }).catch(() => { })
}

const del = async (goodsId) => {
  const res = await request.delete("/goods/" + goodsId)
  if (res.code === '200') {
    ElMessage.success("删除成功")
    loadGoods()
  } else {
    ElMessage.error("删除失败")
  }
}

// 单图上传
const handleImgUploadSuccess = (res) => {
  form.value.img = res;
};
const handleImgUploadFail = () => {
  ElMessage.error('图片上传失败,请重试')
};

// 多图上传
const handleImgListUploadSuccess = (res) => {
  imgList.value.push(res);
};
const handleImgListUploadFail = () => {
  ElMessage.error('图片上传失败,请重试')
};
const removeImgList = (index) => {
  imgList.value.splice(index, 1);
};

// 当前是不是本人
const isSelf = computed(() => {
  return person.id && user.value.id && person.id == user.value.id
})

// 页面初始化
onMounted(async () => {
  await Promise.all([
    loadUser(),
    loadGoods(),
    loadOrders(),
    loadUsers(),
    loadType(),
    getAccount()
  ])
})
</script>

<template>
  <div style="width:80%;min-height: 100vh;margin: 0 auto;background-color: #f1f1f1;padding: 20px;">
    <el-card style="border-radius:20px;">
      <div style="display: flex;justify-content: space-between;">
        <div style="display: flex;align-items: center;gap:10px;">
          <el-avatar :src="user.avatarUrl" :size="100"></el-avatar>
          <h2>{{ user.nickname }}</h2>
        </div>
        <div style="display: flex;align-items:center;">
          <el-button v-if="!isSelf" type="primary" size="large" @click="router.push('/front/chat?id='+user.id)">去私聊</el-button>
          <el-button v-if="isSelf" type="primary" size="large" @click="router.push('/front/person')">编辑资料</el-button>
        </div>
      </div>
    </el-card>

    <el-card style="margin-top: 20px;border-radius: 10px;">
      <div style="display: flex;gap:20px;">
        <div @click="tab='宝贝'" style="cursor: pointer;">
          <span :class="{'option':tab==='宝贝'}">宝贝</span>
          <span style="margin-left: 5px;">{{ goods.length }}</span>
        </div>
        <div @click="tab='历史评价'" style="cursor: pointer;">
          <span :class="{'option':tab==='历史评价'}">历史评价</span>
          <span style="margin-left: 5px;">{{ orders.length }}</span>
        </div>
      </div>
    </el-card>

    <!-- 宝贝列表 -->
    <div v-if="tab==='宝贝'" style="margin-top: 10px;display: grid;grid-template-columns: repeat(5,1fr);gap:10px;">
      <div style="border-radius: 10px;background-color: white;cursor: pointer;" v-for="item in goods" :key="item.id">
        <div @click="router.push('/front/goodsDetail?id=' + item.id)">
          <div class="img-wrap">
            <img class="main-img" :src="item.img" alt="">
            <div class="color-mask" v-if="item.status === '已售出'"></div>
            <img class="mask-img" src="/soldOut.png" v-if="item.status === '已售出'" alt="">
          </div>
          <div style="width: 100%;height: 100px;padding: 10px;">
            <div style="display: flex;gap:5px;">
              <span class="option">{{ item.shipment }}</span>
              <span class="title">{{ item.name }}</span>
            </div>
            <div style="display:flex;gap:10px;align-items: center;">
              <span style="font-size: 16px;font-weight: bolder;color: red;">¥{{ item.price }}</span>
              <span style="font-size: 12px;color: grey;text-decoration: line-through;">¥{{ item.rePrice }}</span>
            </div>
            <div style="display: flex;gap:5px;">
              <el-avatar :src="user.avatarUrl" :size="20"></el-avatar>
              <span>{{ user.nickname }}</span>
            </div>
          </div>
        </div>

        <div>
          <div v-if="isSelf" style="padding: 8px; text-align: right;">
            <el-tooltip v-if="item.status==='上架'" content="编辑" placement="top" effect="light">
              <el-button circle type="primary" :icon="Edit" @click="handleEdit(item)" />
            </el-tooltip>
            <el-tooltip content="删除" placement="top" effect="light">
              <el-button circle type="danger" :icon="Delete" @click="confirmDelete(item.id,item.status)" />
            </el-tooltip>
          </div>
        </div>
      </div>
    </div>

    <!-- 历史评价 -->
    <div v-if="tab==='历史评价'" style="display: flex;flex-direction: column;gap:20px;width:100%;margin-top: 20px;">
      <el-card style="border-radius: 10px;width: 100%;" v-for="order in orders" :key="order.id">
        <div style="border: 1px solid #ececec;min-height: 80px;width: 100%;padding: 20px;">
          <div style="display: flex; flex-direction: column; gap: 20px;">
            <div style="display: flex; justify-content: space-between; align-items: center; gap: 16px;">
              <div 
                style="display: flex; align-items: center; gap: 12px;cursor: pointer;" 
                @click="gotoUser(order.toId)"
              >
                <el-avatar
                  :src="(users.find(u=>u.id === order.toId) || {}).avatarUrl"
                  :size="40"
                />
                <div style="display: flex; flex-direction: column; gap: 4px;">
                  <span style="font-size: 14px; font-weight: 500;">
                    {{ (users.find(u=>u.id === order.toId) || {}).nickname || '未知用户' }}
                  </span>
                  <span style="font-size: 12px; color: #909399;">
                    {{ order.time }}
                  </span>
                </div>
              </div>
              <el-rate
                v-model="order.toRate"
                disabled
                :size="16"
                text-color="#ff9900"
              />
            </div>
            <div style="padding: 0 4px;">
              <p style="margin: 0; font-size: 14px; line-height: 1.6; color: #303133;">
                {{ order.toReview }}
              </p>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>

  <!-- 编辑弹窗 -->
  <el-dialog
    v-model="dialogFormVisible"
    title="编辑"
    width="70%"
    destroy-on-close
    center
    class="goods-edit-dialog"
  >
    <el-form :model="form" label-width="100px" class="goods-form">
      <el-form-item label="商品名称" required>
        <el-input v-model="form.name" placeholder="请输入" />
      </el-form-item>

      <el-form-item label="图片上传">
        <div class="upload-container">
          <el-avatar v-if="form.img" :src="form.img" :size="80" />
          <el-upload
            :action="`${serverHost}/web/upload`"
            :on-success="handleImgUploadSuccess"
            :on-error="handleImgUploadFail"
            :show-file-list="false"
          >
            <el-button type="primary" :icon="UploadFilled">
              {{ form.img ? '更换图片' : '上传图片' }}
            </el-button>
          </el-upload>
        </div>
      </el-form-item>

      <!-- 多图组件上传 -->
      <el-form-item label="多张图片">
        <div class="upload-container">
          <div class="image-list" v-if="imgList.length > 0">
            <div
              v-for="(img, index) in imgList"
              :key="index"
              class="image-item"
            >
              <el-avatar :src="img" :size="80" />
              <el-button
                type="danger"
                circle
                :icon="Delete"
                class="delete-btn"
                @click="removeImgList(index)"
              />
            </div>
          </div>
          <el-upload
            :action="`${serverHost}/web/upload`"
            :on-success="handleImgListUploadSuccess"
            :on-error="handleImgListUploadFail"
            :show-file-list="false"
            multiple
          >
            <el-button type="primary" :icon="UploadFilled">上传图片</el-button>
          </el-upload>
        </div>
      </el-form-item>

      <el-form-item label="分类" required>
        <el-select v-model="form.typeId" placeholder="请选择分类" style="width: 240px">
          <el-option
            v-for="item in types"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="售价" required>
        <el-input v-model="form.price" type="number" placeholder="请输入" />
      </el-form-item>

      <el-form-item label="原价" required>
        <el-input v-model="form.rePrice" type="number" placeholder="请输入" />
      </el-form-item>

      <!-- 富文本编辑：外层100%宽度容器 -->
      <el-form-item label="详情">
        <div class="editor-full-box">
          <div class="editor-wrap">
            <Toolbar
              :editor="editorRefContent"
              :default-config="editorConfig"
              mode="default"
            />
            <Editor
              v-model="htmlContent"
              :default-config="editorConfig"
              mode="default"
              @onCreated="editorRefContent=$event"
            />
          </div>
        </div>
      </el-form-item>

      <el-form-item label="所在城市">
        <el-cascader
          v-model="form.place"
          :options="regionData"
          :props="{value:'label'}"
          placeholder="请选择省市区"
          clearable
          style="width:100%"
        />
      </el-form-item>

      <el-form-item label="发货设置">
        <el-radio-group v-model="form.shipment">
          <el-radio value="包邮">包邮</el-radio>
          <el-radio value="不包邮">不包邮</el-radio>
        </el-radio-group>
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
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="save">确定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style scoped>
.option {
  font-size: 18px;
  font-weight: bold;
  text-decoration: underline;
  text-decoration-color: #ffe610;
  text-decoration-thickness: 8px;
  text-underline-offset: -4px;
  min-width: 50px;
}

.title {
  font-size: 14px;
  line-height: 2;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
}

.img-wrap {
  width: 100%;
  height: 300px;
  position: relative;
}

.main-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.mask-img {
  position: absolute;
  top: 80px;
  left: 50px;
  width: 120px;
  height: auto;
}

.color-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  z-index: 1;
  pointer-events: none;
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

/* 弹窗表单通用样式 */
:deep(.goods-edit-dialog .el-dialog__body) {
  padding: 24px 30px;
}
:deep(.goods-form .el-form-item) {
  margin-bottom: 22px;
}
:deep(.goods-form .el-form-item__label) {
  font-size: 14px;
  color: #303133;
}
:deep(.goods-form .el-input__inner,
.goods-form .el-select .el-input__inner,
.goods-form .el-cascader__inner,
.goods-form .el-date-editor .el-input__inner) {
  height: 40px;
  font-size: 14px;
}
:deep(.goods-form .el-radio-group) {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

/* 富文本核心修复样式：消除高度警告、铺满宽度、横向工具栏 */
.editor-full-box {
  width: 100%;
}
.editor-wrap {
  width: 100%;
  border: 1px solid #dcdcdc;
  border-radius: 6px;
  overflow: hidden;
}
/* 工具栏强制横向排列，禁止竖排 */
:deep(.editor-wrap .w-e-toolbar) {
  width: 100% !important;
  display: flex !important;
  flex-wrap: wrap !important;
  flex-direction: row !important;
}
/* 编辑滚动容器最小高度320px，消除 <300px 警告 */
:deep(.editor-wrap .w-e-scroll) {
  min-height: 320px !important;
  max-height: 500px;
  width: 100% !important;
  overflow-y: auto;
}
:deep(.editor-wrap .w-e-text) {
  width: 100% !important;
  min-height: 320px;
}

.upload-container {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}
.dialog-footer {
  text-align: right;
}
</style>