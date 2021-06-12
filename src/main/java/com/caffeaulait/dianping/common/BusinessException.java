package com.caffeaulait.dianping.common;

public class BusinessException extends Exception{

    private CommonError error;

    public BusinessException(BusinessError businessError) {
        super();
        this.error = new CommonError(businessError);
    }

    public CommonError getError() {
        return error;
    }

    public void setError(CommonError error) {
        this.error = error;
    }
}
