package com.yhs.base.constant;

/**
 * @author 04628-duanchengjun
 * @version Id: LogConstants.java, v 0.1 2019/4/25 15:29 duanchengjun Exp $
 */
public class LogConstants {
    /**
     * 请求跟踪id，traceId需要保持和zipkin中的key一直
     */
    public static final String TRACE_ID = "traceId";
    /**
     * 业务代码返回code，用于记录到access log文件中
     */
    public static final String CODE = "code";

    private LogConstants() {
    }

}
