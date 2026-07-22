package com.yhs.lock.config;

import com.yhs.lock.properties.RedissonProperties;
import org.redisson.config.Config;

/**
 * @author 07664-linwei
 * @version Id: RedissonConfigContext.java, v 0.1 2022/4/24 17:23 lw Exp $
 */
public class RedissonConfigContext {

    private final RedissonConfigStrategy redissonConfigStrategy;

    public RedissonConfigContext(RedissonConfigStrategy redissonConfigStrategy) {
        this.redissonConfigStrategy = redissonConfigStrategy;
    }

    /**
     * 上下文根据构造中传入的具体策略产出真实的Redisson的Config
     */
    public Config createRedissonConfig(RedissonProperties redissonProperties) {
        return this.redissonConfigStrategy.createRedissonConfig(redissonProperties);
    }
}
