package com.yhs.base.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

/**
 * @author 04628-duanchengjun
 * @version Id: UnicodeUtil.java, v 0.1 2019/4/25 10:05 duanchengjun Exp $
 */
@UtilityClass
public class UnicodeUtil {

    public static String unicode2String(String unicode) {
        if (StringUtils.isBlank(unicode)) return null;
        StringBuilder sb = new StringBuilder();
        int i = -1;
        int pos = 0;

        while ((i = unicode.indexOf("\\u", pos)) != -1) {
            sb.append(unicode, pos, i);
            if (i + 5 < unicode.length()) {
                pos = i + 6;
                sb.append((char) Integer.parseInt(unicode.substring(i + 2, i + 6), 16));
            }
        }

        return sb.toString();
    }

    public static String string2Unicode(String string) {

        if (StringUtils.isBlank(string)) return null;
        StringBuffer unicode = new StringBuffer();

        for (int i = 0; i < string.length(); i++) {
            // 取出每一个字符
            char c = string.charAt(i);
            // 转换为unicode
            unicode.append("\\u" + Integer.toHexString(c));
        }

        return unicode.toString();
    }

}
