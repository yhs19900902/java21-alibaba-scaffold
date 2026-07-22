package com.yhs.database.tenant.plugins;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLCallStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.core.toolkit.TableNameParser;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.yhs.base.tenant.TenantContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 07664-linwei
 * @version Id: SchemaInterceptor.java, v 0.1 2022/1/5 15:26 lw Exp $
 */
@Slf4j
public class SchemaInterceptor implements InnerInterceptor {


    private final String multiTenantDatasourcePrefix;

    public SchemaInterceptor(String multiTenantDatasourcePrefix) {
        this.multiTenantDatasourcePrefix = multiTenantDatasourcePrefix;
    }


    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
        MappedStatement ms = mpSh.mappedStatement();
        SqlCommandType sct = ms.getSqlCommandType();
        if (sct == SqlCommandType.SELECT || sct == SqlCommandType.INSERT || sct == SqlCommandType.UPDATE || sct == SqlCommandType.DELETE) {
            if (InterceptorIgnoreHelper.willIgnoreDynamicTableName(ms.getId())) {
                return;
            }
            PluginUtils.MPBoundSql mpBs = mpSh.mPBoundSql();
            mpBs.sql(this.changeSchema(mpBs.sql()));
        }
    }


    private String changeSchema(String sql) {
        // 想要 执行sql时， 不切换到 库直接返回null
        String tenantCode = TenantContextHolder.getTenantId();
        if (StrUtil.isEmpty(tenantCode)) {
            return sql;
        }

        String schemaName = StrUtil.format("{}_{}", multiTenantDatasourcePrefix, tenantCode);
        MySqlStatementParser statementParser = new MySqlStatementParser(sql);
        SQLStatement sqlStatement = statementParser.parseStatement();
        StringBuilder builder = new StringBuilder();
        // 判断是否存储过程
        if (sqlStatement instanceof SQLCallStatement) {
            log.info("执行到 存储过程 这里了");
            SQLCallStatement sqlCallStatement = (SQLCallStatement) sqlStatement;
            SQLName expr = sqlCallStatement.getProcedureName();
            if (expr instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr procedureName = (SQLIdentifierExpr) expr;
                sqlCallStatement.setProcedureName(new SQLPropertyExpr(schemaName, procedureName.getName()));
            } else if (expr instanceof SQLPropertyExpr) {
                SQLPropertyExpr procedureName = (SQLPropertyExpr) expr;
                sqlCallStatement.setProcedureName(new SQLPropertyExpr(schemaName, procedureName.getName()));
            }
            builder.append(sqlStatement);
        } else {
            TableNameParser parser = new TableNameParser(sql);
            // 想要 执行sql时， 切换到 切换到自己指定的库， 直接
            List<TableNameParser.SqlToken> names = new ArrayList<>();
            parser.accept(names::add);
            int last = 0;
            for (TableNameParser.SqlToken name : names) {
                int start = name.getStart();
                if (start != last) {
                    builder.append(sql, last, start);
                    builder.append(schemaName)
                            .append(StrPool.DOT);
                    String value = name.getValue();
                    if (value.lastIndexOf(StrPool.DOT) == -1) {
                        builder.append(value);
                    } else {
                        builder.append(StrUtil.subAfter(value, StrPool.DOT, true));
                    }
                }
                last = name.getEnd();
            }
            if (last != sql.length()) {
                builder.append(sql.substring(last));
            }
        }
        return builder.toString();
    }


}
