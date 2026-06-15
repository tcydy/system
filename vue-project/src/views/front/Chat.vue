<script setup>
import { ref, onBeforeUnmount, onMounted, nextTick, watch } from "vue";
import axios from "axios";
import { ElMessage } from "element-plus";
import { useRoute } from "vue-router";
import request from "@/utils/request.js";

const route = useRoute();

// 当前登录用户
const account = ref(localStorage.getItem('account')
  ? JSON.parse(localStorage.getItem('account'))
  : {});
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
    await loadFriendAndHistory(currentFriendId.value);
  }
  getChatHistory()

  // 初始化 WebSocket
  initWebSocket();
});

// 监听路由切换（新id自动切换聊天）
watch(
  () => route.query.id,
  async (newId) => {
    if (!newId) {
      currentFriendId.value = null;
      currentFriend.value = null;
      messages.value = [];
      return;
    }
    const fid = Number(newId);
    if (!isNaN(fid)) {
      currentFriendId.value = fid;
      await loadFriendAndHistory(fid);
    }
  }
);

// 根据用户ID 加载用户信息 + 历史聊天记录
const loadFriendAndHistory = async (fid) => {
  try {
    // 1. 获取对方用户信息
    const userRes = await axios.get(`/chat/user/${fid}`);
    if (userRes.data.code === 200) {
      currentFriend.value = userRes.data.data;
    }

    

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
    const res = await axios.get("/chat/user");
    if (res.data.code === 200) {
      friendList.value = res.data.data;
    }
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
    await axios.get("/chat/clear", {
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
          <img :src="item.avatarUrl || '/default-avatar.png'" alt="头像" />
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
          <!-- 自己发出的消息 -->
          <div class="self-msg" v-if="msg.fromUserId === userId">
            <div class="msg-text">
              {{ msg.text }}
              <div class="msg-time">{{ msg.time }}</div>
            </div>
          </div>
          <!-- 对方消息 -->
          <div class="other-msg" v-else>
            <div class="msg-text">
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
.chat-container {
  display: flex;
  width: 1000px;
  height: 600px;
  border: 1px solid #e5e6eb;
  border-radius: 4px;
  overflow: hidden;
  margin: 20px auto;
}

/* 好友列表 */
.friend-list {
  width: 240px;
  border-right: 1px solid #e5e6eb;
  background: #f9fafb;
}
.friend-list .title {
  height: 50px;
  line-height: 50px;
  text-align: center;
  font-size: 16px;
  font-weight: bold;
  border-bottom: 1px solid #e5e6eb;
}
.friend-item {
  display: flex;
  align-items: center;
  padding: 10px 15px;
  cursor: pointer;
  position: relative;
}
.friend-item:hover {
  background: #ebeef5;
}
.friend-item.active {
  background: #dce3f4;
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
}
.unread {
  position: absolute;
  right: 15px;
  top: 12px;
  background: #f53f3f;
  color: #fff;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 10px;
  min-width: 18px;
  text-align: center;
}

/* 聊天主体 */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.chat-header {
  height: 50px;
  line-height: 50px;
  padding: 0 20px;
  border-bottom: 1px solid #e5e6eb;
  font-size: 15px;
}
.chat-header.empty {
  color: #999;
  text-align: center;
}

.chat-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: #fff;
}
.msg-item {
  margin-bottom: 16px;
}
.self-msg {
  display: flex;
  justify-content: flex-end;
}
.other-msg {
  display: flex;
  justify-content: flex-start;
}
.msg-text {
  max-width: 60%;
  padding: 8px 12px;
  border-radius: 8px;
  word-wrap: break-word;
}
.self-msg .msg-text {
  background: #409eff;
  color: #fff;
}
.other-msg .msg-text {
  background: #f4f4f5;
  color: #333;
}
.msg-time {
  font-size: 12px;
  opacity: 0.7;
  margin-top: 4px;
  text-align: right;
}

.chat-input {
  display: flex;
  align-items: center;
  padding: 15px;
  border-top: 1px solid #e5e6eb;
}
</style>