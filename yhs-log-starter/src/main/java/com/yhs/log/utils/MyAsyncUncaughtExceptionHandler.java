package com.yhs.log.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

/**
 * @author 07664-linwei
 * @version Id: MyAsyncUncaughtExceptionHandler.java, v 0.1 2022/10/17 11:31 lw Exp $
 */
@Slf4j
public class MyAsyncUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        log.error("method name {} , params {} , exception {}", method.getName(), params, ex.getMessage());
    }
}
