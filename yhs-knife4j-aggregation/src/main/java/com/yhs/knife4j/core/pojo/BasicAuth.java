package com.yhs.knife4j.core.pojo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Basic 验证属性
 *
 * @author 07664-linwei
 * @version Id: BasicAuth.java, v 0.1 2023/7/25 8:42 lw Exp $
 */
@Data
@ConfigurationProperties(prefix = "knife4j.basic-auth")
public class BasicAuth {
    /**
     * 是否开启basic认证
     */
    private boolean enable = false;
    /**
     * Basic 用户名
     */
    private String username;
    /**
     * Basic 密码
     */
    private String password;
}
