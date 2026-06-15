<script setup>
import { ref, onBeforeUnmount, onMounted, nextTick } from "vue";
import axios from "axios";
import { ElMessage } from "element-plus";

// 当前登录用户信息
const account = ref(localStorage.getItem('account') 
  ? JSON.parse(localStorage.getItem('account')) 
  : {});
const userId = account.value?.id || 0;

// 聊天相关状态
let socket = null;
const socketUrl = `ws://localhost:8080/chatServer/${userId}`;

// 好友列表
const friendList = ref([]);
// 当前选中聊天的好友ID & 好友信息
const currentFriendId = ref(null);
const currentFriend = ref(null);
// 聊天消息列表
const messages = ref([]);
// 输入框内容
const text = ref('');

// 页面挂载：加载好友列表
onMounted(async () => {
  if (!userId) {
    ElMessage.warning("请先登录");
    return;
  }
  await getFriendList();
  initWebSocket();
});

// ========== 1. 初始化 WebSocket ==========
const initWebSocket = () => {
  socket = new WebSocket(socketUrl);

  // 连接成功
  socket.onopen = () => {
    console.log("WebSocket 连接成功");
  };

  // 连接错误
  socket.onerror = (err) => {
    console.error("WebSocket 连接异常", err);
    ElMessage.error("聊天服务连接失败");
  };

  // 接收服务端推送的消息
  socket.onmessage = async (event) => {
    const res = JSON.parse(event.data);
    messages.value.push(res);
    // 滚动到底部
    await nextTick();
    scrollToBottom();

    // 收到消息自动标记为已读（当前聊天窗口）
    if (currentFriendId.value === res.fromUserId) {
      await readMessage(res.fromUserId);
      // 刷新好友未读数量
      getFriendList();
    }
  };

  // 连接关闭
  socket.onclose = () => {
    console.log("WebSocket 连接关闭");
  };
};

// ========== 2. 获取好友列表（调用后端 /chat/user 接口） ==========
const getFriendList = async () => {
  try {
    const res = await axios.get("/chat/user");
    if (res.data.code === 200) {
      friendList.value = res.data.data;
    }
  } catch (err) {
    console.error("获取好友列表失败", err);
  }
};

// ========== 3. 选中好友，加载历史聊天记录 ==========
const selectFriend = async (friend) => {
  currentFriendId.value = friend.id;
  currentFriend.value = friend;
  text.value = '';

  // 加载两人历史消息 /chat/message
  try {
    const res = await axios.get("/chat/message", {
      params: {
        fromUserId: userId,
        toUserId: friend.id
      }
    });
    if (res.data.code === 200) {
      messages.value = res.data.data;
      await nextTick();
      scrollToBottom();
    }
  } catch (err) {
    console.error("加载聊天记录失败", err);
  }

  // 标记对方发来的消息为已读 /chat/clear
  await readMessage(friend.id);
  // 刷新未读消息数
  getFriendList();
};

// ========== 4. 标记消息为已读 ==========
const readMessage = async (toUserId) => {
  try {
    await axios.get("/chat/clear", {
      params: {
        fromUserId: userId,
        toUserId: toUserId
      }
    });
  } catch (err) {
    console.error("标记已读失败", err);
  }
};

// ========== 5. 发送消息 ==========
const send = () => {
  if (!text.value.trim()) {
    ElMessage.warning("请输入消息内容");
    return;
  }
  if (!currentFriendId.value) {
    ElMessage.warning("请先选择聊天好友");
    return;
  }
  if (socket.readyState !== WebSocket.OPEN) {
    ElMessage.error("聊天连接异常，请刷新重试");
    return;
  }

  // 组装消息体（和后端、websocket 约定格式）
  const sendData = {
    fromUserId: userId,
    toUserId: currentFriendId.value,
    text: text.value.trim(),
    time: new Date().getTime(),
    isRead: false
  };

  // WebSocket 发送
  socket.send(JSON.stringify(sendData));
  // 清空输入框
  text.value = '';
};

// ========== 6. 聊天区域自动滚动到底部 ==========
const scrollToBottom = () => {
  const chatBox = document.getElementById("chat-box");
  if (chatBox) {
    chatBox.scrollTop = chatBox.scrollHeight;
  }
};

// ========== 7. 页面销毁，关闭 WebSocket ==========
onBeforeUnmount(() => {
  if (socket) {
    socket.close();
    socket = null;
  }
});
</script>

<template>
  <div class="chat-container">
    <!-- 左侧：好友列表 -->
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

    <!-- 右侧：聊天区域 -->
    <div class="chat-main">
      <!-- 聊天头部 -->
      <div class="chat-header" v-if="currentFriend">
        <span>正在和 {{ currentFriend.nickname }} 聊天</span>
      </div>
      <div class="chat-header empty" v-else>
        请选择左侧好友开始聊天
      </div>

      <!-- 聊天内容区 -->
      <div id="chat-box" class="chat-content">
        <div class="msg-item" v-for="msg in messages" :key="msg.id || msg.time">
          <!-- 自己发送的消息（居右） -->
          <div class="self-msg" v-if="msg.fromUserId === userId">
            <div class="msg-text">{{ msg.text }}</div>
          </div>
          <!-- 对方消息（居左） -->
          <div class="other-msg" v-else>
            <div class="msg-text">{{ msg.text }}</div>
          </div>
        </div>
      </div>

      <!-- 消息输入区 -->
      <div class="chat-input">
        <el-input 
          v-model="text" 
          placeholder="请输入消息"
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

/* 左侧好友列表 */
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

/* 右侧聊天区域 */
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

.chat-input {
  display: flex;
  align-items: center;
  padding: 15px;
  border-top: 1px solid #e5e6eb;
}
</style>