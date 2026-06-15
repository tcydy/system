package com.example.fusionsystem.enity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/*
实体类
* */
@Data
@TableName(value="chat")
public class Chat {

    @TableId(value="id",type= IdType.AUTO)
    private Integer id;
    private String text;
    private String type;
    private String time;
    private Integer fromUserId;
    private Integer toUserId;
    private Boolean isRead;
}
