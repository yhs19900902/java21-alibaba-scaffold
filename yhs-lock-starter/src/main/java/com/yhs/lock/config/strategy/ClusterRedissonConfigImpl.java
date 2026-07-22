package com.yhs.lock.config.strategy;

import com.yhs.lock.config.RedissonConfigStrategy;
import com.yhs.lock.constant.RedisGlobalConstant;
import com.yhs.lock.properties.RedissonProperties;
import jodd.util.StringPool;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.config.Config;

import java.util.Arrays;

/**
 * 集群模式
 *
 * @author 07664-linwei
 * @version Id: ClusterRedissonConfigImpl.java, v 0.1 2022/4/24 17:12 lw Exp $
 */
@Slf4j
public class ClusterRedissonConfigImpl implements RedissonConfigStrategy {
    @Override
    public Config createRedissonConfig(RedissonProperties redissonProperties) {
        Config config = new Config();
        String[] addrss = redissonProperties.getAddress().split(StringPool.COMMA);
        //设置cluster节点的服务IP和端口
        for (String s : addrss) {
            config.useClusterServers()
                    .setTimeout(redissonProperties.getConnectTimeout())
                    .setConnectTimeout(redissonProperties.getConnectTimeout())
                    .addNodeAddress(RedisGlobalConstant.REDIS_CONNECTION_PREFIX + s);
            if (StringUtil.isNotBlank(redissonProperties.getPassword())) {
                config.useClusterServers().setPassword(redissonProperties.getPassword());
            }
        }
        log.info("初始化[cluster]方式Config,redisAddress:" + Arrays.toString(addrss));
        return config;
    }
}
