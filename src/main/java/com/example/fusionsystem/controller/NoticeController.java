package com.example.fusionsystem.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.fusionsystem.common.Result;
import com.example.fusionsystem.enity.Notice;
import com.example.fusionsystem.service.INoticeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/*前端控制器*/
@RestController
@RequestMapping("/notice")
public class NoticeController {
    @Resource
    private INoticeService noticeService;

    @PostMapping
    public Result save(@RequestBody Notice notice) {
        return Result.success(noticeService.saveOrUpdate(notice));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){return Result.success(noticeService.removeById(id));}

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){return Result.success(noticeService.removeByIds(ids));}

    @GetMapping
    public Result findAll(){return Result.success(noticeService.list());}

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id){return Result.success(noticeService.getById(id));}

    @GetMapping("/page")
    public  Result findPage(@RequestParam Integer pageNum,
                            @RequestParam Integer pageSize,
                            @RequestParam(defaultValue = "")String keyword){
        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Notice::getId);

        if(StrUtil.isNotBlank(keyword)){
            queryWrapper.like(Notice::getName,keyword);
        }
        return Result.success(noticeService.page(new Page<>(pageNum,pageSize),queryWrapper));
    }

}
