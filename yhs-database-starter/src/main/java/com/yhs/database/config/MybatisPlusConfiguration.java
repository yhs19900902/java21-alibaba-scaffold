package com.yhs.database.config;

import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.IllegalSQLInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.yhs.base.tenant.TenantContextHolder;
import com.yhs.database.handler.DateMetaObjectHandler;
import com.yhs.database.properties.MultiTenantProperties;
import com.yhs.database.properties.MultiTenantType;
import com.yhs.database.resolver.SqlFilterArgumentResolver;
import com.yhs.database.tenant.TenantConfiguration;
import com.yhs.database.tenant.TenantContextHolderFilter;
import com.yhs.database.tenant.plugins.SchemaInterceptor;
import com.yhs.database.tenant.plugins.YHSTenantLineInnerInterceptor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.util.List;

/**
 * mybatis plus 配置
 *
 * @author 07664-linwei
 * @version Id: MybatisPlusConfiguration.java, v 0.1 2022/4/20 11:35 lw Exp $
 */
@Slf4j
@Configuration
@AllArgsConstructor
@ConditionalOnBean(DataSource.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@EnableConfigurationProperties(MultiTenantProperties.class)
@Import(value = {TenantConfiguration.class, TenantContextHolderFilter.class})
public class MybatisPlusConfiguration implements WebMvcConfigurer {


    protected final MultiTenantProperties multiTenantProperties;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new SqlFilterArgumentResolver());
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        log.info("detection yhs.database.multiTenantType={}, enabled {} schema", multiTenantProperties.getMultiTenantType().name(),
                multiTenantProperties.getMultiTenantType().getDescribe());
        if (CharSequenceUtil.equalsAny(MultiTenantType.SCHEMA.name(),
                multiTenantProperties.getMultiTenantType().name())) {
            // schema 动态表名插件
            interceptor.addInnerInterceptor(new SchemaInterceptor(multiTenantProperties.getMultiTenantDatasourcePrefix()));
        }
        if (CharSequenceUtil.equalsAny(MultiTenantType.COLUMN.name(),
                multiTenantProperties.getMultiTenantType().name())) {
            // COLUMN 模式 多租户插件
            YHSTenantLineInnerInterceptor tli = new YHSTenantLineInnerInterceptor();
            tli.setTenantLineHandler(new TenantLineHandler() {
                @Override
                public String getTenantIdColumn() {
                    return multiTenantProperties.getTenantColumn();
                }

                @Override
                public boolean ignoreTable(String tableName) {
                    return multiTenantProperties.getIgnoreTable().
                            stream().anyMatch(ignoreTable -> ignoreTable.equals(tableName));
                }

                @Override
                public Expression getTenantId() {
                    if (CharSequenceUtil.isBlank(TenantContextHolder.getTenantId())) {
                        return null;
                    }
                    return new StringValue(TenantContextHolder.getTenantId());
                }
            });
            interceptor.addInnerInterceptor(tli);
        }

        // 分页插件
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
        // 单页分页条数限制
        paginationInterceptor.setMaxLimit(multiTenantProperties.getLimit());
        // 数据库类型
        paginationInterceptor.setDbType(multiTenantProperties.getDbType());
        // 溢出总页数后是否进行处理
        paginationInterceptor.setOverflow(true);
        interceptor.addInnerInterceptor(paginationInterceptor);
        //防止全表更新与删除插件
        if (multiTenantProperties.getIsBlockAttack()) {
            interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        }
        // sql性能规范插件
        if (multiTenantProperties.getIsIllegalSql()) {
            interceptor.addInnerInterceptor(new IllegalSQLInnerInterceptor());
        }
        OptimisticLockerInnerInterceptor optimisticLockerInnerInterceptor
                = new OptimisticLockerInnerInterceptor();
        interceptor.addInnerInterceptor(optimisticLockerInnerInterceptor);
        return interceptor;
    }

    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new DateMetaObjectHandler();
    }
}
