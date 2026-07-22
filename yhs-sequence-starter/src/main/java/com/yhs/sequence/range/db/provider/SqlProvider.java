package com.yhs.sequence.range.db.provider;

import com.baomidou.mybatisplus.annotation.DbType;

/**
 * 数据源提供者
 *
 * @author 07664-linwei
 * @version Id: SqlProvider.java, v 0.1 2022/4/29 15:46 lw Exp $
 */
public interface SqlProvider {

    /**
     * 获取表是否存在
     *
     * @return 判断是否存在sql
     */
    default String getExistTableSql() {
        return null;
    }

    /**
     * 获取建表语句
     *
     * @return 建表语句
     */
    String getCreateTableSql();

    /**
     * 获取插入范围语句
     *
     * @return 插入语句
     */
    default String getInsertRangeSql() {
        return "INSERT INTO #tableName (id,name,value,created_date,updated_date)" + " VALUES(?,?,?,?,?)";
    }

    /**
     * 获取更新范围语句
     *
     * @return 更新范围语句
     */
    default String getUpdateRangeSql() {
        return "UPDATE #tableName SET value=?,updated_date=? WHERE name=? AND value=?";
    }

    /**
     * 获取查询范围语句
     *
     * @return 查询范围语句
     */
    default String getSelectRangeSql() {
        return "SELECT value FROM #tableName WHERE name=?";
    }

    /**
     * 是否支持数据库类型
     *
     * @param dbType 数据库类型
     * @return 是否支持
     */
    Boolean support(DbType dbType);
}
