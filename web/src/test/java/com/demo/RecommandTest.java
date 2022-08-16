package com.demo;

import com.demo.client.HbaseClient;
import com.demo.client.RedisClient;
import com.demo.domain.ContactEntity;
import com.demo.domain.ProductEntity;
import com.demo.dto.ProductDto;
import com.demo.service.ContactService;
import com.demo.service.ProductService;
import com.demo.service.RecommandService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Fx
 * @date 2022-08-16 9:25
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class RecommandTest {
    @Resource
    private RedisClient redisClient;
    @Autowired
    private ProductService productService;
    @Autowired
    private ContactService contactService;
    @Autowired
    private RecommandService recommandService;

    private final int topSize = 10;
    private final int PRODUCT_LIMIT = 3;

    @Test
    public void testEntityService(){
        // 拿到产品热销表
        List<String> topList = redisClient.getTopList(10);
        // 拿到产品详情表
        List<ContactEntity> contactEntities = contactService.selectByIds(topList);
        // 拿到产品基本信息表
        List<ProductEntity> productEntities = productService.selectByIds(topList);

        System.out.println(topList);

        for(int i = 0; i < topSize; i++){
            System.out.print(contactEntities.get(i).getId()+" ");
        }
        System.out.println();

        for(int i = 0; i < topSize; i++){
            System.out.print(productEntities.get(i).getProductId()+" ");
        }
        System.out.println();
    }

    @Test
    public void testRecommandByHotList(){
        List<ProductDto> productDtos = recommandService.recommandByHotList();
        for(ProductDto productDto : productDtos){
            System.out.println(productDto.toString());
        }
    }

    @Test
    public void testSetPx() throws Exception {
        HbaseClient.putData("px","1", "p","2","0.7");
        HbaseClient.putData("px","1", "p","3","0.8");
        HbaseClient.putData("px","1", "p","4","0.1");
        HbaseClient.putData("px","1", "p","5","0.2");
    }

    @Test
    public void testGetPx() throws IOException {
        List<Map.Entry> px = HbaseClient.getRow("px","1");

//        int end = Math.min(px.size(), PRODUCT_LIMIT);
//        for(int i = 0; i < end; i++){
//            System.out.println(
//                "productId===>"+px.get(i).getKey()+
//                "  score===>"+px.get(i).getValue()
//            );
//        }
    }
    @Test
    public void testRecommandByItemCfCoeff() throws IOException {
        List<ProductDto>productDtos = recommandService.recomandByItemCfCoeff();
        for(ProductDto productDto : productDtos){
            System.out.println(
                    "score===>"+productDto.getScore()+
                    "  contactId===>"+productDto.getContact().getId()+
                    "  productId===>"+productDto.getProduct().getProductId()
            );
        }
    }
}
