package com.yhs.encrypt.handler.encrypt;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.yhs.encrypt.enums.EncryptionSchemeEnum;
import com.yhs.encrypt.properties.EncryptProperties;
import com.yhs.encrypt.utils.CheckUtils;
import org.springframework.stereotype.Component;

/**
 * aes 加解密
 *
 * @author 07664-linwei
 * @version Id: AesEncryptionHandler.java, v 0.1 2022/7/5 14:13 lw Exp $
 */
@Component
public class AesEncryptionHandler implements EncryptHandler {

    @Override
    public boolean support(EncryptionSchemeEnum schemeEnum) {
        return EncryptionSchemeEnum.AES == schemeEnum;
    }

    @Override
    public String encrypt(String content, String secretKey, EncryptProperties encryptProperties) {
        String aesSecretKey = CheckUtils.checkAndGetKey(secretKey, encryptProperties.getAesSecretKey(), "AES 加密秘钥");
        return getAes(aesSecretKey).encryptBase64(content);
    }

    @Override
    public String decrypt(String content, String secretKey, EncryptProperties encryptProperties) {
        String aesSecretKey = CheckUtils.checkAndGetKey(secretKey, encryptProperties.getAesSecretKey(), "AES 加密秘钥");
        return getAes(aesSecretKey).decryptStr(content, CharsetUtil.CHARSET_UTF_8);
    }


    private AES getAes(String secretKey) {
        return SecureUtil.aes(secretKey.getBytes(CharsetUtil.CHARSET_UTF_8));
    }

}
