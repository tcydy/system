package com.example.fusionsystem.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.fusionsystem.common.Result;
import com.example.fusionsystem.enity.Goods;
import com.example.fusionsystem.service.IGoodsService;
import com.example.fusionsystem.utils.TokenUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Resource
    private IGoodsService goodsService;

    @PostMapping
    public Result save(@RequestBody Goods goods) {
        if(goods.getId()==null){
            goods.setUserId(TokenUtils.getCurrentUser().getId());
            goods.setNum(0);
            goods.setStatus("上架");

            goods.setDate(DateUtil.today());


        }
        return Result.success(goodsService.saveOrUpdate(goods));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(goodsService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(goodsService.removeByIds(ids));
    }

    @GetMapping("/hot")
    public Result findALLHot() {
        LambdaQueryWrapper<Goods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Goods::getNum);
        queryWrapper.last("limit 12");
        List <Goods> goodsList = goodsService.list(queryWrapper);
        return Result.success(goodsList);
    }

    @GetMapping
    public Result findAll() {
        LambdaQueryWrapper<Goods> goodsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        goodsLambdaQueryWrapper.orderByDesc(Goods::getNum);
        goodsLambdaQueryWrapper.eq(Goods::getStatus,"上架" );
        List<Goods> goodsList= goodsService.list(goodsLambdaQueryWrapper);
        return Result.success(goodsList);
    }


    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(goodsService.getById(id));
    }


    @GetMapping("/front/page")
    public  Result findFrontPage(@RequestParam Integer pageNum,
                                 @RequestParam Integer pageSize,
                                 @RequestParam Integer typeId,
                                 @RequestParam String sortBy,
                                 @RequestParam(defaultValue = "")String keyword) {
        LambdaQueryWrapper<Goods> queryWrapper = new LambdaQueryWrapper<>();

        if (StrUtil.equals(sortBy, "all")) {
            queryWrapper.orderByDesc(Goods::getNum);
        } else if (StrUtil.equals(sortBy, "date")) {
            queryWrapper.orderByDesc(Goods::getDate);
        } else if (StrUtil.equals(sortBy, "price")) {
            queryWrapper.orderByAsc(Goods::getPrice);
        }

        if (typeId != 0) {
            queryWrapper.eq(Goods::getTypeId, typeId);
        }

        if(StrUtil.isNotBlank(keyword)){
            queryWrapper.like(Goods::getName,keyword);
        }
        return Result.success(goodsService.page(new Page<>(pageNum,pageSize),queryWrapper));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String keyword) {
        LambdaQueryWrapper<Goods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Goods::getId);

        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.like(Goods::getName, keyword);
        }
        return Result.success(goodsService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }
}
