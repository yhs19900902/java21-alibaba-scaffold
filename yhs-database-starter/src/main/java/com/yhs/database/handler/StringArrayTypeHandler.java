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
 * @version Id: StringArrayTypeHandler.java, v 0.1 2022/4/20 10:25 lw Exp $
 */
@MappedTypes(value = {String[].class})
@MappedJdbcTypes(value = JdbcType.VARCHAR)
public class StringArrayTypeHandler extends BaseTypeHandler<String[]> {


    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String[] parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, ArrayUtil.join(parameter, CommonConstant.SYMBOL_COMMA));
    }

    @Override
    public String[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String reString = rs.getString(columnName);
        return Convert.toStrArray(reString);
    }

    @Override
    public String[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String reString = rs.getString(columnIndex);
        return Convert.toStrArray(reString);
    }

    @Override
    public String[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String reString = cs.getString(columnIndex);
        return Convert.toStrArray(reString);
    }
}
