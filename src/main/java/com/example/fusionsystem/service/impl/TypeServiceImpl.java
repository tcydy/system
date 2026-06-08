package com.example.fusionsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.fusionsystem.Mapper.BannerMapper;
import com.example.fusionsystem.Mapper.TypeMapper;
import com.example.fusionsystem.enity.Banner;
import com.example.fusionsystem.enity.Type;
import com.example.fusionsystem.service.IBannerService;
import com.example.fusionsystem.service.ITypeService;
import org.springframework.stereotype.Service;

@Service
public class TypeServiceImpl extends ServiceImpl<TypeMapper, Type> implements ITypeService {

}
