package com.example.fusionsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.fusionsystem.enity.Account;
import com.example.fusionsystem.enity.Admin;

public interface IAdminService extends IService<Admin> {


    Account login(Account account);

    void register(Account account);

    void updatePassword(Account account);

}
