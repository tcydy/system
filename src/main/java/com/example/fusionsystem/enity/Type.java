package com.example.fusionsystem.enity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

/*
实体类
* */
@Data
@TableName(value="type")
public class Type {

    @TableId(value="id",type= IdType.AUTO)
    private Integer id;

    private String name;

    private String info;

    private String icon;

    private String img;

    private Boolean status;

    @TableField(exist=false)
    private List<Goods> goodsList;


}
