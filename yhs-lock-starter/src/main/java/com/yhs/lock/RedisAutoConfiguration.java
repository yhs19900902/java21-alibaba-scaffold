package com.yhs.lock;

import com.yhs.lock.idempotent.aop.IdempotentAspect;
import com.yhs.lock.manger.RedissonLock;
import com.yhs.lock.manger.RedissonManger;
import com.yhs.lock.properties.RedissonProperties;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 07664-linwei
 * @version Id: RedisAutoConfiguration.java, v 0.1 2022/4/24 17:37 lw Exp $
 */
@Slf4j
@Configuration
@ConditionalOnClass(Redisson.class)
@EnableConfigurationProperties(RedissonProperties.class)
public class RedisAutoConfiguration {


    @Bean
    public RedissonManger redissonManger(RedisProperties redisProperties, RedissonProperties redissonProperties) {
        return new RedissonManger(redisProperties, redissonProperties);
    }

    @Bean
    public RedissonLock redissonLock(RedissonManger redissonManger) {
        return new RedissonLock(redissonManger);
    }


    @Bean
    @ConditionalOnBean(RedissonLock.class)
    public IdempotentAspect idempotentAspect(RedissonLock redissonLock) {
        return new IdempotentAspect(redissonLock);
    }
}
