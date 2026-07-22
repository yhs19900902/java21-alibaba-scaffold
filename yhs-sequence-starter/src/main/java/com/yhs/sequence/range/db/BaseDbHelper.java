package com.yhs.sequence.range.db;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.yhs.base.enums.ExceptionCodeEnum;
import com.yhs.sequence.exception.SequenceException;
import com.yhs.sequence.range.db.provider.SqlProviderFactory;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Objects;

/**
 * 操作DB帮助类
 *
 * @author 07664-linwei
 * @version Id: BaseDbHelper.java, v 0.1 2022/4/29 15:44 lw Exp $
 */

@Slf4j
public class BaseDbHelper {


    private static final SqlProviderFactory SQL_PROVIDER_FACTORY = SpringUtil.getBean(SqlProviderFactory.class);
    private static final long DELTA = 100000000L;
    private static final String TABLE_NAME = "#tableName";
    private static final Snowflake SNOW_FLAKE = new Snowflake();

    private BaseDbHelper() {
    }

    /**
     * 创建表
     *
     * @param dataSource DB来源
     * @param tableName  表名
     */
    static void createTable(DataSource dataSource, String tableName) {

        Connection conn = null;
        Statement stmt = null;

        try {
            conn = dataSource.getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate(SQL_PROVIDER_FACTORY.getCreateTableSql().replace(TABLE_NAME, tableName));
        } catch (SQLException e) {
            throw new SequenceException(ExceptionCodeEnum.SQL_EXCEPTION.getCode(), e);
        } finally {
            closeQuietly(stmt);
            closeQuietly(conn);
        }
    }

    /**
     * 插入数据区间
     *
     * @param dataSource DB来源
     * @param tableName  表名
     * @param name       区间名称
     * @param stepStart  初始位置
     */
    private static void insertRange(DataSource dataSource, String tableName, String name, long stepStart) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(SQL_PROVIDER_FACTORY.getInsertRangeSql().replace(TABLE_NAME, tableName));
            stmt.setLong(1, SNOW_FLAKE.nextId());
            stmt.setString(2, name);
            stmt.setLong(3, stepStart);
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SequenceException(ExceptionCodeEnum.SQL_EXCEPTION.getCode(), e);
        } finally {
            closeQuietly(stmt);
            closeQuietly(conn);
        }
    }

    /**
     * 更新区间，乐观策略
     *
     * @param dataSource DB来源
     * @param tableName  表名
     * @param newValue   更新新数据
     * @param oldValue   更新旧数据
     * @param name       区间名称
     * @return 成功/失败
     */
    static boolean updateRange(DataSource dataSource, String tableName, Long newValue, Long oldValue, String name) {

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(SQL_PROVIDER_FACTORY.getUpdateRangeSql().replace(TABLE_NAME, tableName));
            stmt.setLong(1, newValue);
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setString(3, name);
            stmt.setLong(4, oldValue);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new SequenceException(ExceptionCodeEnum.SQL_EXCEPTION.getCode(), e);
        } finally {
            closeQuietly(stmt);
            closeQuietly(conn);
        }
    }

    /**
     * 查询区间，如果区间不存在，会新增一个区间，并返回null，由上层重新执行
     *
     * @param dataSource DB来源
     * @param tableName  来源
     * @param name       区间名称
     * @param stepStart  初始位置
     * @return 区间值
     */
    static Long selectRange(DataSource dataSource, String tableName, String name, long stepStart) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        long oldValue;

        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(SQL_PROVIDER_FACTORY.getSelectRangeSql().replace(TABLE_NAME, tableName));
            stmt.setString(1, name);

            rs = stmt.executeQuery();
            if (!rs.next()) {
                // 没有此类型数据，需要初始化
                insertRange(dataSource, tableName, name, stepStart);
                return null;
            }
            oldValue = rs.getLong(1);

            if (oldValue < 0) {
                String msg = "Sequence value cannot be less than zero, value = " + oldValue
                        + ", please check table sequence" + tableName;
                throw new SequenceException(ExceptionCodeEnum.SQL_EXCEPTION.getCode(), msg);
            }

            if (oldValue > Long.MAX_VALUE - DELTA) {
                String msg = "Sequence value overflow, value = " + oldValue + ", please check table sequence"
                        + tableName;
                throw new SequenceException(ExceptionCodeEnum.SQL_EXCEPTION.getCode(), msg);
            }

            return oldValue;
        } catch (SQLException e) {
            throw new SequenceException(ExceptionCodeEnum.SQL_EXCEPTION.getCode(), e);
        } finally {
            closeQuietly(rs);
            closeQuietly(stmt);
            closeQuietly(conn);
        }
    }

    private static void closeQuietly(AutoCloseable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (Exception e) {
                // Ignore
                log.error(e.getMessage());
            }
        }
    }

    public static Boolean existTable(DataSource dataSource, String tableName) {

        String existTableSql = SQL_PROVIDER_FACTORY.getExistTableSql();
        if (CharSequenceUtil.isBlank(existTableSql)) {
            return true;
        }
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = dataSource.getConnection();
            stmt = conn.createStatement();
            ResultSet resultSet = stmt
                    .executeQuery(SQL_PROVIDER_FACTORY.getExistTableSql().replace(TABLE_NAME, tableName));

            if (!resultSet.next()) {
                return false;
            }
            Object object = resultSet.getObject(1);

            if (Objects.nonNull(object)) {
                return true;
            }
        } catch (SQLException e) {
            throw new SequenceException(ExceptionCodeEnum.SQL_EXCEPTION.getCode(), e);
        } finally {
            closeQuietly(stmt);
            closeQuietly(conn);
        }

        return false;
    }

}
