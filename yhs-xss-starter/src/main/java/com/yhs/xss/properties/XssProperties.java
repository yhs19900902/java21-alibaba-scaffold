package com.yhs.xss.properties;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author 07664-linwei
 * @version Id: XssProperties.java, v 0.1 2022/4/24 10:41 lw Exp $
 */
@Data
@ConfigurationProperties(prefix = XssProperties.PREFIX)
public class XssProperties {


    public static final String PREFIX = "yhs.xss";


    private int order = 1;

    private List<String> patterns = CollUtil.newArrayList("/*");
    private List<String> ignorePaths = CollUtil.newArrayList("favicon.ico",
            "/**/doc.html",
            "/**/swagger-ui.html",
            "/csrf",
            "/webjars/**",
            "/v3/**",
            "/swagger-resources/**",
            "/resources/**",
            "/static/**",
            "/public/**",
            "/classpath:*",
            "/actuator/**",
            "/**/noxss/**",
            "/**/activiti/**",
            "/**/service/model/**",
            "/**/service/editor/**"
    );

}
