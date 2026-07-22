package com.yhs.easyexcel.aop;


import com.yhs.easyexcel.annotate.FillExcel;
import com.yhs.easyexcel.handler.fill.SheetFillHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Comparator;
import java.util.List;

/**
 * @author 07664-linwei
 * @version Id: YHSFillExcelReturnValueHandler.java, v 0.1 2022/6/25 9:49 lw Exp $
 */
@Slf4j
@RequiredArgsConstructor
public class YHSFillExcelReturnValueHandler implements HandlerMethodReturnValueHandler {


    private final List<SheetFillHandler> sheetWriteHandlerList;

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.hasMethodAnnotation(FillExcel.class);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) {
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        Assert.state(response != null, "No HttpServletResponse");
        FillExcel fillExcel = returnType.getMethodAnnotation(FillExcel.class);
        Assert.state(fillExcel != null, "No @FillExcel");
        mavContainer.setRequestHandled(true);

        sheetWriteHandlerList.stream().sorted(Comparator.comparing(SheetFillHandler::order)).
                filter(handler -> handler.support(returnValue)).findFirst()
                .ifPresent(handler -> handler.export(returnValue, response, fillExcel));
    }
}
