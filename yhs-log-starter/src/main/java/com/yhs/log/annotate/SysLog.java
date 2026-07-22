package com.yhs.log.annotate;

import java.lang.annotation.*;

/**
 * @author 07664-linwei
 * @version Id: SysLog.java, v 0.1 2022/4/22 8:39 lw Exp $
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {

    /**
     * 方法描述
     *
     * @return
     */
    String value();
}
