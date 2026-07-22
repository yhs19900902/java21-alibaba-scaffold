package com.yhs.easyexcel.handler.fill;


import cn.idev.excel.ExcelWriter;
import cn.idev.excel.write.metadata.WriteSheet;
import cn.idev.excel.write.metadata.fill.FillConfig;
import cn.idev.excel.write.metadata.fill.FillWrapper;
import com.yhs.easyexcel.annotate.FillExcel;
import com.yhs.easyexcel.properties.ExcelConfigProperties;
import com.yhs.easyexcel.vo.FillData;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collection;
import java.util.List;

/**
 * @author 07664-linwei
 * @version Id: ComplexFillSheetHandler.java, v 0.1 2022/6/28 15:09 lw Exp $
 */
public class ComplexFillSheetHandler extends YHSAbstractFillSheetHandler {


    public ComplexFillSheetHandler(ExcelConfigProperties configProperties) {
        super(configProperties);
    }

    @Override
    public boolean support(Object obj) {
        if (obj instanceof List) {
            List listOjb = (List) obj;
            return !listOjb.isEmpty() && (listOjb.get(0) instanceof FillData);
        }
        return false;
    }

    @Override
    public void fillTemplate(Object o, HttpServletResponse response, FillExcel fillExcel) {
        List<FillData> fillDataList = (List) o;
        try (ExcelWriter excelWriter = getExcelWriter(response, fillExcel)) {
            WriteSheet writeSheet = getWriteSheet();
            for (FillData fillData : fillDataList) {
                FillConfig fillConfig = FillConfig.builder().direction(fillData.getDirectionEnum())
                        .forceNewRow(fillData.getForceNewRow()).build();
                if (fillData.getData() instanceof Collection) {
                    excelWriter.fill(new FillWrapper(fillData.getPrefix(), (Collection) fillData.getData()), fillConfig, writeSheet);
                } else {
                    excelWriter.fill(fillData.getData(), writeSheet);
                }
            }

        }

    }

    @Override
    public int order() {
        return 100;
    }
}
