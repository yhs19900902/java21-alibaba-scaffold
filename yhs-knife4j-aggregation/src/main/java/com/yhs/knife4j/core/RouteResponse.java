package com.yhs.knife4j.core;

import com.yhs.knife4j.core.pojo.HeaderWrapper;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author 07664-linwei
 * @version Id: RouteResponse.java, v 0.1 2023/7/25 9:41 lw Exp $
 */
public interface RouteResponse {
    /**
     * 获取响应头
     *
     * @return 响应头列表
     */
    List<HeaderWrapper> getHeaders();

    /**
     * 是否请求成功
     *
     * @return 请求成功
     */
    boolean success();

    /**
     * 获取响应状态码
     *
     * @return 响应状态码
     */
    int getStatusCode();

    /**
     * 获取响应类型
     *
     * @return 响应类型
     */
    String getContentType();

    /**
     * 响应内容长度
     *
     * @return 内容长度
     */
    Long getContentLength();

    /**
     * 获取encoding
     *
     * @return 字符集编码
     */
    Charset getCharsetEncoding();

    /**
     * 响应实体
     *
     * @return 响应流
     */
    InputStream getBody();

    /**
     * 获取text返回
     *
     * @return 静态文本
     */
    String text();

}
