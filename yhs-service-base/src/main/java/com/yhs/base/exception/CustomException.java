package com.yhs.base.exception;

import com.yhs.base.pojo.vo.BusinessResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 07664-linwei
 * @version Id: CusomException.java, v 0.1 2022/12/13 17:01 lw Exp $
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CustomException extends RuntimeException {

    private int code = BusinessResponse.RESPONSE_ERROR;
    private String message;

    private Object data;

    public CustomException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public CustomException(int code, String message, Object data) {
        super(message);
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
