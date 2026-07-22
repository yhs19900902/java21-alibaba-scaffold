package com.yhs.easyexcel.heand;

/**
 * Excel头生成器，用于自定义生成头部信息
 *
 * @author 07664-linwei
 * @version Id: HeadGenerator.java, v 0.1 2022/6/24 15:43 lw Exp $
 */
public interface HeadGenerator {

    /**
     * <p>
     * 自定义头部信息
     * </p>
     * 实现类根据数据的class信息，定制Excel头<br/>
     *
     * @param clazz 当前sheet的数据类型
     * @return List<List < String>> Head头信息
     */
    HeadMeta head(Class<?> clazz);
}
