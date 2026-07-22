package com.yhs.database.exception;

import com.yhs.base.enums.ExceptionCodeEnum;
import com.yhs.base.exception.BizException;

/**
 * @author 07664-linwei
 * @version Id: TenantException.java, v 0.1 2022/4/20 15:39 lw Exp $
 */
public class TenantException extends BizException {


    public TenantException(int code, String message) {
        super(code, message);
    }

    public TenantException(ExceptionCodeEnum exceptionCode) {
        super(exceptionCode.getCode(), exceptionCode.getMsg());
    }


    public TenantException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public TenantException(int code, Throwable cause) {
        super(code, cause);
    }
}
