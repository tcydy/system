package com.example.fusionsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.fusionsystem.enity.Account;
import com.example.fusionsystem.enity.User;

public interface IUserService extends IService<User> {


    Account login(Account account);

    void register(Account account);

    void updatePassword(Account account);

}
