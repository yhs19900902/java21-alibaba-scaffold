package com.yhs.lock.config.strategy;

import cn.hutool.core.text.CharSequenceUtil;
import com.yhs.lock.config.RedissonConfigStrategy;
import com.yhs.lock.constant.RedisGlobalConstant;
import com.yhs.lock.properties.RedissonProperties;
import jodd.util.StringPool;
import lombok.extern.slf4j.Slf4j;
import org.redisson.config.Config;

/**
 * @author 07664-linwei
 * @version Id: SentinelRedissonConfigImpl.java, v 0.1 2022/4/24 17:34 lw Exp $
 */
@Slf4j
public class SentinelRedissonConfigImpl implements RedissonConfigStrategy {

    @Override
    public Config createRedissonConfig(RedissonProperties redissonProperties) {
        Config config = new Config();
        String[] adds = redissonProperties.getAddress().split(StringPool.COMMA);
        config.useSentinelServers()
                .setMasterName(adds[0]);
        if (CharSequenceUtil.isNotBlank(redissonProperties.getPassword())) {
            config.useSentinelServers().setPassword(redissonProperties.getPassword());
        }
        config.useSentinelServers()
                .setTimeout(redissonProperties.getConnectTimeout())
                .setConnectTimeout(redissonProperties.getConnectTimeout()).
                setDatabase(redissonProperties.getDatabase());
        for (int i = 1; i < adds.length; i++) {
            config.useSentinelServers().addSentinelAddress(RedisGlobalConstant.REDIS_CONNECTION_PREFIX + adds[i]);
        }
        log.info("初始化[sentinel]方式Config,redisAddress:" + redissonProperties.getAddress());
        return config;
    }
}
