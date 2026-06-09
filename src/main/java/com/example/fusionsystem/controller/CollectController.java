package com.example.fusionsystem.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.fusionsystem.common.Result;
import com.example.fusionsystem.enity.Collect;
import com.example.fusionsystem.service.IBannerService;
import com.example.fusionsystem.service.ICollectService;
import com.example.fusionsystem.utils.TokenUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/*前端控制器*/
@RestController
@RequestMapping("/collect")
public class CollectController {
    @Resource
    private ICollectService collectService;

    @PostMapping
    public Result save(@RequestBody Collect collect) {
        Integer userId = TokenUtils.getCurrentUser().getId();
        collect.setUserId(userId);
        try {
            collectService.saveOrUpdate(collect);
        } catch (Exception e) {
            LambdaQueryWrapper<Collect> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Collect::getUserId, userId);
            wrapper.eq(Collect::getItemId, collect.getItemId());
            collectService.remove(wrapper);
            return Result.error("685", "取消收藏成功");
        }
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){

        LambdaQueryWrapper<Collect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Collect::getUserId, TokenUtils.getCurrentUser().getId());
        wrapper.eq(Collect::getItemId, id);
        collectService.remove(wrapper);

        return Result.success(collectService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){return Result.success(collectService.removeByIds(ids));}

    @GetMapping
    public Result findAll(){return Result.success(collectService.list());}

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id){return Result.success(collectService.getById(id));}

    @GetMapping("/page")
    public  Result findPage(@RequestParam Integer pageNum,
                            @RequestParam Integer pageSize,
                            @RequestParam(defaultValue = "")String keyword){
        LambdaQueryWrapper<Collect> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Collect::getId);

        if(StrUtil.isNotBlank(keyword)){
            queryWrapper.like(Collect::getUserId,keyword);
        }
        return Result.success(collectService.page(new Page<>(pageNum,pageSize),queryWrapper));
    }

}
