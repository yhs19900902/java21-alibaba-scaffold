package com.yhs.dynamic;


import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.yhs.dynamic.config.ClearDynamicDataSourceConfiguration;
import com.yhs.dynamic.config.JdbcDynamicDataSourceProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author 07664-linwei
 * @version Id: DynamicDataSourceAutoConfiguration.java, v 0.1 2022/5/18 17:14 lw Exp $
 */
@RequiredArgsConstructor
@EnableConfigurationProperties(DataSourceProperties.class)
public class DynamicDataSourceAutoConfiguration {


    private final Environment environment;

    private final DataSourceProperties properties;

    @Value("${spring.datasource.druid.url:}")
    private String url;

    @Value("${spring.datasource.druid.username:}")
    private String username;

    @Value("${spring.datasource.druid.password:}")
    private String password;

    @Value("${spring.datasource.druid.driver-class-name:}")
    private String driverClassName;

    @Bean
    public DynamicDataSourceProvider dynamicDataSourceProvider() {
        if (StrUtil.isNotBlank(url)) {
            //兼容druid
            properties.setUrl(url);
            properties.setUsername(username);
            properties.setPassword(password);
            properties.setDriverClassName(driverClassName);
        }
        return new JdbcDynamicDataSourceProvider(properties, environment);
    }


    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new ClearDynamicDataSourceConfiguration();
    }
}
