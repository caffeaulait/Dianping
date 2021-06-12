package com.caffeaulait.dianping.common;

public enum BusinessError {
    OBJECT_NOT_FOUND(10001, "请求对象不存在"),
    UNKNOWN_ERROR(10002, "未知错误"),
    NO_HANDLER_FOUND(10003, "找不到执行路径"),
    REQUEST_BIND_ERROR(10004, "请求参数错误")
    ;

    private Integer code;

    private String message;

    BusinessError(Integer code, String message) {
        this.code = code;
        this.message = message;
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
