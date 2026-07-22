package com.yhs.knife4j.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.yhs.knife4j.cloud.CloudRoute;
import com.yhs.knife4j.core.pojo.BasicAuth;
import com.yhs.knife4j.core.pojo.SwaggerRoute;
import com.yhs.knife4j.support.CloudSetting;
import com.yhs.redis.service.RedisService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于本地配置的方式动态聚合云端(http)任意OpenAPI
 *
 * @author 07664-linwei
 * @version Id: CloudRepository.java, v 0.1 2023/7/25 14:27 lw Exp $
 */
public class CloudRepository extends AbstractRepository {
    private static final String ROUTE_MAP_KEY = "aggregation_routes_map";
    private static final String CHECK_ROUTE_MAP_KEY = "aggregation_check_routes_map";
    final Logger logger = LoggerFactory.getLogger(CloudRepository.class);
    private final Map<String, Integer> checkNum = new HashMap<>(256);
    private final Integer maxCheckNum = 10;
    private final RedisService redisService;
    private final CloudSetting cloudSetting;
    private volatile boolean stop = false;
    private Thread thread;

    public CloudRepository(CloudSetting cloudSetting, RedisService redisService) {
        this.redisService = redisService;
        this.cloudSetting = cloudSetting;
        if (cloudSetting != null && CollUtil.isNotEmpty(cloudSetting.getRoutes())) {
            cloudSetting.getRoutes().forEach(cloudRoute -> {
                if (cloudRoute.getRouteAuth() == null || !cloudRoute.getRouteAuth().isEnable()) {
                    cloudRoute.setRouteAuth(cloudSetting.getRouteAuth());
                }
                addRouteMap(new SwaggerRoute(cloudRoute));
            });
        }
    }

    public boolean addRouteMap(SwaggerRoute swaggerRoute) {
        try {
            routeMap.put(swaggerRoute.getHeader(), swaggerRoute);
            redisService.hset(ROUTE_MAP_KEY, swaggerRoute.getHeader(), swaggerRoute);
        } catch (Exception e) {
            logger.error("add routeMap error", e);
            return false;
        }
        return true;
    }

