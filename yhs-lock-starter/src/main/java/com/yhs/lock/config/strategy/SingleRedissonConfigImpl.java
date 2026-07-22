package com.yhs.lock.config.strategy;

import com.yhs.lock.config.RedissonConfigStrategy;
import com.yhs.lock.constant.RedisGlobalConstant;
import com.yhs.lock.properties.RedissonProperties;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.config.Config;

/**
 * @author 07664-linwei
 * @version Id: SingleRedissonConfigImpl.java, v 0.1 2022/4/24 17:40 lw Exp $
 */
@Slf4j
public class SingleRedissonConfigImpl implements RedissonConfigStrategy {

    @Override
    public Config createRedissonConfig(RedissonProperties redissonProperties) {
        Config config = new Config();
        config.useSingleServer().setAddress(RedisGlobalConstant.REDIS_CONNECTION_PREFIX +
                        redissonProperties.getAddress())
                .setTimeout(redissonProperties.getConnectTimeout())
                .setConnectTimeout(redissonProperties.getConnectTimeout())
                .setDatabase(redissonProperties.getDatabase());
        if (StringUtil.isNotBlank(redissonProperties.getPassword())) {
            config.useSingleServer().setPassword(redissonProperties.getPassword());
        }
        log.info("初始化了单节点方式Config.redissonAddress:{}", redissonProperties.getAddress());
        return config;
    }
}
