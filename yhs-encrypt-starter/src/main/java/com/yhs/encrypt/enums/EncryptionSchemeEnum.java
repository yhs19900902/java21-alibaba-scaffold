package com.yhs.encrypt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 加密方式
 *
 * @author 07664-linwei
 * @version Id: EncryptionSchemeEnum.java, v 0.1 2022/7/5 8:05 lw Exp $
 */
@Getter
@AllArgsConstructor
public enum EncryptionSchemeEnum {

    /**
     * aes对称加解密方式
     */
    AES("AES", "AES 对称加解密"),
    /**
     * DES 对称加解密方式
     */
    DES("DES", "des 对称加解密"),
    /**
     * RSA非对称加解密方式
     */
    RSA("RSA", "rsa 非对称加解密"),
    /**
     * ECIES 非对称加解密方式
     */
    ECIES("ECIES", "ECIES 非对称加解密"),
    /**
     * 国密 SM2 非对称加密方式
     */
    SM2("SM2", "SM2 非对称加解密"),
    /**
     * 国密SM4 对称加密方式
     */
    SM4("SM4", "SM4 对称加解密");

    private final String encryptType;


    private final String description;

}
