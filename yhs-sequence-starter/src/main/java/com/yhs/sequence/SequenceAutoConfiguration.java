package com.yhs.sequence;

import com.yhs.sequence.builder.SnowflakeBuilder;
import com.yhs.sequence.builder.db.DBSequenceBuilder;
import com.yhs.sequence.number.DbSequenceGenerate;
import com.yhs.sequence.number.SnowflakeSequence;
import com.yhs.sequence.properties.DbProperties;
import com.yhs.sequence.properties.SnowflakeProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 序号生成自动配置
 *
 * @author 07664-linwei
 * @version Id: SequenceAutoConfiguration.java, v 0.1 2022/4/29 16:31 lw Exp $
 */
@Configuration
@ComponentScan("com.yhs.sequence")
public class SequenceAutoConfiguration {

    /**
     * snowflak 算法作为发号器实现
     *
     * @param properties 雪花配置
     * @return 雪花序号生成器
     */
    @Bean
    @ConditionalOnBean(SnowflakeProperties.class)
    public SnowflakeSequence snowflakeSequence(SnowflakeProperties properties) {
        return SnowflakeBuilder.create().datacenterId(properties.getDatacenterId())
                .workerId(properties.getWorkerId()).build();
    }


    private DbSequenceGenerate dbSequenceGenerate(DbProperties properties, DataSource dataSource) {
        return DBSequenceBuilder.create()
                .dataSource(dataSource)
                .step(properties.getStep())
                .setLocalStorage(properties.isLocalStorage())
                .stepStart(properties.getStepStart())
                .tableName(properties.getTableName())
                .build();
    }

    /**
     * db +redis 算法作为发号器实现 保证多个生成器生成的序号保持有序递增
     *
     * @param properties db配置
     * @return db序号生成器
     */
    @Bean
    @ConditionalOnBean({RedisProperties.class, DbProperties.class, DataSource.class})
    public DbSequenceGenerate dbSequenceRedisGenerate(DbProperties properties, DataSource dataSource, RedisProperties redisProperties) {
        if (properties.isLocalStorage()) {
            return dbSequenceGenerate(properties, dataSource);
        } else {
            return DBSequenceBuilder.create()
                    .dataSource(dataSource)
                    .step(properties.getStep())
                    .stepStart(properties.getStepStart())
                    .tableName(properties.getTableName())
                    .setLocalStorage(properties.isLocalStorage())
                    .redisProperties(redisProperties)
                    .build();
        }
    }
}
