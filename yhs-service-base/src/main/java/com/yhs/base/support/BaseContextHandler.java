package com.yhs.base.support;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 04628-duanchengjun
 * @version Id: BaseContextHandler.java, v 0.1 2019/4/25 9:40 duanchengjun Exp $
 */
@SuppressWarnings("unchecked")
public class BaseContextHandler {

    public static ThreadLocal<Map<String, Object>> threadLocal = new TransmittableThreadLocal<>();

    public static void set(String key, Object value) {
        Map<String, Object> map = threadLocal.get();
        if (map == null) {
            map = new HashMap(8);
            threadLocal.set(map);
        }
        map.put(key, value);
    }

    public static Object get(String key) {
        Map<String, Object> map = threadLocal.get();
        if (map == null) {
            map = new HashMap(8);
            threadLocal.set(map);
        }
        return map.get(key);
    }

    public static void remove(String key) {
        Map<String, Object> map = threadLocal.get();
        if (map != null) {
            map.remove(key);
        }
    }

    public static void clear() {
        Map<String, Object> map = threadLocal.get();
        if (map != null) {
            map.clear();
            threadLocal.remove();
        }
    }

}
