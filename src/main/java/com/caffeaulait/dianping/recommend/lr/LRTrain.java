package com.caffeaulait.dianping.recommend.lr;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.linalg.VectorUDT;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.caffeaulait.dianping.recommend.als.AlsRecallTrain.DATA_PATH;

public class LRTrain {

    private static final Logger logger = LoggerFactory.getLogger(LRTrain.class);

    public static void main(String[] args) throws IOException {
        SparkSession sparkSession = SparkSession.builder().master("local").appName("dianping").getOrCreate();

        JavaRDD<String> csvFile = sparkSession.read().textFile(String.format("file:///%s/feature.csv", DATA_PATH)).toJavaRDD();

        JavaRDD<Row> rowJavaRDD = csvFile.map((Function<String, Row>) s -> {
            s = s.replace("\"", "");
            String[] strArr = s.split(",");
            return RowFactory.create(new Double(strArr[11]),
                    Vectors.dense(Double.parseDouble(strArr[0]),
                            Double.parseDouble(strArr[1]),
                            Double.parseDouble(strArr[2]),
                            Double.parseDouble(strArr[3]),
                            Double.parseDouble(strArr[4]),
                            Double.parseDouble(strArr[5]),
                            Double.parseDouble(strArr[6]),
                            Double.parseDouble(strArr[7]),
                            Double.parseDouble(strArr[8]),
                            Double.parseDouble(strArr[9]),
                            Double.parseDouble(strArr[10])
                            ));
        });
        StructType schema = new StructType(new StructField[] {
                new StructField("label", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("features", new VectorUDT(), false, Metadata.empty())
        });
        Dataset<Row> data = sparkSession.createDataFrame(rowJavaRDD, schema);

        //80%用来训练 20%测试
        Dataset<Row>[] splits = data.randomSplit(new double[]{0.8, 0.2});
        Dataset<Row> trainData = splits[0];
        Dataset<Row> testData = splits[1];

        LogisticRegression lr = new LogisticRegression().setMaxIter(10)
                .setRegParam(0.3).setElasticNetParam(0.8).setFamily("multinomial");
        LogisticRegressionModel lrModel = lr.fit(trainData);

        lrModel.save(String.format("file:///%s/lrModel", DATA_PATH));

        Dataset<Row> predictions =  lrModel.transform(testData);

        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator();
        double accuracy = evaluator.setMetricName("accuracy").evaluate(predictions);

        logger.info("accuracy={}", accuracy);
    }
}
