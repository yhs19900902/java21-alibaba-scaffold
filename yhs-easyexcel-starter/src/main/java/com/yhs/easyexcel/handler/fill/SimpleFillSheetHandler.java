package com.yhs.easyexcel.handler.fill;


import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.yhs.easyexcel.annotate.FillExcel;
import com.yhs.easyexcel.properties.ExcelConfigProperties;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

import java.util.Objects;

/**
 * @author 07664-linwei
 * @version Id: MapFillSheetHanlder.java, v 0.1 2022/6/27 10:28 lw Exp $
 */
public class SimpleFillSheetHandler extends YHSAbstractFillSheetHandler {


    public SimpleFillSheetHandler(ExcelConfigProperties configProperties) {
        super(configProperties);
    }

    @Override
    public boolean support(Object obj) {
        return Objects.nonNull(obj);
    }

    @Override
    @SneakyThrows
    public void fillTemplate(Object o, HttpServletResponse response, FillExcel fillExcel) {

        try (ExcelWriter excelWriter = getExcelWriter(response, fillExcel)) {
            WriteSheet writeSheet = getWriteSheet();
            excelWriter.fill(o, writeSheet);
        }

    }

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }
}
