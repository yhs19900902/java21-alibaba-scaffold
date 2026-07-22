package com.yhs.log.aspect;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.yhs.base.pojo.dto.OptLogDTO;
import com.yhs.base.utils.JSONUtil;
import com.yhs.log.annotate.SysLog;
import com.yhs.log.event.SysLogEvent;
import com.yhs.log.utils.LogTypeEnum;
import com.yhs.log.utils.LogUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @author 07664-linwei
 * @version Id: SysLogAspect.java, v 0.1 2022/4/22 11:08 lw Exp $
 */
@Data
@Slf4j
@Aspect
@RequiredArgsConstructor
public class SysLogAspect {


    private final ApplicationEventPublisher publisher;


    @SneakyThrows
    @Around("@annotation(sysLog)")
    public Object doBefore(ProceedingJoinPoint point, SysLog sysLog) {
        OptLogDTO optLogDTO = LogUtil.buildOptLogDTO(point, sysLog);
        Object obj;
        try {
            obj = point.proceed();
            optLogDTO.setResult(JSONUtil.toJson(obj));
        } catch (Throwable e) {
            optLogDTO.setType(LogTypeEnum.ERROR.getType());
            optLogDTO.setExDetail(ExceptionUtil.stacktraceToString(e, LogUtil.MAX_LENGTH));
            throw e;
        } finally {
            optLogDTO.setFinishTime(LocalDateTime.now());
            optLogDTO.setConsumingTime(optLogDTO.getStartTime().until(optLogDTO.getFinishTime(), ChronoUnit.MILLIS));
            publisher.publishEvent(new SysLogEvent(optLogDTO));
        }
        return obj;
    }

}
