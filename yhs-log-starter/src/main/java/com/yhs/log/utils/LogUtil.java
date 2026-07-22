package com.yhs.log.utils;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.URLUtil;
import com.yhs.base.constant.CommonConstant;
import com.yhs.base.constant.LogConstants;
import com.yhs.base.pojo.dto.OptLogDTO;
import com.yhs.base.utils.JSONUtil;
import com.yhs.base.utils.WebUtil;
import com.yhs.log.annotate.SysLog;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author 07664-linwei
 * @version Id: LogUtil.java, v 0.1 2022/4/22 11:31 lw Exp $
 */
@Slf4j
public class LogUtil {

    public static final int MAX_LENGTH = 65535;
    private static final String FORM_DATA_CONTENT_TYPE = "multipart/form-data";

    @NonNull
    public static OptLogDTO buildOptLogDTO(JoinPoint joinPoint, SysLog sysLog) {
        // 开始时间
        OptLogDTO optLogDTO = new OptLogDTO();

        optLogDTO.setDescription(sysLog.value());
        // 类名
        optLogDTO.setClassPath(joinPoint.getTarget().getClass().getName());
        //获取执行的方法名
        optLogDTO.setActionMethod(joinPoint.getSignature().getName());
        optLogDTO.setType(LogTypeEnum.OPT.getType());
        HttpServletRequest request = setParams(joinPoint, sysLog, optLogDTO);
        optLogDTO.setRequestIp(WebUtil.getIP(request));
        optLogDTO.setRequestUri(URLUtil.getPath(request.getRequestURI()));
        optLogDTO.setHttpMethod(request.getMethod());
        optLogDTO.setUa(CharSequenceUtil.sub(request.getHeader("user-agent"), 0, 500));

        optLogDTO.setTrace(MDC.get(LogConstants.TRACE_ID));
        optLogDTO.setStartTime(LocalDateTime.now());
        return optLogDTO;
    }

    @NonNull
    private static HttpServletRequest setParams(JoinPoint joinPoint, SysLog sysLog, OptLogDTO optLogDTO) {
        // 参数
        Object[] args = joinPoint.getArgs();

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes(), "只能在Spring Web环境使用@SysLog记录日志")).getRequest();
        String strArgs = getArgs(args, request);
        optLogDTO.setParams(getText(strArgs));
        return request;
    }

    /**
     * 截取指定长度的字符串
     *
     * @param val 参数
     * @return 截取文本
     */
    private static String getText(String val) {
        return CharSequenceUtil.sub(val, CommonConstant.ZERO, MAX_LENGTH);
    }


    private static String getArgs(Object[] args, HttpServletRequest request) {
        String strArgs = "";
        Object[] params = Arrays.stream(args).filter(item -> !(item instanceof ServletRequest || item instanceof ServletResponse)).toArray();

        try {
            if (!request.getContentType().contains(FORM_DATA_CONTENT_TYPE)) {
                strArgs = JSONUtil.toJson(params);
            } else {
                strArgs = "文件";
            }
        } catch (Exception e) {
            try {
                strArgs = Arrays.toString(params);
            } catch (Exception ex) {
                log.warn("解析参数异常", ex);
            }
        }
        return strArgs;
    }
}
