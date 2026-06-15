<script setup>
import { reactive, ref } from "vue";
import request from "@/utils/request.js";
import { Star, StarFilled } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router';

const router = useRouter()
const searchForm = reactive({ keyword: '' });
const tableData = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = ref(10);

const load = () => {
  request.get("/goods/collect/page", {
    params: {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: searchForm.keyword,
    }
  }).then(res => {
    if (res.data) {
      tableData.value = res.data.records;
      total.value = res.data.total;
    }
  });
};
load();

const handleSizeChange = (size) => {
  pageSize.value = size;
  load();
};
const handleCurrentChange = (current) => {
  pageNum.value = current;
  load();
};

// 取消收藏
const cancelCollect = (id) => {
  request.delete("/collect/" + id).then(res => {
    load();
    ElMessage.success('取消收藏成功')
  });
};

</script>

<template>
  <div style="max-width: 1000px; margin: 0 auto; padding: 20px;">
    <div style="font-size: 24px; font-weight: bold; margin-bottom: 15px;">我的收藏</div>

    <el-input
      v-model="searchForm.keyword"
      placeholder="请输入商品名称"
      clearable
      :prefix-icon="Search"
      style="margin-bottom: 15px;"
    />

    <el-divider></el-divider>

    <el-row :gutter="20">
      <el-col :span="6" v-for="item in tableData" :key="item.id" class="card-fade">
        <el-card style="border-radius: 10px" class="card-hover-up">

          <div class="img-box">
            <div @click="router.push('/front/goodsDetail?id=' + item.id)" style="cursor: pointer;">
              <div style="width: 100%; height: 220px" v-if="item.status!=='已售出'">
                <img :src="item.img" style="width:100%;height:100%;object-fit:fill;">
              </div>
              <div style="width:100%;height:220px;position:relative" v-else>
                <div class="overlay"></div>
                <img src="../../assets/已售出.png" class="overlay-img">
                <img :src="item.img" style="width:100%;height:100%;object-fit:fill;">
              </div>
            </div>

            <div class="action-bar">
              <button class="btn" @click="cancelCollect(item.id)">
                <el-icon><StarFilled /></el-icon>
                取消收藏
              </button>
              <button class="btn" v-if="item.status !== '已售出'" @click="router.push('/front/chat?id='+item.userId)">
                我想要
              </button>
            </div>
          </div>

          <div style="padding:10px">
            <div>{{ item.name }}</div>
            <div style="color:orangered;margin-top:5px">¥{{ item.price }}</div>
          </div>
        </el-card>
      </el-col>

      <div v-if="tableData.length === 0" style="width:100%;text-align:center;padding:40px 0;color:#999">
        暂无收藏商品
      </div>
    </el-row>

    <div style="display:flex;justify-content:center;margin-top:30px">
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
  </div>
</template>

<style scoped>
:deep(.el-card__body) { padding: 0; }
.overlay { position: absolute; top:0;left:0;right:0;bottom:0; background:#fff; opacity:0.5; z-index:1; }
.overlay-img { position: absolute; top:25%;left:25%; width:70%;height:70%; z-index:2; object-fit:contain; }
.img-box { position: relative; overflow: hidden; height: 220px; }

/* 卡片上滑动画 */
.card-fade { animation: fadeUp 0.6s ease; }
@keyframes fadeUp { from { opacity:0; transform: translateY(20px); } to { opacity:1; transform: translateY(0); } }

/* 卡片悬浮上移 */
.card-hover-up { transition: all 0.3s ease; position: relative; }
.card-hover-up:hover { transform: translateY(-6px); box-shadow: 0 8px 15px rgba(0,0,0,0.1); }

/* 按钮条 */
.action-bar {
  position: absolute; left:0;right:0;bottom:0; height:46px;
  background: rgba(0,0,0,0.45);
  display: flex; align-items:center; justify-content:center; gap:16px;
  transform: translateY(100%);
  transition: transform 0.3s ease;
  z-index:10;
}
.card-hover-up:hover .action-bar { transform: translateY(0); }

/* 按钮样式 */
.btn {
  background: transparent; border:1px solid #fff; color:#fff;
  padding:5px 12px; border-radius:3px; cursor:pointer;
  display:inline-flex; align-items:center; gap:4px;
}
</style>