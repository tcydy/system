package com.example.fusionsystem.service.impl;

import cn.hutool.core.bean.BeanUtil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.fusionsystem.Mapper.UserMapper;
import com.example.fusionsystem.common.Constants;
import com.example.fusionsystem.enity.Account;
import com.example.fusionsystem.enity.User;
import com.example.fusionsystem.exception.ServiceException;
import com.example.fusionsystem.service.IUserService;
import com.example.fusionsystem.utils.TokenUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Resource
   private UserMapper userMapper;


    @Override
    public Account login(Account account)throws ServiceException {
        //登录的话就是通过账号密码，查询是否有有这条数据
        LambdaQueryWrapper<User>wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, account.getUsername());
        User one=userMapper.selectOne(wrapper);
        //若one==null，也就是说明这条数据没查到，登录账号或者密码是错误的
        if(one==null){
            throw new ServiceException(Constants.CODE_605,"用户名或密码错误");
        }
        //使用BCrypt验证密码
        if(!cn.hutool.crypto.digest.BCrypt.checkpw(account.getPassword(),one.getPassword())){
            throw new ServiceException(Constants.CODE_605,"用户名或密码错误");
        }
        //登陆成功，返回用户信息
        String role="ROLE_USER";
        BeanUtils.copyProperties(one,account);
        String token=TokenUtils.createToken(one.getId()+"-"+role,one.getPassword());
        account.setToken(token);
        account.setRole(role);
        account.setPassword(null);
        return account;
    }

    @Override
    public void register(Account account) {
        //先保证注册前，用户名不重复
        LambdaQueryWrapper<User> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, account.getUsername());
        User one=userMapper.selectOne(wrapper);
        //为空，可以注册
        if(one==null)
        {
            one=new User();
            //bean工具，拷贝属性
            BeanUtil.copyProperties(account, one);
            //使用BCrypt加密密码
            one.setPassword(cn.hutool.crypto.digest.BCrypt.hashpw(one.getPassword(),cn.hutool.crypto.digest.BCrypt.gensalt()));

            userMapper.insert(one);
        }
        else {
            throw new ServiceException(Constants.CODE_605,"用户已存在");
        }
    }

    @Override
    public void updatePassword(Account account) {
        //查询用户
        LambdaQueryWrapper<User> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, account.getUsername());
        wrapper.eq(User::getPassword,account.getPassword());
        User user=userMapper.selectOne(wrapper);

        //验证
        if(user==null)
        {
            throw new ServiceException(Constants.CODE_605,"用户不存在");

        }
        //使用BCrypt验证原密码
        if(!cn.hutool.crypto.digest.BCrypt.checkpw(account.getPassword(),user.getPassword())){
            throw  new ServiceException(Constants.CODE_605,"原密码输入错误");

        }
        //加密新密码并更新
        user.setPassword(cn.hutool.crypto.digest.BCrypt.hashpw(account.getNewPassword(), cn.hutool.crypto.digest.BCrypt.gensalt()));
        int updateCount = userMapper.updateById(user);

        //检查更新结果
        if(updateCount==0)
        {
            throw new ServiceException(Constants.CODE_605,"原密码输入错误，请稍后再试");
        }
    }
}
