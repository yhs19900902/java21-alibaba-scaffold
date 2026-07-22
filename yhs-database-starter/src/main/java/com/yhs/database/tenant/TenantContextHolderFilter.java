package com.yhs.database.tenant;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.DES;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.yhs.base.enums.ExceptionCodeEnum;
import com.yhs.base.tenant.TenantContextHolder;
import com.yhs.database.exception.TenantException;
import com.yhs.database.properties.MultiHeaderEncryptType;
import com.yhs.database.properties.MultiTenantProperties;
import com.yhs.database.properties.MultiTenantType;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author 07664-linwei
 * @version Id: TenantContextHolderFilter.java, v 0.1 2022/4/20 14:38 lw Exp $
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantContextHolderFilter extends GenericFilter {

    private final MultiTenantProperties multiTenantProperties;
    private transient final PathMatcher pathMatcher = new AntPathMatcher();


    public TenantContextHolderFilter(MultiTenantProperties multiTenantProperties) {
        this.multiTenantProperties = multiTenantProperties;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 开启租户模式 设置租户id
        if (!CharSequenceUtil.equalsAny(MultiTenantType.NONE.name(),
                multiTenantProperties.getMultiTenantType().name())) {

            String requestUri = request.getRequestURI();
            // 判断是否放行请求
            if (!(Arrays.stream(MultiTenantProperties.IGNORE_PATH).
                    anyMatch(excludePath -> pathMatcher.matchStart(excludePath, requestUri)) ||
                    multiTenantProperties.getExcludePathPatterns().
                            stream().anyMatch(excludePath -> pathMatcher.matchStart(excludePath, requestUri)))) {
                //获取租户id
                String tenant = request.getHeader(multiTenantProperties.getTenantKey());
                if (CharSequenceUtil.isBlank(tenant)) {
                    throw new TenantException(ExceptionCodeEnum.TENANT_ID_IS_NULL);
                }
                TenantContextHolder.setOriginalTenantId(tenant);
                // 解密tenant
                String decryptTenant = tenantDecrypt(multiTenantProperties, tenant);

                TenantContextHolder.setTenantId(decryptTenant);
            }
        }
        filterChain.doFilter(request, response);
        TenantContextHolder.clear();
    }


    /**
     * 租户id 解密
     *
     * @param multiTenantProperties 配置类
     * @param tenant                请求头获取的租户id
     * @return 解密后的租户id
     */
    private String tenantDecrypt(MultiTenantProperties multiTenantProperties, String tenant) {
        SymmetricCrypto symmetricCrypto;
        // aes解密密
        if (MultiHeaderEncryptType.AES.
                eq(multiTenantProperties.getMultiHeaderEncryptType().name())) {
            symmetricCrypto = new AES(multiTenantProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
            return symmetricCrypto.decryptStr(tenant);
        }
        // des 解密密
        if (MultiHeaderEncryptType.DES.
                eq(multiTenantProperties.getMultiHeaderEncryptType().name())) {
            symmetricCrypto = new DES(multiTenantProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
            return symmetricCrypto.decryptStr(tenant);
        }
        return tenant;
    }

}
