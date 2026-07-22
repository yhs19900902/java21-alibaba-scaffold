package com.yhs.knife4j.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自定义header 包装bean
 *
 * @author 07664-linwei
 * @version Id: HeaderWrapper.java, v 0.1 2023/7/25 8:47 lw Exp $
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HeaderWrapper {

    /**
     * header 名
     */
    private String name;
    /**
     * header 值
     */
    private String value;
}
