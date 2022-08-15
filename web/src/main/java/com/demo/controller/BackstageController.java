package com.demo.controller;

import com.demo.client.RedisClient;
import com.demo.domain.ContactEntity;
import com.demo.service.ProductService;
import com.demo.util.Result;
import com.demo.util.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class BackstageController {

    private RedisClient redisClient = new RedisClient();

    private int topSize = 10;

    @Autowired
    ProductService productService;

    /**
     * 获取后台数据
     * @return json
     */
    @GetMapping("/index")
    public String getBackStage(Model model){
        // 获取 top 榜单数据
        List<String> topList = redisClient.getTopList(topSize);
        //System.out.println(topList);
        List<ContactEntity> topProduct = productService.selectByIds(topList);

        for(ContactEntity prod : topProduct){
            System.out.println(prod.getId());
        }

        model.addAttribute("topProduct", topProduct);
        return "index";
    }

    /**
     * 获取1小时内日志接入量
     * @return
     */
    @ResponseBody
    @GetMapping("/meter")
    public Result getMeter(){
        // 获取 1小时内接入量
        String meter = redisClient.getMeter();
        return ResultUtils.success(meter);
    }
}
