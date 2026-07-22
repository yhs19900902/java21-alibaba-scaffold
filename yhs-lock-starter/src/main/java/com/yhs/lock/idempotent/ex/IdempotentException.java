package com.yhs.lock.idempotent.ex;


import com.yhs.base.enums.ExceptionCodeEnum;
import com.yhs.base.exception.BizException;

/**
 * 接口幂等性异常
 *
 * @author 07664-linwei
 * @version Id: IdempotentException.java, v 0.1 2022/4/26 15:56 lw Exp $
 */
public class IdempotentException extends BizException {

    public IdempotentException(String info) {
        super(ExceptionCodeEnum.IDEMPOTENT.getCode(), info);
    }
}
