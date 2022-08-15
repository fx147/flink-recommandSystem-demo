package com.demo.scheduler;

import com.demo.client.HbaseClient;
import com.demo.client.MysqlClient;
import com.demo.client.RedisClient;
import com.demo.domain.ProductPortraitEntity;
import com.demo.util.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 基于产品标签的产品相关度计算
 * @author XINZE
 */
public class ProductCoeff {

    private RedisClient redis = new RedisClient();
    private MysqlClient mysql = new MysqlClient();


    /**
     * 计算一个产品和其他相关产品的评分,并将计算结果放入Hbase
     * @param id 产品id
     * @param others 其他产品的id
     */
    public void getSingelProductCoeff(String id, String[] others) throws Exception {

        List<Map<String,Double>> scores = new ArrayList<>();

        ProductPortraitEntity product = sigleProduct(id);
        for (String proId: others) {
            ProductPortraitEntity entity = sigleProduct(proId);
            Double score = getScore(product, entity);
            HbaseClient.putData("ps",id, "p",proId,score.toString());
        }
    }

    /**
     * 获取一个产品的所有标签数据
     * @param proId 产品id
     * @return 产品标签entity
     * @throws IOException
     */
    private ProductPortraitEntity sigleProduct(String proId) throws IOException {

        ProductPortraitEntity entity = new ProductPortraitEntity();

        String woman = HbaseClient.getData("prod", proId, "sex", Constants.SEX_WOMAN);
        String man = HbaseClient.getData("prod", proId, "sex", Constants.SEX_MAN);
        String age_10 = HbaseClient.getData("prod", proId, "sex", Constants.AGE_10);
        String age_20 = HbaseClient.getData("prod", proId, "sex", Constants.AGE_20);
        String age_30 = HbaseClient.getData("prod", proId, "sex", Constants.AGE_30);
        String age_40 = HbaseClient.getData("prod", proId, "sex", Constants.AGE_40);
        String age_50 = HbaseClient.getData("prod", proId, "sex", Constants.AGE_50);
        String age_60 = HbaseClient.getData("prod", proId, "sex", Constants.AGE_60);

        entity.setMan(Integer.parseInt(man));
        entity.setWoman(Integer.parseInt(woman));
        entity.setAge_10(Integer.parseInt(age_10));
        entity.setAge_20(Integer.parseInt(age_20));
        entity.setAge_30(Integer.parseInt(age_30));
        entity.setAge_40(Integer.parseInt(age_40));
        entity.setAge_50(Integer.parseInt(age_50));
        entity.setAge_60(Integer.parseInt(age_60));
        return entity;

    }

    /**
     * 根据标签计算两个产品之间的相关度
     * @param product 产品
     * @param target 相关产品
     * @return
     */
    private Double getScore(ProductPortraitEntity product, ProductPortraitEntity target){
        double sqrt = Math.sqrt(product.getTotal() + target.getTotal());
        if (sqrt == 0){
            return 0.0;
        }
        int total = product.getMan()*target.getMan() + product.getWoman()*target.getWoman() + product.getAge_10()*target.getAge_10()
                + product.getAge_20()*target.getAge_20() + product.getAge_30()*target.getAge_30() + product.getAge_40()*target.getAge_40()
                + product.getAge_50()*target.getAge_50() + product.getAge_60()*target.getAge_60();

        return Math.sqrt(total) / sqrt;
    }

    public static void main(String[] args) throws IOException {
        String data = HbaseClient.getData("prod", "2", "sex","2");
        System.out.println(data);
    }


}
