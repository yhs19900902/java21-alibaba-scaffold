package com.yhs.database.properties;

import lombok.Getter;

/**
 * 多租户 header value 加密方式
 *
 * @author 07664-linwei
 * @version Id: MultiHeaderEncryptType.java, v 0.1 2022/3/12 12:00 lw Exp $
 */
@Getter
public enum MultiHeaderEncryptType {


    /**
     * 不启用加密方式
     */
    NONE("不启用加密方式"),
    /**
     * aes 加密方式
     */
    AES("aes 加密方式"),
    /**
     * des 加密
     */
    DES("des加密方式");


    private final String encryptDescribe;

    MultiHeaderEncryptType(String encryptDescribe) {
        this.encryptDescribe = encryptDescribe;
    }

    public boolean eq(String val) {
        return this.name().equalsIgnoreCase(val);
    }

    public boolean eq(MultiTenantType val) {
        if (val == null) {
            return false;
        }
        return eq(val.name());
    }
}
