package com.yhs.base.exception;

/**
 * @author 04628-duanchengjun
 * @version Id: ZipException.java, v 0.1 2019/4/25 9:32 duanchengjun Exp $
 */
public class ZipException extends Exception {

    public ZipException() {
        super();
    }

    public ZipException(String message) {
        super(message);
    }

    public ZipException(String message, Throwable cause) {
        super(message, cause);
    }

    public ZipException(Throwable cause) {
        super(cause);
    }
}
