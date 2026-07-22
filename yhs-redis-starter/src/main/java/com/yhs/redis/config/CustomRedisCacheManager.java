package com.yhs.redis.config;

import cn.hutool.core.util.StrUtil;
import com.yhs.base.constant.CommonConstant;
import com.yhs.base.tenant.TenantContextHolder;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;

/**
 * 时间格式解析及设置
 * 格式 @Cacheable(value = xxx#2s)
 * "?ns" 纳秒
 * "?us" 微秒
 * "?ms" 毫秒
 * "?s" 秒
 * "?m" 分
 * "?h" 小时
 * "?d" 天
 *
 * @author 07664-linwei
 * @version Id: RedisAutoCacheManager.java, v 0.1 2022/4/19 17:24 lw Exp $
 */
public class CustomRedisCacheManager extends RedisCacheManager {


    public CustomRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration, Map<String, RedisCacheConfiguration> initialCacheConfigurations, boolean allowInFlightCacheCreation) {
        super(cacheWriter, defaultCacheConfiguration, initialCacheConfigurations, allowInFlightCacheCreation);
    }

    @Override
    protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfig) {
        if (StrUtil.isNotBlank(name) && name.contains(CommonConstant.SYMBOL_NUMBER)) {
            String[] split = name.split(CommonConstant.SYMBOL_NUMBER);
            if (cacheConfig != null) {
                // 设置key 的有效时间
                Duration duration = DurationStyle.detectAndParse(split[1], ChronoUnit.SECONDS);
                cacheConfig = cacheConfig.entryTtl(duration);
                return super.createRedisCache(split[0], cacheConfig);
            }
        }
        return super.createRedisCache(name, cacheConfig);
    }

    /**
     * @param name
     * @return
     */
    @Override
    public Cache getCache(String name) {
        if (StrUtil.isNotBlank(TenantContextHolder.getTenantId())) {
            //开启租户后不区分租户全局存储
            if (name.startsWith(CommonConstant.GLOBALLY)) {
                return super.getCache(name);
            }
            return super.getCache(TenantContextHolder.getTenantId() + StrUtil.COLON + name);
        }
        //未开启租户模式
        return super.getCache(name);
    }
}
