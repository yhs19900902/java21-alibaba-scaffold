package com.yhs.encrypt.advice;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.yhs.encrypt.annotate.Decrypt;
import com.yhs.encrypt.annotate.Sign;
import com.yhs.encrypt.constant.EncryptConstant;
import com.yhs.encrypt.enums.EncryptionSchemeEnum;
import com.yhs.encrypt.exception.DecryptionException;
import com.yhs.encrypt.exception.SignException;
import com.yhs.encrypt.handler.encrypt.EncryptHandler;
import com.yhs.encrypt.message.DecryptHttpInputMessage;
import com.yhs.encrypt.properties.EncryptProperties;
import com.yhs.encrypt.properties.SignProperties;
import com.yhs.encrypt.utils.CheckUtils;
import com.yhs.encrypt.utils.SignUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author 07664-linwei
 * @version Id: DecryptRequestBodyAdvice.java, v 0.1 2022/7/5 15:22 lw Exp $
 */
@Slf4j
@SuppressWarnings("NullableProblems")
@Order(1)
@RestControllerAdvice
@AllArgsConstructor
public class DecryptRequestBodyAdvice implements RequestBodyAdvice {


    private final EncryptProperties encryptProperties;

    private final SignProperties signProperties;

    private final List<EncryptHandler> encryptHandlers;


    @Override
    public boolean supports(MethodParameter methodParameter, @Nullable Type targetType, @Nullable Class<? extends HttpMessageConverter<?>> converterType) {
        // 获取类上面的注解
        Annotation[] annotations = methodParameter.getDeclaringClass().getAnnotations();
        //判断类和接口上是否包含加密注解
        if (Arrays.stream(annotations).anyMatch(Decrypt.class::isInstance) ||
                Arrays.stream(annotations).anyMatch(Sign.class::isInstance)) {
            return true;
        }
        // 获取方法上的注解
        Decrypt methodAnnotation = methodParameter.getMethodAnnotation(Decrypt.class);
        // 获取方法上的注解
        Sign signMethodAnnotation = methodParameter.getMethodAnnotation(Sign.class);

        return methodAnnotation != null || signMethodAnnotation != null;
    }


    @Override
    public HttpInputMessage beforeBodyRead(@Nullable HttpInputMessage inputMessage, @Nullable MethodParameter parameter, @Nullable Type targetType, @Nullable Class<? extends HttpMessageConverter<?>> converterType) throws IOException {


        Decrypt decrypt = Objects.requireNonNull(parameter).getMethodAnnotation(Decrypt.class);
        if (decrypt == null) {
            decrypt = parameter.getDeclaringClass().getDeclaredAnnotation(Decrypt.class);
        }

        Sign sign = Objects.requireNonNull(parameter).getMethodAnnotation(Sign.class);
        if (sign == null) {
            sign = parameter.getDeclaringClass().getDeclaredAnnotation(Sign.class);
        }

        if (decrypt == null && sign == null) {
            return inputMessage;
        }


        if (!Optional.ofNullable(inputMessage).map(x -> {
            try {
                return x.getBody();
            } catch (IOException e) {
                //     log.error(e.getMessage());
                return null;
            }
        }).isPresent()) {
            return inputMessage;
        }

        String body = IOUtils.toString((inputMessage.getBody()), CharsetUtil.CHARSET_UTF_8);
        JSONObject req = JSON.parseObject(body);
        String signStr = getHeader(inputMessage, EncryptConstant.SIGN_HEADER);
        String timestamp = getHeader(inputMessage, EncryptConstant.HEADER_TIMESTAMP);
        String nonce = getHeader(inputMessage, EncryptConstant.NONCE_HEADER);
        if (decrypt != null) {
            req = decrypt(req, decrypt, parameter);
        }

        if (sign != null && sign.validSign()) {
            verifySign(req, sign, timestamp, nonce, signStr);
        }

        return new DecryptHttpInputMessage(IOUtils.toInputStream(req.toJSONString(), CharsetUtil.CHARSET_UTF_8), inputMessage.getHeaders());
    }

    private String getHeader(HttpInputMessage inputMessage, String headerName) {
        return Optional.ofNullable(inputMessage.getHeaders().get(headerName)).map(x -> x.get(0)).orElse(null);
    }

    /**
     * 解密
     *
     * @param req
     * @param decrypt
     */
    private JSONObject decrypt(JSONObject req, Decrypt decrypt, MethodParameter parameter) {
        String body = (String) req.get(encryptProperties.getDecryptionField());

        if (CharSequenceUtil.isBlank(body)) {
            log.error("The request body is an empty string, parsing failed,, method{}", Objects.requireNonNull(parameter.getMethod()).getName());
            throw new DecryptionException("The request body is an empty string, parsing failed,  method " + parameter.getMethod().getName());
        }
        EncryptionSchemeEnum encryptionSchemeEnum = CheckUtils.getEncryptionScheme
                (decrypt.decryptionScheme(), encryptProperties.getEncryptionSchemeEnum());
        String secretKey = decrypt.secretKey();
        //解密密
        String decryptResult = encryptHandlers.stream().filter(handler -> handler.support(encryptionSchemeEnum))
                .findFirst().orElseThrow(() -> new DecryptionException("If no proper decryption handler is found, check whether the configuration is correct"))
                .decrypt(body, secretKey, encryptProperties);
        assert CharSequenceUtil.isBlank(decryptResult);
        JSONObject decryptJson = JSON.parseObject(decryptResult);
        // 验证数据是否超时
        if (decrypt.timeOut() > 0) {
            long timestampParam = Long.parseLong(decryptJson.getOrDefault
                    (EncryptConstant.TIMESTAMP, "0").toString());
            long now = System.currentTimeMillis();
            if (now - timestampParam > decrypt.timeOut()) {
                throw new DecryptionException("Request data invalid");
            }
        }
        return decryptJson;
    }

    private void verifySign(JSONObject req, Sign sign, String timestamp, String nonce, String signStr) {
        String signKey = signProperties.getSignRsaPublicSecretKey();

        if (StrUtil.isBlank(signStr)) {
            log.error("The request sign field cannot be empty");
        }

        // 签名排除字段
        signProperties.getIgnore().forEach(req::remove);
        //拼接入参
        String signService = SignUtils.signParamBuilder(timestamp, nonce, null, JSON.toJSONString(req));

        boolean verify = SignUtils.verify(signKey, signStr, signService);

        if (!verify) {
            log.error("The digital signature verification fails,parameter:{}, sign :{} , To generate the signature：{} ",
                    req, signStr, signStr);
            throw new SignException("The digital signature verification fails:" + req);
        }

        // 验证签名是否超时
        if (sign.timeOut() > 0) {
            long timestampParam = Long.parseLong(timestamp);
            long now = System.currentTimeMillis();
            if (now - timestampParam > sign.timeOut()) {
                throw new SignException("The request sign has timed out");
            }
        }
    }


    @Override
    public Object afterBodyRead(@Nullable Object body, @Nullable HttpInputMessage inputMessage, @Nullable MethodParameter parameter, @Nullable Type targetType, @Nullable Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }


    @Override
    public Object handleEmptyBody(@Nullable Object body, @Nullable HttpInputMessage inputMessage, @Nullable MethodParameter parameter, @Nullable Type targetType, @Nullable Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }
}
