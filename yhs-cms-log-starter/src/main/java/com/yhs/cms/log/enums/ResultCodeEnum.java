package com.yhs.cms.log.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 03952-yehuasheng
 * @version Id: ResultCodeEnum.java, v0.1 2024/11/18 15:16 yehuasheng Exp $
 */
@Getter
@AllArgsConstructor
public enum ResultCodeEnum {
    PLEASE_REFRESH_THE_PAGE(509704, "Other users have updated the data. Please refresh the page and try again", "其他用户已对该数据进行更新,请刷新页面后重试"),
    ;

    private final int code;
    private final String message;
    private final String description;
}
