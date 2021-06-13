package com.caffeaulait.dianping.controller.admin;


import com.caffeaulait.dianping.common.*;
import com.caffeaulait.dianping.model.Category;
import com.caffeaulait.dianping.request.CategoryCreateReq;
import com.caffeaulait.dianping.request.PageQuery;
import com.caffeaulait.dianping.service.CategoryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

@Controller(value = "/admin/category")
@RequestMapping(value = "/admin/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    @AdminPermission
    public ModelAndView indexPage(PageQuery pageQuery) {
        PageHelper.startPage(pageQuery.getPage(), pageQuery.getSize());
        List<Category> categories = categoryService.selectAll();
        PageInfo<Category> data = new PageInfo<>(categories);
        ModelAndView modelAndView = new ModelAndView("/admin/category/index" +
                ".html");
        modelAndView.addObject("data", data);
        modelAndView.addObject("CONTROLLER_NAME", "category");
        modelAndView.addObject("ACTION_NAME", "index");
        return modelAndView;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public List<Category> getAll(){
        return categoryService.selectAll();
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    @AdminPermission
    public ModelAndView createPage(){
        ModelAndView modelAndView = new ModelAndView("/admin/category/create" +
                ".html");
        modelAndView.addObject("CONTROLLER_NAME", "category");
        modelAndView.addObject("ACTION_NAME", "create");
        return modelAndView;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @AdminPermission
    public String create(@Valid CategoryCreateReq categoryCreateReq,
                         BindingResult bindingResult) throws BusinessException {
        if (bindingResult.hasErrors()) {
            throw new BusinessException(BusinessError.PARAMETER_VALIDATION_ERROR, CommonUtil.processError(bindingResult));
        } else {
            Category category = new Category();
            category.setName(categoryCreateReq.getName());
            category.setIconUrl(categoryCreateReq.getIconUrl());
            category.setSort(categoryCreateReq.getSort());
            categoryService.create(category);
            return "redirect:/admin/category/index";
        }
    }

}
