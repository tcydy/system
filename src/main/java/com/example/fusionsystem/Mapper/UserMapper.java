package com.example.fusionsystem.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.fusionsystem.enity.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/*
* mapper接口*/
public interface UserMapper extends BaseMapper<User> {

    @Select("select * from sys_user")
    List<User>getuserlist();
}
