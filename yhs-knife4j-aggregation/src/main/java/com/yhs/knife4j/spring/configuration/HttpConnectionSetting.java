package com.yhs.knife4j.spring.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 07664-linwei
 * @version Id: HttpConnectionSetting.java, v 0.1 2023/7/25 9:49 lw Exp $
 */
@Data
@ConfigurationProperties(prefix = "knife4j.connection-setting")
public class HttpConnectionSetting {
    /**
     * 默认值
     */
    private final static int DEFAULT_TIMEOUT = 10000;

    /**
     * SocketTimeout
     */
    private int socketTimeout = DEFAULT_TIMEOUT;

    /**
     * ConnectTimeout
     */
    private int connectTimeout = DEFAULT_TIMEOUT;

    /**
     * 最大连接上限数
     */
    private int maxConnectionTotal = 200;

    /**
     * 单个路由基础连接数
     */
    private int maxPreRoute = 20;
}
