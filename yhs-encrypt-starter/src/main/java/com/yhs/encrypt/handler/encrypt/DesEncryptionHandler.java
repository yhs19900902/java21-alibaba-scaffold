package com.yhs.encrypt.handler.encrypt;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.DES;
import com.yhs.encrypt.enums.EncryptionSchemeEnum;
import com.yhs.encrypt.properties.EncryptProperties;
import com.yhs.encrypt.utils.CheckUtils;
import org.springframework.stereotype.Component;

/**
 * des 加解密处理
 *
 * @author 07664-linwei
 * @version Id: DesEncryptionHandler.java, v 0.1 2022/7/6 9:49 lw Exp $
 */
@Component
public class DesEncryptionHandler implements EncryptHandler {

    @Override
    public boolean support(EncryptionSchemeEnum schemeEnum) {
        return EncryptionSchemeEnum.DES == schemeEnum;
    }

    @Override
    public String encrypt(String content, String secretKey, EncryptProperties encryptProperties) {
        return getDes(CheckUtils.checkAndGetKey(secretKey, encryptProperties.getDesSecretKey(), "des 加密秘钥"))
                .encryptBase64(content);
    }

    @Override
    public String decrypt(String content, String secretKey, EncryptProperties encryptProperties) {
        return getDes(CheckUtils.checkAndGetKey(secretKey, encryptProperties.getDesSecretKey(), "des 加密秘钥"))
                .decryptStr(content);
    }

    private DES getDes(String secretKey) {
        return SecureUtil.des(secretKey.getBytes(CharsetUtil.CHARSET_UTF_8));
    }

}
