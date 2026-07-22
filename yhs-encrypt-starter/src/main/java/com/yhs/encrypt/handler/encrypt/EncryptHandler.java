package com.yhs.encrypt.handler.encrypt;


import com.yhs.encrypt.enums.EncryptionSchemeEnum;
import com.yhs.encrypt.properties.EncryptProperties;

/**
 * 加解密接口
 *
 * @author 07664-linwei
 * @version Id: EncryptHandler.java, v 0.1 2022/7/5 8:20 lw Exp $
 */
public interface EncryptHandler {


    /**
     * 是否支持加密类型
     *
     * @param schemeEnum 加密类型
     * @return 是否支持加密类型
     */
    boolean support(EncryptionSchemeEnum schemeEnum);

    /**
     * 加密
     *
     * @param content           内容
     * @param secretKey         秘钥
     * @param encryptProperties 全局配置
     * @return 加密后的字符串
     */
    String encrypt(String content, String secretKey, EncryptProperties encryptProperties);

    /**
     * 解密
     *
     * @param content           加密后的字符串
     * @param secretKey         秘钥
     * @param encryptProperties 全局配置
     * @return 原始数据
     */
    String decrypt(String content, String secretKey, EncryptProperties encryptProperties);
}
