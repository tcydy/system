package com.example.fusionsystem.controller;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.fusionsystem.common.Result;
import com.example.fusionsystem.enity.Account;
import com.example.fusionsystem.enity.Chat;
import com.example.fusionsystem.enity.User;
import com.example.fusionsystem.service.IChatService;
import com.example.fusionsystem.service.IUserService;
import com.example.fusionsystem.utils.TokenUtils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import jakarta.annotation.Resource;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Resource
    private IChatService chatService;

    @Resource
    private IUserService userService;

    @PostMapping
    public Result save(@RequestBody Chat chat) {
        return Result.success(chatService.saveOrUpdate(chat));
    }

    @GetMapping("/clear")
    public Result clear(@RequestParam Integer fromUserId, @RequestParam Integer toUserId) {

        // 查询对方发给当前用户发的未读消息
        LambdaQueryWrapper<Chat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Chat::getFromUserId, toUserId);
        queryWrapper.eq(Chat::getToUserId, fromUserId);
        queryWrapper.eq(Chat::getIsRead, false);
        List<Chat> list = chatService.list(queryWrapper);

        // 更新对方发给当前用户的消息为已读
        list.stream().forEach(item -> item.setIsRead(true));

        //批量更新数据库
        chatService.updateBatchById(list);

        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(chatService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(chatService.removeByIds(ids));
    }

    @GetMapping("/messagehistory")
    public Result messages(@RequestParam Integer fromUserId,
                        @RequestParam Integer toUserId) {

        // 获取两个用户间的所有对话
        LambdaQueryWrapper<Chat> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(
            i -> i.eq(Chat::getFromUserId, fromUserId).eq(Chat::getToUserId, toUserId)
        ).or(
            i -> i.eq(Chat::getFromUserId, toUserId).eq(Chat::getToUserId, fromUserId)
        ).orderByAsc(Chat::getTime);

        List<Chat> chatList = chatService.list(wrapper);
        return Result.success(chatList);
    }

    @GetMapping("/user")
    public Result users() {
        Account account = TokenUtils.getCurrentUser();

        LambdaQueryWrapper<Chat> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(
            i -> i.eq(Chat::getFromUserId, account.getId())
        ).or(
            i -> i.eq(Chat::getToUserId, account.getId())
        ).orderByDesc(Chat::getTime);

        // 获取与当前用户有关的所有对话
        List<Chat> chatList = chatService.list(wrapper);

        // 记录不同用户有多少消息是发给自己并且还未读的
        Map<Integer, Long> unreadMap = chatList.stream()
            .filter(item -> ObjectUtil.equals(item.getToUserId(), account.getId()) && !item.getIsRead())
            .collect(Collectors.groupingBy(Chat::getFromUserId, Collectors.counting()));

        // 记录所有和自己聊过天的用户，可以使用LinkedHashSet保证有序
        Set<Integer> userIds = new LinkedHashSet<>();

        for (Chat chat : chatList) {
            userIds.add(chat.getFromUserId());
            userIds.add(chat.getToUserId());
        }

        // 排除自己
        userIds.remove(account.getId());

        // 如果没有对话过，直接返回
        if (CollectionUtil.isEmpty(userIds)) {
            return Result.success(new ArrayList<>());
        }
        
        // 和自己沟通过的所有用户
        List<User> userList = userService.listByIds(userIds);

        JSONArray array = new JSONArray();
        for (User user : userList) {
            JSONObject object = new JSONObject();
            object.set("id", user.getId());
            object.set("nickname", user.getNickname());
            object.set("avatarUrl", user.getAvatarUrl());
            object.set("count", unreadMap.getOrDefault(user.getId(), 0L));
            object.set("online", false);
            array.add(object);
        }
        return Result.success(array);
    }

    @GetMapping("/user/{id}")
    public Result user(@PathVariable Integer id) {
        User user = userService.getById(id);

        JSONObject object = new JSONObject();
        object.set("id", user.getId());
        object.set("nickname", user.getNickname());
        object.set("avatarUrl", user.getAvatarUrl());
        object.set("count", 0);
        object.set("online", false);

        return Result.success(object);
    }
    
    @GetMapping
    public Result findAll() {
        return Result.success(chatService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(chatService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                            @RequestParam Integer pageSize,
                            @RequestParam(defaultValue = "") String keyword) {

        LambdaQueryWrapper<Chat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Chat::getId);

        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.like(Chat::getText, keyword);
        }

        return Result.success(chatService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }
}
