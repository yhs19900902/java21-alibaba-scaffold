package com.yhs.encrypt.annotate;


import com.yhs.encrypt.enums.EncryptionSchemeEnum;

import java.lang.annotation.*;

/**
 * 加密注解
 *
 * @author 07664-linwei
 * @version Id: EncryptBody.java, v 0.1 2022/7/2 11:14 lw Exp $
 */
@Documented
@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Encrypt {

    /**
     * 秘钥
     *
     * @return 秘钥
     */
    String secretKey() default "";

    /**
     * 加密方式
     *
     * @return 加密方式
     */
    EncryptionSchemeEnum encryptionScheme() default EncryptionSchemeEnum.AES;


    /**
     * 加密添加时间戳
     *
     * @return 是否添加时间戳
     */
    boolean addTimestamp() default false;
}
