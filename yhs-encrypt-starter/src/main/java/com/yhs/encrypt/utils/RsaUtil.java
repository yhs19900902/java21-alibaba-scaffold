package com.yhs.encrypt.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 07664-linwei
 * @version Id: RsaUtil.java, v 0.1 2023/8/18 9:56 lw Exp $
 */
public class RsaUtil {

    private static final int MAX_DECRYPT_BLOCK = 128;
    private static final String RSA = "RSA";
    private static final Logger log = LoggerFactory.getLogger(RsaUtil.class);
    private static PrivateKey privateKeyFactory;

    private RsaUtil() {

    }


    /**
     * 私钥解密
     *
     * @param privateKey
     * @param data
     * @return
     */
    public static String decryptToLong(String privateKey, byte[] data) {
        try {
            Cipher cipher = getCipher(privateKey);
            if (Objects.isNull(cipher)) {
                return null;
            }
            int inputLen = data.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            String decryptString = out.toString();
            out.close();
            return decryptString;
        } catch (Exception e) {
            return null;
        }

    }


    /**
     * 初始化Cipher
     *
     * @return
     */
    public static Cipher getCipher(String privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA);
            if (privateKeyFactory == null) {
                getPrivateKey(privateKey);
            }
            cipher.init(Cipher.DECRYPT_MODE, privateKeyFactory);
            return cipher;
        } catch (Exception e) {
            return null;
        }

    }

    private static void getPrivateKey(String privateKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(privateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            privateKeyFactory = keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            privateKeyFactory = null;
        }
    }

    /**
     * 参数拼接
     *
     * @param timestamp   时间戳
     * @param nonce       信息
     * @param queryParams 发送参数
     * @param requestBody 请求体
     * @return signParamBuilder
     */
    public static String signParamBuilder(String timestamp, String nonce, MultiValueMap<String, String> queryParams, String requestBody) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("timestamp").append(timestamp).append("nonce").append(nonce);

        if (CollUtil.isNotEmpty(queryParams)) {
            /**
             * 将参数按升序排序
             */
            LinkedHashMap<String, List<String>> collectSort = queryParams.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldVal, newVal) -> oldVal, LinkedHashMap::new));
            collectSort.forEach((k, v) -> stringBuilder.append(k).append(CollUtil.isEmpty(v) ? "" : v.get(0)));
        }
        //处理body请求参数
        if (CharSequenceUtil.isNotBlank(requestBody)) {
//            if (MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
            Object parse = JSON.parse(requestBody);
            if (parse instanceof JSONArray) {
                treeArray((JSONArray) parse, stringBuilder);
            } else if (parse instanceof JSONObject) {
                treeObject((JSONObject) parse, stringBuilder);
            }
//            } else {
//                //表单形式
//                String[] split = requestBody.split("&");
//                Map<String, String> map = new HashMap<>();
//                Arrays.stream(split).forEach(e -> {
//                    map.put(e.split("=")[0], e.split("=")[1]);
//                });
//                treeObject(JSONObject.parseObject(JSON.toJSONString(map)), stringBuilder);
//            }

        }
        return SecureUtil.md5(stringBuilder.toString());
    }

    public static void treeObject(JSONObject jsonObject, StringBuilder stringBuilder) {
        LinkedHashMap<String, Object> hashMap = new LinkedHashMap<>();
        // 不能使用stream处理map有值为null的
        // 不然抛出空指针 collectors.toMap 对值要求必须不能为空
        List<Map.Entry<String, Object>> sortList = jsonObject.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());

        for (Map.Entry<String, Object> stringObjectEntry : sortList) {
            hashMap.put(stringObjectEntry.getKey(), stringObjectEntry.getValue());
        }
        hashMap.forEach((k, v) -> {
            //把键先加入
            stringBuilder.append(k);
//            String value = toJsonString(v);
            if (v instanceof JSONArray) {
                treeArray((JSONArray) v, stringBuilder);
            } else if (v instanceof JSONObject) {
                treeObject((JSONObject) v, stringBuilder);
            } else {
                stringBuilder.append(v);
            }
        });
    }

    public static void treeArray(JSONArray jsonArray, StringBuilder stringBuilder) {
        if (!jsonArray.isEmpty()) {
            jsonArray.forEach(e -> {
//                String array = toJsonString(e);
                if (e instanceof JSONArray) {
                    treeArray((JSONArray) e, stringBuilder);
                } else if (e instanceof JSONObject) {
                    treeObject((JSONObject) e, stringBuilder);
                } else {
                    stringBuilder.append(e);
                }
            });
        }
    }

    /**
     * toJsonString
     *
     * @param value
     * @return
     */
    private static String toJsonString(Object value) {
        return JSON.toJSONString(value, SerializerFeature.WriteMapNullValue);
    }

}
