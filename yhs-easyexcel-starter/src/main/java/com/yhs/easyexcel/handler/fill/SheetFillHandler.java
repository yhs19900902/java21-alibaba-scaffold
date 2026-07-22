package com.yhs.easyexcel.handler.fill;


import com.yhs.easyexcel.annotate.FillExcel;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author 07664-linwei
 * @version Id: SheetWriteHandler.java, v 0.1 2022/6/27 9:28 lw Exp $
 */
public interface SheetFillHandler {

    /**
     * 是否支持
     *
     * @param obj obj
     * @return boolean
     */
    boolean support(Object obj);


    /**
     * 返回的对象
     *
     * @param o         obj
     * @param response  输出对象
     * @param fillExcel 注解
     */
    void export(Object o, HttpServletResponse response, FillExcel fillExcel);

    /**
     * 填充模板
     *
     * @param o         参数
     * @param response  响应
     * @param fillExcel 填充注解
     */
    void fillTemplate(Object o, HttpServletResponse response, FillExcel fillExcel);

    /**
     * 排序使用
     *
     * @return 位置
     */
    int order();
}
