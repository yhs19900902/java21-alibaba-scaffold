package com.yhs.sequence.range.db.provider;

import com.baomidou.mybatisplus.annotation.DbType;
import org.springframework.stereotype.Component;

/**
 * pg 数据库
 *
 * @author 07664-linwei
 * @version Id: PostgreSqlProvider.java, v 0.1 2022/4/29 15:59 lw Exp $
 */
@Component
public class PostgreSqlProvider implements SqlProvider {

    /**
     * 获取建表语句
     *
     * @return
     */
    @Override
    public String getCreateTableSql() {
        return "CREATE TABLE IF NOT EXISTS #tableName (ID int8 PRIMARY KEY NOT NULL"
                + ",VALUE int8 NOT NULL,NAME VARCHAR (266) NOT NULL,created_date TIMESTAMP (6) NOT NULL"
                + ",updated_date TIMESTAMP (6) NOT NULL)";
    }

    @Override
    public Boolean support(DbType dbType) {
        return DbType.POSTGRE_SQL.equals(dbType);
    }
}
