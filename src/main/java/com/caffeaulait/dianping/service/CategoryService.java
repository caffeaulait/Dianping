package com.caffeaulait.dianping.service;

import com.caffeaulait.dianping.common.BusinessException;
import com.caffeaulait.dianping.model.Category;

import java.util.List;

public interface CategoryService {

    Category create(Category category) throws BusinessException;

    Category get(Integer id);

    List<Category> selectAll();
}
