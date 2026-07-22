package com.yhs.easyexcel.handler.write;


import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.yhs.easyexcel.annotate.Sheet;
import com.yhs.easyexcel.annotate.WriteExcel;
import com.yhs.easyexcel.aop.DynamicNameAspect;
import com.yhs.easyexcel.exception.ExcelException;
import com.yhs.easyexcel.heand.HeadGenerator;
import com.yhs.easyexcel.heand.HeadMeta;
import com.yhs.easyexcel.properties.ExcelConfigProperties;
import com.yhs.easyexcel.utils.ExcelWriterUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.lang.reflect.Modifier;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author 07664-linwei
 * @version Id: YHSAbstractSheetWriteHandler.java, v 0.1 2022/6/23 20:38 lw Exp $
 */
@RequiredArgsConstructor
public abstract class YHSAbstractSheetWriteHandler implements SheetWriteHandler, ApplicationContextAware {

    private final ExcelConfigProperties configProperties;

    private ApplicationContext applicationContext;

    @Override
    public void check(WriteExcel writeExcel) {
        if (writeExcel.sheets().length == 0) {
            throw new ExcelException("@WriteExcel sheet 配置不合法");
        }
    }

    @Override
    public boolean support(Object obj, WriteExcel writeExcel) {
        if (obj instanceof List) {
            List listOjb = (List) obj;
            return !listOjb.isEmpty() && !(listOjb.get(0) instanceof List);
        }
        return false;
    }


    @Override
    @SneakyThrows
    public void export(Object o, HttpServletResponse response, WriteExcel writeExcel) {
        check(writeExcel);
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        String name = (String) Objects.requireNonNull(requestAttributes).getAttribute(DynamicNameAspect.EXCEL_NAME_KEY,
                RequestAttributes.SCOPE_REQUEST);
        String fileName = String.format("%s%s", URLEncoder.encode(name, "UTF-8"), writeExcel.suffix().getValue());
        // 根据实际的文件类型找到对应的 contentType
        String contentType = MediaTypeFactory.getMediaType(fileName).map(MediaType::toString)
                .orElse("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName);
        response.setContentType(contentType);
        write(o, response, writeExcel);
    }

    @SneakyThrows
    public ExcelWriter getExcelWriter(HttpServletResponse response, WriteExcel writeExcel) {
        ExcelWriterBuilder excelWriterBuilder = EasyExcelFactory.write(response.getOutputStream())
                .autoCloseStream(true).excelType(writeExcel.suffix()).inMemory(writeExcel.inMemory());


        // 设置密码
        if (StringUtils.isNotBlank(writeExcel.password())) {
            excelWriterBuilder.password(writeExcel.password());
        }
        // 排除字段
        if (writeExcel.exclude().length > 0) {
            excelWriterBuilder.excludeColumnFieldNames(Arrays.asList(writeExcel.exclude()));
        }

        //包含字段
        if (writeExcel.include().length > 0) {
            excelWriterBuilder.includeColumnFieldNames(Arrays.asList(writeExcel.include()));
        }

        //设置自定义转换器
        if (writeExcel.converter().length > 0) {
            for (Class<? extends Converter> aClass : writeExcel.converter()) {
                excelWriterBuilder.registerConverter(BeanUtils.instantiateClass(aClass));
            }
        }

        //设置拦截器 自定义样式等
        if (writeExcel.writeHandler().length > 0) {
            for (Class<? extends WriteHandler> aClass : writeExcel.writeHandler()) {
                excelWriterBuilder.registerWriteHandler(BeanUtils.instantiateClass(aClass));
            }
        }

        //模板处理
        return ExcelWriterUtil.getExcelWriterTemplate(excelWriterBuilder, configProperties, writeExcel.template());
    }


    public WriteSheet createSheet(Sheet sheet, Class<?> dataClass, String template,
                                  Class<? extends HeadGenerator> headClass) {

        Integer sheetNo = Math.max(sheet.sheetNo(), 0);

        // 是否模板写入
        ExcelWriterSheetBuilder writerSheetBuilder = StringUtils.isNotBlank(template) ? EasyExcelFactory.writerSheet(sheetNo)
                : EasyExcelFactory.writerSheet(sheetNo, sheet.sheetName());


        // 头信息增强 1. 优先使用 sheet 指定的头信息增强 2. 其次使用 @ResponseExcel 中定义的全局头信息增强
        Class<? extends HeadGenerator> headGenerateClass = null;
        if (isNotInterface(sheet.headGenerateClass())) {
            headGenerateClass = sheet.headGenerateClass();
        } else if (isNotInterface(headClass)) {
            headGenerateClass = headClass;
        }

        // 定义头信息增强则使用其生成头信息，否则使用 dataClass 来自动获取
        if (headGenerateClass != null) {
            HeadGenerator headGenerator = this.applicationContext.getBean(headClass);
            Assert.notNull(headGenerator, "The header generated bean does not exist.");
            HeadMeta head = headGenerator.head(dataClass);
            writerSheetBuilder.head(head.getHead());
            writerSheetBuilder.excludeColumnFieldNames(head.getIgnoreHeadFields());
        } else if (dataClass != null) {
            writerSheetBuilder.head(dataClass);
        }
        if (sheet.excludes().length > 0) {
            writerSheetBuilder.excludeColumnFieldNames(Arrays.asList(sheet.excludes()));
        }
        if (sheet.includes().length > 0) {
            writerSheetBuilder.includeColumnFieldNames(Arrays.asList(sheet.includes()));
        }
        return writerSheetBuilder.build();
    }

    /**
     * 是否为Null Head Generator
     *
     * @param headGeneratorClass 头部生成器
     * @return true 已指定 false 未指定(默认值)
     */
    private boolean isNotInterface(Class<? extends HeadGenerator> headGeneratorClass) {
        return !Modifier.isInterface(headGeneratorClass.getModifiers());
    }

    /**
     * 注入应用上下文
     *
     * @param applicationContext 应用上下文
     * @throws BeansException bean 异常
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
