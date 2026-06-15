<script setup>
import { ref, onBeforeUnmount, onMounted, nextTick, watch } from "vue";
import axios from "axios";
import { ElMessage } from "element-plus";
import { useRoute } from "vue-router";

const route = useRoute();

// 当前登录用户
const account = ref(localStorage.getItem('account')
  ? JSON.parse(localStorage.getItem('account'))
  : {});
const userId = Number(account.value?.id) || 0;

let socket = null;
const socketUrl = `ws://localhost:8080/chatServer/${userId}`;

const friendList = ref([]);
const currentFriendId = ref(null);
const currentFriend = ref(null);
const messages = ref([]);
const text = ref('');

// ========== 关键：一进来就强制设置 currentFriendId，不再依赖好友列表 ==========
onMounted(async () => {
  if (!userId) {
    ElMessage.warning("请先登录");
    return;
  }

  // 1. 直接从路由拿 id，转数字，强制赋值
  const targetIdStr = route.query.id;
  if (targetIdStr) {
    const targetId = Number(targetIdStr);
    if (!isNaN(targetId)) {
      currentFriendId.value = targetId; // 直接赋值！
      console.log("✅ 从路由设置聊天对象ID：", targetId);
    }
  }

  // 2. 加载好友列表（正常展示用）
  await getFriendList();

  // 3. 如果有路由id，尝试获取用户信息（显示昵称头像）
  if (currentFriendId.value) {
    await getSingleUser(currentFriendId.value);
  }

  // 4. 初始化 WebSocket
  initWebSocket();
});

// 监听路由变化
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
      currentFriendId.value = fid; // 直接赋值
      await getSingleUser(fid);
    }
  }
);

// 根据ID获取单个用户
const getSingleUser = async (fid) => {
  try {
    const res = await axios.get(`/chat/user/${fid}`);
    if (res.data.code === 200) {
      currentFriend.value = res.data.data;
      // 加载历史记录
      const msgRes = await axios.get("/chat/message", {
        params: {
          fromUserId: userId,
          toUserId: fid
        }
      });
      if (msgRes.data.code === 200) {
        messages.value = msgRes.data.data;
        await nextTick();
        scrollToBottom();
      }
    }
  } catch (err) {
    console.error("获取用户信息失败：", err);
  }
};

// 初始化 WebSocket
const initWebSocket = () => {
  if (socket && socket.readyState === WebSocket.OPEN) return;

  socket = new WebSocket(socketUrl);

  socket.onopen = () => {
    console.log("✅ WebSocket 连接成功");
  };

  socket.onerror = (err) => {
    console.error("❌ WebSocket 错误：", err);
    ElMessage.error("聊天服务连接失败，请刷新页面重试");
  };

  socket.onmessage = async (event) => {
    console.log("📥 收到后端消息：", event.data);
    const res = JSON.parse(event.data);
    messages.value.push(res);
    await nextTick();
    scrollToBottom();
  };

  socket.onclose = () => {
    console.log("🔌 WebSocket 连接已关闭");
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

// 选中好友
const selectFriend = async (friend) => {
  currentFriendId.value = Number(friend.id);
  currentFriend.value = friend;
  text.value = '';

  try {
    const res = await axios.get("/chat/message", {
      params: {
        fromUserId: userId,
        toUserId: currentFriendId.value
      }
    });
    if (res.data.code === 200) {
      messages.value = res.data.data;
      await nextTick();
      scrollToBottom();
    }
  } catch (err) {
    console.error("加载聊天记录失败：", err);
  }
};

// 发送消息（只改判断：只要 currentFriendId 有值就可以发）
const send = () => {
  const content = text.value.trim();
  if (!content) {
    ElMessage.warning("请输入消息内容");
    return;
  }
  if (!currentFriendId.value || isNaN(currentFriendId.value)) {
    ElMessage.warning("请先选择聊天对象");
    return;
  }
  if (!socket || socket.readyState !== WebSocket.OPEN) {
    ElMessage.error("聊天连接断开，请刷新页面");
    return;
  }

  const sendData = {
    fromUserId: userId,
    toUserId: currentFriendId.value,
    text: content,
    time: new Date().getTime(),
    isRead: false
  };

  console.log("📤 发送消息：", sendData);

  // ========== 关键：本地立刻添加自己的消息，不用等服务器 ==========
  messages.value.push(sendData);

  // 发送给服务器
  socket.send(JSON.stringify(sendData));

  // 清空输入
  text.value = '';

  // 滚动到底部
  nextTick(() => {
    scrollToBottom();
  });
};

const scrollToBottom = () => {
  const chatBox = document.getElementById("chat-box");
  if (chatBox) {
    chatBox.scrollTop = chatBox.scrollHeight;
  }
};

onBeforeUnmount(() => {
  if (socket) {
    socket.close();
    socket = null;
  }
});
</script>

<template>
  <div class="chat-container">
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

    <div class="chat-main">
      <div class="chat-header" v-if="currentFriend">
        正在和 {{ currentFriend.nickname }} 聊天（ID:{{ currentFriendId }}）
      </div>
      <div class="chat-header empty" v-else-if="currentFriendId">
        正在和用户 {{ currentFriendId }} 聊天（无信息）
      </div>
      <div class="chat-header empty" v-else>
        请选择好友开始聊天
      </div>

      <div id="chat-box" class="chat-content">
        <div class="msg-item" v-for="msg in messages" :key="msg.id || msg.time">
          <div class="self-msg" v-if="msg.fromUserId === userId">
            <div class="msg-text">{{ msg.text }}</div>
          </div>
          <div class="other-msg" v-else>
            <div class="msg-text">{{ msg.text }}</div>
          </div>
        </div>
      </div>

      <div class="chat-input">
        <el-input
          v-model="text"
          placeholder="输入消息，回车/点击发送"
          style="flex: 1; margin-right: 10px"
          @keyup.enter.native="send"
        />
        <el-button type="primary" @click="send">发送</el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 样式不变，沿用之前即可 */
.chat-container {
  display: flex;
  width: 1000px;
  height: 600px;
  border: 1px solid #e5e6eb;
  border-radius: 4px;
  overflow: hidden;
  margin: 20px auto;
}
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