package com.yhs.xss.filter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharPool;
import cn.hutool.core.text.CharSequenceUtil;
import com.yhs.xss.wrapper.XssRequestWrapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.List;

/**
 * @author 07664-linwei
 * @version Id: XssFilter.java, v 0.1 2022/4/24 10:23 lw Exp $
 */
@Slf4j
public class XssFilter implements Filter {
    /**
     * 可放行的请求路径
     */

    public static final String IGNORE_PATH = "ignorePath";

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    /**
     * 可放行的请求路径列表
     */
    private List<String> ignorePathList;


    @Override
    public void init(FilterConfig fc) {
        this.ignorePathList = CharSequenceUtil.split(fc.getInitParameter(IGNORE_PATH), CharPool.COMMA);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // 判断uri是否包含项目名称
        String uriPath = ((HttpServletRequest) request).getRequestURI();
        if (isIgnorePath(uriPath)) {
            log.debug("忽略过滤路径=[{}]", uriPath);
            chain.doFilter(request, response);
            return;
        }
        log.debug("过滤器包装请求路径=[{}]", uriPath);
        chain.doFilter(new XssRequestWrapper((HttpServletRequest) request), response);
    }

    private boolean isIgnorePath(String uriPath) {
        if (CharSequenceUtil.isBlank(uriPath)) {
            return true;
        }
        if (CollUtil.isEmpty(ignorePathList)) {
            return false;
        }
        return ignorePathList.stream().anyMatch(url -> uriPath.startsWith(url) || ANT_PATH_MATCHER.match(url, uriPath));
    }


}
