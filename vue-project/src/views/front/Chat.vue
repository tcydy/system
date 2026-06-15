<script setup>
import { ref, onBeforeUnmount, onMounted, nextTick, reactive } from "vue";
import { ElMessage } from "element-plus";
import { useRoute } from "vue-router";
import request from "@/utils/request.js";

const route = useRoute();

//当前登录的用户
const account=ref(
    localStorage.getItem('account')?JSON.parse(localStorage.getItem('account')):{}
)
const my=reactive({})
//获取用户信息
const getAccount=()=>{
  request.get('/web/userInfo').then(res=>{
    if(res.code==='200'&&res.data){
      my.value = res.data
      console.log('my.value:',my.value)
    }else{
      ElMessage.error(res.msg)
    }
  })
}


const userId = Number(account.value?.id) || 0;


// WebSocket 实例 & 地址
let socket = null;
const socketUrl = `ws://localhost:8080/chatServer/${userId}`;

// 全局状态
const friendList = ref([]);
const currentFriendId = ref(null);
const currentFriend = ref(null);
const messages = ref([]);
const text = ref('');

// 格式化时间 → 适配后端 String 类型 time
const formatTime = () => {
  const now = new Date();
  return `${now.getFullYear()}-${(now.getMonth() + 1).toString().padStart(2, '0')}-${now.getDate().toString().padStart(2, '0')} ${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}:${now.getSeconds().toString().padStart(2, '0')}`;
};

// 页面初始化
onMounted(async () => {
  if (!userId) {
    ElMessage.warning("请先登录");
    return;
  }
  console.log('account:',account)
  getAccount()

  // 解析路由参数，自动设置聊天对象
  const targetIdStr = route.query.id;
  if (targetIdStr) {
    const targetId = Number(targetIdStr);
    if (!isNaN(targetId)) {
      currentFriendId.value = targetId;
      console.log("✅ 路由自动选中聊天ID：", targetId);
    }
  }

  // 加载好友列表
  await getFriendList();

  // 有聊天对象则加载用户信息 + 历史记录
  if (currentFriendId.value) {
    await loadFriendAndHistory(currentFriend.id);
  }

  loadFriendAndHistory(currentFriend.id)
  getChatHistory()

  // 初始化 WebSocket
  initWebSocket();
});

// 根据用户ID 加载用户信息 + 历史聊天记录
const loadFriendAndHistory = async (fid) => {
  try {
    // 1. 获取对方用户信息
    const userRes = await request.get(`/chat/user/${fid}`);
    currentFriend.value = userRes.data;
    console.log('currentFriend.value:',currentFriend.value)
    console.log('userRes:',userRes)

    // 3. 标记消息为已读
    await readMessage(fid);
    getFriendList();
  } catch (err) {
    console.error("加载聊天数据失败：", err);
  }
};

// 初始化 WebSocket
const initWebSocket = () => {
  // 防止重复创建连接
  if (socket && socket.readyState === WebSocket.OPEN) return;

  socket = new WebSocket(socketUrl);

  socket.onopen = () => {
    console.log("✅ WebSocket 连接成功");
  };

  socket.onerror = (err) => {
    console.error("❌ WebSocket 异常：", err);
    ElMessage.error("聊天服务连接失败，请刷新页面");
  };

  // 接收后端推送的消息
  socket.onmessage = async (event) => {
    console.log("📥 收到服务端消息：", event.data);
    const res = JSON.parse(event.data);
    messages.value.push(res);
    await nextTick();
    scrollToBottom();

    // 收到对方消息自动标为已读
    if (res.fromUserId === currentFriendId.value) {
      await readMessage(res.fromUserId);
      getFriendList();
    }
  };

  socket.onclose = () => {
    console.log("🔌 WebSocket 连接关闭");
    socket = null;
  };
};

// 获取好友列表
const getFriendList = async () => {
  try {
    const res = await request.get("/chat/user");
    console.log('getFriendList res:', res)
    friendList.value = res.data;
    console.log('friendList.value',friendList.value)

  } catch (err) {
    console.error("获取好友列表失败：", err);
  }
};

// 左侧点击好友切换聊天
const selectFriend = async (friend) => {
  currentFriendId.value = Number(friend.id);
  text.value = '';
  await loadFriendAndHistory(currentFriendId.value);
};

// 标记消息已读（对接后端 /chat/clear）
const readMessage = async (toUserId) => {
  try {
    await request.get("/chat/clear", {
      params: {
        fromUserId: userId,
        toUserId: toUserId
      }
    });
  } catch (err) {
    console.error("标记已读失败：", err);
  }
};

// 发送消息（严格对齐 Chat 实体）
const send = () => {
  const content = text.value.trim();
  if (!content) {
    ElMessage.warning("请输入消息内容");
    return;
  }
  if (!currentFriendId.value) {
    ElMessage.warning("请选择聊天对象");
    return;
  }
  if (!socket || socket.readyState !== WebSocket.OPEN) {
    ElMessage.error("聊天连接已断开");
    return;
  }

  // 组装数据：和后端 Chat 实体 100% 匹配
  const sendData = {
    text: content,
    type: "text",       // 文本类型消息
    time: formatTime(), // 字符串格式时间
    fromUserId: userId,
    toUserId: currentFriendId.value,
    isRead: false
  };

  console.log("📤 发送消息：", sendData);

  // 本地即时渲染自己的消息
  messages.value.push(sendData);
  // WebSocket 发送到后端
  socket.send(JSON.stringify(sendData));
  request.post("/chat",sendData).then(res=>{
    if(res.code==='200'){
      ElMessage.success("发送成功")
    }else{
      ElMessage.error("发送失败")
    }
  })

  // 清空输入框 + 滚动到底部
  text.value = '';
  nextTick(scrollToBottom);
};

