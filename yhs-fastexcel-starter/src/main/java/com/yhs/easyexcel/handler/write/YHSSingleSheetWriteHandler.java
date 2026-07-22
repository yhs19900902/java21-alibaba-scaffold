package com.yhs.easyexcel.handler.write;


import cn.idev.excel.ExcelWriter;
import cn.idev.excel.write.metadata.WriteSheet;
import com.yhs.easyexcel.annotate.WriteExcel;
import com.yhs.easyexcel.exception.ExcelException;
import com.yhs.easyexcel.properties.ExcelConfigProperties;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * @author 07664-linwei
 * @version Id: YHSSingleSheetWriteHandler.java, v 0.1 2022/6/24 14:09 lw Exp $
 */
public class YHSSingleSheetWriteHandler extends YHSAbstractSheetWriteHandler {


    public YHSSingleSheetWriteHandler(ExcelConfigProperties configProperties) {
        super(configProperties);
    }


    @Override
    public boolean support(Object obj, WriteExcel writeExcel) {
        if (super.support(obj, writeExcel)) {
            return writeExcel.sheets().length == 1;
        }
        throw new ExcelException("@WriteExcel 返回值必须为List类型");
    }

    @Override
    public void write(Object o, HttpServletResponse response, WriteExcel writeExcel) {
        List list = (List) o;
        ExcelWriter excelWriter = getExcelWriter(response, writeExcel);

        // 有模板则不指定sheet名
        Class<?> dataClass = list.get(0).getClass();
        WriteSheet sheet = createSheet(writeExcel.sheets()[0], dataClass, writeExcel.template(),
                writeExcel.headGenerator());
        // FillConfig fillConfig = FillConfig.builder().direction(WriteDirectionEnum.HORIZONTAL).build();
        // excelWriter.fill(new FillWrapper("",null),fillConfig,sheet);
        excelWriter.write(list, sheet);
        excelWriter.finish();
    }
}
