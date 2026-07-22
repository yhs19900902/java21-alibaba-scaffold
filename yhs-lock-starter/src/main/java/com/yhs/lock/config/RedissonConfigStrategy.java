package com.yhs.lock.config;

import com.yhs.lock.properties.RedissonProperties;
import org.redisson.config.Config;

/**
 * @author 07664-linwei
 * @version Id: RedissonConfigStrategy.java, v 0.1 2022/4/24 17:11 lw Exp $
 */
public interface RedissonConfigStrategy {

    /**
     * 创建Redisson Config
     *
     * @param redissonProperties
     * @return config
     */
    Config createRedissonConfig(RedissonProperties redissonProperties);
}
