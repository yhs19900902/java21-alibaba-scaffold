package com.yhs.knife4j.repository;

import cn.hutool.core.util.StrUtil;
import com.yhs.knife4j.core.RouteRepository;
import com.yhs.knife4j.core.ext.PoolingConnectionManager;
import com.yhs.knife4j.core.pojo.SwaggerRoute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author 07664-linwei
 * @version Id: AbstractRepository.java, v 0.1 2023/7/25 14:22 lw Exp $
 */
public abstract class AbstractRepository extends PoolingConnectionManager implements RouteRepository {

    /**
     * 心跳检测间隔(30s)
     */
    protected static final Long HEART_BEAT_DURATION = 10000L;

    protected final ConcurrentHashMap<String, SwaggerRoute> routeMap = new ConcurrentHashMap<>();

    protected ConcurrentHashMap<String, SwaggerRoute> checkRouteMap = new ConcurrentHashMap<>(256);

    @Override
    public boolean checkRoute(String header) {
        if (StrUtil.isNotBlank(header)) {
            return routeMap.containsKey(header);
        }
        return false;
    }

    @Override
    public SwaggerRoute getRoute(String header) {
        return routeMap.get(header);
    }

    @Override
    public List<SwaggerRoute> getRoutes() {
        //排序规则,asc
        Collection<SwaggerRoute> swaggerRoutes = routeMap.values();
        return swaggerRoutes.stream().sorted(Comparator.comparingInt(SwaggerRoute::getOrder))
                .collect(Collectors.groupingBy(SwaggerRoute::getName))
                .values()
                .stream()
                .map(list -> list.get(0))
                .collect(Collectors.toList());
    }

    @Override
    public List<SwaggerRoute> getRoutesAll() {
        Collection<SwaggerRoute> swaggerRoutes = routeMap.values();
        return new ArrayList<>(swaggerRoutes);
    }
}

