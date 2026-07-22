package com.yhs;

import com.yhs.manger.RedisJsonManger;
import com.yhs.properties.RedisJsonProperties;
import com.yhs.service.RedisJsonService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * redis json 自动注入
 *
 * @author 07664-linwei
 * @version Id: RedisJsonAutoConfiguration.java, v 0.1 2022/8/30 10:16 lw Exp $
 */
@Configuration
@EnableConfigurationProperties(value = RedisJsonProperties.class)
public class RedisJsonAutoConfiguration {


    @Bean
    public RedisJsonManger redisJsonManger(RedisJsonProperties redisJsonProperties) {
        return new RedisJsonManger(redisJsonProperties);
    }

    @Bean
    public RedisJsonService redisJsonService(RedisJsonManger redisJsonManger) {
        return new RedisJsonService(redisJsonManger.getUnifiedJedis());
    }
}
