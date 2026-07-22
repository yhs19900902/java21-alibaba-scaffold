package com.yhs.easyexcel.aop;


import com.alibaba.excel.EasyExcelFactory;
import com.yhs.easyexcel.annotate.ReadExcel;
import com.yhs.easyexcel.listener.ListAnalysisEventListener;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import java.io.InputStream;
import java.util.List;

/**
 * 上传excel 解析注解
 *
 * @author 07664-linwei
 * @version Id: YHSRequestExcelArgumentResolver.java, v 0.1 2022/6/23 10:25 lw Exp $
 */
public class YHSReadExcelArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * 确定是否包含 RequestExcel 注解
     *
     * @param parameter 方法参数
     * @return 是否支持
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(ReadExcel.class);
    }

    /**
     * 读取 excel数据并且绑定到参数上
     *
     * @param parameter     方法参数
     * @param mavContainer  mvc容器
     * @param webRequest    request 对象
     * @param binderFactory 绑定工厂
     * @return 参数对象
     * @throws Exception 异常
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        Class<?> parameterType = parameter.getParameterType();
        if (!parameterType.isAssignableFrom(List.class)) {
            throw new IllegalArgumentException(
                    "Excel upload request resolver error, @ReadExcel parameter is not List " + parameterType);
        }
        // 处理自定义 readListener
        ReadExcel readExcel = parameter.getParameterAnnotation(ReadExcel.class);
        assert readExcel != null;
        Class<? extends ListAnalysisEventListener<?>> readListenerClass = readExcel.readListener();

        ListAnalysisEventListener<?> readListener = BeanUtils.instantiateClass(readListenerClass);
        // 获取请求文件流
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        readListener.setMaxRows(readExcel.maxRows());
        assert request != null;
        InputStream inputStream;
        if (request instanceof MultipartRequest) {
            MultipartFile file = ((MultipartRequest) request).getFile(readExcel.fileName());
            assert file != null;
            inputStream = file.getInputStream();
        } else {
            inputStream = request.getInputStream();
        }

        // 获取目标类型
        Class<?> excelModelClass = ResolvableType.forMethodParameter(parameter).getGeneric(0).resolve();

        // 这里需要指定读用哪个 class 去读，然后读取第一个 sheet 文件流会自动关闭
        EasyExcelFactory.read(inputStream, excelModelClass, readListener).ignoreEmptyRow(readExcel.ignoreEmptyRow())
                .sheet().doRead();

        // 校验失败的数据处理 交给 BindResult
        WebDataBinder dataBinder = binderFactory.createBinder(webRequest, readListener.getErrors(), "excel");
        ModelMap model = mavContainer.getModel();
        model.put(BindingResult.MODEL_KEY_PREFIX + "excel", dataBinder.getBindingResult());

        return readListener.getList();
    }
}
