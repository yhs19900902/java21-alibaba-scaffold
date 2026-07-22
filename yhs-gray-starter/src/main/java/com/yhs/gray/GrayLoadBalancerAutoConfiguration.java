package com.yhs.gray;

import com.yhs.gray.feign.GrayFeignRequestInterceptor;
import com.yhs.gray.rule.GrayLoadBalancerClientConfiguration;
import feign.RequestInterceptor;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 07664-linwei
 * @version Id: GrayLoadBalancerAutoConfiguration.java, v 0.1 2022/5/6 19:30 lw Exp $
 */
@Configuration
@LoadBalancerClients(defaultConfiguration = GrayLoadBalancerClientConfiguration.class)
public class GrayLoadBalancerAutoConfiguration {

    @Bean
    public RequestInterceptor grayFeignRequestInterceptor() {
        return new GrayFeignRequestInterceptor();
    }
}
