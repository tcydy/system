package com.example.fusionsystem.enity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/*
实体类
* */
@Data
@TableName(value="notice")
public class Notice {

    @TableId(value="id",type= IdType.AUTO)
    private Integer id;

    private String name;

    private String info;

    private String time;

}
