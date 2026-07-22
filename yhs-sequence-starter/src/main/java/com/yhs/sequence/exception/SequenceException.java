package com.yhs.sequence.exception;


import com.yhs.base.exception.BizException;

/**
 * @author 07664-linwei
 * @version Id: SequenceException.java, v 0.1 2022/4/29 15:50 lw Exp $
 */
public class SequenceException extends BizException {

    public SequenceException(int code, String message) {
        super(code, message);
    }

    public SequenceException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public SequenceException(int code, Throwable cause) {
        super(code, cause);
    }
}
