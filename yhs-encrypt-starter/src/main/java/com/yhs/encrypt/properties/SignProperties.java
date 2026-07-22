package com.yhs.encrypt.properties;

import com.yhs.encrypt.constant.EncryptConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * @author 07664-linwei
 * @version Id: SignProperties.java, v 0.1 2022/7/8 14:34 lw Exp $
 */
@Data
@Configuration
@ConfigurationProperties(value = SignProperties.SIGN_KEY)
public class SignProperties {

    public static final String SIGN_KEY = "yhs.sign";

    /**
     * 签名时排除字段
     */
    private List<String> ignore = Arrays.asList(EncryptConstant.TOKEN_HEADER, EncryptConstant.SIGN_HEADER,
            EncryptConstant.SIGN_IGNORE_CODE, EncryptConstant.SIGN_IGNORE_MSG);

    /**
     * 返回对象加签字段,默认sign
     */
    private String signName = EncryptConstant.SIGN_HEADER;

    /**
     * 签名 rsa 全局私钥
     */
    private String signRsaPrivateSecretKey;
    /**
     * 签名 rsa 全局 公钥
     */
    private String signRsaPublicSecretKey;
}
