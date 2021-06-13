package com.caffeaulait.dianping.controller.front;

import com.caffeaulait.dianping.common.Result;
import com.caffeaulait.dianping.model.Category;
import com.caffeaulait.dianping.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller("/category")
@RequestMapping(value = "/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public Result listAll(){
        List<Category> categoryList = categoryService.selectAll();
        return Result.success(categoryList);
    }
}
