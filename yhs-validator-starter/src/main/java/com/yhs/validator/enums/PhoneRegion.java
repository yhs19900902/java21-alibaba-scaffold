package com.yhs.validator.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 手机号地区
 *
 * @author 07664-linwei
 * @version Id: PhomeRegion.java, v 0.1 2022/4/23 10:35 lw Exp $
 */
@Getter
@AllArgsConstructor
public enum PhoneRegion {

    /**
     * 中国全地区(包含 港 澳 台)
     */
    ALL("all", "中国全地区(包含 港 澳 台)"),
    /**
     * 除 港 澳 台 国内地区
     */
    INLAND("inland", "除 港 澳 台 国内地区"),
    /**
     * 香港地区
     */
    HK("hk", "香港地区"),
    /**
     * 澳门地区
     */
    MO("mo", "澳门地区"),
    /**
     * 台湾地区
     */
    TW("tw", "台湾地区");


    private String region;

    private String description;

}
