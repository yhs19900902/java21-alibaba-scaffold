package com.yhs.job.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import com.yhs.job.config.properties.JobProperties;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.stream.Collectors;

/**
 * @author 07664-linwei
 * @version Id: XxlJobAutoConfiguration.java, v 0.1 2022/4/27 14:36 lw Exp $
 */
@Configuration(proxyBeanMethods = false)
@EnableAutoConfiguration
@EnableConfigurationProperties(JobProperties.class)
public class XxlJobAutoConfiguration {

    /**
     * 服务名称 包含 XXL_JOB_ADMIN 则说明是 Admin
     */
    private static final String XXL_JOB_ADMIN = "yhs-xxljob-admin";

    /**
     * 配置xxl-job 执行器，提供自动发现 xxl-job-admin 能力
     *
     * @param jobProperties   xxl 配置
     * @param discoveryClient 注册发现客户端
     * @return
     */
    @Bean
    public XxlJobSpringExecutor xxlJobSpringExecutor(JobProperties jobProperties, Environment environment,
                                                     DiscoveryClient discoveryClient) {
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        // 应用名默认为服务名
        String appName = jobProperties.getAppName();
        if (!StringUtils.hasText(appName)) {
            appName = environment.getProperty("spring.application.name");
        }
        xxlJobSpringExecutor.setAppname(appName);
        xxlJobSpringExecutor.setAddress(jobProperties.getAddress());
        xxlJobSpringExecutor.setIp(jobProperties.getIp());
        xxlJobSpringExecutor.setPort(jobProperties.getPort());
        xxlJobSpringExecutor.setAccessToken(jobProperties.getAccessToken());
        xxlJobSpringExecutor.setLogPath(jobProperties.getLogPath());
        xxlJobSpringExecutor.setLogRetentionDays(jobProperties.getLogRetentionDays());

        // 如果配置为空则获取注册中心的服务列表
        if (!StringUtils.hasText(jobProperties.getAdminAddresses())) {
            String serverList = discoveryClient.getServices().stream().filter(s -> s.contains(XXL_JOB_ADMIN))
                    .flatMap(s -> discoveryClient.getInstances(s).stream()).map(instance -> String
                            .format("http://%s:%s/%s", instance.getHost(), instance.getPort(), "job"))
                    .collect(Collectors.joining(","));
            xxlJobSpringExecutor.setAdminAddresses(serverList);
        } else {
            xxlJobSpringExecutor.setAdminAddresses(jobProperties.getAdminAddresses());
        }

        return xxlJobSpringExecutor;
    }
}
