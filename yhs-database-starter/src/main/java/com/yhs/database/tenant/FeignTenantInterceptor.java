package com.yhs.database.tenant;

import com.yhs.base.tenant.TenantContextHolder;
import com.yhs.database.properties.MultiTenantProperties;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * feign 调用设置租户id
 *
 * @author 07664-linwei
 * @version Id: FeginTenantInterceptor.java, v 0.1 2022/4/20 14:05 lw Exp $
 */
@Slf4j
public class FeignTenantInterceptor implements RequestInterceptor {

    private final MultiTenantProperties multiTenantProperties;

    public FeignTenantInterceptor(MultiTenantProperties multiTenantProperties) {
        this.multiTenantProperties = multiTenantProperties;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        if (TenantContextHolder.getTenantId() == null) {
            log.debug("TTL 中的 租户ID为空，feign拦截器 >> 跳过");
            return;
        }
        requestTemplate.header(multiTenantProperties.getTenantKey(), TenantContextHolder.getOriginalTenantId());
    }
}
