package com.demo.client;

import com.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class RedisClient {
    private Jedis jedis = new Jedis("150.158.13.150", 6379);

    @Autowired
    private ProductService productService;


    @PostConstruct //通过@PostConstruct实现初始化bean之前进行的操作
    public void init() {
        RedisClient redisClient = this;
        redisClient.productService = this.productService;
    }

    private String getData(String key){
        return jedis.get(key);
    }
    
    public String setData(String key, String value){
        return jedis.set(key,value);
    }

    /**
     * 存储元素到列表中
     * @param listName
     * @param values
     */
    public void pushData(String listName, String[] values){
        jedis.lpush(listName,values);
    }

    /**
     * 获取列表中的数据
     * @param listname
     * @param start
     * @param stop
     * @return
     */
    public List<String> getListData(String listname, int start, int stop){
        return jedis.lrange(listname,start,stop);
    }

    public void putTopList(String[] productIds){
        for(int i = 0; i < productIds.length; i++){
            setData(String.valueOf(i), productIds[i]);
        }
    }

    public List<String> getTopList(int topRange){
        List<String> res = new ArrayList<>();

        for (int i = 0; i < topRange; i++) {
            res.add(getData(String.valueOf(i)));
        }
        return res;
    }


    public static void main(String[] args) {
        RedisClient client = new RedisClient();

        String data = client.getData("1");
        System.out.println(data);
    }
}
