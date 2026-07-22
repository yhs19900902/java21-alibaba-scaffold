package com.yhs.dynamic.annotate;

import com.yhs.dynamic.DynamicDataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启动态数据源
 *
 * @author 07664-linwei
 * @version Id: EnableDynanicDataSource.java, v 0.1 2022/5/18 14:50 lw Exp $
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(value = {com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration.class,
        DynamicDataSourceAutoConfiguration.class})
public @interface EnableDynamicDataSource {
}
