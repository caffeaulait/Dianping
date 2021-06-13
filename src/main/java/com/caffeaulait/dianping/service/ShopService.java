package com.caffeaulait.dianping.service;

import com.caffeaulait.dianping.common.BusinessException;
import com.caffeaulait.dianping.model.Shop;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ShopService {

    Shop create(Shop shop) throws BusinessException;

    Shop get(Integer id);

    List<Shop> selectAll();

    Integer countAll();

    List<Shop> recommend(BigDecimal longitude, BigDecimal latitude);

    List<Shop> search(BigDecimal longitude, BigDecimal latitude,
                      String keyword, Integer orderby, Integer categoryId,
                      String tags);

    List<Map<String, Object>> searchGroupByTags(String keyword,
                                                Integer categoryId,
                                                String tags);
}
