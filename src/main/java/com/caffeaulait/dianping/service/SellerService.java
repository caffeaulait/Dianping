package com.caffeaulait.dianping.service;

import com.caffeaulait.dianping.common.BusinessException;
import com.caffeaulait.dianping.model.Seller;

import java.util.List;

public interface SellerService {

    Seller create(Seller seller);

    Seller get(Integer id);

    List<Seller> selectAll();

    Seller changeStatus(Integer id, Integer flag) throws BusinessException;
}
