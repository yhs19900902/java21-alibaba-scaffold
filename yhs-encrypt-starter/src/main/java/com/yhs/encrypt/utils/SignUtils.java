package com.yhs.encrypt.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 07664-linwei
 * @version Id: SignUtils.java, v 0.1 2022/7/8 12:02 lw Exp $
 */

@Slf4j
@UtilityClass
public class SignUtils {


    public String signParamBuilder(String timestamp, String nonce, Map<String, String> queryParams, String requestBody) {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("timestamp").append(timestamp).append("nonce").append(nonce);

        if (CollUtil.isNotEmpty(queryParams)) {
            /**
             * 将参数按升序排序
             */
            LinkedHashMap<String, String> collectSort = queryParams.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldVal, newVal) -> oldVal, LinkedHashMap::new));
            collectSort.forEach((k, v) -> stringBuilder.append(k).append(v));
        }
        //处理body请求参数
        if (CharSequenceUtil.isNotBlank(requestBody)) {
            Object parse = JSON.parse(requestBody);

            if (parse instanceof JSONArray) {
                RsaUtil.treeArray((JSONArray) parse, stringBuilder);
            } else if (parse instanceof JSONObject) {
                RsaUtil.treeObject((JSONObject) parse, stringBuilder);
            }

        }

        //获取摘要md5
        return SecureUtil.md5(stringBuilder.toString());

    }


    public String sign(String signValue, String privateKeyStr) {
        try {


            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            // 使用RSA私钥进行签名
            byte[] signature = signData(signValue, privateKey);

            //base64转码
            return Base64.getEncoder().encodeToString(signature);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private byte[] signData(String data, PrivateKey privateKey) {
        try {
            Signature signature = Signature.getInstance(SignAlgorithm.SHA256withRSA.getValue());

            signature.initSign(privateKey);

            signature.update(data.getBytes(StandardCharsets.UTF_8));


            return signature.sign();
        } catch (Exception e) {
            return new byte[0];
        }


    }

    public boolean verify(String publicKey, String signStr, String body) {

        try {
            Sign sign = SecureUtil.sign(SignAlgorithm.SHA256withRSA, null, publicKey);
            byte[] signBody = Base64.getDecoder().decode(signStr);
            return sign.verify(body.getBytes(), signBody);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }

}
