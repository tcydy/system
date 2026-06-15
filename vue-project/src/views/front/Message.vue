<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request.js'

const router = useRouter()
// 当前登录用户ID
const userId = ref('')
// 好友列表
const friendList = ref([])
// 加载状态
const loading = ref(false)

// 1. 获取当前登录用户信息
const getAccount = async () => {
  try {
    const res = await request.get('/web/userInfo')
    if (res.code === '200' && res.data) {
      userId.value = res.data.id
      return true
    }
    ElMessage.error('获取用户信息失败')
    return false
  } catch (err) {
    console.error('获取用户信息异常：', err)
    ElMessage.error('网络异常')
    return false
  }
}

// 2. 根据 自己ID + 好友ID 获取双方最后一条消息
const getLastMsg = async (toUserId) => {
  if (!userId.value || !toUserId) return ''
  try {
    const res = await request.get('/chat/messagehistory', {
      params: {
        fromUserId: userId.value,
        toUserId: toUserId
      }
    })
    const list = res.data || []
    // 取最后一条消息的文本内容
    if (list.length > 0) {
      return list[list.length - 1].text
    }
    return ''
  } catch (err) {
    console.error('查询聊天记录失败：', err)
    return ''
  }
}

// 3. 获取好友列表，并批量查询最新消息
const getFriendList = async () => {
  loading.value = true
  try {
    const res = await request.get('/chat/user')
    if (res.code === '200') {
      const list = res.data || []
      friendList.value = list

      // 遍历好友，逐个查询最后一条消息
      for (const item of friendList.value) {
        const lastMsg = await getLastMsg(item.id)
        // 给当前好友项挂载最新消息字段
        item.lastMsg = lastMsg
      }
    } else {
      ElMessage.error(res.msg || '获取好友列表失败')
    }
  } catch (err) {
    console.error('获取好友列表异常：', err)
    ElMessage.error('网络异常，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 跳转到聊天页面
const goToChat = (targetId) => {
  if (!targetId) return
  router.push({
    path: '/front/chat',
    query: { id: targetId }
  })
}

onMounted(async () => {
  // 先拿到自身ID，再加载列表和消息预览
  const hasUser = await getAccount()
  if (hasUser) {
    await getFriendList()
  }
})
</script>

<template>
  <div class="message-container">
    <div class="title">消息列表</div>
    <div class="friend-list">
      <!-- 加载中提示 -->
      <div class="loading-tip" v-if="loading">加载中...</div>

      <div
        class="friend-item"
        v-for="item in friendList"
        :key="item.id"
        @click="goToChat(item.id)"
      >
        <!-- 头像 -->
        <div class="avatar">
          <img :src="item.avatarUrl || ''" alt="头像" />
        </div>
        <!-- 昵称 + 最新消息 -->
        <div class="info">
          <div class="name">{{ item.nickname }}</div>
          <div class="last-msg">
            {{ item.lastMsg || '暂无聊天记录' }}
          </div>
        </div>
        <!-- 未读消息角标 -->
        <div class="unread" v-if="item.count > 0">{{ item.count }}</div>
      </div>

      <!-- 空列表兜底 -->
      <div class="empty-tip" v-if="!loading && friendList.length === 0">
        暂无聊天好友
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 外层容器 */
.message-container {
  width: 600px;
  margin: 30px auto;
  border: 1px solid #e5e6eb;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
  background: #fff;
}

/* 标题栏 */
.title {
  height: 50px;
  line-height: 50px;
  text-align: center;
  font-size: 16px;
  font-weight: 600;
  border-bottom: 1px solid #ebeef5;
  color: #333;
}

/* 列表容器 */
.friend-list {
  background-color: #f8f9fa;
  max-height: 700px;
  overflow-y: auto;
}

/* 列表项 */
.friend-item {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  cursor: pointer;
  position: relative;
  transition: background 0.2s;
  border-bottom: 1px solid #f0f2f5;
}
.friend-item:hover {
  background-color: #e9edf5;
}

/* 头像 */
.avatar img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

/* 昵称 + 最新消息区域 */
.info {
  margin-left: 12px;
  flex: 1;
  overflow: hidden;
}
.name {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
}
/* 最新消息文本，超出自动省略 */
.last-msg {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 未读角标 */
.unread {
  position: absolute;
  right: 15px;
  top: 14px;
  background: #f53f3f;
  color: #fff;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 12px;
  min-width: 18px;
  text-align: center;
}

/* 加载/空数据提示 */
.loading-tip,
.empty-tip {
  text-align: center;
  padding: 40px 0;
  color: #999;
  font-size: 14px;
}
</style>