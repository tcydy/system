package com.example.fusionsystem.enity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/*
实体类
* */
@Data

public class Account {
    private Integer id;

    private String username;

    private String password;

    private String nickname;

    private String avatarUrl;

    @TableField(exist=false)
    private String role;

    @TableField(exist=false)
    private  String newPassword;

    @TableField(exist=false)
    private  String token;


}
