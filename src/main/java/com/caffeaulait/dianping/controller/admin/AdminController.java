package com.caffeaulait.dianping.controller.admin;

import com.caffeaulait.dianping.common.*;
import com.caffeaulait.dianping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.StringUtils;
import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;

@Controller("/admin/admin")
@RequestMapping("/admin/admin")
public class AdminController {

    @Value("${admin.email}")
    private String email;

    @Value("${admin.password}")
    private String password;

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    public static final String CURRENT_ADMIN_SESSION = "CURRENT_ADMIN_SESSION";

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    @AdminPermission
    public ModelAndView indexPage(){
        ModelAndView modelAndView = new ModelAndView("/admin/admin/index");
        modelAndView.addObject("CONTROLLER_NAME", "admin");
        modelAndView.addObject("ACTION_NAME", "index");
        modelAndView.addObject("userCount", userService.countAllUser());
        return modelAndView;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView loginPage(){
        ModelAndView modelAndView = new ModelAndView("/admin/admin/login");
        return modelAndView;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestParam String email,
                              @RequestParam String password) throws BusinessException, NoSuchAlgorithmException {
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(password)) {
            throw new BusinessException(BusinessError.PARAMETER_VALIDATION_ERROR, "用户名或密码不能为空");
        }
        if (this.email.equals(email) && this.password.equals(CommonUtil.encodePassword(password))) {
            httpServletRequest.getSession().setAttribute(CURRENT_ADMIN_SESSION, email);
            return "redirect:/admin/admin/index";
        } else {
            throw new BusinessException(BusinessError.PARAMETER_VALIDATION_ERROR, "用户名密码错误");
        }
    }
}
