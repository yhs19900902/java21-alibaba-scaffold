package com.yhs.encrypt.constant;

/**
 * @author 07664-linwei
 * @version Id: EncryptConstant.java, v 0.1 2022/7/8 14:40 lw Exp $
 */
public interface EncryptConstant {


    String TOKEN_HEADER = "token";
    String SIGN_HEADER = "Signature";

    String NONCE_HEADER = "Nonce";
    String DATA_SECRET_HEADER = "data";
    String DATA_HEADER = "data";
    String HEADER_TIMESTAMP = "Timestamp";

    String TIMESTAMP = "timestamp";
    String SIGN_DATA_FORMAT = "yyyy-MM-dd HH:mm:ss";
    String SIGN_IGNORE_MSG = "rt_msg";
    String SIGN_IGNORE_CODE = "rt_code";
}
