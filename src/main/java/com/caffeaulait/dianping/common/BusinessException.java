package com.caffeaulait.dianping.common;

public class BusinessException extends Exception{

    private CommonError error;

    public BusinessException(BusinessError businessError) {
        super();
        this.error = new CommonError(businessError);
    }

    public BusinessException(BusinessError businessError, String message){
        super();
        this.error = new CommonError(businessError);
        this.error.setMessage(message);
    }

    public CommonError getError() {
        return error;
    }

    public void setError(CommonError error) {
        this.error = error;
    }
}
