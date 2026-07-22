package com.yhs.base.exception;

import com.yhs.base.pojo.vo.BusinessResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author 04628-duanchengjun
 * @version Id: FinalExceptionHandler.java, v 0.1 2019/4/26 14:50 duanchengjun Exp $
 */
@RestController
@RequestMapping("${server.error.path:${error.path:/error}}")
public class FinalExceptionHandler implements ErrorController {

    @RequestMapping(produces = "application/json; charset=UTF-8")
    public BusinessResponse<Void> error(HttpServletResponse resp, HttpServletRequest request) {
        // 错误处理逻辑
        HttpStatus status = getStatus(request);
        return BusinessResponse.fail(status.value(), status.getReasonPhrase());
    }

    protected HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try {
            return HttpStatus.valueOf(statusCode);
        } catch (Exception ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    public String getErrorPath() {
        return "/error";
    }

}
