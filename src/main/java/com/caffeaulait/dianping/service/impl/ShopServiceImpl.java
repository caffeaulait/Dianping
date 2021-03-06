package com.caffeaulait.dianping.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caffeaulait.dianping.common.BusinessError;
import com.caffeaulait.dianping.common.BusinessException;
import com.caffeaulait.dianping.dao.ShopMapper;
import com.caffeaulait.dianping.model.Category;
import com.caffeaulait.dianping.model.Seller;
import com.caffeaulait.dianping.model.Shop;
import com.caffeaulait.dianping.recommend.RecommendService;
import com.caffeaulait.dianping.recommend.RecommendSortService;
import com.caffeaulait.dianping.service.CategoryService;
import com.caffeaulait.dianping.service.SellerService;
import com.caffeaulait.dianping.service.ShopService;
import org.apache.http.util.EntityUtils;
import org.apache.spark.sql.sources.In;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ShopServiceImpl implements ShopService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SellerService sellerService;

    @Autowired
    private RecommendService recommendService;

    @Autowired
    private RecommendSortService recommendSortService;

    @Autowired
    private RestHighLevelClient client;

    @Override
    public Shop create(Shop shop) throws BusinessException {
        Seller seller = sellerService.get(shop.getSellerId());
        if (seller == null) {
            throw new BusinessException(BusinessError.PARAMETER_VALIDATION_ERROR, "???????????????");
        }
        if (seller.getDisabledFlag() == 1) {
            throw new BusinessException(BusinessError.PARAMETER_VALIDATION_ERROR, "???????????????");
        }
        Category category = categoryService.get(shop.getCategoryId());
        if (category == null) {
            throw new BusinessException(BusinessError.PARAMETER_VALIDATION_ERROR, "???????????????");
        }
        shopMapper.insertSelective(shop);
        return get(shop.getId());
    }

    @Override
    public Shop get(Integer id) {
        Shop shop = shopMapper.selectByPrimaryKey(id);
        if (shop == null) {
            return null;
        }
        shop.setSeller(sellerService.get(shop.getSellerId()));
        shop.setCategory(categoryService.get(shop.getCategoryId()));
        return shop;
    }

    @Override
    public List<Shop> selectAll() {
        List<Shop> shops = shopMapper.selectAll();
        shops.forEach(shop -> {
            shop.setSeller(sellerService.get(shop.getSellerId()));
            shop.setCategory(categoryService.get(shop.getCategoryId()));
        });
        return shops;
    }

    @Override
    public Integer countAll() {
        return shopMapper.countAll();
    }

