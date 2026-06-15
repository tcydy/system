<script setup>
import { ref, onBeforeUnmount, onMounted, nextTick, computed } from "vue";
import { ElMessage } from "element-plus";
import { useRoute } from "vue-router";
import request from "@/utils/request.js";

const route = useRoute();

// ========== 登录账号处理（加try-catch防缓存损坏报错） ==========
let accountRaw = {};
try {
  const storageStr = localStorage.getItem('account');
  if (storageStr) accountRaw = JSON.parse(storageStr);
} catch (e) {
  console.error("账号缓存解析失败，重置登录信息", e);
  localStorage.removeItem('account');
}
const account = ref(accountRaw);
const userId = Number(account.value?.id) || 0;

// ========== WebSocket 重连相关变量 ==========
let socket = null;
const socketUrl = `ws://localhost:8080/chatServer/${userId}`;
// 重连配置
const maxReconnectTimes = 5;
let reconnectCount = 0;
let reconnectTimer = null;

// ========== 聊天全局状态 ==========
const friendList = ref([]);
const currentFriendId = ref(null);
const currentFriend = ref(null);
const messages = ref([]);
const text = ref('');
// loading状态
const friendListLoading = ref(false);
const historyLoading = ref(false);

// 【计算属性：兜底空对象，杜绝undefined读取】
const friendInfo = computed(() => {
  return currentFriend.value || { avatarUrl: "", nickname: "" };
});

// 格式化时间
const formatTime = () => {
  const now = new Date();
  const pad = num => num.toString().padStart(2, '0');
  return `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())} ${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`;
};

// 滚动到底部
const scrollToBottom = () => {
  const chatBox = document.getElementById("chat-box");
  if (chatBox) chatBox.scrollTop = chatBox.scrollHeight;
};

// 清除所有重连定时器
const clearReconnectTimer = () => {
  if (reconnectTimer) {
    clearTimeout(reconnectTimer);
    reconnectTimer = null;
  }
};

// ========== 接口函数 ==========
// 获取好友列表（带loading）
const getFriendList = async () => {
  friendListLoading.value = true;
  try {
    const res = await request.get("/chat/user");
    if (res.code === 200) {
      friendList.value = res.data || [];
    }
  } catch (err) {
    console.error("获取好友列表失败：", err);
    ElMessage.error("好友列表加载失败");
  } finally {
    friendListLoading.value = false;
  }
};

// 判断目标id是否在好友列表
const isInFriendList = (targetId) => {
  return friendList.value.some(item => item.id === targetId);
};

// 标记消息已读
const readMessage = (toUserId) => {
  if (!userId || !toUserId) return;
  request.get("/chat/clear", {
    params: {
      fromUserId: userId,
      toUserId: toUserId
    }
  }).then(() => getFriendList()).catch(err => console.error("标记已读失败：", err));
};

// 获取聊天历史记录（带loading）
const getChatHistory = async () => {
  if (!userId || !currentFriendId.value || !currentFriend.value) return;
  historyLoading.value = true;
  try {
    const res = await request.get("/chat/messagehistory", {
      params: {
        fromUserId: userId,
        toUserId: currentFriendId.value
      }
    });
    messages.value = res.data || [];
    await nextTick(scrollToBottom);

    // 关键逻辑：有聊天记录 && 当前用户不在好友列表 → 刷新好友列表，自动出现
    if (messages.value.length > 0 && !isInFriendList(currentFriendId.value)) {
      await getFriendList();
    }
  } catch (err) {
    console.error("加载聊天记录失败", err);
    ElMessage.error("聊天记录加载异常");
  } finally {
    historyLoading.value = false;
  }
};

// 加载对方用户信息 + 历史记录
const loadFriendAndHistory = async (fid) => {
  if (!fid) return;
  messages.value = [];
  try {
    const userRes = await request.get(`/chat/user/${fid}`);
    currentFriend.value = userRes.data;
    currentFriendId.value = Number(fid);
    await getChatHistory();
    readMessage(fid);
  } catch (err) {
    console.error("加载聊天数据失败：", err);
    currentFriend.value = null;
    ElMessage.error("该用户信息加载失败");
  }
};

