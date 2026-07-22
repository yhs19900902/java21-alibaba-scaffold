package com.yhs.lock.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 07664-linwei
 * @version Id: RedissonProperties.java, v 0.1 2022/4/24 17:11 lw Exp $
 */
@Data
@ConfigurationProperties(prefix = RedissonProperties.PREFIX)
public class RedissonProperties {

    public static final String PREFIX = "yhs.redisson";
    /**
     * redis 主机地址，ip:port 多个逗号分隔
     */
    private String address;
    /**
     * 连接类型  默认 单机   集群
     */
    private RedisConnType type = RedisConnType.SINGLE;
    /**
     * redis 密码
     */
    private String password;
    /**
     * 数据库
     */
    private int database = 0;


    private int connectTimeout = 30000;
}
