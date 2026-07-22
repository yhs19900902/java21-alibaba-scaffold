package com.yhs.dynamic.config;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author 07664-linwei
 * @version Id: ClearDynamicDatasourceConfiguration.java, v 0.1 2022/5/18 15:37 lw Exp $
 */
public class ClearDynamicDataSourceConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new DynamicDataSourceClearInterceptor())
                .addPathPatterns("/**");
    }
}
