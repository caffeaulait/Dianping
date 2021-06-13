package com.caffeaulait.dianping.controller.front;

import com.caffeaulait.dianping.common.BusinessError;
import com.caffeaulait.dianping.common.BusinessException;
import com.caffeaulait.dianping.common.Result;
import com.caffeaulait.dianping.model.Category;
import com.caffeaulait.dianping.model.Shop;
import com.caffeaulait.dianping.service.CategoryService;
import com.caffeaulait.dianping.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller("/shop")
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @Autowired
    private CategoryService categoryService;

    @RequestMapping(value = "/recommend", method = RequestMethod.GET)
    @ResponseBody
    public Result recommend(@RequestParam BigDecimal longitude,
                            @RequestParam BigDecimal latitude) throws BusinessException {
        if (longitude == null || latitude == null) {
            throw new BusinessException(BusinessError.PARAMETER_VALIDATION_ERROR);
        }
        List<Shop> shops = shopService.recommend(longitude, latitude);
        return Result.success(shops);
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public Result search(@RequestParam BigDecimal longitude,
                         @RequestParam BigDecimal latitude,
                         @RequestParam String keyword,
                         @RequestParam(required = false) Integer orderby,
                         @RequestParam(required = false) Integer categoryId,
                         @RequestParam(required = false) String tags) throws BusinessException {
        if (StringUtils.isEmpty(keyword) || latitude == null || longitude == null) {
            throw new BusinessException(BusinessError.PARAMETER_VALIDATION_ERROR);
        }
        List<Shop> shops = shopService.search(longitude, latitude, keyword,
               orderby, categoryId, tags);
        List<Category> categories = categoryService.selectAll();
        List<Map<String,Object>> tagsAggregation =
                shopService.searchGroupByTags(keyword, categoryId, tags);
        Map<String, Object> map = new HashMap<>();
        map.put("shop", shops);
        map.put("category", categories);
        map.put("tags", tagsAggregation);
        return Result.success(map);
    }
}
