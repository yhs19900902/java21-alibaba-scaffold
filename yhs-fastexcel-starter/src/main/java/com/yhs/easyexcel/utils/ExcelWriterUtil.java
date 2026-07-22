package com.yhs.easyexcel.utils;

import cn.idev.excel.ExcelWriter;
import cn.idev.excel.util.StringUtils;
import cn.idev.excel.write.builder.ExcelWriterBuilder;
import com.yhs.easyexcel.properties.ExcelConfigProperties;
import lombok.experimental.UtilityClass;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author 07664-linwei
 * @version Id: ExcelWriterUtil.java, v 0.1 2022/6/29 8:13 lw Exp $
 */
@UtilityClass
public class ExcelWriterUtil {

    /**
     * 模板处理
     *
     * @param excelWriterBuilder excelWriterBuilder
     * @param configProperties   配置
     * @param template           模板
     * @return ExcelWriter
     * @throws IOException io异常
     */
    public static ExcelWriter getExcelWriterTemplate(ExcelWriterBuilder excelWriterBuilder, ExcelConfigProperties configProperties, String template) throws IOException {
        String templatePath = configProperties.getTemplatePath();
        if (StringUtils.isNotBlank(template)) {
            ClassPathResource classPathResource = new ClassPathResource(
                    templatePath + File.separator + template);
            InputStream inputStream = classPathResource.getInputStream();
            excelWriterBuilder.withTemplate(inputStream);
        }

        return excelWriterBuilder.build();
    }
}
