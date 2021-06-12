package com.caffeaulait.dianping.common;

public class Result {

    private String status;

    private Object data;

    public static Result success(Object data) {
        return create("success", data);
    }

    public static Result fail(Object data){
        return create("fail", data);
    }

    public static Result create(String status, Object data) {
        return new Result(status, data);
    }

    public Result(String status, Object data) {
        this.status = status;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public Object getData() {
        return data;
    }
}
