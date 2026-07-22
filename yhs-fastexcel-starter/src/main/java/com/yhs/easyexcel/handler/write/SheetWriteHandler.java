package com.yhs.easyexcel.handler.write;


import com.yhs.easyexcel.annotate.WriteExcel;
import jakarta.servlet.http.HttpServletResponse;

/**
 * sheet 写出处理器
 *
 * @author 07664-linwei
 * @version Id: SheetWriteHandler.java, v 0.1 2022/6/23 20:35 lw Exp $
 */
public interface SheetWriteHandler {

    /**
     * 是否支持
     *
     * @param obj 参数
     * @return 是否支持
     */
    boolean support(Object obj, WriteExcel writeExcel);

    /**
     * 校验
     *
     * @param writeExcel 注解
     */
    void check(WriteExcel writeExcel);

    /**
     * 返回的对象
     *
     * @param o          obj
     * @param response   输出对象
     * @param writeExcel 注解
     */
    void export(Object o, HttpServletResponse response, WriteExcel writeExcel);

    /**
     * 写成对象
     *
     * @param o          obj
     * @param response   输出对象
     * @param writeExcel 注解
     */
    void write(Object o, HttpServletResponse response, WriteExcel writeExcel);
}
