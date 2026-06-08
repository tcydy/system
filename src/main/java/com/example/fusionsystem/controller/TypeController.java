package com.example.fusionsystem.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.fusionsystem.common.Result;
import com.example.fusionsystem.enity.Banner;
import com.example.fusionsystem.enity.Goods;
import com.example.fusionsystem.enity.Type;
import com.example.fusionsystem.service.IBannerService;
import com.example.fusionsystem.service.IGoodsService;
import com.example.fusionsystem.service.ITypeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/*前端控制器*/
@RestController
@RequestMapping("/type")
public class TypeController {
    @Resource
    private ITypeService typeService;
    @Resource
    private IGoodsService goodsService;

    @PostMapping
    public Result save(@RequestBody Type type) {
        return Result.success(typeService.saveOrUpdate(type));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){return Result.success(typeService.removeById(id));}

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){return Result.success(typeService.removeByIds(ids));}

    @GetMapping("/hot")
    public Result findAllHot(){

        LambdaQueryWrapper<Type> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Type::getStatus,true);
        wrapper.last("limit 4");

        List<Type> list = typeService.list(wrapper);
        for(Type type:list){
            LambdaQueryWrapper<Goods> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Goods::getTypeId,type.getId());
            queryWrapper.eq(Goods::getStatus,"上架");
            queryWrapper.orderByDesc(Goods::getNum);
            queryWrapper.last("limit 3");
            List<Goods>goodsList=goodsService.list(queryWrapper);
            type.setGoodsList(goodsList);
        }

        return Result.success(list);
    }



    @GetMapping
    public Result findAll(){return Result.success(typeService.list());}

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id){return Result.success(typeService.getById(id));}


    @GetMapping("/page")
    public  Result findPage(@RequestParam Integer pageNum,
                            @RequestParam Integer pageSize,
                            @RequestParam(defaultValue = "")String keyword){
        LambdaQueryWrapper<Type> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Type::getId);

        if(StrUtil.isNotBlank(keyword)){
            queryWrapper.like(Type::getName,keyword);
        }
        return Result.success(typeService.page(new Page<>(pageNum,pageSize),queryWrapper));
    }

}
