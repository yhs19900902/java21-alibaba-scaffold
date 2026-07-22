package com.yhs.base.enums;

import lombok.Getter;

/**
 * @author 04628-duanchengjun
 * @version Id: ClientEnum.java, v 0.1 2019/4/25 9:44 duanchengjun Exp $
 */
@Getter
public enum ClientEnum {

    MEN("0", "男"),
    WOMEN("1", "女"),
    GENTLEMAN("0", "先生"),
    LADIES("1", "女士");

    private final String code;//状态码
    private final String desc;//状态描述

    ClientEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
