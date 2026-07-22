package com.yhs.encrypt.handler.encrypt;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.crypto.asymmetric.ECIES;
import cn.hutool.crypto.asymmetric.KeyType;
import com.yhs.encrypt.enums.EncryptionSchemeEnum;
import com.yhs.encrypt.exception.EncryptException;
import com.yhs.encrypt.properties.EncryptProperties;
import org.springframework.stereotype.Component;

/**
 * ecies 非对称加解密
 *
 * @author 07664-linwei
 * @version Id: EciesEncryptionHandler.java, v 0.1 2022/7/6 16:47 lw Exp $
 */
@Component
public class EciesEncryptionHandler implements EncryptHandler {
    @Override
    public boolean support(EncryptionSchemeEnum schemeEnum) {
        return EncryptionSchemeEnum.DES == schemeEnum;
    }

    @Override
    public String encrypt(String content, String secretKey, EncryptProperties encryptProperties) {
        return getECIES(encryptProperties).encryptBase64(content, KeyType.PrivateKey);
    }

    @Override
    public String decrypt(String content, String secretKey, EncryptProperties encryptProperties) {
        return getECIES(encryptProperties).decryptStr(content, KeyType.PublicKey);
    }


    private ECIES getECIES(EncryptProperties encryptProperties) {
        if (CharSequenceUtil.isBlank(encryptProperties.getEciesPrivateSecretKey())
                || CharSequenceUtil.isBlank(encryptProperties.getEciesPublicSecretKey())) {
            throw new EncryptException("ECIES 加密初始化异常，未配置全局公钥或者私钥");
        }
        return new ECIES(encryptProperties.getRsaPrivateSecretKey(), encryptProperties.getRsaPublicSecretKey());
    }
}
