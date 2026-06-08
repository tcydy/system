package com.example.fusionsystem.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.fusionsystem.Mapper.GoodsMapper;
import com.example.fusionsystem.enity.Goods;
import com.example.fusionsystem.service.IGoodsService;
import org.springframework.stereotype.Service;

@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

}
