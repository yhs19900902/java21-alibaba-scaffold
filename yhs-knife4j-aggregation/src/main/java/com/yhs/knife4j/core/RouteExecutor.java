package com.yhs.knife4j.core;

/**
 * @author 07664-linwei
 * @version Id: RouteExecutor.java, v 0.1 2023/7/25 9:40 lw Exp $
 */
public interface RouteExecutor {

    RouteResponse executor(RouteRequestContext requestContext);
}