// 聊天框滚动到底部
const scrollToBottom = () => {
  const chatBox = document.getElementById("chat-box");
  if (chatBox) {
    chatBox.scrollTop = chatBox.scrollHeight;
  }
};

// 页面销毁关闭连接
onBeforeUnmount(() => {
  if (socket) {
    socket.close();
    socket = null;
  }
});

const getChatHistory=async() => {
  try {
    const res = await request.get("/chat/messagehistory", {
      params: {
        fromUserId: userId,
        toUserId: currentFriendId.value
      }
    });
    console.log('res',res)
    // 接口返回的聊天列表
    console.log("聊天记录", res.data);
    // 赋值给消息列表，渲染到页面
    messages.value = res.data;
    // 滚动到底部
    await nextTick(scrollToBottom);
  } catch (err) {
    console.error("加载聊天记录失败", err);
    ElMessage.error("加载记录异常");
  }
}
</script>
<template>
  <div class="chat-container">
    <!-- 左侧好友列表 -->
    <div class="friend-list">
      <div class="title">聊天列表</div>
      <div
        class="friend-item"
        v-for="item in friendList"
        :key="item.id"
        :class="{ active: currentFriendId === item.id }"
        @click="selectFriend(item)"
      >
        <div class="avatar">
          <img :src="item.avatarUrl || ''" alt="头像" />
        </div>
        <div class="info">
          <div class="name">{{ item.nickname }}</div>
          <div class="unread" v-if="item.count > 0">{{ item.count }}</div>
        </div>
      </div>
    </div>

    <!-- 右侧聊天区域 -->
    <div class="chat-main">
      <div class="chat-header" v-if="currentFriend">
        正在和 {{ currentFriend.nickname }} 聊天
      </div>
      <div class="chat-header empty" v-else-if="currentFriendId">
        正在和用户 {{ currentFriendId }} 聊天
      </div>
      <div class="chat-header empty" v-else>
        请选择好友开始聊天
      </div>

      <!-- 聊天内容区 -->
      <div id="chat-box" class="chat-content">
        <div class="msg-item" v-for="msg in messages" :key="msg.id || msg.time">
          <!-- 自己的消息：整体靠右 | 气泡在左，头像在右 -->
          <div v-if="msg.fromUserId === userId" class="msg-row self-row">
            <div class="msg-bubble self-bubble">
              {{ msg.text }}
              <div class="msg-time">{{ msg.time }}</div>
            </div>
            <img class="msg-avatar" :src="my.avatarUrl || ''" alt="头像" />
          </div>

          <!-- 对方消息：整体靠左 | 头像在左，气泡在右 -->
          <div v-else class="msg-row other-row">
            <img class="msg-avatar" :src="currentFriend.avatarUrl || '/default-avatar.png'" alt="头像" />
            <div class="msg-bubble">
              {{ msg.text }}
              <div class="msg-time">{{ msg.time }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="chat-input">
        <el-input
          v-model="text"
          placeholder="输入消息，回车发送"
          style="flex: 1; margin-right: 10px"
          @keyup.enter.native="send"
        />
        <el-button type="primary" @click="send">发送</el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 整体容器 */
.chat-container {
  display: flex;
  width: 1000px;
  height: 600px;
  border: 1px solid #e5e6eb;
  border-radius: 8px;
  overflow: hidden;
  margin: 20px auto;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
  background: #fff;
}

/* 左侧好友列表 */
.friend-list {
  width: 240px;
  border-right: 1px solid #ebeef5;
  background-color: #f8f9fa;
}
.friend-list .title {
  height: 50px;
  line-height: 50px;
  text-align: center;
  font-size: 16px;
  font-weight: 600;
  border-bottom: 1px solid #ebeef5;
  color: #333;
}
.friend-item {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  cursor: pointer;
  position: relative;
  transition: background 0.2s;
}
.friend-item:hover {
  background-color: #e9edf5;
}
.friend-item.active {
  background-color: #d7e3fc;
}
.avatar img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}
.info {
  margin-left: 12px;
  flex: 1;
}
.name {
  font-size: 14px;
  color: #333;
}
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

/* 右侧聊天主体 */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.chat-header {
  height: 50px;
  line-height: 50px;
  padding: 0 20px;
  border-bottom: 1px solid #ebeef5;
  font-size: 15px;
  color: #333;
}
.chat-header.empty {
  color: #999;
  text-align: center;
}

/* 聊天内容区域 */
.chat-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #fafafa;
}
.msg-item {
  margin-bottom: 18px;
}

/* 消息行：改为顶部对齐，实现头像与气泡顶部平齐 */
.msg-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}
/* 自己消息：整体右对齐 */
.self-row {
  justify-content: flex-end;
}
/* 对方消息：整体左对齐 */
.other-row {
  justify-content: flex-start;
}

/* 消息气泡通用样式 */
.msg-bubble {
  max-width: 60%;
  padding: 10px 14px;
  border-radius: 12px;
  word-wrap: break-word;
  font-size: 14px;
  line-height: 1.5;
}
/* 自己的气泡 */
.self-bubble {
  background-color: #409eff;
  color: #ffffff;
  border-bottom-right-radius: 4px;
}
/* 对方气泡 */
.msg-bubble:not(.self-bubble) {
  background-color: #ffffff;
  color: #333;
  border: 1px solid #e5e6eb;
  border-bottom-left-radius: 4px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}

/* 头像样式 */
.msg-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

/* 消息时间 */
.msg-time {
  font-size: 12px;
  opacity: 0.75;
  margin-top: 4px;
  text-align: right;
}

/* 输入区域 */
.chat-input {
  display: flex;
  align-items: center;
  padding: 15px;
  border-top: 1px solid #ebeef5;
  background: #fff;
}
</style>