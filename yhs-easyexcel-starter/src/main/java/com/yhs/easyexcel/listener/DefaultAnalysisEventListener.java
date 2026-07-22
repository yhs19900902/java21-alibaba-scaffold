package com.yhs.easyexcel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.yhs.easyexcel.exception.ExcelException;
import com.yhs.easyexcel.utils.Validators;
import com.yhs.easyexcel.vo.ErrorMessage;
import jakarta.validation.ConstraintViolation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 默认的 AnalysisEventListener
 *
 * @author 07664-linwei
 * @version Id: DefaultAnalysisEventListener.java, v 0.1 2022/6/23 10:36 lw Exp $
 */
public class DefaultAnalysisEventListener extends ListAnalysisEventListener<Object> {

    private final List<Object> list = new ArrayList<>();

    private final List<ErrorMessage> errorMessageList = new ArrayList<>();

    private Long lineNum = 1L;

    @Override
    public List<Object> getList() {
        return list;
    }

    @Override
    public void invoke(Object o, AnalysisContext analysisContext) {
        lineNum++;
        Set<ConstraintViolation<Object>> violations = Validators.validate(o);
        if (super.getMaxRows() != -1L && lineNum > super.getMaxRows()) {
            throw new ExcelException("The number of uploaded file lines exceeds  " + super.getMaxRows());
        }
        if (!violations.isEmpty()) {
            Set<String> messageSet = violations.stream().map(ConstraintViolation::getMessage)
                    .collect(Collectors.toSet());
            errorMessageList.add(new ErrorMessage(lineNum, messageSet));
        } else {
            list.add(o);
        }
    }

    @Override
    public List<ErrorMessage> getErrors() {
        return errorMessageList;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

}
