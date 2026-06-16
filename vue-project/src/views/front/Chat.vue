<script setup>
import { ref, onBeforeUnmount, onMounted, nextTick, reactive } from "vue";
import { ElMessage } from "element-plus";
import { useRoute } from "vue-router";
import request from "@/utils/request.js";

const route = useRoute();

// 个人信息
const my = reactive({});
const userId = ref("");

// 聊天状态
const friendList = ref([]);
const currentFriendId = ref(null);
const currentFriend = ref(null);
const messages = ref([]);
const text = ref("");

// WebSocket 相关
let socket = null;
let socketUrl = "";
let isManualClose = false;
const isWsOnline = ref(false);

// 本地离线留言存储 key
const LOCAL_OFFLINE_MSG_KEY = "chat_offline_message_list";

// 格式化时间
const formatTime = () => {
  const now = new Date();
  const pad = (num) => num.toString().padStart(2, "0");
  return `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())} ${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`;
};

// 获取本地缓存的离线消息
const getLocalOfflineMsg = () => {
  const str = localStorage.getItem(LOCAL_OFFLINE_MSG_KEY);
  return str ? JSON.parse(str) : [];
};

// 保存消息到本地缓存
const saveLocalOfflineMsg = (msgList) => {
  localStorage.setItem(LOCAL_OFFLINE_MSG_KEY, JSON.stringify(msgList));
};

// 移除单条本地缓存消息
const removeLocalMsg = (time) => {
  let list = getLocalOfflineMsg();
  list = list.filter(item => item.time !== time);
  saveLocalOfflineMsg(list);
};

// 获取当前登录用户信息
const getAccount = async () => {
  try {
    const res =await request.get("/web/userInfo");
    console.log('获取当前登录用户信息res:',res)
    console.log('获取当前登录用户信息res.code:',res.code)
    console.log('获取当前登录用户信息res.data:', res.data)

    if (res.code === "200" && res.data) {
      Object.assign(my, res.data);
      userId.value = my.id;
      console.log('获取当前登录用户信息userId.value:', userId.value)
      return true;
    } else {
      ElMessage.error(res.msg || "获取个人信息失败");
      return false;
    }
  } catch (err) {
    console.error("获取用户信息异常：", err);
    ElMessage.error("网络异常，获取个人信息失败");
    return false;
  }
};

// 获取好友列表（异步不阻塞渲染）
const getFriendList = async () => {
  try {
    const res = await request.get("/chat/user");
    friendList.value = res.data || [];
  } catch (err) {
    console.error("获取好友列表失败：", err);
  }
};

// 标记消息已读
const readMessage = async (toUserId) => {
  if (!userId.value || !toUserId) return;
  await request.get("/chat/clear", {
    params: {
      fromUserId: userId.value,
      toUserId: toUserId
    }
  }).then(() => getFriendList()).catch(err => console.error("标记已读失败", err));
};

// 加载好友信息 + 历史聊天记录
const loadFriendAndHistory =async (fid) => {
  if (!fid) return;
  try {
    const userRes =await  request.get(`/chat/user/${fid}`);
    currentFriend.value = userRes.data || {};
    currentFriendId.value = Number(fid);
    getChatHistory();
    readMessage(fid);
  } catch (err) {
    console.error("加载聊天数据失败：", err);
    ElMessage.error("加载聊天信息异常");
  }
};

// 获取聊天历史记录
const getChatHistory =async  () => {
  if (!userId.value || !currentFriendId.value) return;
  try {
    const res =await request.get("/chat/messagehistory", {
      params: {
        fromUserId: userId.value,
        toUserId: currentFriendId.value
      }
    });
    messages.value = res.data || [];
    scrollToBottom();
  } catch (err) {
    console.error("加载聊天记录失败：", err);
    ElMessage.error("加载聊天记录异常");
  }
};

// 滚动到底部（独立nextTick，保证DOM刷新后执行）
const scrollToBottom = () => {
  nextTick(() => {
    const chatBox = document.getElementById("chat-box");
    if (chatBox) chatBox.scrollTop = chatBox.scrollHeight;
  });
};

