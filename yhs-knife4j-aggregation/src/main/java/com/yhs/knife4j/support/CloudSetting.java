package com.yhs.knife4j.support;

import com.yhs.knife4j.cloud.CloudRoute;
import com.yhs.knife4j.core.pojo.BasicAuth;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author 07664-linwei
 * @version Id: CloudSetting.java, v 0.1 2023/7/25 14:28 lw Exp $
 */
@ConfigurationProperties(prefix = "knife4j.cloud")
public class CloudSetting {

    /**
     * 是否启用
     */
    private boolean enable;
    /**
     * 微服务集合
     */
    private List<CloudRoute> routes;

    /**
     * 配置的Route路由服务的公共Basic验证信息，仅作用与访问Swagger接口时使用，具体服务的其他接口不使用该配置
     */
    private BasicAuth routeAuth;


    public BasicAuth getRouteAuth() {
        return routeAuth;
    }

    public void setRouteAuth(BasicAuth routeAuth) {
        this.routeAuth = routeAuth;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public List<CloudRoute> getRoutes() {
        return routes;
    }

    public void setRoutes(List<CloudRoute> routes) {
        this.routes = routes;
    }
}
