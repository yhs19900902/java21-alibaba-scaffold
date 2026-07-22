package com.yhs.easyexcel.handler.write;


import cn.idev.excel.ExcelWriter;
import cn.idev.excel.write.metadata.WriteSheet;
import com.yhs.easyexcel.annotate.Sheet;
import com.yhs.easyexcel.annotate.WriteExcel;
import com.yhs.easyexcel.exception.ExcelException;
import com.yhs.easyexcel.properties.ExcelConfigProperties;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * @author 07664-linwei
 * @version Id: YHSManySheetWriteHandler.java, v 0.1 2022/6/24 14:48 lw Exp $
 */
public class YHSManySheetWriteHandler extends YHSAbstractSheetWriteHandler {


    public YHSManySheetWriteHandler(ExcelConfigProperties configProperties) {
        super(configProperties);
    }


    @Override
    public boolean support(Object obj, WriteExcel writeExcel) {
        if (super.support(obj, writeExcel)) {
            return writeExcel.sheets().length > 1;
        }
        throw new ExcelException("@WriteExcel 返回值必须为List类型");
    }

    @Override
    public void write(Object o, HttpServletResponse response, WriteExcel writeExcel) {
        List objList = (List) o;
        ExcelWriter excelWriter = getExcelWriter(response, writeExcel);

        Sheet[] sheets = writeExcel.sheets();
        WriteSheet sheet;
        for (int i = 0; i < sheets.length; i++) {
            List eleList = (List) objList.get(i);
            Class<?> dataClass = eleList.get(0).getClass();
            // 创建sheet
            sheet = createSheet(sheets[i], dataClass, writeExcel.template(), writeExcel.headGenerator());
            // 写入sheet
            excelWriter.write(eleList, sheet);
        }
        excelWriter.finish();
    }
}
