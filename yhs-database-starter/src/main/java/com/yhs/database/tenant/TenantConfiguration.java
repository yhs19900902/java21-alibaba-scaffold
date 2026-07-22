package com.yhs.database.tenant;

import com.yhs.database.properties.MultiTenantProperties;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestInterceptor;

/**
 * 租户信息拦截
 *
 * @author 07664-linwei
 * @version Id: TenantConfiguration.java, v 0.1 2022/4/20 12:02 lw Exp $
 */

public class TenantConfiguration {

    @Bean
    public RequestInterceptor feignTenantInterceptor(MultiTenantProperties multiTenantProperties) {
        return new FeignTenantInterceptor(multiTenantProperties);
    }

    @Bean
    public ClientHttpRequestInterceptor tenantRequestInterceptor(MultiTenantProperties multiTenantProperties) {
        return new TenantRequestInterceptor(multiTenantProperties);
    }
}
