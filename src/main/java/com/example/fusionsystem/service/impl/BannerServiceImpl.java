package com.example.fusionsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.fusionsystem.Mapper.BannerMapper;
import com.example.fusionsystem.Mapper.NoticeMapper;
import com.example.fusionsystem.enity.Banner;
import com.example.fusionsystem.enity.Notice;
import com.example.fusionsystem.service.IBannerService;
import com.example.fusionsystem.service.INoticeService;
import org.springframework.stereotype.Service;

@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements IBannerService {

}
