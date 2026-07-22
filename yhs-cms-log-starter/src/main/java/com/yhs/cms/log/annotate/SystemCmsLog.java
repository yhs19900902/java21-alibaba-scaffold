package com.yhs.cms.log.annotate;


import com.yhs.cms.log.enums.SystemCmsLogTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 03952-yehuasheng
 * @version Id: SystemCmsLog.java, v0.1 2024/11/18 15:07 yehuasheng Exp $
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SystemCmsLog {
    String value();

    /**
     * 日志类型
     *
     * @return SystemCmsLogTypeEnum
     */
    SystemCmsLogTypeEnum type();

    /**
     * 表数据
     *
     * @return Tables
     */
    Tables[] tables();
}
