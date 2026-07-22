package com.yhs.knife4j.spring.configuration;

import cn.hutool.core.util.StrUtil;
import com.yhs.knife4j.core.RouteCache;
import com.yhs.knife4j.core.RouteDispatcher;
import com.yhs.knife4j.core.RouteRepository;
import com.yhs.knife4j.core.cache.RouteInMemoryCache;
import com.yhs.knife4j.core.common.ExecutorEnum;
import com.yhs.knife4j.core.filter.Knife4jRouteProxyFilter;
import com.yhs.knife4j.core.filter.Knife4jSecurityBasicAuthFilter;
import com.yhs.knife4j.core.pojo.BasicAuth;
import com.yhs.knife4j.core.pojo.SwaggerRoute;
import com.yhs.knife4j.repository.CloudRepository;
import com.yhs.knife4j.support.CloudSetting;
import com.yhs.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author 07664-linwei
 * @version Id: Knife4jAggregationAutoConfiguration.java, v 0.1 2023/7/25 14:31 lw Exp $
 */
@Component
@Configuration
@EnableConfigurationProperties({Knife4jAggregationProperties.class, CloudSetting.class, BasicAuth.class, HttpConnectionSetting.class})
@ConditionalOnProperty(name = "knife4j.enable-aggregation", havingValue = "true")
public class Knife4jAggregationAutoConfiguration {


    final Environment environment;

    @Autowired
    public Knife4jAggregationAutoConfiguration(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public RouteCache<String, SwaggerRoute> routeCache() {
        return new RouteInMemoryCache();
    }

    @Bean(initMethod = "start", destroyMethod = "close")
    @ConditionalOnProperty(name = "knife4j.cloud.enable", havingValue = "true")
    public CloudRepository cloudRepository(@Autowired Knife4jAggregationProperties knife4jAggregationProperties
            , @Autowired RedisService redisService) {
        return new CloudRepository(knife4jAggregationProperties.getCloud(), redisService);
    }


    @Bean
    @ConditionalOnClass(RouteDispatcher.class)
    public RouteDispatcher routeDispatcher(@Autowired RouteRepository routeRepository,
                                           @Autowired RouteCache<String, SwaggerRoute> routeCache) {
        //获取当前项目的contextPath
        String contextPath = environment.getProperty("server.servlet.context-path");
        if (StrUtil.isBlank(contextPath)) {
            contextPath = "/";
        }
        if (StrUtil.isNotBlank(contextPath) && !StrUtil.equals(contextPath, RouteDispatcher.ROUTE_BASE_PATH)) {
            //判断是否/开头
            if (!StrUtil.startWith(contextPath, RouteDispatcher.ROUTE_BASE_PATH)) {
                contextPath = RouteDispatcher.ROUTE_BASE_PATH + contextPath;
            }
        }
        return new RouteDispatcher(routeRepository, routeCache, ExecutorEnum.APACHE, contextPath);
    }

    @Bean
    public FilterRegistrationBean routeProxyFilter(@Autowired RouteDispatcher routeDispatcher) {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new Knife4jRouteProxyFilter(routeDispatcher));
        filterRegistrationBean.setOrder(99);
        filterRegistrationBean.setEnabled(true);
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }

    @Bean
    @ConditionalOnProperty(name = "knife4j.basic-auth.enable", havingValue = "true")
    public FilterRegistrationBean routeBasicFilter(@Autowired Knife4jAggregationProperties knife4jAggregationProperties) {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new Knife4jSecurityBasicAuthFilter(knife4jAggregationProperties.getBasicAuth()));
        filterRegistrationBean.setOrder(10);
        filterRegistrationBean.setEnabled(true);
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }
}
