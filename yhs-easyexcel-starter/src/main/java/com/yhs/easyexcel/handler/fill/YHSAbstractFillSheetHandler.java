package com.yhs.easyexcel.handler.fill;


import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.yhs.easyexcel.annotate.FillExcel;
import com.yhs.easyexcel.properties.ExcelConfigProperties;
import com.yhs.easyexcel.utils.ExcelWriterUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @author 07664-linwei
 * @version Id: YHSAbstractFillSheetHandler.java, v 0.1 2022/6/28 14:19 lw Exp $
 */
@RequiredArgsConstructor
public abstract class YHSAbstractFillSheetHandler implements SheetFillHandler {

    private final ExcelConfigProperties configProperties;


    @Override
    @SneakyThrows
    public void export(Object o, HttpServletResponse response, FillExcel fillExcel) {

        String name = StringUtils.isNotBlank(fillExcel.name()) ? fillExcel.name() : UUID.randomUUID().toString();
        String fileName = String.format("%s%s", URLEncoder.encode(name, StandardCharsets.UTF_8), fillExcel.suffix().getValue());
        // 根据实际的文件类型找到对应的 contentType
        String contentType = MediaTypeFactory.getMediaType(fileName).map(MediaType::toString)
                .orElse("application/vnd.ms-excel");
        response.setContentType(contentType);
        response.setCharacterEncoding("utf-8");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName);
        fillTemplate(o, response, fillExcel);
    }


    @SneakyThrows
    public ExcelWriter getExcelWriter(HttpServletResponse response, FillExcel fillExcel) {
        ExcelWriterBuilder excelWriterBuilder = EasyExcelFactory.write(response.getOutputStream())
                .autoCloseStream(true).excelType(fillExcel.suffix()).inMemory(true);

        // 设置密码
        if (StringUtils.isNotBlank(fillExcel.password())) {
            excelWriterBuilder.password(fillExcel.password());
        }
        //模板处理
        return ExcelWriterUtil.getExcelWriterTemplate(excelWriterBuilder, configProperties, fillExcel.template());
    }


    public WriteSheet getWriteSheet() {
        return EasyExcelFactory.writerSheet().build();
    }
}
