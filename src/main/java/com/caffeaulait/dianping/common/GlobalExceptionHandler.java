package com.caffeaulait.dianping.common;

import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result doError(HttpServletRequest request, HttpServletResponse response, Exception exception){
        if (exception instanceof BusinessException) {
            return Result.fail(((BusinessException) exception).getError());
        } else if (exception instanceof NoHandlerFoundException) {
            return Result.fail(new CommonError(BusinessError.NO_HANDLER_FOUND));
        } else if (exception instanceof ServletRequestBindingException) {
            return Result.fail(new CommonError(BusinessError.REQUEST_BIND_ERROR));
        } else {
            return Result.fail(new CommonError(BusinessError.UNKNOWN_ERROR));
        }
    }
}
