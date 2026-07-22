package com.yhs.easyexcel.aop;


import com.yhs.easyexcel.annotate.WriteExcel;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author 07664-linwei
 * @version Id: DynamicNameAspect.java, v 0.1 2022/6/24 16:19 lw Exp $
 */
@Aspect
@RequiredArgsConstructor
public class DynamicNameAspect {

    public static final String EXCEL_NAME_KEY = "YHS_EXCEL_NAME_KEY__";


    @After("@annotation(excel)")
    public void around(JoinPoint point, WriteExcel excel) {
        MethodSignature ms = (MethodSignature) point.getSignature();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        String attribute = (String) Objects.requireNonNull(requestAttributes).getAttribute(EXCEL_NAME_KEY, RequestAttributes.SCOPE_REQUEST);
        if (StringUtils.hasText(attribute)) {
            return;
        }
        String name = excel.name();
        // 当配置的 excel 名称为空时，取当前时间
        if (!StringUtils.hasText(name)) {
            name = LocalDateTime.now().toString();
        }

        Objects.requireNonNull(requestAttributes).setAttribute(EXCEL_NAME_KEY, name, RequestAttributes.SCOPE_REQUEST);
    }
}
