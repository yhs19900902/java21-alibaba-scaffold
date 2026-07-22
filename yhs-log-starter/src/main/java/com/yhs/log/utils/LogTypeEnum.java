package com.yhs.log.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 07664-linwei
 * @version Id: LogTypeEnum.java, v 0.1 2022/4/22 11:54 lw Exp $
 */
@Getter
@RequiredArgsConstructor
public enum LogTypeEnum {

    /**
     * 正常日志
     */
    OPT("opt", "正常日志"),
    /**
     * 错误日志
     */
    ERROR("error", "错误日志");

    private final String type;

    private final String description;


}
