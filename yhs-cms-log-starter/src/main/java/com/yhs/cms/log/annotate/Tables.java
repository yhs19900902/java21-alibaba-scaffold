package com.yhs.cms.log.annotate;


import com.baomidou.mybatisplus.core.mapper.Mapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 03952-yehuasheng
 * @version Id: Tables.java, v0.1 2024/11/18 15:07 yehuasheng Exp $
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Tables {
    /**
     * 表名，只支持获取一张表的原数据
     *
     * @return String
     */
    String tableName();

    /**
     * 对应的mapper
     *
     * @return Class
     */
    Class<? extends Mapper<?>> dao();

    /**
     * 更新是的where条件字段
     * 多个使用,分隔
     *
     * @return Field[]
     */
    Field[] fields() default @Field(isAlias = false);

    /**
     * 是否同名
     *
     * @return boolean
     */
    boolean nameSake() default false;

    /**
     * 查询条件字段
     */
    @interface Field {
        // 是否有别名
        boolean isAlias() default false;

        // 属性
        String property() default "id";

        // 别名
        String alias() default "";

        // 如果为in 目前只支持String List
        boolean isMany() default false;

    }
}
