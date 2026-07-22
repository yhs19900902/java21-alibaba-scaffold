package com.yhs.manger;

import cn.hutool.core.util.StrUtil;
import com.yhs.base.constant.CommonConstant;
import com.yhs.properties.RedisJsonProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author 07664-linwei
 * @version Id: RedisJsonManger.java, v 0.1 2022/8/30 10:18 lw Exp $
 */
@Slf4j
public class RedisJsonManger {

    private RedisJsonProperties redisJsonProperties;


    private UnifiedJedis unifiedJedis;


    public RedisJsonManger(RedisJsonProperties redisJsonProperties) {
        this.redisJsonProperties = redisJsonProperties;
        this.unifiedJedis = RedisJsonConfigFactory.getInstance().create(redisJsonProperties);
    }


    public UnifiedJedis getUnifiedJedis() {
        return unifiedJedis;
    }


    private static class RedisJsonConfigFactory {

        private static final RedisJsonConfigFactory CONFIG_FACTORY = new RedisJsonConfigFactory();

        private RedisJsonConfigFactory() {
        }

        public static RedisJsonConfigFactory getInstance() {
            return CONFIG_FACTORY;
        }

        UnifiedJedis create(RedisJsonProperties redisJsonProperties) {
            log.info("init jedis conn");
            if (redisJsonProperties == null) {
                // Todo 抛出异常
                return null;
            }
            RedisJsonProperties.Cluster cluster = redisJsonProperties.getCluster();

            GenericObjectPoolConfig<Connection> poolConfig
                    = new GenericObjectPoolConfig<>();
            if (cluster != null) {
                log.info("init jedis cluster conn");
                // cluster conn
                Set<HostAndPort> hostAndPorts = new HashSet<>();
                List<String> nodes = cluster.getNodes();
                nodes.forEach(node ->
                {
                    String[] split = node.split(CommonConstant.SYMBOL_COLON);
                    hostAndPorts.add(new HostAndPort(split[0], Integer.parseInt(split[1])));
                });
                if (StrUtil.isBlank(redisJsonProperties.getUsername())) {
                    return new JedisCluster(hostAndPorts, redisJsonProperties.getConnectTimeout().getNano(),
                            redisJsonProperties.getTimeout().getNano(), cluster.getMaxRedirects(),
                            redisJsonProperties.getPassword(), poolConfig);
                }
                if (StrUtil.isBlank(redisJsonProperties.getPassword())) {
                    return new JedisCluster(hostAndPorts, redisJsonProperties.getConnectTimeout().getNano(),
                            redisJsonProperties.getTimeout().getNano(), cluster.getMaxRedirects(), poolConfig);
                } else {
                    return new JedisCluster(hostAndPorts, redisJsonProperties.getConnectTimeout().getNano(),
                            redisJsonProperties.getTimeout().getNano(), cluster.getMaxRedirects(),
                            redisJsonProperties.getUsername(), redisJsonProperties.getPassword(), poolConfig);
                }
            } else {
                log.info("init jedis single conn");
                // single conn
                if (StrUtil.isBlank(redisJsonProperties.getUsername())) {
                    return new JedisPooled(poolConfig, redisJsonProperties.getHost(), redisJsonProperties.getPort(), redisJsonProperties.getConnectTimeout().getNano()
                            , redisJsonProperties.getPassword(), redisJsonProperties.getDatabase(), redisJsonProperties.isSsl());
                }
                if (StrUtil.isBlank(redisJsonProperties.getPassword())) {
                    return new JedisPooled(redisJsonProperties.getHost(), redisJsonProperties.getPort());
                } else {
                    return new JedisPooled(poolConfig, redisJsonProperties.getHost(), redisJsonProperties.getPort(), redisJsonProperties.getConnectTimeout().getNano()
                            , redisJsonProperties.getUsername(), redisJsonProperties.getPassword(),
                            redisJsonProperties.getDatabase(), redisJsonProperties.isSsl());
                }

            }
        }

    }
}
