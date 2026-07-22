package com.yhs.knife4j.core.pojo;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.yhs.knife4j.cloud.CloudRoute;
import com.yhs.knife4j.core.RouteDispatcher;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 最终返回其阿奴单Swagger 的数据结构
 *
 * @author 07664-linwei
 * @version Id: SwaggerRoute.java, v 0.1 2023/7/25 8:49 lw Exp $
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwaggerRoute {

    private String name;
    /**
     * 该属性JSON 序列化时不能序列化出去，防止暴露服务的真实地址，存在安全隐患
     */
    private transient String uri;

    private String header;
    /**
     * 是否需要添加auth的header
     */
    private String basicAuth;

    private String location;
    /**
     * Disk模式返回的OpenAPI规范json数据，作为结构来说不需要序列化
     */
    private transient String content;

    private String swaggerVersion;

    private String servicePath;

    private boolean debug = true;
    /**
     * 当前的分组请求是否需要服务端代理
     */
    private boolean routerProxy = true;

    private String version;

    private Integer order = 1;

    public SwaggerRoute(CloudRoute cloudRoute) {
        if (Objects.nonNull(cloudRoute)) {
            this.header = cloudRoute.pkId();
            if (Objects.nonNull(cloudRoute.getRouteAuth()) && cloudRoute.getRouteAuth().isEnable()) {
                this.basicAuth = cloudRoute.pkId();
            }
            this.name = cloudRoute.getName();
            if (StrUtil.isNotBlank(cloudRoute.getUri())) {
                // 判断
                if (!ReUtil.isMatch("(http|https)://.*?$", cloudRoute.getUri())) {
                    this.uri = "http://" + cloudRoute.getUri();
                } else {
                    this.uri = cloudRoute.getUri();
                }
            }
            if (StrUtil.isNotBlank(cloudRoute.getServicePath()) && !StrUtil.equals(cloudRoute.getServicePath(), RouteDispatcher.ROUTE_BASE_PATH)) {
                // 判断是否是/ 开头
                if (!StrUtil.startWithAny(cloudRoute.getServicePath(), RouteDispatcher.ROUTE_BASE_PATH, "http://", "https://")) {
                    this.servicePath = RouteDispatcher.ROUTE_BASE_PATH + cloudRoute.getServicePath();
                } else {
                    this.servicePath = cloudRoute.getServicePath();
                }

            }
            this.version = cloudRoute.getVersion();
            this.location = cloudRoute.getLocation();
            this.swaggerVersion = cloudRoute.getSwaggerVersion();
            this.order = cloudRoute.getOrder();
        }
    }
}
