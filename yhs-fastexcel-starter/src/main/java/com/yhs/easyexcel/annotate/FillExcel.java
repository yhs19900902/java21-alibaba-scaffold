package com.yhs.easyexcel.annotate;


import cn.idev.excel.support.ExcelTypeEnum;

import java.lang.annotation.*;

/**
 * @author 07664-linwei
 * @version Id: FillExcel.java, v 0.1 2022/6/25 9:48 lw Exp $
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FillExcel {

    /**
     * 文件名称
     *
     * @return string
     */
    String name() default "";

    /**
     * 文件密码
     *
     * @return password
     */
    String password() default "";

    /**
     * 文件类型 （xlsx xls）
     *
     * @return string
     */
    ExcelTypeEnum suffix() default ExcelTypeEnum.XLSX;


    /**
     * excel 模板
     *
     * @return String
     */
    String template() default "";
}