// ========== WebSocket初始化&重连逻辑 ==========
const initWebSocket = () => {
  // 未登录不建立连接
  if (!userId) return;
  // 已有活跃连接，不再重复创建
  if (socket && socket.readyState === WebSocket.OPEN) return;
  // 清除旧重连定时器
  clearReconnectTimer();

  socket = new WebSocket(socketUrl);

  socket.onopen = () => {
    console.log("✅ WebSocket 连接成功，重连次数重置");
    reconnectCount = 0;
    clearReconnectTimer();
  };

  socket.onerror = (err) => {
    console.error("❌ WebSocket 异常：", err);
  };

  socket.onmessage = (event) => {
    try {
      const res = JSON.parse(event.data);
      console.log("📥 收到服务端消息：", res);
      // 使用id去重（后端每条消息都有唯一id）
      const exist = messages.value.some(item => item.id === res.id);
      if (!exist) {
        messages.value.push(res);
        nextTick(scrollToBottom);
        // 收到消息实时刷新好友列表，更新未读红点
        getFriendList();
      }
      // 当前聊天窗口收到对方消息自动标已读
      if (res.fromUserId === currentFriendId.value) {
        readMessage(res.fromUserId);
      }
    } catch (err) {
      console.error("解析消息失败：", err);
    }
  };

  socket.onclose = (e) => {
    console.log("🔌 WebSocket 连接关闭", e.code, e.reason);
    socket = null;
    clearReconnectTimer();

    // 主动关闭不重连（页面卸载手动close）
    if (e.code === 1000) return;
    // 超出最大重试次数不再重连
    if (reconnectCount >= maxReconnectTimes) {
      ElMessage.error("聊天服务连接失败，已达最大重连次数，请刷新页面");
      return;
    }

    // 延时自动重连
    reconnectCount++;
    console.log(`开始第${reconnectCount}次重连，共允许${maxReconnectTimes}次`);
    reconnectTimer = setTimeout(() => {
      initWebSocket();
    }, 2000);
  };
};

// ========== 页面交互方法 ==========
// 点击好友切换聊天
const selectFriend = async (friend) => {
  if (!friend?.id) return;
  currentFriendId.value = Number(friend.id);
  text.value = '';
  await loadFriendAndHistory(friend.id);
};

// 发送消息（入库失败删除本地临时消息）
const send = async () => {
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
    ElMessage.error("聊天连接已断开，等待自动重连");
    return;
  }

  const sendData = {
    text: content,
    type: "text",
    time: formatTime(),
    fromUserId: userId,
    toUserId: currentFriendId.value,
    isRead: false
  };

  console.log("📤 发送消息：", sendData);
  // 本地临时渲染消息
  messages.value.push(sendData);
  const tempMsgIndex = messages.value.length - 1;
  text.value = '';
  nextTick(scrollToBottom);

  socket.send(JSON.stringify(sendData));

  try {
    const res = await request.post("/chat", sendData);
    if (res.code !== 200 && res.code !== '200') {
      throw new Error(res.msg || "服务端存储失败");
    }
    // 发送成功，不弹窗提示，减少打扰
  } catch (err) {
    console.error("消息入库请求异常：", err);
    // 需求A：入库失败删除本地临时消息
    messages.value.splice(tempMsgIndex, 1);
    ElMessage.error("消息发送失败，请重试");
  }
};

// ========== 生命周期 ==========
onMounted(async () => {
  if (!userId) {
    ElMessage.warning("请先登录");
    return;
  }

  const targetIdStr = route.query.id;
  let jumpTargetId = null;
  if (targetIdStr) {
    const targetId = Number(targetIdStr);
    if (!isNaN(targetId)) jumpTargetId = targetId;
  }

  // 先加载好友列表
  await getFriendList();

  // 【修改点：不再校验是否在好友列表，直接打开聊天】
  if (jumpTargetId) {
    currentFriendId.value = jumpTargetId;
    await loadFriendAndHistory(jumpTargetId);
  }

  // 初始化WS连接
  initWebSocket();
});

