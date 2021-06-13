package com.caffeaulait.dianping.controller;


import com.caffeaulait.dianping.common.BusinessError;
import com.caffeaulait.dianping.common.BusinessException;
import com.caffeaulait.dianping.common.CommonUtil;
import com.caffeaulait.dianping.common.Result;
import com.caffeaulait.dianping.model.User;
import com.caffeaulait.dianping.request.LoginReq;
import com.caffeaulait.dianping.request.RegisterReq;
import com.caffeaulait.dianping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;

@Controller
@RequestMapping("/user")
public class UserController {

    public static final String CURRENT_USER_SESSION = "CURRENT_USER_SESSION";

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @GetMapping("/{id}")
    @ResponseBody
    public Result getUser(@PathVariable Integer id) throws BusinessException {
        User user = userService.getUser(id);
        if (user == null) {
            throw new BusinessException(BusinessError.OBJECT_NOT_FOUND);
        } else {
            return Result.success(user);
        }
    }

    @PostMapping("/register")
    @ResponseBody
    public Result register(@Valid @RequestBody RegisterReq registerReq,
                           BindingResult bindingResult) throws BusinessException,
            NoSuchAlgorithmException {
        if (bindingResult.hasErrors()) {
            throw new BusinessException(BusinessError.PARAMETER_VALIDATION_ERROR, CommonUtil.processError(bindingResult));
        }
        User user = new User();
        user.setTelephone(registerReq.getTelephone());
        user.setPassword(registerReq.getPassword());
        user.setNickName(registerReq.getNickName());
        user.setGender(registerReq.getGender());
        User data = userService.register(user);
        return Result.success(data);
    }

    @PostMapping("/login")
    @ResponseBody
    public Result login(@Valid @RequestBody LoginReq loginReq, BindingResult bindingResult) throws BusinessException, NoSuchAlgorithmException {
        if (bindingResult.hasErrors()) {
            throw new BusinessException(BusinessError.PARAMETER_VALIDATION_ERROR, CommonUtil.processError(bindingResult));
        }
        User user = userService.login(loginReq.getTelephone(), loginReq.getPassword());
        httpServletRequest.getSession().setAttribute(CURRENT_USER_SESSION, user);
        return Result.success(user);
    }

    @PostMapping("/logout")
    @ResponseBody
    public Result logout(){
        httpServletRequest.getSession().invalidate();
        return Result.success(null);
    }

    @GetMapping("/current")
    @ResponseBody
    public Result getCurrentUser(){
        User user = (User) httpServletRequest.getSession().getAttribute(CURRENT_USER_SESSION);
        return Result.success(user);
    }

    @RequestMapping("/index")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("/index.html");
        modelAndView.addObject("name", "test name");
        return modelAndView;
    }
}
