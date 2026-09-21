package com.example.fusionsystem.enity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


import java.math.BigDecimal;

@Data
@TableName(value="goods")
public class Goods {
    @TableId(value="id",type= IdType.AUTO)
    private  Integer id;
    private String name;
    private String img;
    @TableField("img_list")
    private String imgList;
    @TableField("type_id")
    private Integer typeId;
    private BigDecimal price;
    @TableField("re_price")
    private BigDecimal rePrice;
    private String content;
    private String place;
    private String shipment;
    @TableField("user_id")
    private Integer userId;
    private Integer num;
    private String status;
    private String quality;
    private String date;

    @TableField(exist = false)
    private Boolean isCollected;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getImgList() {
        return imgList;
    }

    public void setImgList(String imgList) {
        this.imgList = imgList;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getRePrice() {
        return rePrice;
    }

    public void setRePrice(BigDecimal rePrice) {
        this.rePrice = rePrice;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getShipment() {
        return shipment;
    }

    public void setShipment(String shipment) {
        this.shipment = shipment;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Goods(Integer id, String name, String img, String imgList, Integer typeId, BigDecimal price, BigDecimal rePrice, String content, String place, String shipment, Integer userId, Integer num, String status, String quality, String date) {
        this.id = id;
        this.name = name;
        this.img = img;
        this.imgList = imgList;
        this.typeId = typeId;
        this.price = price;
        this.rePrice = rePrice;
        this.content = content;
        this.place = place;
        this.shipment = shipment;
        this.userId = userId;
        this.num = num;
        this.status = status;
        this.quality = quality;
        this.date = date;
    }

    public Goods() {
    }
}
