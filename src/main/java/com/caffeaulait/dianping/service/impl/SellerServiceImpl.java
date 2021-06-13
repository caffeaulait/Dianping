package com.caffeaulait.dianping.service.impl;

import com.caffeaulait.dianping.common.BusinessError;
import com.caffeaulait.dianping.common.BusinessException;
import com.caffeaulait.dianping.dao.SellerMapper;
import com.caffeaulait.dianping.model.Seller;
import com.caffeaulait.dianping.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SellerServiceImpl implements SellerService {

    @Autowired
    private SellerMapper sellerMapper;

    @Override
    @Transactional
    public Seller create(Seller seller) {
        seller.setRemarkScore(new BigDecimal(0));
        seller.setDisabledFlag(0);
        sellerMapper.insertSelective(seller);
        return get(seller.getId());
    }

    @Override
    public Seller get(Integer id) {
        return sellerMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Seller> selectAll() {
        return sellerMapper.selectAll();
    }

    @Override
    public Seller changeStatus(Integer id, Integer flag) throws BusinessException {
        Seller seller = get(id);
        if (seller == null) {
            throw new BusinessException(BusinessError.PARAMETER_VALIDATION_ERROR);
        }
        seller.setDisabledFlag(flag);
        sellerMapper.updateByPrimaryKeySelective(seller);
        return seller;
    }

    @Override
    public Integer countAll() {
        return sellerMapper.countAll();
    }
}
