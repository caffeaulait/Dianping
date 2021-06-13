package com.caffeaulait.dianping.service.impl;

import com.caffeaulait.dianping.common.BusinessError;
import com.caffeaulait.dianping.common.BusinessException;
import com.caffeaulait.dianping.dao.CategoryMapper;
import com.caffeaulait.dianping.model.Category;
import com.caffeaulait.dianping.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public Category create(Category category) throws BusinessException {
        try {
            categoryMapper.insertSelective(category);
        }catch (DuplicateKeyException e) {
            throw new BusinessException(BusinessError.CATEGORY_NAME_DUPLICATE);
        }
        return get(category.getId());
    }

    @Override
    public Category get(Integer id) {
        return categoryMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Category> selectAll() {
        return categoryMapper.selectAll();
    }
}
