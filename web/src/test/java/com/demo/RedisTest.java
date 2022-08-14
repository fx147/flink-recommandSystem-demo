package com.demo;

import com.demo.client.RedisClient;
import org.junit.Test;

import java.util.List;

/**
 * @author Fx
 * @date 2022-08-14 14:46
 */
public class RedisTest {
    private final RedisClient redisClient = new RedisClient();
    String[] productIds = {"2","5","3","1","7","11","14","16","19","20"};
    @Test
    public void testRedis(){
        int topSize = 10;
        redisClient.putTopList(productIds);
        List<String> topList = redisClient.getTopList(topSize);
        System.out.println(topList);
    }

    @Test
    public void testPushData(){

        redisClient.pushData("topN",productIds);
    }

    @Test
    public void testGetData(){
        List<String> productIds = redisClient.getListData("topN",0,10);
        System.out.println(productIds);
    }


}
