package com.yhs.easyexcel.aop;


import com.yhs.easyexcel.annotate.WriteExcel;
import com.yhs.easyexcel.handler.write.SheetWriteHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

/**
 * @author 07664-linwei
 * @version Id: YHSResponseExcelReturnValueHandler.java, v 0.1 2022/6/23 16:15 lw Exp $
 */
@Slf4j
@RequiredArgsConstructor
public class YHSWriteExcelReturnValueHandler implements HandlerMethodReturnValueHandler {


    private final List<SheetWriteHandler> sheetWriteHandlerList;

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.hasMethodAnnotation(WriteExcel.class);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) {
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        Assert.state(response != null, "No HttpServletResponse");
        WriteExcel writeExcel = returnType.getMethodAnnotation(WriteExcel.class);
        Assert.state(writeExcel != null, "No @WriteExcel");
        mavContainer.setRequestHandled(true);

        sheetWriteHandlerList.stream().filter(handler -> handler.support(returnValue, writeExcel))
                .findFirst().ifPresent(handler -> handler.export(returnValue, response, writeExcel));
    }
}
