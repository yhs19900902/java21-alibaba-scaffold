package com.yhs.lock.idempotent.aop;

import cn.hutool.core.util.StrUtil;
import com.yhs.base.constant.CommonConstant;
import com.yhs.lock.idempotent.annotate.IdempotentLock;
import com.yhs.lock.idempotent.ex.IdempotentException;
import com.yhs.lock.manger.RedissonLock;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 07664-linwei
 * @version Id: IdempotentAspect.java, v 0.1 2022/4/26 15:33 lw Exp $
 */
@Slf4j
@Aspect
public class IdempotentAspect {


    private static final SpelExpressionParser PARSER = new SpelExpressionParser();

    private static final String CACHE_KEY = "idempotent";


    private final RedissonLock redissonLock;


    public IdempotentAspect(RedissonLock redissonLock) {
        this.redissonLock = redissonLock;
    }

    @Pointcut("@annotation(com.yhs.lock.idempotent.annotate.IdempotentLock)")
    public void pointCut() {
    }


    @Around("pointCut()")
    public Object aroundPointCut(ProceedingJoinPoint joinPoint) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        Object resultObj = null;
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        if (!method.isAnnotationPresent(IdempotentLock.class)) {
            return resultObj;
        }
        IdempotentLock idempotent = method.getAnnotation(IdempotentLock.class);

        String key;
        Object[] args = joinPoint.getArgs();

        if (idempotent.key().contains(CommonConstant.SYMBOL_NUMBER)) {
            // 使用 spel 解析参数
            String[] params = signature.getParameterNames();
            StandardEvaluationContext context = new StandardEvaluationContext();

            for (int i = 0; i < params.length; i++) {
                context.setVariable(params[i], args[i]);
            }
            List<String> paramList = new ArrayList<>();
            String[] keyArr = idempotent.key().split(CommonConstant.AMPERSAND);
            Arrays.stream(keyArr).forEach(keyStr -> {
                Expression expression = PARSER.parseExpression(keyStr);
                String value = expression.getValue(context, String.class);
                if (StrUtil.isNotBlank(value)) {
                    paramList.add(value);
                }
            });
            key = StrUtil.join(CommonConstant.AT, paramList);
        } else if (StrUtil.isNotBlank(idempotent.key())) {
            // 没有使用spel 表达式 直接使用key 值
            key = idempotent.key();
        } else {
            // 若没有配置 幂等 标识编号，则使用 url + 参数列表作为区分
            String url = request.getRequestURL().toString();
            String argString = Arrays.asList(args).toString();
            key = url + argString;
        }

        long expireTime = idempotent.expireTime();
        String info = idempotent.info();
        TimeUnit timeUnit = idempotent.timeUnit();

        boolean lock = redissonLock.lock(key, 0, timeUnit, expireTime);
        if (!lock) {
            log.info("[idempotent]:has stored key={},expireTime={}{},now={}", key, expireTime,
                    timeUnit, LocalDateTime.now());
            throw new IdempotentException("[idempotent]:" + info);
        }
        try {
            resultObj = joinPoint.proceed(args);
        } catch (Throwable throwable) {
            log.error("[idempotent] error :{}", throwable.getMessage());
        } finally {
            if (idempotent.delKey()) {
                redissonLock.unLock(key);
                log.info("[idempotent]:has removed key={}", key);
            }
        }
        return resultObj;
    }
}
