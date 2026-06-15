package com.example.fusionsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.fusionsystem.Mapper.ChatMapper;
import com.example.fusionsystem.enity.Chat;
import com.example.fusionsystem.service.IChatService;

import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat> implements IChatService {

}
