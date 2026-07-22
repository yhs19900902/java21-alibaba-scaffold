package com.yhs.dynamic.config;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;


/**
 * 清空当前线程的数据源信息
 *
 * @author 07664-linwei
 * @version Id: DynamicDatasourceClearInterceptor.java, v 0.1 2022/5/18 15:34 lw Exp $
 */
public class DynamicDataSourceClearInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        DynamicDataSourceContextHolder.clear();
    }
}
