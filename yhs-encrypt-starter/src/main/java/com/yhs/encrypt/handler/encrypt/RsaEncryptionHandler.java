package com.yhs.encrypt.handler.encrypt;


import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.text.CharSequenceUtil;
import com.yhs.encrypt.enums.EncryptionSchemeEnum;
import com.yhs.encrypt.properties.EncryptProperties;
import com.yhs.encrypt.utils.RsaUtil;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.exceptions.EncryptionInitializationException;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * 非对称加密Rsa 加解密
 *
 * @author 07664-linwei
 * @version Id: RsaEncryptionHandler.java, v 0.1 2022/7/6 16:14 lw Exp $
 */
@Slf4j
@Component
public class RsaEncryptionHandler implements EncryptHandler {
    @Override
    public boolean support(EncryptionSchemeEnum schemeEnum) {
        return EncryptionSchemeEnum.RSA == schemeEnum;
    }

    @Override
    public String encrypt(String content, String secretKey, EncryptProperties encryptProperties) {

        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(encryptProperties.getRsaPublicSecretKey());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);

            RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);

            Cipher cipher = Cipher.getInstance("RSA");

            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            // RSA加密是分段加密，设置每次加密的最大长度  66是填充和其他开销的字节数
            int maxLength = (publicKey.getModulus().bitLength() / 8) - 66;

            byte[] data = URLEncoder.encode(content, StandardCharsets.UTF_8.name()).getBytes(StandardCharsets.UTF_8);
            // 分段加密数据
            int offset = 0;

            byte[] encryptedData = new byte[0];

            while (offset < data.length) {
                byte[] chunk = new byte[Math.min(maxLength, data.length - offset)];

                System.arraycopy(data, offset, chunk, 0, chunk.length);

                byte[] encryptedChunk = cipher.doFinal(chunk);

                byte[] newEncryptedData = new byte[encryptedData.length + encryptedChunk.length];

                System.arraycopy(encryptedData, 0, newEncryptedData, 0, encryptedData.length);

                System.arraycopy(encryptedChunk, 0, newEncryptedData, encryptedData.length, encryptedChunk.length);

                encryptedData = newEncryptedData;

                offset += maxLength;
            }
            return Base64.getEncoder().encodeToString(encryptedData);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public String decrypt(String content, String secretKey, EncryptProperties encryptProperties) {
        if (CharSequenceUtil.isBlank(encryptProperties.getRsaPrivateSecretKey())
                || CharSequenceUtil.isBlank(encryptProperties.getRsaPublicSecretKey())) {
            throw new EncryptionInitializationException("RSA 加密初始化异常，未配置全局公钥或者私钥");
        }
        byte[] base64 = Base64.getDecoder().decode(content);
        String decryptStr = RsaUtil.decryptToLong(encryptProperties.getRsaPrivateSecretKey(), base64);
        return URLDecoder.decode(decryptStr, Charset.defaultCharset());
    }


}
