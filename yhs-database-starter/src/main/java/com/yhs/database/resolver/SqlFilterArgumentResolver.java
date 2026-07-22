package com.yhs.database.resolver;

import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhs.database.utils.AntiSqlFilterUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;


/**
 * @author 07664-linwei
 * @version Id: SqlFilterArgumentResolver.java, v 0.1 2022/4/20 11:42 lw Exp $
 */
@Slf4j
public class SqlFilterArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(Page.class);
    }

    /**
     * @param parameter     入参集合
     * @param mavContainer  model 和 view
     * @param webRequest    web相关
     * @param binderFactory 入参解析
     * @return 检查后新的page对象
     * <p>
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        assert request != null;
        String[] ascArrays = request.getParameterValues("ascs");
        String[] descArrays = request.getParameterValues("descs");
        String current = request.getParameter("current");
        String size = request.getParameter("size");

        Page page = new Page();
        if (CharSequenceUtil.isNotBlank(current)) {
            page.setCurrent(Long.parseLong(current));
        }

        if (CharSequenceUtil.isNotBlank(size)) {
            page.setSize(Long.parseLong(size));
        }
        page.addOrder(AntiSqlFilterUtil.getOrderItem(ascArrays, descArrays));
        return page;
    }


}