onBeforeUnmount(() => {
  // 清除重连定时器，停止后台重试
  clearReconnectTimer();
  reconnectCount = maxReconnectTimes;
  // 安全关闭WS，标记正常关闭不触发重连
  if (socket) {
    socket.close(1000, "页面卸载主动关闭");
    socket = null;
  }
});
</script>

<template>
  <div class="chat-container">
    <!-- 左侧好友列表 -->
    <div class="friend-list">
      <div class="title">聊天列表</div>
      <div v-if="friendListLoading" class="loading-tip">加载好友中...</div>
      <div
        class="friend-item"
        v-for="item in friendList"
        :key="item.id"
        :class="{ active: currentFriendId === item.id }"
        @click="selectFriend(item)"
      >
        <div class="avatar">
          <img
            :src="item.avatarUrl || '/default-avatar.png'"
            alt="头像"
            @error="$event.target.src = ''"
          />
        </div>
        <div class="info">
          <div class="name">{{ item.nickname }}</div>
          <div class="unread" v-if="item.count > 0">{{ item.count }}</div>
        </div>
      </div>
      <div v-if="!friendListLoading && friendList.length === 0" class="empty-tip">暂无好友</div>
    </div>

    <!-- 右侧聊天区域 -->
    <div class="chat-main">
      <!-- 区分好友 / 临时会话用户 -->
      <div class="chat-header" v-if="currentFriend">
        {{ isInFriendList(currentFriendId) ? '正在和' : '临时会话：' }} {{ friendInfo.nickname }}
      </div>
      <div class="chat-header empty" v-else-if="currentFriendId">
        用户ID：{{ currentFriendId }}（临时会话）
      </div>
      <div class="chat-header empty" v-else>
        请选择好友或通过链接打开临时会话
      </div>

      <div id="chat-box" class="chat-content" v-if="currentFriend">
        <div v-if="historyLoading" class="loading-tip chat-loading">加载聊天记录中...</div>
        <div class="msg-item" v-for="msg in messages" :key="msg.id">
          <!-- 自己消息 -->
          <div v-if="msg.fromUserId === userId" class="self-msg">
            <div class="msg-text">
              {{ msg.text }}
              <div class="msg-time">{{ msg.time }}</div>
            </div>
            <img
              class="msg-avatar"
              :src="account.value.avatarUrl"
              alt="我的头像"
              @error="$event.target.src = ''"
            />
          </div>

          <!-- 对方消息 -->
          <div v-else class="other-msg-wrap">
            <img
              class="msg-avatar"
              :src="friendInfo.avatarUrl"
              alt="对方头像"
              @error="$event.target.src = ''"
            />
            <div class="msg-text other-msg">
              {{ msg.text }}
              <div class="msg-time">{{ msg.time }}</div>
            </div>
          </div>
        </div>
        <div v-if="!historyLoading && messages.length === 0" class="empty-tip chat-empty">暂无聊天记录，发一条消息开启对话</div>
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

.chat-input {
  display: flex;
  align-items: center;
  padding: 15px;
  border-top: 1px solid #e5e6eb;
}

.msg-item {
  margin-bottom: 16px;
}
.self-msg {
  display: flex;
  align-items: flex-end;
  flex-direction: row-reverse;
  gap: 8px;
}
.other-msg-wrap {
  display: flex;
  align-items: flex-end;
  gap: 8px;
}
.msg-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
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
.msg-text.other-msg {
  background: #f4f4f5;
  color: #333;
}
.msg-time {
  font-size: 12px;
  opacity: 0.7;
  margin-top: 4px;
  text-align: right;
}

/* 加载/空状态样式 */
.loading-tip, .empty-tip {
  text-align: center;
  padding: 20px;
  color: #999;
  font-size: 14px;
}
.chat-loading, .chat-empty {
  padding: 60px 0;
}
</style>