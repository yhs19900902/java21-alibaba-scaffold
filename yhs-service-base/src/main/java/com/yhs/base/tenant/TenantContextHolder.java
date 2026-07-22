package com.yhs.base.tenant;

import cn.hutool.core.convert.Convert;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.yhs.base.constant.CommonConstant;
import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 07664-linwei
 * @version Id: TenantContextHolder.java, v 0.1 2022/5/16 16:49 lw Exp $
 */
@UtilityClass
public class TenantContextHolder {

    private final ThreadLocal<Map<String, String>> THREAD_LOCAL_TENANT = new TransmittableThreadLocal<>();

    private final String TENANT_KEY = "tenantKey";

    private final String ORIGINAL_TENANT_KEY = "originalTenant";

    public static void set(String key, Object value) {
        Map<String, String> map = getLocalMap();
        map.put(key, value == null ? CommonConstant.EMPTY : value.toString());
    }

    public static <T> T get(String key, Class<T> type) {
        Map<String, String> map = getLocalMap();
        return Convert.convert(type, map.get(key));
    }

    public static <T> T get(String key, Class<T> type, Object def) {
        Map<String, String> map = getLocalMap();
        return Convert.convert(type, map.getOrDefault(key, String.valueOf(def == null ? CommonConstant.EMPTY : def)));
    }

    public static Map<String, String> getLocalMap() {
        Map<String, String> map = THREAD_LOCAL_TENANT.get();
        if (map == null) {
            map = new ConcurrentHashMap<>(10);
            THREAD_LOCAL_TENANT.set(map);
        }
        return map;
    }

    /**
     * 原始加密的tenantId
     *
     * @return
     */
    public String getOriginalTenantId() {
        return get(ORIGINAL_TENANT_KEY, String.class, CommonConstant.EMPTY);
    }

    /**
     * 原始加密的tenantId
     *
     * @param tenantId
     */
    public void setOriginalTenantId(String tenantId) {
        set(ORIGINAL_TENANT_KEY, tenantId);
    }

    /**
     * 获取TTL中的租户ID
     *
     * @return
     */
    public String getTenantId() {
        return get(TENANT_KEY, String.class, CommonConstant.EMPTY);
    }

    /**
     * TTL 设置租户ID<br/>
     * <b>谨慎使用此方法,避免嵌套调用。尽量使用 {@code TenantBroker} </b>
     *
     * @param tenantId
     */
    public void setTenantId(String tenantId) {
        set(TENANT_KEY, tenantId);
    }

    public void clear() {
        THREAD_LOCAL_TENANT.remove();
    }
}
