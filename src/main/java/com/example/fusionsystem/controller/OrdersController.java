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
import com.example.fusionsystem.service.IUserService;
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

    @Resource
    private IUserService userService;

    @PostMapping
    public Result save(@RequestBody Orders orders) {
        // 最大重试次数
        final int MAX_RETRY = 1000;
        int retryCount = 0;

        // ========== 新增模式：生成并校验唯一订单号 ==========
        if (orders.getId() == null) {
            String orderNo;

            // 循环生成单号，直到不重复 / 达到重试上限
            while (retryCount < MAX_RETRY) {
                // 生成订单号
                orderNo = DateUtil.format(new Date(), "yyyyMMddHHmmss") + RandomUtil.randomNumbers(6);
                orders.setNo(orderNo);

                // 先补全基础业务字段（避免空字段）
                Goods goods = goodsService.getById(orders.getItemId());
                orders.setItemName(goods.getName());
                orders.setItemImg(goods.getImg());
                orders.setFromId(goods.getUserId());

                //此用户不是管理员
                if (userService.getById(TokenUtils.getCurrentUser().getId()) != null) {
                    orders.setToId(TokenUtils.getCurrentUser().getId());
                }

                orders.setPrice(goods.getPrice());
                orders.setTime(DateUtil.now());
                orders.setStatus("待支付");

                if (orders.getAddressId() == null) {
                    orders.setAddress(orders.getAddress());
                    orders.setInfo(orders.getInfo());
                    orders.setName(orders.getName());
                    orders.setPhone(orders.getPhone());
                } else {
                    Address address = addressService.getById(orders.getAddressId());
                    orders.setAddress(address.getAddress());
                    orders.setInfo(address.getInfo());
                    orders.setName(address.getName());
                    orders.setPhone(address.getPhone());
                }

                // 尝试保存，捕获唯一索引冲突（单号重复）
                try {
                    ordersService.save(orders);
                    // 保存成功：单号唯一，跳出循环
                    break;
                } catch (Exception e) {
                    // 单号重复，清空ID继续重试
                    orders.setId(null);
                    retryCount++;
                }
            }

            // 修改商品状态
            Goods goods = goodsService.getById(orders.getItemId());
            goods.setStatus("已售出");
            goodsService.updateById(goods);
        }
        // ========== 编辑模式：id != null，直接更新，不重新生成单号 ==========
        else {
            // 编辑场景：订单号不允许修改，直接执行更新
            ordersService.updateById(orders);
        }

        return Result.success(orders);
    }

    @GetMapping("/user/{id}")
    public Result user(@PathVariable Integer id) {
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getFromId, id);
        wrapper.isNotNull(Orders::getToRate);
        wrapper.isNotNull(Orders::getToReview);
        return Result.success(ordersService.list(wrapper));
    }


    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){return Result.success(ordersService.removeById(id));}

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){return Result.success(ordersService.removeByIds(ids));}

    @GetMapping
    public Result findAll() {
        return Result.success(ordersService.list());
    }

    @GetMapping("/pay/{id}")
    public Result pay(@PathVariable Integer id) {
        Orders orders = ordersService.getById(id);
        orders.setStatus("待发货");
        ordersService.updateById(orders);
        return Result.success();
    }

    @GetMapping("/cancel/{id}")
    public Result cancel(@PathVariable Integer id) {
        Orders orders = ordersService.getById(id);
        orders.setStatus("交易关闭");
        ordersService.updateById(orders);

        Goods goods = goodsService.getById(orders.getItemId());
        goods.setStatus("已上架");
        goodsService.updateById(goods);
        return Result.success();
    }

    @GetMapping("/shipment/{id}")
    public Result shipment(@PathVariable Integer id) {
        Orders orders = ordersService.getById(id);
        orders.setStatus("待收货");
        ordersService.updateById(orders);
        return Result.success();
    }

    @PostMapping("/front")
    public Result saveJudge(@RequestBody Orders orders) {
        ordersService.updateById(orders);
        
        return Result.success(orders);
    }
    
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
            queryWrapper.eq(Orders::getFromId, account.getId());
        }

        if (StrUtil.equals(flag, "我买到的")) {
            queryWrapper.eq(Orders::getToId, account.getId());
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
