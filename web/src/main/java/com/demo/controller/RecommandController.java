package com.demo.controller;

import com.demo.dto.ProductDto;
import com.demo.service.RecommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
public class RecommandController {

    @Autowired
    RecommandService recommandService;

    /**
     * 返回推荐页面
     * @param userId
     * @return
     * @throws IOException
     */
    @GetMapping("/recommand")
    public String recommandByUserId(@RequestParam("userId") String userId,
                                    Model model) throws IOException {

        // 拿到不同推荐方案的结果
        List<ProductDto> hotList = recommandService.recommandByHotList();
        List<ProductDto> itemCfCoeffList = recommandService.recomandByItemCfCoeff();
        List<ProductDto> productCoeffList = recommandService.recomandByProductCoeff();

        // 将结果返回给前端
        model.addAttribute("hotList",hotList);
        model.addAttribute("itemCfCoeffList", itemCfCoeffList);
        model.addAttribute("productCoeffList", productCoeffList);

        return "user";
    }

}
