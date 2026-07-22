package com.yhs.base.utils;

import lombok.experimental.UtilityClass;

/**
 * @author 07664-linwei
 * @version Id: KaiserUtil.java, v 0.1 2023/3/23 15:29 lw Exp $
 */
@UtilityClass
public class KaiserUtil {

    public static String encryptKaiser(String original, int key) {
        char[] chars = original.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char aChar : chars) {
            int asciiCode = aChar;
            asciiCode += key;
            char result = (char) asciiCode;
            sb.append(result);
        }
        return sb.toString();
    }

    public static String decryptKaiser(String encryptedData, int key) {
        // 将字符串转为字符数组
        char[] chars = encryptedData.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char aChar : chars) {
            // 获取字符的ASCII编码
            int asciiCode = aChar;
            // 偏移数据
            asciiCode -= key;
            // 将偏移后的数据转为字符
            char result = (char) asciiCode;
            // 拼接数据
            sb.append(result);
        }
        return sb.toString();
    }

}
