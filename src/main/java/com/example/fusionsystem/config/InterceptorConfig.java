package com.example.fusionsystem.config;

import com.example.fusionsystem.config.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.io.File;

@Configuration
public class InterceptorConfig extends WebMvcConfigurationSupport {

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/web/login","/web/register")
                .excludePathPatterns("/web/upload","web/download/**","/files/**");
        super.addInterceptors(registry);
    }

    @Bean
    public JwtInterceptor jwtInterceptor() {return new JwtInterceptor();}

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        //配置上传文件的静态资源映射
        String filePath=System.getProperty("user.dir")+ File.separator+"files"+File.separator;
        registry.addResourceHandler("/files/**").addResourceLocations("file:"+filePath);
    }

}
