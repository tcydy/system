package com.example.fusionsystem.controller;
    
import com.example.fusionsystem.common.Result;
import com.example.fusionsystem.enity.Goods;
import com.example.fusionsystem.enity.Type; // 假设Type实体在此包下，请根据实际路径调整
import com.example.fusionsystem.service.ITypeService;
import com.example.fusionsystem.service.IGoodsService; // 需确保存在此接口
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
    
import java.util.List;
    
@RestController
@RequestMapping("/echarts")
public class EchartsController {
        
    @Resource
    private ITypeService typeService;
        
    @Resource
    private IGoodsService goodsService;
        
    @GetMapping("/count")
    public Result count(){
        List<Type> typeList = typeService.list();
        List<Goods> goodsList = goodsService.list();
    
        JSONArray array = new JSONArray();
    
        for (Type type : typeList) {
            int count = 0;
            for (Goods goods : goodsList) {
                if (ObjectUtil.equals(goods.getTypeId(), type.getId())&&StrUtil.equals(goods.getStatus(), "上架")){
                        count++;
                }
            }
            JSONObject object = new JSONObject();
            object.set("value", count);
            object.set("name", type.getName());
            array.add(object);
        }
    
        return Result.success(array);
    }
}