package com.yhs.easyexcel;

import com.yhs.easyexcel.aop.DynamicNameAspect;
import com.yhs.easyexcel.aop.YHSFillExcelReturnValueHandler;
import com.yhs.easyexcel.aop.YHSReadExcelArgumentResolver;
import com.yhs.easyexcel.aop.YHSWriteExcelReturnValueHandler;
import com.yhs.easyexcel.handler.fill.SheetFillHandler;
import com.yhs.easyexcel.handler.write.SheetWriteHandler;
import com.yhs.easyexcel.properties.ExcelConfigProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 07664-linwei
 * @version Id: FastExcelAutoConfiguration.java, v 0.1 2022/6/23 11:11 lw Exp $
 */
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@Import(WriteHandlerConfiguration.class)
@EnableConfigurationProperties(ExcelConfigProperties.class)
public class FastExcelAutoConfiguration {

    private final RequestMappingHandlerAdapter requestMappingHandlerAdapter;


    private final List<SheetWriteHandler> sheetWriteHandlerList;

    private final List<SheetFillHandler> sheetFillHandlers;

    /**
     * Excel名称解析处理切面
     *
     * @return DynamicNameAspect
     */
    @Bean
    @ConditionalOnMissingBean
    public DynamicNameAspect dynamicNameAspect() {
        return new DynamicNameAspect();
    }

    /**
     * 追加 Excel 请求处理器 到 springmvc 中
     */
    @PostConstruct
    public void setRequestExcelArgumentResolver() {
        List<HandlerMethodArgumentResolver> argumentResolvers = requestMappingHandlerAdapter.getArgumentResolvers();
        List<HandlerMethodArgumentResolver> resolverList = new ArrayList<>();
        resolverList.add(new YHSReadExcelArgumentResolver());
        if (argumentResolvers != null) {
            resolverList.addAll(argumentResolvers);
        }
        requestMappingHandlerAdapter.setArgumentResolvers(resolverList);
    }

    /**
     * 追加 Excel返回值处理器 到 springmvc 中
     */
    @PostConstruct
    public void setReturnValueHandlers() {
        List<HandlerMethodReturnValueHandler> returnValueHandlers = requestMappingHandlerAdapter
                .getReturnValueHandlers();
        List<HandlerMethodReturnValueHandler> newHandlers = new ArrayList<>();
        newHandlers.add(new YHSWriteExcelReturnValueHandler(sheetWriteHandlerList));
        newHandlers.add(new YHSFillExcelReturnValueHandler(sheetFillHandlers));
        assert returnValueHandlers != null;
        newHandlers.addAll(returnValueHandlers);
        requestMappingHandlerAdapter.setReturnValueHandlers(newHandlers);
    }
}
