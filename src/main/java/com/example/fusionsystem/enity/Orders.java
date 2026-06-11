package com.example.fusionsystem.enity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("orders")
public class Orders {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private String no;
    private Integer itemId;
    private String itemName;
    private String itemImg;
    private Integer fromId;
    private Integer toId;
    private BigDecimal price;
    private String time;
    private String status;
    private Integer toRate;
    private String toReview;
    private String address;
    private String info;
    private String name;
    private String phone;

    @TableField(exist = false)
    private Integer addressId;

}
