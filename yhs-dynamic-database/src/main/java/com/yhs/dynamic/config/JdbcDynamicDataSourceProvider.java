package com.yhs.dynamic.config;


import com.baomidou.dynamic.datasource.provider.AbstractJdbcDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.druid.DruidConfig;
import com.yhs.dynamic.constants.DataSourceConstants;
import com.yhs.dynamic.util.DataSourceJdbcUrlEnum;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.core.env.Environment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * 从数据库加载数据源
 *
 * @author 07664-linwei
 * @version Id: JdbcDynamicDataSourceProvider.java, v 0.1 2022/5/18 16:21 lw Exp $
 */
public class JdbcDynamicDataSourceProvider extends AbstractJdbcDataSourceProvider {

    String sql = "select * from dynamic_data_source where del_flag = 0";
    private DataSourceProperties dataSourceProperties;
    private Environment environment;


    public JdbcDynamicDataSourceProvider(DataSourceProperties dataSourceProperties, Environment environment) {
        super(dataSourceProperties.getUrl(), dataSourceProperties.getUsername(), dataSourceProperties.getPassword());
        this.dataSourceProperties = dataSourceProperties;
        this.environment = environment;
    }

    @Override
    protected Map<String, DataSourceProperty> executeStmt(Statement statement) throws SQLException {
        String jdbcEnabled = environment.getProperty("yhs.dynamic.jdbc.enabled");
        Map<String, DataSourceProperty> map = new HashMap<>(8);
        if (!Boolean.parseBoolean(jdbcEnabled)) {
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                DataSourceProperty property = new DataSourceProperty();
                String name = rs.getString(DataSourceConstants.NAME);
                String host = rs.getString(DataSourceConstants.HOST);
                String port = rs.getString(DataSourceConstants.PORT);
                String databaseName = rs.getString(DataSourceConstants.DATABASE_NAME);
                String userName = rs.getString(DataSourceConstants.USER_NAME);
                String password = rs.getString(DataSourceConstants.PASSWORD);
                String type = rs.getString(DataSourceConstants.DATABASE_TYPE);
                DataSourceJdbcUrlEnum dataSourceJdbcUrlEnum = DataSourceJdbcUrlEnum.get(type);
                String url = String.format(dataSourceJdbcUrlEnum.getUrl(), host, port, databaseName);
                property.setUsername(userName);
                property.setPassword(password);
                // Druid Config
                DruidConfig druidConfig = new DruidConfig();
                druidConfig.setValidationQuery(dataSourceJdbcUrlEnum.getValidationQuery());
                property.setDruid(druidConfig);
                property.setUrl(url);
                map.put(name, property);
            }
        }
        // 添加默认主数据源
        DataSourceProperty property = new DataSourceProperty();
        property.setUsername(dataSourceProperties.getUsername());
        property.setPassword(dataSourceProperties.getPassword());
        property.setUrl(dataSourceProperties.getUrl());
        map.put(DataSourceConstants.DS_MASTER, property);
        return map;
    }
}
