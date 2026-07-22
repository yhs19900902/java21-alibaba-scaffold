package com.yhs.base.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 04628-duanchengjun
 * @version Id: RsaUtil.java, v 0.1 2019/4/25 10:04 duanchengjun Exp $
 */
@UtilityClass
public class RsaUtil {

    private static final String KEY_ALGORITHM = "RSA";
    private static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";
    private static final String PUBLIC_KEY = "publicKey";
    private static final String PRIVATE_KEY = "privateKey";

    /**
     * RSA密钥长度必须是64的倍数，在512~65536之间。默认是1024
     */
    private static final int KEY_SIZE = 512;

    public static String encrypt(String input, String publicKey) throws Exception {
        try {
            PublicKey pk = restorePublicKey(SecurityUtil.base642byte(publicKey));
            byte[] encodedText = RSAEncode(pk, input.getBytes());
            return Base64.encodeBase64String(encodedText);
        } catch (Exception e) {
            throw new SecurityException(e);
        }
    }

    public static String decrypt(String input, String privateKey) throws Exception {
        try {
            PrivateKey pk = restorePrivateKey(SecurityUtil.base642byte(privateKey));
            return RSADecode(pk, SecurityUtil.base642byte(input));
        } catch (Exception e) {
            throw new SecurityException(e);
        }
    }

    /**
     * 生成密钥对。注意这里是生成密钥对KeyPair，再由密钥对获取公私钥
     *
     * @return
     */
    public static Map<String, byte[]> generateKeyBytes() throws Exception {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGenerator.initialize(KEY_SIZE);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

            Map<String, byte[]> keyMap = new HashMap<>();
            keyMap.put(PUBLIC_KEY, publicKey.getEncoded());
            keyMap.put(PRIVATE_KEY, privateKey.getEncoded());
            return keyMap;
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException(e);
        }
    }

    private static Map<String, byte[]> generateKeyBytesTest() {
        try {
            String public_key = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAOVBMu1RGD747pebKeqLM7vRGI25v2pE5KmOQD6rMhXg\n"
                    + "IxBq2XLYl9bwx/mDJMKK0+y5cZu2aLKtvl3K9X4ZDMkCAwEAAQ==";
            String private_key = "MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEA5UEy7VEYPvjul5sp6oszu9EYjbm/\n"
                    + "akTkqY5APqsyFeAjEGrZctiX1vDH+YMkworT7Llxm7Zosq2+Xcr1fhkMyQIDAQABAkEAjmaFP6vT\n"
                    + "p6nwafNTggCdTdh/q+jo4RWSA0/8z/B+196IaKBXmF0NdbhUDjashAF4lW6U/j2fZxTZD3npmtT3\n"
                    + "QQIhAPZZVGw1CKYIOQZ65HFsu2jKsqoXGAl7ekukRt/hsa+VAiEA7jxtowsFT7467T923QZiV/8+\n"
                    + "ITb5f08FXR1clatN62UCIQChrdcefWADt/nVKuqrFnWZPQ8tWqLH9mY5JsPtQ67eUQIgGMQlbaau\n"
                    + "payT+af4Vl/ch2NcdR1+8HIpj5WG03RchFUCIG3ZMEytfCMS5rQIbijs1hV4MJUu+YepEtIkbqLV\n" + "jVis";
            Map<String, byte[]> keyMap = new HashMap<>();
            keyMap.put(PUBLIC_KEY, SecurityUtil.base642byte(public_key));
            keyMap.put(PRIVATE_KEY, SecurityUtil.base642byte(private_key));
            return keyMap;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 还原公钥，X509EncodedKeySpec 用于构建公钥的规范
     *
     * @param keyBytes
     * @return
     */
    private static PublicKey restorePublicKey(byte[] keyBytes) throws Exception {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);

        try {
            KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
            PublicKey publicKey = factory.generatePublic(x509EncodedKeySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new SecurityException(e);
        }
    }

    /**
     * 还原私钥，PKCS8EncodedKeySpec 用于构建私钥的规范
     *
     * @param keyBytes
     * @return
     */
    private static PrivateKey restorePrivateKey(byte[] keyBytes) throws Exception {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        try {
            KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey privateKey = factory.generatePrivate(pkcs8EncodedKeySpec);
            return privateKey;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new SecurityException(e);
        }
    }

    /**
     * 加密，三步走。
     *
     * @param key
     * @param plainText
     * @return
     */
    private static byte[] RSAEncode(PublicKey key, byte[] plainText) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(plainText);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new SecurityException(e);
        }
    }

    /**
     * 解密，三步走。
     *
     * @param key
     * @param encodedText
     * @return
     */
    private static String RSADecode(PrivateKey key, byte[] encodedText) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(encodedText));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new SecurityException(e);
        }
    }

}