//    @Override
//    public List<Shop> recommend(BigDecimal longitude, BigDecimal latitude) {
//        List<Shop> shops =  shopMapper.recommend(longitude, latitude);
//        shops.forEach(shop -> {
//            shop.setSeller(sellerService.get(shop.getSellerId()));
//            shop.setCategory(categoryService.get(shop.getCategoryId()));
//        });
//        return shops;
//    }

    public List<Shop> recommend(BigDecimal longitude, BigDecimal latitude) {
        int userId = 202020;
        List<Integer> shopIdList = recommendService.recall(userId);
        shopIdList = recommendSortService.sort(shopIdList, userId);
        List<Shop> shops = shopIdList.stream().map(this::get).collect(Collectors.toList());
        shops.forEach(shop -> {
            shop.setSeller(sellerService.get(shop.getSellerId()));
            shop.setCategory(categoryService.get(shop.getCategoryId()));
        });
        return shops;
    }

    @Override
    public List<Shop> search(BigDecimal longitude, BigDecimal latitude,
                             String keyword, Integer orderby,
                             Integer categoryId, String tags) {
        List<Shop> shops = shopMapper.search(longitude, latitude, keyword,
                orderby, categoryId, tags);
        shops.forEach(shop -> {
            shop.setSeller(sellerService.get(shop.getSellerId()));
            shop.setCategory(categoryService.get(shop.getCategoryId()));
        });
        return shops;
    }

    @Override
    public List<Map<String, Object>> searchGroupByTags(String keyword, Integer categoryId, String tags) {
        return shopMapper.searchGroupByTags(keyword, categoryId, tags);
    }

    @Override
    public Map<String, Object> searchES(BigDecimal longitude, BigDecimal latitude,
                                 String keyword, Integer orderby, Integer categoryId,
                                 String tags) throws IOException {
        Map<String, Object> result = new HashMap<>();
//        SearchRequest request = new SearchRequest("shop");
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        sourceBuilder.query(QueryBuilders.matchQuery("name", keyword));
//        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
//        request.source(sourceBuilder);
//        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
//        List<Integer> shopIds = new ArrayList<>();
//        SearchHit[] hits = response.getHits().getHits();
//        for (SearchHit hit : hits) {
//            shopIds.add(new Integer(hit.getSourceAsMap().get("id").toString()));
//        }
//        List<Shop> shops = shopIds.stream().map(this::get).collect(Collectors.toList());

        Request request = new Request("GET", "shop/_search");

        JSONObject requestObj = new JSONObject();
        // ??????source
        requestObj.put("_source", "*");
        // ?????????????????????
        requestObj.put("script_fields",new JSONObject());
        requestObj.getJSONObject("script_fields").put("distance",new JSONObject());
        requestObj.getJSONObject("script_fields").getJSONObject("distance").put("script",new JSONObject());
        requestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script")
                .put("source","haversin(lat, lon, doc['location'].lat, doc['location'].lon)");
        requestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script")
                .put("lang","expression");
        requestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script")
                .put("params",new JSONObject());
        requestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script")
                .getJSONObject("params").put("lat",latitude);
        requestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script")
                .getJSONObject("params").put("lon",longitude);
        requestObj.put("query",new JSONObject());
        

        //??????query
        Map<String, Object> keywordMap = analyzeCategoryKeyword(keyword);
        boolean isAffectingFilter = false;
        boolean isAffectingOrder = true;
        //??????function score
        requestObj.getJSONObject("query").put("function_score",new JSONObject());

        //??????function score??????query
        requestObj.getJSONObject("query").getJSONObject("function_score").put("query",new JSONObject());
        requestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").put("bool",new JSONObject());
        requestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool").put("must",new JSONArray());
        requestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                .getJSONArray("must").add(new JSONObject());

        logger.info("keyword size: " + keywordMap.size());
        int queryIndex = 0;
        if (!keywordMap.isEmpty() && isAffectingFilter) {

            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).put("bool",new JSONObject());
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool").put("should", new JSONArray());
            int filterQueryIndex = 0;
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool").getJSONArray("should").add(new JSONObject());
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool").getJSONArray("should").getJSONObject(filterQueryIndex)
                    .put("match",new JSONObject());
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool").getJSONArray("should").getJSONObject(filterQueryIndex)
                    .getJSONObject("match").put("name",new JSONObject());
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool").getJSONArray("should").getJSONObject(filterQueryIndex)
                    .getJSONObject("match").getJSONObject("name").put("query",keyword);
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool").getJSONArray("should").getJSONObject(filterQueryIndex)
                    .getJSONObject("match").getJSONObject("name").put("boost",0.1);
            for(String key : keywordMap.keySet()) {
                filterQueryIndex++;
                Integer id = (Integer) keywordMap.get(key);
                requestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                        .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool").getJSONArray("should").add(new JSONObject());
                requestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                        .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool").getJSONArray("should").getJSONObject(filterQueryIndex)
                        .put("term", new JSONObject());
                requestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                        .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool").getJSONArray("should").getJSONObject(filterQueryIndex)
                        .getJSONObject("term").put("category_id", new JSONObject());
                requestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                        .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool").getJSONArray("should").getJSONObject(filterQueryIndex)
                        .getJSONObject("term").getJSONObject("category_id").put("value", id);
                requestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                        .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("bool").getJSONArray("should").getJSONObject(filterQueryIndex)
                        .getJSONObject("term").getJSONObject("category_id").put("boost", 0);
            }
        } else {
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).put("match",new JSONObject());
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("match").put("name", new JSONObject());
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("match").getJSONObject("name").put("query",keyword);
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("match").getJSONObject("name").put("boost",0.1);
        }
        queryIndex++;
        //???????????????query?????????
        requestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                .getJSONArray("must").add(new JSONObject());
        requestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                .getJSONArray("must").getJSONObject(queryIndex).put("term",new JSONObject());
        requestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("term").put("seller_disabled_flag",0);

        //????????????
        if(tags != null){
            queryIndex++;
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").add(new JSONObject());
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).put("term",new JSONObject());
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("term").put("tags",tags);
        }
        //????????????
        if(categoryId != null){
            queryIndex++;
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").add(new JSONObject());
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).put("term",new JSONObject());
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("term").put("category_id",categoryId);
        }

        //??????functions??????
        int functionIndex = 0;
        requestObj.getJSONObject("query").getJSONObject("function_score").put("functions",new JSONArray());
        if(orderby == null) {
            //if?????????????????????
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").add(new JSONObject());
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("gauss", new JSONObject());
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("gauss").put("location", new JSONObject());
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("gauss")
                    .getJSONObject("location").put("origin", latitude.toString() + "," + longitude.toString());
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("gauss")
                    .getJSONObject("location").put("scale", "100km");
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("gauss")
                    .getJSONObject("location").put("offset", "0km");
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("gauss")
                    .getJSONObject("location").put("decay", "0.5");
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("weight", 9);

            functionIndex++;
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").add(new JSONObject());
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("field_value_factor", new JSONObject());
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("field_value_factor")
                    .put("field", "remark_score");
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("weight", 0.2);

            functionIndex++;
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").add(new JSONObject());
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("field_value_factor", new JSONObject());
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("field_value_factor")
                    .put("field", "seller_remark_score");
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("weight", 0.1);
            
            requestObj.getJSONObject("query").getJSONObject("function_score").put("score_mode","sum");
            requestObj.getJSONObject("query").getJSONObject("function_score").put("boost_mode","sum");

            if(keywordMap.keySet().size() > 0 && isAffectingOrder){
                for(String key : keywordMap.keySet()){
                    functionIndex++;
                    requestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").add(new JSONObject());
                    requestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("filter",new JSONObject());
                    requestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("filter").put("term",new JSONObject());
                    requestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("filter").getJSONObject("term").put("category_id",keywordMap.get(key));
                    requestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("weight",3);
                }

            }
        }else{
            //else??????????????????
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").add(new JSONObject());
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("field_value_factor",new JSONObject());
            requestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("field_value_factor")
                    .put("field","price_per_man");
            requestObj.getJSONObject("query").getJSONObject("function_score").put("score_mode","sum");
            requestObj.getJSONObject("query").getJSONObject("function_score").put("boost_mode","replace");
        }
        //????????????
        requestObj.put("sort",new JSONArray());
        requestObj.getJSONArray("sort").add(new JSONObject());
        requestObj.getJSONArray("sort").getJSONObject(0).put("_score",new JSONObject());
        if(orderby == null){
            requestObj.getJSONArray("sort").getJSONObject(0).getJSONObject("_score").put("order","desc");
        }else{
            requestObj.getJSONArray("sort").getJSONObject(0).getJSONObject("_score").put("order","asc");
        }

        //????????????
        requestObj.put("aggs",new JSONObject());
        requestObj.getJSONObject("aggs").put("group_by_tags",new JSONObject());
        requestObj.getJSONObject("aggs").getJSONObject("group_by_tags").put("terms",new JSONObject());
        requestObj.getJSONObject("aggs").getJSONObject("group_by_tags").getJSONObject("terms").put("field","tags");

        String reqJson = requestObj.toJSONString();
        request.setJsonEntity(reqJson);
        Response response = client.getLowLevelClient().performRequest(request);
        String responseStr = EntityUtils.toString(response.getEntity());
        JSONObject jsonObject = JSONObject.parseObject(responseStr);
        JSONArray jsonArr = jsonObject.getJSONObject("hits").getJSONArray("hits");
        List<Shop> shops = new ArrayList<>();
        for(int i = 0; i < jsonArr.size(); i++){
            JSONObject jsonObj = jsonArr.getJSONObject(i);
            Integer id = new Integer(jsonObj.get("_id").toString());
            BigDecimal distance = new BigDecimal(jsonObj.getJSONObject("fields").getJSONArray("distance").get(0).toString());
            Shop shop = get(id);
            shop.setDistance(distance.multiply(new BigDecimal(1000).setScale(0, BigDecimal.ROUND_CEILING)).intValue());
            shops.add(shop);
        }
        List<Map> tagsList = new ArrayList<>();
        JSONArray tagsJsonArray = jsonObject.getJSONObject("aggregations").getJSONObject("group_by_tags").getJSONArray("buckets");
        for(int i = 0; i < tagsJsonArray.size();i++){
            JSONObject jsonObj = tagsJsonArray.getJSONObject(i);
            Map<String,Object> tagMap = new HashMap<>();
            tagMap.put("tags",jsonObj.getString("key"));
            tagMap.put("num",jsonObj.getInteger("doc_count"));
            tagsList.add(tagMap);
        }
        result.put("tags",tagsList);
        result.put("shops", shops);
        return result;
    }

    private Map<Integer, List<String>> categoryMap = new HashMap<>();

    //?????????????????????
    @PostConstruct
    public void init() {
        categoryMap.put(1, new ArrayList<>());
        categoryMap.put(2, new ArrayList<>());
        categoryMap.get(1).add("??????");
        categoryMap.get(1).add("?????????");

        categoryMap.get(2).add("??????");
        categoryMap.get(2).add("??????");
        categoryMap.get(2).add("??????");
    }

    private Integer getCategoryIdByToken(String token){
        for (Integer k : categoryMap.keySet()) {
            List<String> tokens = categoryMap.get(k);
            if (tokens.contains(token)) {
                return k;
            }
        }
        return null;
    }

    private Map<String,Object> analyzeCategoryKeyword(String keyword) throws IOException {
        Map<String,Object> res = new HashMap<>();

        Request request = new Request("GET","/shop/_analyze");
        JSONObject requestObj = new JSONObject();
        requestObj.put("field", "name");
        requestObj.put("text", keyword);
        request.setJsonEntity(requestObj.toJSONString());
        Response response = client.getLowLevelClient().performRequest(request);
        String responseStr = EntityUtils.toString(response.getEntity());
        JSONObject jsonObject = JSONObject.parseObject(responseStr);
        JSONArray jsonArray = jsonObject.getJSONArray("tokens");
        for(int i = 0; i < jsonArray.size(); i++){
            String token = jsonArray.getJSONObject(i).getString("token");
            Integer categoryId = getCategoryIdByToken(token);
            if(categoryId != null){
                res.put(token,categoryId);
            }
        }
        return res;
    }
}