    public boolean addCheckRoute(CloudRoute cloudRoute) {
        try {
            SwaggerRoute swaggerRoute = new SwaggerRoute(cloudRoute);
            checkRouteMap.put(cloudRoute.pkId(), swaggerRoute);
            checkNum.put(cloudRoute.pkId(), 1);
            redisService.hset(CHECK_ROUTE_MAP_KEY, cloudRoute.pkId(), swaggerRoute);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }

    @XxlJob("checkRoute")
    public void checkRoute() {
        if (checkRouteMap.size() > 0) {
            checkRouteMap.forEach((pkId, cloudRoute) -> {
                if (!queryCheckNum(pkId)) {
                    removeCheckRouteMap(pkId);
                    return;
                }
                String uri = cloudRoute.getUri();
                StringBuilder urlBuilder = new StringBuilder();
                if (!StrUtil.startWith(uri, "http")) {
                    urlBuilder.append("http://");
                }
                urlBuilder.append(uri + cloudRoute.getLocation());
                if (logger.isDebugEnabled()) {
                    logger.debug("hearbeat url:{}", urlBuilder);
                }
                HttpGet get = new HttpGet(urlBuilder.toString());
                try {
                    CloseableHttpResponse response = getClient().execute(get);
                    if (response != null) {
                        int statusCode = response.getStatusLine().getStatusCode();
                        if (statusCode == 200) {
                            System.out.println("addRouteMap:" + cloudRoute);
                            addRouteMap(cloudRoute);
                            removeCheckRouteMap(pkId);
                            checkNum.remove(pkId);
                        }
                    }
                } catch (Exception ignored) {
                }
            });
        }
    }

    private boolean queryCheckNum(String pkId) {
        Integer num = checkNum.get(pkId);
        if (null != num && num <= maxCheckNum) {
            checkNum.put(pkId, num + 1);
            return true;
        }
        checkNum.remove(pkId);
        return false;
    }

    @Override
    public BasicAuth getAuth(String header) {
        BasicAuth basicAuth = null;
        if (cloudSetting != null && CollectionUtil.isNotEmpty(cloudSetting.getRoutes())) {
            if (cloudSetting.getRouteAuth() != null && cloudSetting.getRouteAuth().isEnable()) {
                basicAuth = cloudSetting.getRouteAuth();
                //判断route服务中是否再单独配置
                BasicAuth routeBasicAuth = getAuthByRoute(header, cloudSetting.getRoutes());
                if (routeBasicAuth != null) {
                    basicAuth = routeBasicAuth;
                }
            } else {
                basicAuth = getAuthByRoute(header, cloudSetting.getRoutes());
            }
        }
        return basicAuth;
    }

    public CloudSetting getCloudSetting() {
        return cloudSetting;
    }

    @Override
    public void start() {
        // 初始化routeMap
        initializationRoutesMap();
    }

    @XxlJob("cloudHeartbeat")
    public void cloudHeartbeat() {
        logger.debug("Cloud hearbeat start working...");
        try {
            List<SwaggerRoute> routes = getRoutesAll();
            if (this.cloudSetting != null && CollectionUtil.isNotEmpty(routes)) {
                routes.forEach(cloudRoute -> {
                    String uri = cloudRoute.getUri();
                    logger.debug("Cloud hearbeat start working... {}", uri);
                    StringBuilder urlBuilder = new StringBuilder();
                    if (!StrUtil.startWith(uri, "http")) {
                        urlBuilder.append("http://");
                    }
                    urlBuilder.append(uri);
                    urlBuilder.append(cloudRoute.getLocation());
                    if (logger.isDebugEnabled()) {
                        logger.debug("hearbeat url:{}", urlBuilder);
                    }
                    HttpGet get = new HttpGet(urlBuilder.toString());
                    try {
                        CloseableHttpResponse response = getClient().execute(get);
                        if (response != null) {
                            int statusCode = response.getStatusLine().getStatusCode();
                            EntityUtils.consumeQuietly(response.getEntity());
                            if (logger.isDebugEnabled()) {
                                logger.debug("statusCode:{}", statusCode);
                            }
                            if (!(statusCode == 200)) {
                                //服务不存在,下线处理
                                this.removeRouteMap(cloudRoute.getHeader());
                            }
                        } else {
                            //服务不存在,下线处理
                            this.removeRouteMap(cloudRoute.getHeader());
                            get.abort();
                        }
                    } catch (Exception e) {
                        logger.debug("heartBeat url check error,message:" + e.getMessage(), e);
                        //服务不存在,下线处理
                        this.removeRouteMap(cloudRoute.getHeader());
                    }

                });
            }
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
        }
    }


    public void removeRouteMap(String header) {
        routeMap.remove(header);
        redisService.hdel(ROUTE_MAP_KEY, header);
    }

    private void removeCheckRouteMap(String pkId) {
        checkRouteMap.remove(pkId);
        redisService.hdel(CHECK_ROUTE_MAP_KEY, pkId);
    }

    public void initializationRoutesMap() {
        if (redisService.hasKey(ROUTE_MAP_KEY)) {
            Map<String, SwaggerRoute> hmget = (Map<String, SwaggerRoute>) redisService.hmget(ROUTE_MAP_KEY);
            routeMap.putAll(hmget);
            if (redisService.hasKey(CHECK_ROUTE_MAP_KEY)) {
                checkRouteMap.putAll((Map<String, SwaggerRoute>) redisService.hmget(CHECK_ROUTE_MAP_KEY));
            }
        }

    }

    @Override
    public void close() {
        logger.info("stop Cloud heartbeat Holder thread.");
        this.stop = true;
        if (this.thread != null) {
            ThreadUtil.interrupt(this.thread, true);
        }
    }

}
