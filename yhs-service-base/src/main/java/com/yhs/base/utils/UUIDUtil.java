package com.yhs.base.utils;

import lombok.experimental.UtilityClass;

import java.util.UUID;

/**
 * UUID工具类
 *
 * @author 05697-LongLiHua
 * @version Id: UUIDUtils.java, v 0.1 2020/9/8 16:54  LongLiHua Exp $
 * @Description
 */
@UtilityClass
public class UUIDUtil {

    /**
     * 获取长整uuid数字
     *
     * @return java.lang.String
     **/
    public static String getLongUUID() {
        UUID uuid = UUID.randomUUID();
        long mostSignificantBits = uuid.getMostSignificantBits();
        return String.valueOf(mostSignificantBits);
    }

    /**
     * 获取32位字符串 不要-
     *
     * @return java.lang.String
     **/
    public static String getStringUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "");
    }


    /**
     * 获取短整uuid数字
     *
     * @return java.lang.String
     **/
    public static String getShortUUID() {
        UUID uuid = UUID.randomUUID();
        long leastSignificantBits = uuid.getLeastSignificantBits();
        return String.valueOf(leastSignificantBits);
    }
}
    