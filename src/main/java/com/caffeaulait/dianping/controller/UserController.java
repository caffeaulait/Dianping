package com.caffeaulait.dianping.controller;


import com.caffeaulait.dianping.common.BusinessError;
import com.caffeaulait.dianping.common.BusinessException;
import com.caffeaulait.dianping.common.CommonError;
import com.caffeaulait.dianping.common.Result;
import com.caffeaulait.dianping.model.User;
import com.caffeaulait.dianping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller("/user")
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    @ResponseBody
    public Result getUser(@PathVariable Integer id) throws BusinessException{
        User user =  userService.getUser(id);
        if (user == null) {
            throw new BusinessException(BusinessError.OBJECT_NOT_FOUND);
        } else {
            return Result.success(user);
        }
    }
}
