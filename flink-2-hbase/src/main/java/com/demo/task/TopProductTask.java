package com.demo.task;

import com.demo.agg.CountAgg;
import com.demo.entity.LogEntity;
import com.demo.entity.TopProductEntity;
import com.demo.map.TopProductMapFunction;
import com.demo.sink.TopNRedisSink;
import com.demo.top.TopNHotItems;
import com.demo.util.Property;
import com.demo.window.WindowResultFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;
import org.apache.flink.util.Collector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * 热门商品 -> redis
 *
 * @author XINZE
 */
public class TopProductTask {

    private static final int topSize = 5;

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 开启EventTime
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        FlinkJedisPoolConfig conf = new FlinkJedisPoolConfig.Builder().setHost("192.168.0.100").build();
        Properties properties = Property.getKafkaProperties("topProuct");
        DataStreamSource<String> dataStream = env.addSource(new FlinkKafkaConsumer<String>("con", new SimpleStringSchema(), properties));
        List<String> top = new ArrayList<>();
        DataStream<TopProductEntity> topProduct = dataStream
                //将字符串log转换成logEntity类
                .map(new TopProductMapFunction())
                //指定时间戳
                .assignTimestampsAndWatermarks(new AscendingTimestampExtractor<LogEntity>() {
                    @Override
                    public long extractAscendingTimestamp(LogEntity logEntity) {
                        return logEntity.getTime();
                    }
                })
                //根据产品Id进行分流
                .keyBy("productId")
                //滑动窗口
                .timeWindow(Time.seconds(60),Time.seconds(5))
                //预聚合，第一个参数：求操作次数总和 第二个参数：每个商品在每个窗口的点击量
                .aggregate(new CountAgg(), new WindowResultFunction())
                //不同商品但同一窗口的数据分到一起
                .keyBy("windowEnd")
                //在自定义函数中进行业务逻辑处理
                .process(new TopNHotItems(topSize))
                .flatMap((FlatMapFunction<List<String>, TopProductEntity>) (strings, collector) -> {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmm");
                    String time = sdf.format(new Date());
                    for (int i = 0; i < strings.size(); i++) {
                        TopProductEntity top1 = new TopProductEntity();
                        top1.setRankName(String.valueOf(i));
                        top1.setWindowEnd(new Long(time));
                        top1.setProductId(Integer.parseInt(strings.get(i)));
                    }
                });
        topProduct.addSink(new RedisSink<>(conf,new TopNRedisSink()));

        env.execute("Top N ");
    }
}
