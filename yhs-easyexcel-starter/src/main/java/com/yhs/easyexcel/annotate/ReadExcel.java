package com.yhs.easyexcel.annotate;


import com.yhs.easyexcel.listener.DefaultAnalysisEventListener;
import com.yhs.easyexcel.listener.ListAnalysisEventListener;

import java.lang.annotation.*;

/**
 * 导入excel
 *
 * @author 07664-linwei
 * @version Id: RequestExcel.java, v 0.1 2022/6/23 8:52 lw Exp $
 */
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReadExcel {

    /**
     * 前端上传字段名称 file
     */
    String fileName() default "file";

    /**
     * 读取的监听器类
     *
     * @return readListener
     */
    Class<? extends ListAnalysisEventListener<?>> readListener() default DefaultAnalysisEventListener.class;

    /**
     * 是否跳过空行
     *
     * @return 默认跳过
     */
    boolean ignoreEmptyRow() default true;

    /**
     * 限制上传行数
     */
    long maxRows() default -1L;
}
