package com.yhs.cms.log.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 03952-yehuasheng
 * @version Id: SystemCmsLogTypeEnum.java, v0.1 2024/11/18 15:08 yehuasheng Exp $
 */
@Getter
@AllArgsConstructor
public enum SystemCmsLogTypeEnum {
    UPDATED("UPDATED", "更新"),
    CREATED("CREATED", "创建"),
    DELETED("DELETED", "删除"),
    ;


    private final String type;
    private final String description;
}
