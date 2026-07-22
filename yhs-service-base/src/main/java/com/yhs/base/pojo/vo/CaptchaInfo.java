package com.yhs.base.pojo.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 03952-yehuasheng
 * @version Id: CaptchaInfo.java, v0.1 2023/9/12 11:03 yehuasheng Exp $
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CaptchaInfo extends BaseVO {
    /**
     * 验证码id
     */
    private String id;

    /**
     * 验证码
     */
    private String codes;

    /**
     * base64验证码
     */
    private String base64Code;
}
