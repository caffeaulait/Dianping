package com.caffeaulait.dianping.service;

import com.caffeaulait.dianping.common.BusinessException;
import com.caffeaulait.dianping.model.Shop;

import java.util.List;

public interface ShopService {

    Shop create(Shop shop) throws BusinessException;

    Shop get(Integer id);

    List<Shop> selectAll();

    Integer countAll();
}
