package com.yhs.easyexcel.annotate;


import com.yhs.easyexcel.heand.HeadGenerator;

import java.lang.annotation.*;

/**
 * @author 07664-linwei
 * @version Id: Sheet.java, v 0.1 2022/6/23 20:41 lw Exp $
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Sheet {

    int sheetNo() default -1;

    /**
     * sheet name
     */
    String sheetName();

    /**
     * 包含字段
     */
    String[] includes() default {};

    /**
     * 排除字段
     */
    String[] excludes() default {};

    /**
     * 头生成器
     */
    Class<? extends HeadGenerator> headGenerateClass() default HeadGenerator.class;
}
