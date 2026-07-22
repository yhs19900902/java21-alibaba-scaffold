package com.yhs.base.utils;


import com.alibaba.fastjson2.JSON;

/**
 * @author 04628-duanchengjun
 * @version Id: JSONUtil.java, v 0.1 2019/4/25 9:38 duanchengjun Exp $
 */
public class JSONUtil {

    public static <T> T fromObject(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    public static String toJson(Object obj) {
        return JSON.toJSONString(obj);
    }


}
