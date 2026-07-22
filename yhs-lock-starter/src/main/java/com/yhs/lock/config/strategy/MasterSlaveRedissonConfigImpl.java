package com.yhs.lock.config.strategy;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import com.yhs.lock.config.RedissonConfigStrategy;
import com.yhs.lock.constant.RedisGlobalConstant;
import com.yhs.lock.properties.RedissonProperties;
import lombok.extern.slf4j.Slf4j;
import org.redisson.config.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 07664-linwei
 * @version Id: MasterSlaveRedissonConfigImpl.java, v 0.1 2022/4/24 17:16 lw Exp $
 */
@Slf4j
public class MasterSlaveRedissonConfigImpl implements RedissonConfigStrategy {

    @Override
    public Config createRedissonConfig(RedissonProperties redissonProperties) {

        Config config = new Config();
        String[] adds = redissonProperties.getAddress().split(StrPool.COMMA);
        config.useMasterSlaveServers()
                .setMasterAddress(adds[0]);
        if (CharSequenceUtil.isNotBlank(redissonProperties.getPassword())) {
            config.useMasterSlaveServers().setPassword(redissonProperties.getPassword());
        }
        config.useMasterSlaveServers().setDatabase(redissonProperties.getDatabase());
        List<String> slaveList = new ArrayList<>();
        for (String addrToken : adds) {
            slaveList.add(RedisGlobalConstant.REDIS_CONNECTION_PREFIX + addrToken);
        }
        slaveList.remove(0);
        config.useMasterSlaveServers()
                .setTimeout(redissonProperties.getConnectTimeout())
                .setConnectTimeout(redissonProperties.getConnectTimeout()).
                addSlaveAddress(slaveList.toArray(new String[0]));
        log.info("初始化[MASTERSLAVE]方式Config,redisAddress:" + redissonProperties.getAddress());
        return config;
    }
}
