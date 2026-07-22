package com.yhs.encrypt.annotate;


import com.yhs.encrypt.enums.EncryptionSchemeEnum;

import java.lang.annotation.*;

/**
 * @author 07664-linwei
 * @version Id: DecryptBody.java, v 0.1 2022/7/5 15:24 lw Exp $
 */
@Documented
@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Decrypt {

    /**
     * 秘钥
     *
     * @return 秘钥
     */
    String secretKey() default "";

    /**
     * 加密方式
     * 使用rsa 加密使用全局秘钥
     *
     * @return 加密方式
     */
    EncryptionSchemeEnum decryptionScheme() default EncryptionSchemeEnum.AES;

    /**
     * 注意 使用数据有效时长验证，加密数据内容内必须包含timestamp字段
     * 超时 0为默认值 不设置时间
     * TIME_OUT = 60L * 1000L 时间为 60秒
     *
     * @return 超时时长 毫秒
     */
    long timeOut() default 0L;

}
