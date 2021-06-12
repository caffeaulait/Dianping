package com.caffeaulait.dianping.common;

public class CommonError {

    private Integer code;

    private String message;

    public CommonError(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public CommonError(BusinessError error) {
        this.code = error.getCode();
        this.message = error.getMessage();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
