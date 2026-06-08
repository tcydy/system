package com.example.fusionsystem.enity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;
import java.util.Map;

/*
实体类
* */
@Data
@TableName(value="sys_user")
public class User extends Account {

    @TableId(value="id",type= IdType.AUTO)

    private Integer id;

    private String username;

    private String password;

    private String nickname;

    private String avatarUrl;


}
