package com.yhs.knife4j.core.cache;

import com.yhs.knife4j.core.RouteCache;
import com.yhs.knife4j.core.pojo.SwaggerRoute;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 07664-linwei
 * @version Id: RouteInMemoryCache.java, v 0.1 2023/7/25 9:23 lw Exp $
 */
@Primary
@Service
public class RouteInMemoryCache implements RouteCache<String, SwaggerRoute> {
    private final ConcurrentHashMap<String, SwaggerRoute> cache = new ConcurrentHashMap<>();

    @Override
    public boolean put(String s, SwaggerRoute swaggerRoute) {
        cache.put(s, swaggerRoute);
        return true;
    }

    @Override
    public SwaggerRoute get(String s) {
        return cache.get(s);
    }

    @Override
    public boolean remove(String s) {
        cache.remove(s);
        return true;
    }
}
