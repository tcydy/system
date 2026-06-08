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
@TableName(value="sys_admin")
public class Admin extends Account {

    @TableId(value="id",type= IdType.AUTO)
    private Integer id;

    private String username;

    private String password;

    private String nickname;

    private String avatarUrl;

}
