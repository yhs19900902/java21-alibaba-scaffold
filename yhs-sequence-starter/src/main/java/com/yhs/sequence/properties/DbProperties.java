package com.yhs.sequence.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 07664-linwei
 * @version Id: dbProperties.java, v 0.1 2022/4/29 17:01 lw Exp $
 */
@Data
@Component
@ConfigurationProperties(prefix = "yhs.sequence.db")
public class DbProperties {

    /**
     * 获取range步长[可选，默认：1000]
     */
    private int step = 1000;
    /**
     * 是否本地存储序号 true 本地存储 false redis存储（redis 存储保证本地有redis配置否则走本地存储）
     */
    private boolean localStorage = false;

    /**
     * 序列号分配起始值[可选：默认：0]
     */
    private long stepStart = 0;

    /**
     * 表名称
     */
    private String tableName = "yhs_sequence";

    /**
     * 重试次数
     */
    private int retryTimes = 1;


}
