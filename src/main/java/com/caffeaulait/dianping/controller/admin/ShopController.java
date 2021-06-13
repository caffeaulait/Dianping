package com.caffeaulait.dianping.controller.admin;

import com.caffeaulait.dianping.common.AdminPermission;
import com.caffeaulait.dianping.common.BusinessError;
import com.caffeaulait.dianping.common.BusinessException;
import com.caffeaulait.dianping.common.CommonUtil;
import com.caffeaulait.dianping.model.Shop;
import com.caffeaulait.dianping.request.PageQuery;
import com.caffeaulait.dianping.request.ShopCreateReq;
import com.caffeaulait.dianping.service.ShopService;
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

@Controller("/admin/shop")
@RequestMapping(value = "/admin/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    @AdminPermission
    public ModelAndView indexPage(PageQuery pageQuery) {
        PageHelper.startPage(pageQuery.getPage(), pageQuery.getSize());
        List<Shop> shops = shopService.selectAll();
        PageInfo<Shop> data = new PageInfo<>(shops);
        ModelAndView modelAndView = new ModelAndView("/admin/shop/index" +
                ".html");
        modelAndView.addObject("data", data);
        modelAndView.addObject("CONTROLLER_NAME", "shop");
        modelAndView.addObject("ACTION_NAME", "index");
        return modelAndView;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public List<Shop> getAll(){
        return shopService.selectAll();
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    @AdminPermission
    public ModelAndView createPage(){
        ModelAndView modelAndView = new ModelAndView("/admin/shop/create" +
                ".html");
        modelAndView.addObject("CONTROLLER_NAME", "shop");
        modelAndView.addObject("ACTION_NAME", "create");
        return modelAndView;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @AdminPermission
    public String create(@Valid ShopCreateReq shopCreateReq,
                         BindingResult bindingResult) throws BusinessException {
        if (bindingResult.hasErrors()) {
            throw new BusinessException(BusinessError.PARAMETER_VALIDATION_ERROR, CommonUtil.processError(bindingResult));
        } else {
            Shop shop = new Shop();
            shop.setIconUrl(shopCreateReq.getIconUrl());
            shop.setAddress(shopCreateReq.getAddress());
            shop.setCategoryId(shopCreateReq.getCategoryId());
            shop.setStartTime(shopCreateReq.getStartTime());
            shop.setEndTime(shopCreateReq.getEndTime());
            shop.setLongitude(shopCreateReq.getLongitude());
            shop.setLatitude(shopCreateReq.getLatitude());
            shop.setName(shopCreateReq.getName());
            shop.setPricePerMan(shopCreateReq.getPricePerMan());
            shop.setSellerId(shopCreateReq.getSellerId());
            shopService.create(shop);
            return "redirect:/admin/shop/index";
        }
    }
}
