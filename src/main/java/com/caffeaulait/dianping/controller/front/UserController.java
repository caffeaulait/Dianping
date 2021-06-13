package com.caffeaulait.dianping.controller.front;


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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;

@Controller("/user")
@RequestMapping("/user")
public class UserController {

    public static final String CURRENT_USER_SESSION = "CURRENT_USER_SESSION";

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Result getUser(@PathVariable Integer id) throws BusinessException {
        User user = userService.getUser(id);
        if (user == null) {
            throw new BusinessException(BusinessError.OBJECT_NOT_FOUND);
        } else {
            return Result.success(user);
        }
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
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

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Result login(@Valid @RequestBody LoginReq loginReq, BindingResult bindingResult) throws BusinessException, NoSuchAlgorithmException {
        if (bindingResult.hasErrors()) {
            throw new BusinessException(BusinessError.PARAMETER_VALIDATION_ERROR, CommonUtil.processError(bindingResult));
        }
        User user = userService.login(loginReq.getTelephone(), loginReq.getPassword());
        httpServletRequest.getSession().setAttribute(CURRENT_USER_SESSION, user);
        return Result.success(user);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public Result logout(){
        httpServletRequest.getSession().invalidate();
        return Result.success(null);
    }

    @RequestMapping(value = "/current", method = RequestMethod.GET)
    @ResponseBody
    public Result getCurrentUser(){
        User user = (User) httpServletRequest.getSession().getAttribute(CURRENT_USER_SESSION);
        return Result.success(user);
    }
}
