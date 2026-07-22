package com.yhs.base.enums;

import lombok.Getter;

/**
 * 状态码
 *
 * @author 07664-linwei
 * @version Id: ExceptionCodeEnum.java, v 0.1 2022/4/20 9:44 lw Exp $
 */
@Getter
public enum ExceptionCodeEnum {

    /**
     * SQL执行异常 异常
     */
    SQL_EXCEPTION(8401, "SQL执行异常"),
    /**
     * redis 异常
     */
    REDIS_ERROR(7901, "redis 异常"),
    /**
     * redis 设置递增异常
     */
    REDIS_INCR(3001, "递增因子必须大于0"),
    /**
     * redis 设置递增异常
     */
    IDEMPOTENT(3002, "重复请求"),
    /**
     * 租户id 为空
     */
    TENANT_ID_IS_NULL(2001, "租户id为空");

    /**
     * code
     */
    private final int code;
    /**
     * msg
     */
    private final String msg;

    ExceptionCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
