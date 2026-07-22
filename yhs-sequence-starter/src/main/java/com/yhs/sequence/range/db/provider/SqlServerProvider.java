package com.yhs.sequence.range.db.provider;

import com.baomidou.mybatisplus.annotation.DbType;
import org.springframework.stereotype.Component;

/**
 * @author 07664-linwei
 * @version Id: SqlServerProvider.java, v 0.1 2022/7/12 15:40 lw Exp $
 */
@Component
public class SqlServerProvider implements SqlProvider {

    @Override
    public String getCreateTableSql() {
        return "create table  #tableName \n" +
                "(\n" +
                "   id bigint primary key,\n" +
                "   value bigint  not null,\n" +
                "   name varchar(32)  not null,\n" +
                "   created_date datetime not null,\n" +
                "\t updated_date datetime not null\n" +
                ");";
    }

    @Override
    public String getInsertRangeSql() {
        return "INSERT INTO #tableName (id,name,value,created_date,updated_date)" + " VALUES(?,?,?,?,?)";
    }

    @Override
    public String getUpdateRangeSql() {
        return SqlProvider.super.getUpdateRangeSql();
    }

    @Override
    public String getSelectRangeSql() {
        return SqlProvider.super.getSelectRangeSql();
    }


    @Override
    public String getExistTableSql() {
        return "select name from sysobjects where id = object_id('#tableName') and OBJECTPROPERTY(id, N'IsUserTable') = 1";
    }

    @Override
    public Boolean support(DbType dbType) {
        return DbType.SQL_SERVER.equals(dbType) || DbType.SQL_SERVER2005.equals(dbType);
    }
}
