package com.example.fusionsystem.common;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.hutool.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * websocket服务
 */
@ServerEndpoint(value = "/chatServer/{userId}")
@Component
public class WebSocketServer {

    //用于打印日志
    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);
    
    //记录当前在线连接数
    private static final Map<Integer, Session> sessionMap = new ConcurrentHashMap<>();

    //连接建立成功调用的方法
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Integer userId) {
        sessionMap.put(userId, session);
        log.info("用户加入, userId={}", userId);
    }

    //连接关闭调用的方法
    @OnClose
    public void onClose(Session session, @PathParam("userId") Integer userId) {
        sessionMap.remove(userId, session);
        log.info("用户退出, userId={}", userId);
    }

    /**
     * 收到客户端消息后调用的方法
     * 后台收到客户端发送过来的消息
     * onMessage 是一个消息的中转站
     * 接受 浏览器端 socket.send 发送过来的 json数据
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(Session session, String message, @PathParam("userId") Integer userId) {
        log.info("服务端收到用户userId={}的消息:{}", userId, message);

        JSONObject object = new JSONObject(message);
        Integer toUserId = object.getInt("toUserId");
        Session toSession = sessionMap.get(toUserId);
        sendMessage(toSession, message);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error(throwable.getMessage());
    }

    /**
     * 服务端发送消息给客户端
     */
    private void sendMessage(Session toSession, String message) {
        try {
            log.info("服务端给客户端[{}]发送消息{}", toSession.getId(), message);
            toSession.getBasicRemote().sendText(message);
        } catch (Exception e) {
            log.error("服务端发送消息给客户端失败", e);
        }
    }
}