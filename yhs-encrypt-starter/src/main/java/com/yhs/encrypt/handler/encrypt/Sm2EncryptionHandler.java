package com.yhs.encrypt.handler.encrypt;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import com.yhs.encrypt.enums.EncryptionSchemeEnum;
import com.yhs.encrypt.exception.EncryptException;
import com.yhs.encrypt.properties.EncryptProperties;
import org.springframework.stereotype.Component;

/**
 * 国密 SM2 非对称加密
 *
 * @author 07664-linwei
 * @version Id: Sm2EncryptionHandler.java, v 0.1 2022/7/6 17:34 lw Exp $
 */
@Component
public class Sm2EncryptionHandler implements EncryptHandler {

    @Override
    public boolean support(EncryptionSchemeEnum schemeEnum) {
        return EncryptionSchemeEnum.SM2 == schemeEnum;
    }

    @Override
    public String encrypt(String content, String secretKey, EncryptProperties encryptProperties) {
        return getSm2(encryptProperties).encryptBase64(content, KeyType.PrivateKey);
    }

    @Override
    public String decrypt(String content, String secretKey, EncryptProperties encryptProperties) {
        return getSm2(encryptProperties).decryptStr(content, KeyType.PrivateKey);
    }


    private SM2 getSm2(EncryptProperties encryptProperties) {
        if (CharSequenceUtil.isBlank(encryptProperties.getSm2PrivateSecretKey())
                || CharSequenceUtil.isBlank(encryptProperties.getSm2PublicSecretKey())) {
            throw new EncryptException("SM2 加密初始化异常，未配置全局公钥或者私钥");
        }
        return SmUtil.sm2(encryptProperties.getRsaPrivateSecretKey(), encryptProperties.getRsaPublicSecretKey());
    }
}
