package com.caffeaulait.dianping.recommend.als;

import org.apache.commons.lang.StringUtils;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.ForeachPartitionFunction;
import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.caffeaulait.dianping.recommend.als.AlsRecallTrain.DATA_PATH;

public class AlsRecallPredict {


    public static void main(String[] args) {
        SparkSession sparkSession = SparkSession.builder().master("local").appName("dianping").getOrCreate();
        //加载模型
        ALSModel alsModel = ALSModel.load(String.format("file:///%s/alsModel", DATA_PATH));

        //给5个用户做召回结果预测
        JavaRDD<String> csvFile = sparkSession.read()
                .textFile(String.format("file:///%s/behavior.csv", DATA_PATH)).toJavaRDD();
        JavaRDD<Rating> ratingJavaRDD = csvFile.map(Rating::parseRating);
        Dataset<Row> ratings = sparkSession.createDataFrame(ratingJavaRDD, Rating.class);
        Dataset<Row> users = ratings.select(alsModel.getUserCol()).distinct().limit(5);
        Dataset<Row> userRecalls = alsModel.recommendForUserSubset(users, 20);
        userRecalls.foreachPartition((ForeachPartitionFunction<Row>) iterator -> {
            Connection connection = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/dianping?user=root&password=password&characterEncoding=utf8&useSSL=false&serverTimezone=UTC");
            PreparedStatement statement = connection.prepareStatement("insert into recommend(id, shops) values(?, ?)");
            List<Map<String, Object>> data = new ArrayList<>();
            iterator.forEachRemaining(action -> {
                int userId = action.getInt(0);
                List<GenericRowWithSchema> recommendList = action.getList(1);
                List<Integer> shopIdList = new ArrayList<>();
                recommendList.forEach(row -> {
                    int shopId = row.getInt(0);
                    shopIdList.add(shopId);
                });
                String shops = StringUtils.join(shopIdList, ",");
                Map<String, Object> map = new HashMap<>();
                map.put("id", userId);
                map.put("shops", shops);
                data.add(map);
            });
            data.forEach(el -> {
                try {
                    statement.setInt(1, (Integer) el.get("id"));
                    statement.setString(2, (String) el.get("shops"));
                    statement.addBatch();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
            statement.executeBatch();
            connection.close();
        });
    }
}
