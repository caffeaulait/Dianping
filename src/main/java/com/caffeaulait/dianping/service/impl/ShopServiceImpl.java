package com.caffeaulait.dianping.service.impl;

import com.caffeaulait.dianping.common.BusinessError;
import com.caffeaulait.dianping.common.BusinessException;
import com.caffeaulait.dianping.dao.ShopMapper;
import com.caffeaulait.dianping.model.Category;
import com.caffeaulait.dianping.model.Seller;
import com.caffeaulait.dianping.model.Shop;
import com.caffeaulait.dianping.service.CategoryService;
import com.caffeaulait.dianping.service.SellerService;
import com.caffeaulait.dianping.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SellerService sellerService;

    @Override
    public Shop create(Shop shop) throws BusinessException {
        Seller seller = sellerService.get(shop.getSellerId());
        if (seller == null) {
            throw new BusinessException(BusinessError.PARAMETER_VALIDATION_ERROR, "商户不存在");
        }
        if (seller.getDisabledFlag() == 1) {
            throw new BusinessException(BusinessError.PARAMETER_VALIDATION_ERROR, "商户已下架");
        }
        Category category = categoryService.get(shop.getCategoryId());
        if (category == null) {
            throw new BusinessException(BusinessError.PARAMETER_VALIDATION_ERROR, "类目不存在");
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
}
