package com.yhs.knife4j.core;

/**
 * @author 07664-linwei
 * @version Id: RouteCache.java, v 0.1 2023/7/25 14:16 lw Exp $
 */
public interface RouteCache<K, V> {

    /**
     * @param k 键
     * @param v 值
     * @return 是否缓存成功
     */
    boolean put(K k, V v);

    /**
     * 获取缓存值
     *
     * @param k 键
     * @return 缓存值
     */
    V get(K k);

    /**
     * 移除缓存
     *
     * @param k 键
     * @return 是否删除成功
     */
    boolean remove(K k);
}
