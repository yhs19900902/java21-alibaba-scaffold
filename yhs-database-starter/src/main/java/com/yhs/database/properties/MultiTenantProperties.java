package com.yhs.database.properties;

import com.baomidou.mybatisplus.annotation.DbType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 多租户配置
 *
 * @author 07664-linwei
 * @version Id: MultiTenantProperties.java, v 0.1 2022/4/20 11:51 lw Exp $
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "yhs.multi.tenant")
public class MultiTenantProperties {


    public static final String[] IGNORE_PATH = {
            "/error/**",
            "/favicon.ico",
            "/actuator/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/doc.html",
            "/webjars/**",
            "**/favicon.ico",
            "/swagger-resources",
            "/swagger-resources/**",
            "/swagger-ui.html"
    };
    /**
     * 是否启用 防止全表更新与删除插件
     */
    private Boolean isBlockAttack = Boolean.TRUE;
    /**
     * 是否启用  sql性能规范插件
     */
    private Boolean isIllegalSql = Boolean.FALSE;
    /**
     * 分页大小限制
     */
    private long limit = -1;
    /**
     * db类型
     */
    private DbType dbType = DbType.MYSQL;
    /**
     * 多租户字段模式排除无需多租户表
     */
    private List<String> ignoreTable = new ArrayList<>();
    /**
     * 多租户模式
     */
    private MultiTenantType multiTenantType = MultiTenantType.NONE;
    /**
     * 前端hearer中的租户 请求key
     */
    private String tenantKey = "tenant";
    /**
     * 接口请求过滤
     */
    private List<String> excludePathPatterns = new ArrayList<>();
    /**
     * 数据库 租户字段名
     */
    private String tenantColumn = "tenant_id";
    private String multiTenantDatasourcePrefix = "yhs";
    /**
     * 多租户前端value 加密方式
     */
    private MultiHeaderEncryptType multiHeaderEncryptType = MultiHeaderEncryptType.NONE;
    /**
     * secretKey 加密秘钥  aes秘钥 长度为 16/24/32 默认 yhs_service_yyds
     */
    private String secretKey = "yhs_service_YYDS";

}
