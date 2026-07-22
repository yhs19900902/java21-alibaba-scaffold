package com.yhs.sequence.range.db.provider;

import com.baomidou.mybatisplus.annotation.DbType;
import org.springframework.stereotype.Component;

/**
 * @author 07664-linwei
 * @version Id: MysqlSqlProvider.java, v 0.1 2022/4/29 15:55 lw Exp $
 */
@Component
public class MysqlSqlProvider implements SqlProvider {
    /**
     * 获取建表语句
     */
    @Override
    public String getCreateTableSql() {
        return "CREATE TABLE IF NOT EXISTS #tableName(" + "id bigint(20) NOT NULL AUTO_INCREMENT,"
                + "value bigint(20) NOT NULL," + "name varchar(32) NOT NULL," + "created_date DATETIME NOT NULL,"
                + "updated_date DATETIME NOT NULL," + "PRIMARY KEY (`id`),UNIQUE uk_name (`name`)" + ")";
    }

    @Override
    public Boolean support(DbType dbType) {
        return DbType.MYSQL.equals(dbType);
    }

}
