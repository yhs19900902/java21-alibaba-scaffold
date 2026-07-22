package com.yhs.base.utils;

import com.yhs.base.constant.CommonConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

/**
 * @author 04628-duanchengjun
 * @version Id: SecurityUtil.java, v 0.1 2019/4/25 9:39 duanchengjun Exp $
 */
public class SecurityUtil {
    public static final char[] digital = "0123456789ABCDEF".toCharArray();
    private static final String HMAC_SHA1 = "HmacSHA1";
    private static final Logger log = LogUtil.getLogger();

    private SecurityUtil() {
    }

    public static String byte2HexStr(final byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        char[] result = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            result[i * 2] = digital[(bytes[i] & 0xf0) >> 4];
            result[i * 2 + 1] = digital[bytes[i] & 0x0f];
        }
        return new String(result);
    }

    public static byte[] hexStr2Byte(final String str) {
        if (str == null) {
            return null;
        }
        char[] charArray = str.toCharArray();
        if (charArray.length % 2 != 0) {
            throw new RuntimeException("hex str length must can mod 2, str:" + str);
        }
        byte[] bytes = new byte[charArray.length / 2];
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            int b;
            if (c >= '0' && c <= '9') {
                b = (c - '0') << 4;
            } else if (c >= 'A' && c <= 'F') {
                b = (c - 'A' + 10) << 4;
            } else {
                throw new RuntimeException("unsupport hex str:" + str);
            }
            c = charArray[++i];
            if (c >= '0' && c <= '9') {
                b |= c - '0';
            } else if (c >= 'A' && c <= 'F') {
                b |= c - 'A' + 10;
            } else {
                throw new RuntimeException("unsupport hex str:" + str);
            }
            bytes[i / 2] = (byte) b;
        }
        return bytes;
    }

    /**
     * byte to base64
     *
     * @param bytes
     */
    public static String byte2base64(byte[] bytes) {
//        BASE64Encoder base64Encoder = new BASE64Encoder();
//        return base64Encoder.encode(bytes);
        Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(bytes);
    }

    /**
     * base64 to byte
     *
     * @param base64
     * @throws IOException
     */
    public static byte[] base642byte(String base64) throws IOException {
//        BASE64Decoder base64Decoder = new BASE64Decoder();
//        return base64Decoder.decodeBuffer(base64);
        Decoder decoder = Base64.getDecoder();
        return decoder.decode(base64);
    }

    public static boolean verifySignByHmacSha1(String content, String key, String sign) throws Exception {
        if (StringUtils.isNotBlank(content) && StringUtils.isNotBlank(sign)) {
            String contentStr = getHmacSha1(content, key);
            return StringUtils.equals(contentStr, sign);
        }
        return false;
    }

    /**
     * hmac_sha1加密
     *
     * @param datas
     * @param key
     * @return
     */
    public static byte[] hmacSha1(byte[] datas, byte[] key) {
        SecretKeySpec signingKey = new SecretKeySpec(key, HMAC_SHA1);
        Mac mac = null;
        try {
            mac = Mac.getInstance(HMAC_SHA1);
            mac.init(signingKey);
            return mac.doFinal(datas);
        } catch (Throwable e) {
            log.warn("hmacSha1 exception", e);
            throw new SecurityException(e.getMessage(), e);
        }
    }

    /**
     * hmac_sha1 签名, 调用和泰接口专用
     *
     * @param data 需要签名的内容
     * @param key  密匙(使用appkey)
     * @return
     */
    public static String getHmacSha1(String data, String key) {
        byte[] datas = data.getBytes(StandardCharsets.UTF_8);
        byte[] keys = key.getBytes(StandardCharsets.UTF_8);
        byte[] results = hmacSha1(datas, keys);
        return byte2HexStr(results);
    }

    /**
     * 获取hmac_sha1 签名, 调用tencent接口专用
     *
     * @param data
     * @param seller_secretkey
     * @return
     */
    private static String getHmacSha14Tx(String data, String seller_secretkey) {
        try {
            byte[] datas = data.getBytes(StandardCharsets.UTF_8);
            byte[] keys = seller_secretkey.getBytes(StandardCharsets.UTF_8);
            byte[] results = hmacSha1(datas, keys);
            return byte2base64(results);
        } catch (Throwable e) {
            log.warn("getHmacSha14Tx exception", e);
            throw new SecurityException(e.getMessage(), e);
        }
    }

    /**
     * 获取调用腾讯接口的签名
     *
     * @param method
     * @param urlPath
     * @param params
     * @param seller_secretkey
     * @return
     */
    public static String sign4Tx(String method, String urlPath, Map<String, Object> params, String seller_secretkey) {
        //HTTP请求方式 & urlencode(uri) & urlencode(a=x&b=y&…)
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(method).append("&");
            builder.append(URLEncoder.encode(urlPath, CommonConstant.UTF8)).append("&");

            List<String> paramValueList = new ArrayList<>();
            if (params != null) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    paramValueList.add(entry.getKey());
                }
            }
            Collections.sort(paramValueList);
            StringBuilder paramsBuilder = new StringBuilder();
            for (String key : paramValueList) {
                paramsBuilder.append(key).append("=").append(params.get(key)).append("&");
            }
            int length = paramsBuilder.length();
            if (length > 0) {
                String temp = URLEncoder.encode(paramsBuilder.substring(0, length - 1), CommonConstant.UTF8);
                // java编码在将空格编码为加号（+）方面与 RFC1738 编码不同
                temp = temp.replace("+", "%20");
                builder.append(temp);
            }
            String content = builder.toString();
            String sign = getHmacSha14Tx(content, seller_secretkey);
            return URLEncoder.encode(sign, CommonConstant.UTF8);
        } catch (Throwable t) {
            log.warn("sign4Tx exception", t);
            throw new SecurityException(t.getMessage(), t);
        }
    }

    /**
     * 生成AES密钥
     *
     * @throws Exception
     */
    public static SecretKey genKeyAES() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey key = keyGen.generateKey();
        return key;
    }

    /**
     * @param key
     * @return
     * @throws Exception
     */
    public static SecretKey genKeyAES(String key) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(key.getBytes());
        kgen.init(128, random);
        return kgen.generateKey();
    }

    /**
     * AES128加密
     *
     * @param content 需要加密的内容
     * @param key     密匙(使用secretKey)
     * @return
     */
    public static String encryptAES128(String content, String key) {
        try {
            SecretKey secretKey = string2SecretKey(key);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] result = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return byte2HexStr(result);
        } catch (Throwable e) {
            log.warn("encryptAES128 exception", e);
            throw new SecurityException(e.getMessage(), e);
        }
    }

    /**
     * AES128解密
     *
     * @param content 需要解密的内容
     * @param key     密匙(使用secretkey)
     * @return
     */
    public static String decryptAES128(String content, String key) {
        try {
            SecretKey secretKey = string2SecretKey(key);
            byte[] resource = hexStr2Byte(content);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] result = cipher.doFinal(resource);
            return new String(result, StandardCharsets.UTF_8);
        } catch (Throwable e) {
            log.warn("decryptAES128 exception", e);
            throw new SecurityException(e.getMessage(), e);
        }
    }

    /**
     * SecretKey转字符串
     *
     * @param secretKey
     * @return
     */
    public static String secretKey2String(SecretKey secretKey) {
        return byte2base64(secretKey.getEncoded());
    }

    /**
     * 字符串转SecretKey
     *
     * @param key
     * @return
     */
    public static SecretKey string2SecretKey(String key) throws IOException {
        byte[] decodedKey = base642byte(key);
        SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        return secretKey;
    }

    public static String MD5(String string) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte[] inputByteArray = string.getBytes();
        messageDigest.update(inputByteArray);
        byte[] resultByteArray = messageDigest.digest();
        return byteArrayToHex(resultByteArray);
    }

    private static String byteArrayToHex(byte[] byteArray) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] resultCharArray = new char[byteArray.length * 2];
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        return new String(resultCharArray);
    }

}
