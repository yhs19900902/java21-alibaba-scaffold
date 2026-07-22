package com.yhs.encrypt.handler.encrypt;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SM4;
import com.yhs.encrypt.enums.EncryptionSchemeEnum;
import com.yhs.encrypt.properties.EncryptProperties;
import com.yhs.encrypt.utils.CheckUtils;
import org.springframework.stereotype.Component;


/**
 * 国密 sm4 对称加密
 *
 * @author 07664-linwei
 * @version Id: Sm4EncryptionHandler.java, v 0.1 2022/7/6 17:48 lw Exp $
 */
@Component
public class Sm4EncryptionHandler implements EncryptHandler {
    @Override
    public boolean support(EncryptionSchemeEnum schemeEnum) {
        return EncryptionSchemeEnum.SM4 == schemeEnum;
    }

    @Override
    public String encrypt(String content, String secretKey, EncryptProperties encryptProperties) {
        return getSm4(CheckUtils.checkAndGetKey(secretKey, encryptProperties.getSm4SecretKey(), "SM4 对称加密"))
                .encryptBase64(content);
    }

    @Override
    public String decrypt(String content, String secretKey, EncryptProperties encryptProperties) {
        return getSm4(CheckUtils.checkAndGetKey(secretKey, encryptProperties.getSm4SecretKey(), "SM4 对称加密"))
                .decryptStr(content);
    }

    private SM4 getSm4(String secretKey) {
        return SmUtil.sm4(secretKey.getBytes(CharsetUtil.CHARSET_UTF_8));
    }
}
