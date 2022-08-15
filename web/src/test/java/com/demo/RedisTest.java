package com.demo;

import com.demo.client.RedisClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Fx
 * @date 2022-08-14 14:46
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class RedisTest {
    @Resource
    private RedisClient redisClient;

    String[] productIds = {"2","5","3","1","7","11","14","16","19","20"};

    @Test
    public void testRedis(){
        int topSize = 10;
        redisClient.putTopList(productIds);
        redisClient.setData("meter","50");

        List<String> topList = redisClient.getTopList(topSize);
        System.out.println(topList);
    }

    @Test
    public void testPushData(){

        redisClient.pushListData("topN",productIds);
        List<String> topProductIds = redisClient.getListData("topN",0,9);
        System.out.println(topProductIds);
    }

    @Test
    public void testGetData(){
        List<String> productIds = redisClient.getListData("topN",0,9);
        System.out.println(productIds);
    }


}
