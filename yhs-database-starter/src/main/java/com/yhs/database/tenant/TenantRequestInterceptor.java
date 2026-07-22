package com.yhs.database.tenant;

import com.yhs.base.tenant.TenantContextHolder;
import com.yhs.database.properties.MultiTenantProperties;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * @author 07664-linwei
 * @version Id: TenantRequestInterceptor.java, v 0.1 2022/4/20 14:36 lw Exp $
 */
public class TenantRequestInterceptor implements ClientHttpRequestInterceptor {

    private final MultiTenantProperties multiTenantProperties;

    public TenantRequestInterceptor(MultiTenantProperties multiTenantProperties) {
        this.multiTenantProperties = multiTenantProperties;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        if (TenantContextHolder.getTenantId() != null) {
            request.getHeaders().set(multiTenantProperties.getTenantKey(), TenantContextHolder.getOriginalTenantId());
        }

        return execution.execute(request, body);
    }
}
