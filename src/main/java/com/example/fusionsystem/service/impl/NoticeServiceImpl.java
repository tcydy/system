package com.example.fusionsystem.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.fusionsystem.Mapper.NoticeMapper;
import com.example.fusionsystem.Mapper.UserMapper;
import com.example.fusionsystem.common.Constants;
import com.example.fusionsystem.enity.Account;
import com.example.fusionsystem.enity.Notice;
import com.example.fusionsystem.enity.User;
import com.example.fusionsystem.exception.ServiceException;
import com.example.fusionsystem.service.INoticeService;
import com.example.fusionsystem.service.IUserService;
import com.example.fusionsystem.utils.TokenUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements INoticeService {

}
