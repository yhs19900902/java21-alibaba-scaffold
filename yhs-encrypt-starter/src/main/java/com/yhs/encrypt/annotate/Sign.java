package com.yhs.encrypt.annotate;

import java.lang.annotation.*;

/**
 * @author 07664-linwei
 * @version Id: Sign.java, v 0.1 2022/7/7 11:58 lw Exp $
 */
@Documented
@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Sign {


    /**
     * 返回数据是否加签
     *
     * @return 是否加签
     */
    boolean outIsSign() default true;

    /**
     * 请求数据是否验签
     */
    boolean validSign() default true;

    /**
     * 注意 使用签名数据有效时长验证，加密数据内容内必须包含timestamp字段
     * 超时 0为默认值 不设置时间
     * TIME_OUT = 60L * 1000L 时间为 60秒
     *
     * @return 超时时长 毫秒
     */
    long timeOut() default 0L;
}