// 初始化 WebSocket
const initWebSocket = () => {
  if (socket && socket.readyState === WebSocket.OPEN) return;
  isManualClose = false;
  isWsOnline.value = false;

  socket = new WebSocket(socketUrl);

  socket.onopen = () => {
    console.log("✅ WebSocket 连接成功");
    isWsOnline.value = true;
  };

  socket.onerror = (err) => {
    console.error("❌ WebSocket 异常：", err);
    ElMessage.error("聊天服务连接失败，请刷新页面");
    isWsOnline.value = false;
  };

  socket.onmessage = async (event) => {
    try {
      console.log("📥 收到服务端消息：", event.data);
      const msg = JSON.parse(event.data);
      const senderId = msg.fromUserId;
      // 仅当前聊天好友消息渲染到聊天框
      if (senderId === currentFriendId.value) {
        // 消息去重
        const hasSameMsg = messages.value.some(item => item.time === msg.time);
        if (!hasSameMsg) {
          messages.value.push(msg);
          await nextTick();
          scrollToBottom();
          readMessage(senderId);
        }
      }
      // 任意消息刷新好友未读数量
      getFriendList();
    } catch (err) {
      console.error("解析ws消息失败", err);
    }
  };

  socket.onclose = () => {
    console.log("🔌 WebSocket 连接关闭");
    socket = null;
    isWsOnline.value = false;
    if (!isManualClose) setTimeout(initWebSocket, 3000);
  };
};

// 切换好友
const selectFriend = async (friend) => {
  if (!friend?.id) return;
  text.value = "";
  await loadFriendAndHistory(friend.id);
};

// 发送消息核心：优先走WebSocket实时通道，再异步入库
const send  = async () => {
  const content = text.value.trim();
  if (!content) return ElMessage.warning("请输入消息内容");
  if (!currentFriendId.value) return ElMessage.warning("请选择聊天对象");

  const sendData = {
    text: content,
    type: "text",
    time: formatTime(),
    fromUserId: userId.value,
    toUserId: currentFriendId.value,
    isRead: false
  };
  text.value = "";

  // 1. WS在线：优先发送WebSocket，实时推送给对方
  if (isWsOnline.value && socket.readyState === WebSocket.OPEN) {
    socket.send(JSON.stringify(sendData));
  }

  // 2. 同步调用后端保存消息
  try {
    const res =await request.post("/chat", sendData);
    if (res.code === "200") {
      removeLocalMsg(sendData.time);
      // 入库成功再渲染自己消息，持久化不丢失
      messages.value.push(sendData);
      scrollToBottom();
      ElMessage.success("发送成功");
    } else {
      ElMessage.error(res.msg || "发送失败");
      const list = getLocalOfflineMsg();
      list.push(sendData);
      saveLocalOfflineMsg(list);
    }
  } catch (err) {
    console.error("消息入库失败，离线缓存", err);
    const list = getLocalOfflineMsg();
    list.push(sendData);
    saveLocalOfflineMsg(list);
    // 离线临时展示，下次联网补发
    messages.value.push(sendData);
    scrollToBottom();
  }
};

// 页面挂载
onMounted(async () => {
  const hasUser =await getAccount();
  if (!hasUser) return;
  socketUrl = `ws://localhost:8080/chatServer/${my.id}`;

  // 路由参数自动打开聊天
  const targetIdStr = route.query.id;
  if (targetIdStr) {
    const targetId = Number(targetIdStr);
    if (!isNaN(targetId)) {
      loadFriendAndHistory(targetId);
    }
  }

  getFriendList();

  initWebSocket();
});

// 页面销毁关闭ws
onBeforeUnmount(() => {
  isManualClose = true;
  if (socket) {
    socket.close();
    socket = null;
  }
});
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
      <div class="chat-header" v-if="currentFriend?.nickname">
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
        <div class="msg-item" v-for="msg in messages" :key="msg.time">
          <!-- 自己的消息 -->
          <div v-if="msg.fromUserId === userId" class="msg-row self-row">
            <div class="msg-bubble self-bubble">
              {{ msg.text }}
              <div class="msg-time">{{ msg.time }}</div>
            </div>
            <img class="msg-avatar" :src="my.avatarUrl || ''" alt="头像" />
          </div>
          <!-- 对方消息 -->
          <div v-else class="msg-row other-row">
            <img
              class="msg-avatar"
              :src="currentFriend?.avatarUrl || '/default-avatar.png'"
              alt="头像"
            />
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
          @keyup.enter="send"
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

.msg-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}
.self-row {
  justify-content: flex-end;
}
.other-row {
  justify-content: flex-start;
}

.msg-bubble {
  max-width: 60%;
  padding: 10px 14px;
  border-radius: 12px;
  word-wrap: break-word;
  font-size: 14px;
  line-height: 1.5;
}
.self-bubble {
  background-color: #409eff;
  color: #ffffff;
  border-bottom-right-radius: 4px;
}
.msg-bubble:not(.self-bubble) {
  background-color: #ffffff;
  color: #333;
  border: 1px solid #e5e6eb;
  border-bottom-left-radius: 4px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}

.msg-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

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