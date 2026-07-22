package com.yhs.knife4j.spring.configuration;

import com.yhs.knife4j.core.pojo.BasicAuth;
import com.yhs.knife4j.support.CloudSetting;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * @author 07664-linwei
 * @version Id: Knife4jAggregationProperties.java, v 0.1 2023/7/25 14:29 lw Exp $
 */
@Getter
@Component
@Primary
@ConfigurationProperties(prefix = "knife4j")
public class Knife4jAggregationProperties {

    /**
     * 是否开启Knife4j聚合模式
     */
    private boolean enableAggregation = false;

    /**
     * 文档Basic保护
     */
    private BasicAuth basicAuth;

    /**
     * HTTP接口聚合
     */
    private CloudSetting cloud;


    /**
     * http链接对象属性配置
     */
    private HttpConnectionSetting connectionSetting;

    public void setConnectionSetting(HttpConnectionSetting connectionSetting) {
        this.connectionSetting = connectionSetting;
    }

    public void setEnableAggregation(boolean enableAggregation) {
        this.enableAggregation = enableAggregation;
    }

    public void setBasicAuth(BasicAuth basicAuth) {
        this.basicAuth = basicAuth;
    }

    public void setCloud(CloudSetting cloud) {
        this.cloud = cloud;
    }


}
