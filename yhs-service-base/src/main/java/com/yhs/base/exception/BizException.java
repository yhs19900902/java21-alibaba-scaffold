package com.yhs.base.exception;


import com.yhs.base.pojo.vo.BusinessResponse;

/**
 * @author 04628-duanchengjun
 * @version Id: BizException.java, v 0.1 2019/4/25 9:30 duanchengjun Exp $
 */
public class BizException extends RuntimeException {

    private int code = BusinessResponse.RESPONSE_ERROR;
    private String message;

    public BizException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BizException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public BizException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
