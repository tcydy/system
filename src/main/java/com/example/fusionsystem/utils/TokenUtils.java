package com.example.fusionsystem.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.fusionsystem.enity.Account;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.fusionsystem.service.IUserService;
import com.example.fusionsystem.service.IAdminService;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class TokenUtils {
    private static IUserService staticUserService;
    private  static IAdminService staticAdminService;

    @Resource
    private  IUserService userService;
    @Resource
    private  IAdminService adminService;

    //初始化方法，在类被实例化后执行
    @PostConstruct
    public void initService(){
        staticUserService = userService;
        staticAdminService = adminService;

    }


    /*生成token*/
    public static String createToken(String data,String sign)
    {
        return JWT.create().withAudience(data)//将userid-role保存到token里面作为载荷
                .withExpiresAt(DateUtil.offsetHour(new Date(),2))//2小时后toke过期
                .sign(Algorithm.HMAC256(sign));//以password作为token的密钥
    }
    /*获取当前登录的用户信息*/
    public static Account getCurrentUser(){
        try{
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token=request.getHeader("token");
            if(StrUtil.isNotEmpty(token))
            {
                String accountInfo=JWT.decode(token).getAudience().get(0);
                String accountId=accountInfo.split("-")[0];
                String accountRole=accountInfo.split("-")[1];

                Account account=null;
                if(StrUtil.equals(accountRole,"ROLE_USER")){
                    account=staticUserService.getById(Integer.valueOf(accountId));
                }
                if(StrUtil.equals(accountRole,"ROLE_ADMIN")){
                    account=staticAdminService.getById(Integer.valueOf(accountId));
                }
                //如果成功获取到用户信息，设置角色
                if(account!=null)
                {
                    account.setRole(accountRole);//设置父类中的角色字段
                }
                return account;//返回用户信息
            }
        }
        catch (Exception e){
            return null;//异常返回null
        }
        return new Account();
    }
}
