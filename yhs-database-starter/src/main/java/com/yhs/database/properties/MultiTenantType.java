package com.yhs.database.properties;

import lombok.Getter;

/**
 * @author 07664-linwei
 * @version Id: MultiTenantType.java, v 0.1 2022/1/5 11:07 lw Exp $
 */
@Getter
public enum MultiTenantType {
    /**
     * 非租户模式
     */
    NONE("非租户模式"),
    /**
     * 字段模式
     * 在sql中拼接 tenant_code 字段
     */
    COLUMN("字段模式"),
    /**
     * 独立schema模式
     * 在sql中拼接 数据库 schema
     * <p>
     * 该模式暂不支持复杂sql、存储过程、函数等，欢迎大家提供解决方案。
     */
    SCHEMA("独立schema模式");
    private final String describe;


    MultiTenantType(String describe) {
        this.describe = describe;
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
