package com.yhs.job.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 07664-linwei
 * @version Id: JobProperties.java, v 0.1 2022/4/27 14:33 lw Exp $
 */
@Data
@ConfigurationProperties(prefix = "yhs.job")
public class JobProperties {
    /**
     * 调度中心部署跟地址
     */
    private String adminAddresses;
    /**
     * 执行器通讯TOKEN
     */
    private String accessToken;
    /**
     * 执行器AppName
     */
    private String appName;
    /**
     * 服务注册地址
     */
    private String address;
    /**
     * 执行器IP,：默认为空表示自动获取IP，多网卡时可手动设置指定IP
     */
    private String ip;
    /**
     * 执行器端口号
     */
    private int port = 0;
    /**
     * 执行器运行日志文件存储磁盘路径
     */
    private String logPath = "logs/applogs/xxl-job/jobhandler";
    /**
     * 执行器日志保存天数
     */
    private int logRetentionDays = 30;
}
