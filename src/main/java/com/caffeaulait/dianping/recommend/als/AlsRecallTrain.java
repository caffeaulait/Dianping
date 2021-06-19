package com.caffeaulait.dianping.recommend.als;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.ml.evaluation.RegressionEvaluator;
import org.apache.spark.ml.recommendation.ALS;
import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.Serializable;


public class AlsRecallTrain implements Serializable {

    public static final String DATA_PATH = "Users/yang/IdeaProjects/dianping/src/main/resources/ml_data";

    private static final Logger logger = LoggerFactory.getLogger(AlsRecallTrain.class);

    public static void main(String[] args) throws IOException {
        //初始化spark环境
        SparkSession sparkSession = SparkSession.builder().master("local").appName("dianping").getOrCreate();
        JavaRDD<String> csvFile = sparkSession.read()
                .textFile(String.format("file:///%s/behavior.csv", DATA_PATH)).toJavaRDD();
        JavaRDD<Rating> ratingJavaRDD = csvFile.map(Rating::parseRating);
        Dataset<Row> ratings = sparkSession.createDataFrame(ratingJavaRDD, Rating.class);

        //80%用来训练 20%测试
        Dataset<Row>[] splits = ratings.randomSplit(new double[]{0.8, 0.2});
        Dataset<Row> trainData = splits[0];
        Dataset<Row> testData = splits[1];

        //若过拟合：增大数据量，减少rank，增大正则化系数
        ALS als = new ALS().setMaxIter(10).setRank(5).setRegParam(0.01)
                .setUserCol("userId").setItemCol("shopId").setRatingCol("rating");

        //训练
        ALSModel alsModel = als.fit(trainData);
        alsModel.save(String.format("file:///%s/alsModel", DATA_PATH));

        //预测
        Dataset<Row> predictions = alsModel.transform(testData);

        //rmse root mean squared error
        RegressionEvaluator evaluator = new RegressionEvaluator().setMetricName("rmse").setLabelCol("rating").setPredictionCol("prediction");
        double rmse = evaluator.evaluate(predictions);
        logger.info("rmse = {}", rmse);
    }
}
