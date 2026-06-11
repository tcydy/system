package com.example.fusionsystem.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.fusionsystem.Mapper.OrdersMapper;
import com.example.fusionsystem.enity.Orders;
import com.example.fusionsystem.service.IOrdersService;
import org.springframework.stereotype.Service;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements IOrdersService {

}
