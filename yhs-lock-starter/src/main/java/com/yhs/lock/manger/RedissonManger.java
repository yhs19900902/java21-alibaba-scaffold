package com.yhs.lock.manger;

import cn.hutool.core.util.StrUtil;
import com.yhs.base.constant.CommonConstant;
import com.yhs.lock.config.RedissonConfigContext;
import com.yhs.lock.config.strategy.ClusterRedissonConfigImpl;
import com.yhs.lock.config.strategy.MasterSlaveRedissonConfigImpl;
import com.yhs.lock.config.strategy.SentinelRedissonConfigImpl;
import com.yhs.lock.config.strategy.SingleRedissonConfigImpl;
import com.yhs.lock.properties.RedisConnType;
import com.yhs.lock.properties.RedissonProperties;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

import java.util.Objects;

/**
 * @author 07664-linwei
 * @version Id: RedissonManger.java, v 0.1 2022/4/24 17:42 lw Exp $
 */
@Slf4j
public class RedissonManger {

    private Redisson redisson;

    public RedissonManger() {

    }

    public RedissonManger(RedisProperties redisProperties, RedissonProperties redissonProperties) {
        try {
            Config config = RedissonConfigFactory.getInstance().createConfig(redisProperties, redissonProperties);
            redisson = (Redisson) Redisson.create(config);
        } catch (Exception e) {
            log.error("redisson init error", e);
        }
    }

    public Redisson getRedisson() {
        return redisson;
    }

    /**
     * 配置工厂
     */
    private static class RedissonConfigFactory {

        private static final RedissonConfigFactory CONFIG_FACTORY = new RedissonConfigFactory();

        private RedissonConfigFactory() {
        }

        public static RedissonConfigFactory getInstance() {
            return CONFIG_FACTORY;
        }


        Config createConfig(RedisProperties redisProperties, RedissonProperties redissonProperties) throws IllegalAccessException {
            if (redisProperties != null) {
                if (StrUtil.isBlank(redissonProperties.getPassword()) &&
                        StrUtil.isNotBlank(redisProperties.getPassword())) {
                    redissonProperties.setPassword(redisProperties.getPassword());
                }

                if (StrUtil.isBlank(redissonProperties.getAddress())) {
                    RedisProperties.Cluster cluster = redisProperties.getCluster();
                    RedisProperties.Sentinel sentinel = redisProperties.getSentinel();
                    if (!Objects.isNull(sentinel)) {
                        redissonProperties.setAddress(StrUtil.join(CommonConstant.SYMBOL_COMMA, sentinel.getMaster(), sentinel.getNodes()));
                    } else if (!Objects.isNull(cluster)) {
                        redissonProperties.setAddress(StrUtil.join(CommonConstant.SYMBOL_COMMA, cluster.getNodes()));
                    } else {
                        redissonProperties.setAddress(redisProperties.getHost() +
                                CommonConstant.SYMBOL_COLON + redisProperties.getPort());
                    }
                }


            }
            // 单机
            RedissonConfigContext redissonConfigContext = null;
            if (RedisConnType.SINGLE.getConnType().equals(redissonProperties.getType().getConnType())) {
                redissonConfigContext = new RedissonConfigContext(new SingleRedissonConfigImpl());
            } else if (RedisConnType.CLUSTER.getConnType().equals(redissonProperties.getType().getConnType())) {
                redissonConfigContext = new RedissonConfigContext(new ClusterRedissonConfigImpl());
            } else if (RedisConnType.SENTINEL.getConnType().equals(redissonProperties.getType().getConnType())) {
                redissonConfigContext = new RedissonConfigContext(new SentinelRedissonConfigImpl());
            } else if (RedisConnType.MASTER_SLAVE.getConnType().equals(redissonProperties.getType().getConnType())) {
                redissonConfigContext = new RedissonConfigContext(new MasterSlaveRedissonConfigImpl());
            } else {
                log.error("create redisson connection Config fail！,connection type:{}  " +
                        "redis host {}", redissonProperties.getType().getConnType(), redisProperties.getHost());
                throw new IllegalAccessException("create redisson connection Config fail！,connection type:" + redissonProperties.getType().getConnType());
            }
            return redissonConfigContext.createRedissonConfig(redissonProperties);
        }
    }
}
