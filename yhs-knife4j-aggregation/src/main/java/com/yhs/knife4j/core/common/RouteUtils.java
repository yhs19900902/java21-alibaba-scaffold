package com.yhs.knife4j.core.common;


import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author 07664-linwei
 * @version Id: RouteUtils.java, v 0.1 2023/7/25 9:31 lw Exp $
 */
public class RouteUtils {

    private RouteUtils() {
    }

    public static String authorize(String username, String password) {
        String encodeStr = username + ":" + password;
        return "Basic " +
                Base64.getEncoder().encodeToString(encodeStr.getBytes(StandardCharsets.UTF_8));
    }
}
