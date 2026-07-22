package com.yhs.knife4j.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.yhs.knife4j.core.pojo.BasicAuth;
import com.yhs.knife4j.core.pojo.CommonAuthRoute;
import com.yhs.knife4j.core.pojo.SwaggerRoute;

import java.util.List;
import java.util.Optional;

/**
 * @author 07664-linwei
 * @version Id: RouteRepository.java, v 0.1 2023/7/25 14:16 lw Exp $
 */
public interface RouteRepository {

    /**
     * start心跳监听程序
     */
    default void start() {
    }

    /**
     * stop心跳监听乘车
     */
    default void close() {
    }

    /**
     * 校验请求Header是否正确
     *
     * @param header 请求头
     * @return 是否校验成功
     */
    boolean checkRoute(String header);

    /**
     * 根据请求header获取
     *
     * @param header 请求头
     * @return 服务Route
     */
    SwaggerRoute getRoute(String header);

    /**
     * 获取所有
     *
     * @return 返回所有Routes服务
     */
    List<SwaggerRoute> getRoutes();

    List<SwaggerRoute> getRoutesAll();

    /**
     * 根据Header请求头获取Basic基础信息
     *
     * @param header 请求头
     * @return Basic基础信息
     */
    default BasicAuth getAuth(String header) {
        return null;
    }

    /**
     * 获取route中配置的Basic信息
     *
     * @param header           请求头
     * @param commonAuthRoutes routes集合
     * @return Basic基础信息
     */
    default BasicAuth getAuthByRoute(String header, List<? extends CommonAuthRoute> commonAuthRoutes) {
        BasicAuth basicAuth = null;
        if (CollUtil.isNotEmpty(commonAuthRoutes)) {
            //判断route中是否设置了basic，如果route中存在，则以route中为准
            Optional<? extends CommonAuthRoute> cloudRouteOptional = commonAuthRoutes.stream().filter(cloudRoute -> CharSequenceUtil.equalsIgnoreCase(cloudRoute.pkId(), header)).findFirst();
            if (cloudRouteOptional.isPresent()) {
                CommonAuthRoute route = cloudRouteOptional.get();
                if (route.getRouteAuth() != null && route.getRouteAuth().isEnable()) {
                    basicAuth = route.getRouteAuth();
                }
            }
        }
        return basicAuth;
    }
}
