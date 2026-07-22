package com.yhs.database.handler;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import com.yhs.base.constant.CommonConstant;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 数据库中的数据类型 与java 类型转换  （ 逗号分隔）
 *
 * @author 07664-linwei
 * @version Id: JsonLongArrayTypeHandler.java, v 0.1 2022/4/20 10:17 lw Exp $
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes({Long[].class})
public class LongArrayTypeHandler extends BaseTypeHandler<Long[]> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Long[] parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, ArrayUtil.join(parameter, CommonConstant.SYMBOL_COMMA));
    }

    @Override
    public Long[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String reString = rs.getString(columnName);
        return Convert.toLongArray(reString);
    }

    @Override
    public Long[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String reString = rs.getString(columnIndex);
        return Convert.toLongArray(reString);
    }

    @Override
    public Long[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String reString = cs.getString(columnIndex);
        return Convert.toLongArray(reString);
    }
}
