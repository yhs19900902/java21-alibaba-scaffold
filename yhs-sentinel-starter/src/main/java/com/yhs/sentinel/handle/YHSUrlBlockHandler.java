package com.yhs.sentinel.handle;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSONObject;
import com.yhs.base.pojo.vo.BusinessResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

/**
 * @author 07664-linwei
 * @version Id: YHSUrlBlockHandler.java, v 0.1 2023/7/19 14:53 lw Exp $
 */
@Slf4j
public class YHSUrlBlockHandler implements BlockExceptionHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        log.error("Too many request, please retry later.", e);
        BusinessResponse<?> result = BusinessResponse.fail("Too many request, please retry later.");
        response.getWriter().print(JSONObject.toJSONString(result));
    }
}
