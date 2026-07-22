package com.yhs.base.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author 04628-duanchengjun
 * @version Id: UrlSecurityUtil.java, v 0.1 2019/4/25 9:57 duanchengjun Exp $
 */
@UtilityClass
public class UrlSecurityUtil {

    private final static Logger logger = LogUtil.getLogger();

    private static final String KEY = "ElJ1SSGsjd7z8Gs62H";//key = key + config(dev\stg\prd)
    private static final String SALT_FIGURE_PREFIX = "_1DdJ1SdddSGsjaad2z8Gs6";
    private static final String SALT_FIGURE_SUFIX = "skdhdhsendh_sdjh23f";
    private static final String SEPARATOR = "-----";
    private static final String ALGORITHM = "AES";
    private static final String UTF8 = "UTF-8";
    private static final String DATE_FORMAT = "yyyyMMddHHmmss";

    public static String getAesKey(String keySuffix) {
        if (StringUtils.isBlank(keySuffix)) {
            logger.error("no keySuffix");
            return null;
        }
        String aesKey = KEY + keySuffix;
        logger.info("getAesKey: {}", aesKey);
        return aesKey;
    }

    public static String encryptByAes(String content, String aesKey) {
        if (StringUtils.isBlank(content)) {
            return null;
        }
        content = generateContent(content);
        try {
            KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
            if (StringUtils.isBlank(aesKey)) {
                logger.error("no asc key.");
                return null;
            }
            kgen.init(128, new SecureRandom(aesKey.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            byte[] byteContent = content.getBytes(StandardCharsets.UTF_8);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] result = cipher.doFinal(byteContent);
            return parseByte2HexStr(result);
        } catch (Exception e) {
            logger.warn("url encryptByAes failure.", e);
        }
        return null;
    }

    public static String decryptByAes(String decryptString, String aesKey) {
        if (StringUtils.isBlank(decryptString)) {
            return null;
        }
        byte[] content = parseHexStr2Byte(decryptString);
        try {
            KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
            if (StringUtils.isBlank(aesKey)) {
                logger.error("no asc key.");
                return null;
            }
            kgen.init(128, new SecureRandom(aesKey.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] result = cipher.doFinal(content);
            return parseDecryptContent(new String(result));
        } catch (Exception e) {
            logger.warn("url decryptByAes failure.", e);
        }
        return null;
    }

    private static String generateContent(String content) {
        String result = SALT_FIGURE_PREFIX +
                SEPARATOR +
                content +
                SEPARATOR +
                SALT_FIGURE_SUFIX;
        return result;
    }

    private static String parseByte2HexStr(byte[] buf) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    private static byte[] parseHexStr2Byte(String hexStr) {
        if (StringUtils.isBlank(hexStr)) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    private static String parseDecryptContent(String content) {
        String[] contents = content.split(SEPARATOR);

        logger.info("contents length:{}", contents.length);
        for (String str : contents) {
            logger.info(str);
        }

        if (null == contents || contents.length != 3) {
            logger.info("illegal parmas content:{}", content);
            return null;
        }
        if (StringUtils.equals(SALT_FIGURE_PREFIX, contents[0]) && StringUtils.equals(SALT_FIGURE_SUFIX, contents[2])) {
            return contents[1];
        }
        return null;
    }

    public static String applyCurrentDate(boolean hasSeconds) {
        if (hasSeconds) {
            return DateFormatUtils.format(Calendar.getInstance().getTime(), DATE_FORMAT);
        }
        return DateFormatUtils.format(Calendar.getInstance().getTime(), "yyyyMMdd");
    }

    public static boolean checkExpiredDate(String dateStr, int expireLimit, String unit) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        Date limitDate = null;
        try {
            Date date = format.parse(dateStr);
            Calendar cl = Calendar.getInstance();
            cl.setTime(date);
            if (StringUtils.equals(unit, "DAY_OF_YEAR")) {
                cl.set(Calendar.DAY_OF_YEAR, cl.get(Calendar.DAY_OF_YEAR) + expireLimit);
            } else {
                cl.set(Calendar.MINUTE, cl.get(Calendar.MINUTE) + expireLimit);
            }
            limitDate = cl.getTime();
            if (limitDate.before(Calendar.getInstance().getTime())) {
                logger.info("limitDate before currentDate");
                return true;
            }
        } catch (ParseException e) {
            logger.info("invalid dateStr");
            return true;
        }
        return false;
    }
}
