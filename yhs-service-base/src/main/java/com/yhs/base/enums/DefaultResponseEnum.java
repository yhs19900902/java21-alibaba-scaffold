package com.yhs.base.enums;

/**
 * @author 03952-yehuasheng
 * @version Id: DefaultResponseEnum.java, v0.1 2023/9/12 10:21 yehuasheng Exp $
 */
public interface DefaultResponseEnum {
    /**
     * 返回响应码
     *
     * @return int
     */
    int getCode();

    /**
     * 返回响应信息
     *
     * @return String
     */
    String getMessage();
}
