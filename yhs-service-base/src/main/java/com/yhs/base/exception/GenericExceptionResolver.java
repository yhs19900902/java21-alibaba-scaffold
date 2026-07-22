package com.yhs.base.exception;

import com.yhs.base.pojo.vo.BusinessResponse;
import com.yhs.base.utils.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import java.io.PrintWriter;

/**
 * @author 04628-duanchengjun
 * @version Id: GenericExceptionResolver.java, v 0.1 2019/4/26 14:56 duanchengjun Exp $
 */
public class GenericExceptionResolver implements HandlerExceptionResolver, Ordered {

    private static final int PARAMETER_ERROR_CODE = 500001;

    private static final Logger logger = LoggerFactory.getLogger(GenericExceptionResolver.class);

    /**
     * 解析异常
     *
     * @param request  请求
     * @param response 响应
     * @param handler  执行处理穷
     * @param e        参数
     * @return 视图
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {
        logger.info("Resolve exception, msg:{}", e.getMessage(), e);
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache, must-revalidate");
        String data;
        if (e instanceof BizException bizException) {
            data = JSONUtil.toJson(BusinessResponse.fail(bizException.getCode(), bizException.getMessage()));
        } else if (e instanceof MaxUploadSizeExceededException) {
            data = JSONUtil.toJson(BusinessResponse.fail("The file size exceeds the limit"));
        } else if (e instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
            data = JSONUtil.toJson(BusinessResponse.fail(PARAMETER_ERROR_CODE, methodArgumentNotValidException.getBindingResult().getAllErrors().get(0).getDefaultMessage()));
        } else if (e instanceof IllegalArgumentException illegalArgumentException) {
            data = JSONUtil.toJson(BusinessResponse.fail(PARAMETER_ERROR_CODE, illegalArgumentException.getMessage()));
        } else if (e instanceof ConstraintViolationException constraintViolationException) {
            data = JSONUtil.toJson(BusinessResponse.fail(PARAMETER_ERROR_CODE, constraintViolationException.getMessage()));
        } else if (e instanceof BindException bindException) {
            data = JSONUtil.toJson(BusinessResponse.fail(PARAMETER_ERROR_CODE, bindException.getBindingResult().getAllErrors().get(0).getDefaultMessage()));
        } else if (e instanceof CustomException customException) {
            data = JSONUtil.toJson(BusinessResponse.fail(customException.getCode(), customException.getMessage(), customException.getData()));
        } else {
            data = JSONUtil.toJson(BusinessResponse.fail("service exception"));
        }
        try {
            PrintWriter writer = response.getWriter();
            writer.write(data);
            writer.flush();
        } catch (Exception ex) {
            logger.error("Exception:", ex);
        }
        return new ModelAndView();
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

}
