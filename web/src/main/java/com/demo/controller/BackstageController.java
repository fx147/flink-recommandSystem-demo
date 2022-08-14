package com.demo.controller;

import com.demo.client.RedisClient;
import com.demo.domain.ProductEntity;
import com.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class BackstageController {

    private final RedisClient redisClient = new RedisClient();

    @Autowired
    ProductService productService;

    /**
     * 获取后台数据
     * @return json
     */
    @GetMapping
    public String getBackStage(Model model){
        int topSize = 10;
        List<String> topList = redisClient.getTopList(topSize);
        List<ProductEntity> topProduct = productService.selectByIds(topList);
        model.addAttribute("topProduct", topProduct);
        return "index";
    }

}
