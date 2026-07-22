package com.yhs.zipkin.constant;

/**
 * @author 03952-yehuasheng
 * @version Id: SingleCorrelationNameConstant.java, v0.1 2023/9/15 09:00 yehuasheng Exp $
 */
public class SingleCorrelationNameConstant {
    public static final String X_B3_TRACE_ID = "X-B3-TraceId";
    public static final String X_B3_PARENT_SPAN_ID = "X-B3-ParentSpanId";
    public static final String X_B3_SPAN_ID = "X-B3-SpanId";
    public static final String X_SPAN_EXPORT = "X-Span-Export";
    private SingleCorrelationNameConstant() {
    }
}
