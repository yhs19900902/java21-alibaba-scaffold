package com.yhs.encrypt.utils;

import cn.hutool.core.text.CharSequenceUtil;
import com.yhs.encrypt.enums.EncryptionSchemeEnum;
import com.yhs.encrypt.exception.EncryptException;
import lombok.experimental.UtilityClass;

import java.util.Objects;

/**
 * @author 07664-linwei
 * @version Id: CheckUtils.java, v 0.1 2022/7/5 14:31 lw Exp $
 */
@UtilityClass
public class CheckUtils {


    /**
     * 注解的 key 优先级高于 全局配置的 key
     *
     * @param globalSecretKey 全局
     * @param secretKey       注解
     * @param keyName         来源信息
     * @return key
     */
    public String checkAndGetKey(String secretKey, String globalSecretKey, String keyName) {
        if (CharSequenceUtil.isBlank(secretKey) && CharSequenceUtil.isBlank(globalSecretKey)) {
            throw new EncryptException(String.format("%s  Not configured,secretKey:%s,globalSecretKey:%s", keyName, secretKey, globalSecretKey));
        }
        return CharSequenceUtil.isBlank(secretKey) ? globalSecretKey : secretKey;
    }

    public String getEncryptionField(String encryptBodyField, String globalEncryptField) {
        if (CharSequenceUtil.isBlank(encryptBodyField) &&
                CharSequenceUtil.isBlank(globalEncryptField)) {
            throw new EncryptException("The global encryption field is not configured");
        }
        return CharSequenceUtil.isNotBlank(encryptBodyField) ? encryptBodyField : globalEncryptField;
    }


    public EncryptionSchemeEnum getEncryptionScheme(EncryptionSchemeEnum encryptionSchemeEnum, EncryptionSchemeEnum globalEncryptionSchemeEnum) {
        if (Objects.isNull(encryptionSchemeEnum) && Objects.isNull(globalEncryptionSchemeEnum)) {
            throw new EncryptException("The global or annotation encryption mode is not configured");
        }
        return Objects.isNull(encryptionSchemeEnum) ? globalEncryptionSchemeEnum : encryptionSchemeEnum;
    }
}
