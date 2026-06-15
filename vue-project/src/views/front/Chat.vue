<script setup>
import { ref, onBeforeUnmount } from "vue";

const account = ref(localStorage.getItem('account') ? JSON.parse(localStorage.getItem('account')) : {})
let socket = null

const userId = account.value.id
const socketUrl = "ws://localhost:8080/chatServer/" + userId;

// 开启一个websocket服务
socket = new WebSocket(socketUrl)

// 打开事件
socket.onopen = () => {
  console.log("websocket已打开")
}

// 错误事件
socket.onerror = (error) => {
  console.error("WebSocket错误:", error)
}

const text=ref('')
const send = ()=>{
    // 发送消息事件

    const data = {
        text:text.value,
        toUserId:20
    }

    //变成一个json数据
    socket.send(JSON.stringify(data))
}


const messages = ref([])

// 接受消息事件
socket.onmessage = (msg) => {
  console.log(msg.data)
  messages.value.push(JSON.parse(msg.data))
}

onBeforeUnmount(() => {
  if (socket) {
    socket.onclose = () => {
      console.log("websocket已关闭")
    }
    socket.close()
  }
})
</script>


<template>
<el-input v-model="text" style="width: 200px"></el-input>
<el-button type="primary" @click="send">发送</el-button>

<div v-for="item in messages">
  {{item.toUserId}}:{{item.text}}
</div>
</template>


<style scoped>

</style>