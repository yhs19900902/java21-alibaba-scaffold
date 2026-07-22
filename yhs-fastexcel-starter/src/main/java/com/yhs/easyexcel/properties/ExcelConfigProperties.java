package com.yhs.easyexcel.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 07664-linwei
 * @version Id: ExcelConfigProperties.java, v 0.1 2022/6/24 16:02 lw Exp $
 */
@Data
@ConfigurationProperties(prefix = ExcelConfigProperties.PREFIX)
public class ExcelConfigProperties {

    public static final String PREFIX = "yhs.excel";

    /**
     * 模板存放路径
     */
    private String templatePath = "excel";

}
