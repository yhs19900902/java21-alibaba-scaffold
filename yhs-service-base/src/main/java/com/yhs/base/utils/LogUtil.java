package com.yhs.base.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 03952-yehuasheng
 * @version Id: LogUtil.java, v0.1 2023/9/11 17:46 yehuasheng Exp $
 */
public final class LogUtil {
    private static final Logger normal = LoggerFactory.getLogger("normal");
    private static final Logger threshold = LoggerFactory.getLogger("threshold");
    private static final Logger trace = LoggerFactory.getLogger("trace");

    private LogUtil() {
    }

    public static Logger getLogger() {
        return normal;
    }

    public static Logger getLogger(String logName) {
        return LoggerFactory.getLogger(logName);
    }

    public static Logger getThresholdLogger() {
        return threshold;
    }

    public static Logger getTraceLogger() {
        return trace;
    }
}
