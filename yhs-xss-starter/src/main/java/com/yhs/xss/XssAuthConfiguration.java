package com.yhs.xss;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yhs.xss.filter.XssFilter;
import com.yhs.xss.properties.XssProperties;
import com.yhs.xss.wrapper.XssStringJsonSerializer;
import jakarta.servlet.Filter;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.HashMap;
import java.util.Map;

import static com.yhs.xss.filter.XssFilter.IGNORE_PATH;

/**
 * xss 跨站攻击自动配置
 *
 * @author 07664-linwei
 * @version Id: XssAuthConfiguration.java, v 0.1 2022/4/24 10:40 lw Exp $
 */
@AllArgsConstructor
@EnableConfigurationProperties({XssProperties.class})
public class XssAuthConfiguration {

    private final XssProperties xssProperties;

    /**
     * 配置跨站攻击过滤器
     */
    @Bean
    public FilterRegistrationBean<Filter> filterRegistrationBean() {
        FilterRegistrationBean<Filter> filterRegistration = new FilterRegistrationBean<>();
        filterRegistration.setFilter(new XssFilter());
        filterRegistration.addUrlPatterns(xssProperties.getPatterns().toArray(new String[0]));
        filterRegistration.setOrder(xssProperties.getOrder());
        Map<String, String> initParameters = new HashMap<>(4);
        initParameters.put(IGNORE_PATH, CollUtil.join(xssProperties.getIgnorePaths(), ","));
        filterRegistration.setInitParameters(initParameters);
        filterRegistration.setOrder(1);
        return filterRegistration;
    }


    @Bean
    @ConditionalOnClass(value = {ObjectMapper.class, Jackson2ObjectMapperBuilder.class})
    public Jackson2ObjectMapperBuilderCustomizer jacksonXssCleanJsonDeserializerCustomer() {
        return builder -> builder.deserializers(new XssStringJsonSerializer());
    }


}
