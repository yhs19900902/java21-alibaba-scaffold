package com.yhs.base.enums;

import lombok.Getter;

/**
 * @author 03952-yehuasheng
 * @version Id: DateResultEnum.java, v0.1 2023/9/12 11:59 yehuasheng Exp $
 */
@Getter
public enum DateResultEnum {
    LATER(1, "给定日期晚于当前日期"),
    EARLY(-1, "给定日期早于当前日期"),
    EQUAL(0, "给定日期等于当前日期"),
    ERROR(99, "给定日期解析错误");

    private final int retCode;

    private final String retDesc;

    DateResultEnum(int retCode, String retDesc) {
        this.retCode = retCode;
        this.retDesc = retDesc;
    }
}
