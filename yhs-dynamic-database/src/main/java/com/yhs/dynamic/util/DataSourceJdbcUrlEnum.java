package com.yhs.dynamic.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author 07664-linwei
 * @version Id: DsJdbcUrlEnum.java, v 0.1 2022/5/18 16:57 lw Exp $
 */
@Getter
@AllArgsConstructor
public enum DataSourceJdbcUrlEnum {


    /**
     * mysql 数据库
     */
    MY_SQL("MY_SQL",
            "jdbc:mysql://%s:%s/%s?characterEncoding=utf8"
                    + "?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=Asia/Shanghai&useSSL=false",
            "select 1", "mysql8 链接"),

    /**
     * pg 数据库
     */
    PG("PG", "jdbc:postgresql://%s:%s/%s", "select 1", "postgresql 链接"),

    /**
     * SQL SERVER
     */
    SQL_SERVER("SQL_SERVER", "jdbc:sqlserver://%s:%s;database=%s;characterEncoding=UTF-8", "select 1", "sqlserver 链接"),

    /**
     * oracle
     */
    ORACLE("ORACLE", "jdbc:oracle:thin:@%s:%s:%s", "select 1 from dual", "oracle 链接");


    private final String dbName;

    private final String url;

    private final String validationQuery;

    private final String description;

    public static DataSourceJdbcUrlEnum get(String type) {
        return Arrays.stream(DataSourceJdbcUrlEnum.values()).filter(dsJdbcUrlEnum -> type.equals(dsJdbcUrlEnum.getDbName()))
                .findFirst().get();
    }

}
