package com.yhs.encrypt.handler.sign;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.yhs.base.pojo.vo.BusinessResponse;
import com.yhs.encrypt.annotate.Encrypt;
import com.yhs.encrypt.annotate.Sign;
import com.yhs.encrypt.constant.EncryptConstant;
import com.yhs.encrypt.enums.EncryptionSchemeEnum;
import com.yhs.encrypt.handler.encrypt.EncryptHandler;
import com.yhs.encrypt.properties.EncryptProperties;
import com.yhs.encrypt.properties.SignProperties;
import com.yhs.encrypt.utils.CheckUtils;
import com.yhs.encrypt.utils.SignUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author 07664-linwei
 * @version Id: SignReturnValueHandler.java, v 0.1 2022/7/8 11:05 lw Exp $
 */
@Slf4j
@RequiredArgsConstructor
public class SignReturnValueHandler implements HandlerMethodReturnValueHandler {

    private final SignProperties signProperties;

    private final EncryptProperties encryptProperties;

    private final List<EncryptHandler> encryptHandlers;

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.hasMethodAnnotation(Encrypt.class) ||
                returnType.hasMethodAnnotation(Sign.class);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, @Nullable NativeWebRequest webRequest) throws Exception {

        mavContainer.setRequestHandled(true);
        String jsonString;
        boolean isBusinessResponse = false;
        if (returnValue instanceof BusinessResponse) {
            BusinessResponse resultResponse = (BusinessResponse) returnValue;
            jsonString = JSON.toJSONString(resultResponse.getData());
            isBusinessResponse = true;
        } else {
            jsonString = JSON.toJSONString(returnValue, EncryptConstant.SIGN_DATA_FORMAT);
        }

        if (Objects.isNull(jsonString)) {
            writer(Objects.requireNonNull(webRequest), returnValue, null);
            return;
        }

        JSONObject encryptJson = JSONObject.parseObject(jsonString);

        Sign sign = returnType.getMethodAnnotation(Sign.class);
        Map<String, String> headers = sign(sign, encryptJson);

        String encryptStr = encrypt(returnType, encryptJson);
        if (StrUtil.isNotBlank(encryptStr)) {
            if (isBusinessResponse) {
                encryptJson = JSON.parseObject(JSON.toJSONString(returnValue, EncryptConstant.SIGN_DATA_FORMAT));
            } else {
                encryptJson.clear();
            }
            encryptJson.put(EncryptConstant.DATA_SECRET_HEADER, encryptStr);
        } else {
            jsonString = JSON.toJSONString(returnValue, EncryptConstant.SIGN_DATA_FORMAT);
            encryptJson = JSONObject.parseObject(jsonString);
        }

        writer(Objects.requireNonNull(webRequest), encryptJson, headers);
    }


    private Map<String, String> sign(Sign sign, JSONObject returnMap) {
        if (sign == null || !sign.outIsSign()) {
            return null;
        }
        // 需要签名的map
        Map<String, Object> signMap = new HashMap<>(returnMap.size());
        returnMap.forEach((k, v) -> {
            if (!signProperties.getIgnore().contains(k)) {
                signMap.put(k, v);
            }
        });

        Map<String, String> headers = new HashMap<>(3);
        //添加时间戳 防止数据签名一致
        String timestamp = String.valueOf(System.currentTimeMillis());
        headers.put(EncryptConstant.HEADER_TIMESTAMP, timestamp);
        //随机数
        String nonce = "signature_" + IdUtil.simpleUUID();
        headers.put(EncryptConstant.NONCE_HEADER, nonce);
        //签名
        String md5Str = SignUtils.signParamBuilder(timestamp, nonce, null, JSON.toJSONString(signMap));
        String signStr = SignUtils.sign(md5Str, signProperties.getSignRsaPrivateSecretKey());
        headers.put(signProperties.getSignName(), signStr);
        return headers;
    }

    private String encrypt(MethodParameter returnType, JSONObject returnMap) {
        Encrypt encrypt = Objects.requireNonNull(returnType).getMethodAnnotation(Encrypt.class);
        if (encrypt == null) {
            encrypt = returnType.getDeclaringClass().getDeclaredAnnotation(Encrypt.class);
        }
        if (encrypt == null) {
            return null;
        }
        // 获取需要加密的数据
        if (encrypt.addTimestamp()) {
            returnMap.put(EncryptConstant.TIMESTAMP, System.currentTimeMillis());
        }
        EncryptionSchemeEnum encryptionSchemeEnum = CheckUtils.getEncryptionScheme(encrypt.encryptionScheme(),
                encryptProperties.getEncryptionSchemeEnum());
        String secretKey = encrypt.secretKey();
        //加密
        return encryptHandlers.stream().filter(handler -> handler.support(encryptionSchemeEnum))
                .findFirst().orElseThrow(() -> new RuntimeException(""))
                .encrypt(JSON.toJSONString(returnMap), secretKey, encryptProperties);
    }

    private void writer(NativeWebRequest webRequest, Object obj, Map<String, String> headers) throws IOException {
        //写出数据
        HttpServletResponse httpResponse = webRequest.getNativeResponse(HttpServletResponse.class);
        assert httpResponse != null;
        httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        if (headers != null && headers.size() > 0) {
            headers.forEach(httpResponse::setHeader);
        }
        httpResponse.setCharacterEncoding("UTF-8");
        PrintWriter writer = httpResponse.getWriter();
        writer.write(JSON.toJSONString(obj));
        writer.flush();
    }
}
