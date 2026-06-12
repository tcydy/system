package com.example.fusionsystem.controller;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.fusionsystem.common.Result;
import com.example.fusionsystem.enity.Account;
import com.example.fusionsystem.enity.Address;
import com.example.fusionsystem.enity.Goods;
import com.example.fusionsystem.enity.Orders;
import com.example.fusionsystem.service.IAddressService;
import com.example.fusionsystem.service.IGoodsService;
import com.example.fusionsystem.service.IOrdersService;
import com.example.fusionsystem.utils.TokenUtils;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Date;


/*前端控制器*/
@RestController
@RequestMapping("/orders")
public class OrdersController {
    @Resource
    private IOrdersService ordersService;

    @Resource
    private IGoodsService goodsService;

    @Resource
    private IAddressService addressService;

    @PostMapping
    public Result save(@RequestBody Orders orders) {
        if (orders.getId() == null) {

            orders.setNo(DateUtil.format(new Date(), "yyyyMMddHHmmss") + RandomUtil.randomNumbers(6));
            Goods goods = goodsService.getById(orders.getItemId());
            
            //拍快照保存商品信息到订单
            orders.setItemName(goods.getName());
            orders.setItemImg(goods.getImg());
            orders.setFromId(goods.getUserId());
            orders.setToId(TokenUtils.getCurrentUser().getId());
            orders.setPrice(goods.getPrice());
            orders.setTime(DateUtil.now());
            orders.setStatus("待支付");
            if (orders.getAddressId() == null) {
                orders.setAddress(orders.getAddress());
                orders.setInfo(orders.getInfo());
                orders.setName(orders.getName());
                orders.setPhone(orders.getPhone());
            }
            else {
                Address address = addressService.getById(orders.getAddressId());
                orders.setAddress(address.getAddress());
                orders.setInfo(address.getInfo());
                orders.setName(address.getName());
                orders.setPhone(address.getPhone());
            }
            
            goods.setStatus("已售出");//java层面修改
            goodsService.updateById(goods);//更新到数据库


        }
        return Result.success(ordersService.saveOrUpdate(orders));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){return Result.success(ordersService.removeById(id));}

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){return Result.success(ordersService.removeByIds(ids));}

    @GetMapping
    public Result findAll(){return Result.success(ordersService.list());}

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(ordersService.getById(id));
    }

    
    @GetMapping("/front/page")
    public Result findFrontPage(@RequestParam Integer pageNum,
                                @RequestParam Integer pageSize, 
                                @RequestParam String status,
                                @RequestParam String flag,
                                @RequestParam(defaultValue = "") String keyword){
        
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Orders::getId);

        //状态转换
        if (!StrUtil.equals(status, "全部")) {
            queryWrapper.eq(Orders::getStatus, status);
        }

        Account account = TokenUtils.getCurrentUser();
        
        if (StrUtil.equals(flag, "我卖出的")) {
            queryWrapper.eq(Orders::getFromId, getId());
        }

        if (StrUtil.equals(flag, "我买到的")) {
            queryWrapper.eq(Orders::getToId, getId());
        }

        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.like(Orders::getNo, keyword);
        }
        return Result.success(ordersService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }
    
    @GetMapping("/page")
    public  Result findPage(@RequestParam Integer pageNum,
                            @RequestParam Integer pageSize,
                            @RequestParam(defaultValue = "")String keyword){
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Orders::getId);

        if(StrUtil.isNotBlank(keyword)){
            queryWrapper.like(Orders::getNo,keyword);
        }
        return Result.success(ordersService.page(new Page<>(pageNum,pageSize),queryWrapper));
    }

}
