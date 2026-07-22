package com.yhs.cms.log.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * @author 03952-yehuasheng
 * @version Id: SecurityClassProperties.java, v0.1 2024/11/20 16:32 yehuasheng Exp $
 */
@Data
@Validated
@ConfigurationProperties(prefix = "security")
@Configuration
public class SecurityClassProperties {

    @NotBlank
    private String classz;

    @NotBlank
    private String method;

}
