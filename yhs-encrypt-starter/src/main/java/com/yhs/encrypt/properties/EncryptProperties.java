package com.yhs.encrypt.properties;

import com.yhs.encrypt.constant.EncryptConstant;
import com.yhs.encrypt.enums.EncryptionSchemeEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author 07664-linwei
 * @version Id: EncryptProperties.java, v 0.1 2022/7/5 8:28 lw Exp $
 */
@Data
@Configuration
@ConfigurationProperties(value = EncryptProperties.ENCRYPT_KEY)
public class EncryptProperties {

    public static final String ENCRYPT_KEY = "yhs.encrypt";

    /**
     * 全局默认加密方式 优先级小于注解
     */
    private EncryptionSchemeEnum encryptionSchemeEnum;
    /**
     * 全局默认加密字段
     */
    private String encryptionField = EncryptConstant.DATA_HEADER;
    /**
     * 全局默认解密字段
     */
    private String decryptionField = EncryptConstant.DATA_SECRET_HEADER;
    /**
     * aes 全局秘钥
     */
    private String aesSecretKey;
    /**
     * des 全局秘钥
     */
    private String desSecretKey;
    /**
     * rsa 全局私钥
     */
    private String rsaPrivateSecretKey;
    /**
     * rsa 全局 公钥
     */
    private String rsaPublicSecretKey;

    /**
     * ecies 全局私钥
     */
    private String eciesPrivateSecretKey;
    /**
     * ecies 全局 公钥
     */
    private String eciesPublicSecretKey;
    /**
     * SM2 全局私钥
     */
    private String sm2PrivateSecretKey;
    /**
     * SM2 全局 公钥
     */
    private String sm2PublicSecretKey;
    /**
     * sm4 全局秘钥
     */
    private String sm4SecretKey;
}
