package com.example.fusionsystem.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.fusionsystem.common.Result;
import com.example.fusionsystem.enity.Admin;
import com.example.fusionsystem.service.IAdminService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/*前端控制器*/
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Resource
    private IAdminService adminService;

    @PostMapping
    public Result save(@RequestBody Admin admin) {
        // 如果是新增(id为null)或者密码字段有值,则需要加密密码
        if (admin.getId() == null || (admin.getPassword() != null && !admin.getPassword().isEmpty())) {
            // 检查密码是否已经是BCrypt格式(以$2a$开头)
            if (admin.getPassword() != null && !admin.getPassword().startsWith("$2a$")) {
                // 使用BCrypt加密密码
                admin.setPassword(cn.hutool.crypto.digest.BCrypt.hashpw(admin.getPassword(), cn.hutool.crypto.digest.BCrypt.gensalt()));
            }
        }
        return Result.success(adminService.saveOrUpdate(admin));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){return Result.success(adminService.removeById(id));}

    @PostMapping("/del/batch")
    public  Result deleteBatch(@RequestBody List<Integer> ids){return Result.success(adminService.removeByIds(ids));}

    @GetMapping
    public Result findAll(){return Result.success(adminService.list());}

    @GetMapping("/{id}")
    public  Result findOne(@PathVariable Integer id){return Result.success(adminService.getById(id));}

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String keyword) {

        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Admin::getId);

        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.like(Admin::getNickname, keyword);
        }

        return Result.success(adminService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

}
