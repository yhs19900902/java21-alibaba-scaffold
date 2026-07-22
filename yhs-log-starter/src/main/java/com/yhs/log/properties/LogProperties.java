package com.yhs.log.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 07664-linwei
 * @version Id: LogProperties.java, v 0.1 2022/6/1 14:24 lw Exp $
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "yhs.log")
public class LogProperties {

    public static final String PREFIX = "yhs.log";

    /**
     * 是否启用
     */
    private Boolean enabled = true;

    /**
     * 日志存储类型
     */
    private OptLogType type = OptLogType.LOGGER;

    /**
     * 远程接口记录日志请求地址
     */
    private String remoteUrl;

}
