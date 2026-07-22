package com.yhs.sequence.range.db;

import cn.hutool.core.text.CharSequenceUtil;
import com.yhs.base.exception.BizException;
import com.yhs.base.pojo.vo.BusinessResponse;
import com.yhs.sequence.range.SequenceManagement;
import com.yhs.sequence.range.SequenceRange;
import lombok.Data;

import javax.sql.DataSource;

/**
 * db 区间管理器
 *
 * @author 07664-linwei
 * @version Id: DbSequenceRangeManagement.java, v 0.1 2022/4/29 16:02 lw Exp $
 */
@Data
public class DbSequenceRangeManagement implements SequenceManagement {

    /**
     * 区间步长
     */
    private int step;

    /**
     * 区间起始位置，真实从stepStart+1开始
     */
    private long stepStart;

    /**
     * 获取区间失败重试次数
     */
    private int retryTimes = 10;

    /**
     * DB来源
     */
    private DataSource dataSource;

    /**
     * 表名
     */
    private String tableName;

    @Override
    public SequenceRange nextRange(String name, Long dynamicStepStart) {
        if (CharSequenceUtil.isBlank(name)) {
            throw new SecurityException("SequenceRange  name is empty.");
        }
        Long oldValue;
        for (int i = 0; i < getRetryTimes(); i++) {
            if (dynamicStepStart != null) {
                oldValue = BaseDbHelper.selectRange(dataSource, tableName, name, dynamicStepStart);
            } else {
                oldValue = BaseDbHelper.selectRange(dataSource, tableName, name, stepStart);
            }
            if (null == oldValue) {
                continue;
            }
            long newValue = oldValue + step;
            if (BaseDbHelper.updateRange(dataSource, tableName, newValue, oldValue, name)) {
                return new SequenceRange(oldValue + 1L, newValue);
            }
        }
        throw new BizException(BusinessResponse.RESPONSE_ERROR,
                "Retried too many times, retryTimes = " + getRetryTimes());
    }

    @Override
    public void init() {
        checkParam();
        if (!BaseDbHelper.existTable(dataSource, tableName)) {
            BaseDbHelper.createTable(dataSource, tableName);
        }
    }


    private void checkParam() {
        if (step <= 0) {
            throw new SecurityException("SequenceRange step must greater than 0.");
        }
        if (stepStart < 0) {
            throw new SecurityException("SequenceRange stepStart < 0.");
        }
        if (null == dataSource) {
            throw new SecurityException("SequenceRange dataSource is null.");
        }
        if (CharSequenceUtil.isBlank(tableName)) {
            throw new SecurityException("SequenceRange tableName is empty.");
        }
    }
}
