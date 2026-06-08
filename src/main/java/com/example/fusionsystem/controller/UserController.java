package com.example.fusionsystem.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.fusionsystem.common.Result;
import com.example.fusionsystem.enity.User;
import com.example.fusionsystem.service.IUserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/*前端控制器*/
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private IUserService userService;

    @PostMapping
    public Result save(@RequestBody User user) {
        // 如果是新增(id为null)或者密码字段有值,则需要加密密码
        if (user.getId() == null || (user.getPassword() != null && !user.getPassword().isEmpty())) {
            // 检查密码是否已经是BCrypt格式(以$2a$开头)
            if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
                // 使用BCrypt加密密码
                user.setPassword(cn.hutool.crypto.digest.BCrypt.hashpw(user.getPassword(), cn.hutool.crypto.digest.BCrypt.gensalt()));
            }
        }
        return Result.success(userService.saveOrUpdate(user));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){return Result.success(userService.removeById(id));}

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){return Result.success(userService.removeByIds(ids));}

    @GetMapping
    public Result findAll(){return Result.success(userService.list());}

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id){return Result.success(userService.getById(id));}

    @GetMapping("/page")
    public  Result findPage(@RequestParam Integer pageNum,
                            @RequestParam Integer pageSize,
                            @RequestParam(defaultValue = "")String keyword){
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(User::getId);

        if(StrUtil.isNotBlank(keyword)){
            queryWrapper.like(User::getNickname,keyword);
        }
        return Result.success(userService.page(new Page<>(pageNum,pageSize),queryWrapper));
    }

}
