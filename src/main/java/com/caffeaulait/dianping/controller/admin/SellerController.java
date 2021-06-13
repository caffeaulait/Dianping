package com.caffeaulait.dianping.controller.admin;

import com.caffeaulait.dianping.common.*;
import com.caffeaulait.dianping.model.Seller;
import com.caffeaulait.dianping.request.PageQuery;
import com.caffeaulait.dianping.request.SellerCreateReq;
import com.caffeaulait.dianping.service.SellerService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin/seller")
public class SellerController {

    @Autowired
    private SellerService sellerService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    @AdminPermission
    public ModelAndView indexPage(PageQuery pageQuery) {
        PageHelper.startPage(pageQuery.getPage(), pageQuery.getSize());
        List<Seller> sellers = sellerService.selectAll();
        PageInfo<Seller> data = new PageInfo<>(sellers);
        ModelAndView modelAndView = new ModelAndView("/admin/seller/index" +
                ".html");
        modelAndView.addObject("data", data);
        modelAndView.addObject("CONTROLLER_NAME", "seller");
        modelAndView.addObject("ACTION_NAME", "index");
        return modelAndView;
    }

    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    public List<Seller> getAll(){
        return sellerService.selectAll();
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    @AdminPermission
    public ModelAndView createPage(){
        ModelAndView modelAndView = new ModelAndView("/admin/seller/create" +
                ".html");
        modelAndView.addObject("CONTROLLER_NAME", "seller");
        modelAndView.addObject("ACTION_NAME", "create");
        return modelAndView;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @AdminPermission
    public String create(@Valid SellerCreateReq sellerCreateReq,
                         BindingResult bindingResult) throws BusinessException {
        if (bindingResult.hasErrors()) {
            throw new BusinessException(BusinessError.PARAMETER_VALIDATION_ERROR, CommonUtil.processError(bindingResult));
        } else {
            Seller seller = new Seller();
            seller.setName(sellerCreateReq.getName());
            sellerService.create(seller);
            return "redirect:/admin/seller/index";
        }
    }

    @RequestMapping(value = "/down", method = RequestMethod.POST)
    @AdminPermission
    @ResponseBody
    public Result down(@RequestParam Integer id) throws BusinessException {
        Seller seller = sellerService.changeStatus(id, 1);
        return Result.success(seller);
    }

    @RequestMapping(value = "/up", method = RequestMethod.POST)
    @AdminPermission
    @ResponseBody
    public Result up(@RequestParam Integer id) throws BusinessException {
        Seller seller = sellerService.changeStatus(id, 0);
        return Result.success(seller);
    }
}
