package com.yhs.sequence.range.db.provider;

import com.baomidou.mybatisplus.annotation.DbType;
import org.springframework.stereotype.Component;

/**
 * oracle数据源
 *
 * @author 07664-linwei
 * @version Id: OracleSqlProvider.java, v 0.1 2022/4/29 15:58 lw Exp $
 */
@Component
public class OracleSqlProvider implements SqlProvider {

    /**
     * 获取表是否存在
     *
     * @return
     */
    @Override
    public String getExistTableSql() {
        return "select count(*) from user_tables where table_name =upper('#tableName')";
    }

    /**
     * 获取建表语句
     *
     * @return SQL
     */
    @Override
    public String getCreateTableSql() {
        return "CREATE TABLE #tableName " + "(id NUMBER (20,0) VISIBLE NOT NULL,value NUMBER (20,0) VISIBLE NOT NULL"
                + ",name VARCHAR2 (96 BYTE) VISIBLE,created_date DATE VISIBLE NOT NULL"
                + ",updated_date DATE VISIBLE NOT NULL)";
    }


    @Override
    public Boolean support(DbType dbType) {
        return DbType.ORACLE.equals(dbType);
    }
}
