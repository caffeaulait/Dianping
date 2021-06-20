package com.caffeaulait.dianping.recommend;

import com.caffeaulait.dianping.model.ShopSortModel;
import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.SparkSession;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.caffeaulait.dianping.recommend.als.AlsRecallTrain.DATA_PATH;

@Service
public class RecommendSortService {

    private SparkSession sparkSession;

    private LogisticRegressionModel lrModel;

    @PostConstruct
    public void init() {
        //加载LR模型
        sparkSession = SparkSession.builder().master("local").appName("dianping").getOrCreate();
        lrModel = LogisticRegressionModel.load(String.format("file:///%s/lrModel", DATA_PATH));
    }

    public List<Integer> sort(List<Integer> shopIdList, Integer userId) {
        List<ShopSortModel> list = new ArrayList<>();
        for (Integer id : shopIdList) {
            //构造假的11维特征数据
            Vector vector = Vectors.dense(1, 0, 0, 0, 0, 1, 0.6, 0, 0, 1, 0);
            Vector result = lrModel.predictProbability(vector);
            double[] arr = result.toArray();
            double score = arr[1];
            ShopSortModel sortModel = new ShopSortModel();
            sortModel.setId(id);
            sortModel.setScore(score);
            list.add(sortModel);
        }
        list.sort((a,b) -> (int) (b.getScore() - a.getScore()));
        return list.stream().map(ShopSortModel::getId).collect(Collectors.toList());
    }
}
