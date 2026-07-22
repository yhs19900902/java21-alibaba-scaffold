package com.yhs.sequence.number;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
 * hutool 雪花算法生成id
 *
 * @author 07664-linwei
 * @version Id: SnowflakeSequence.java, v 0.1 2022/4/29 16:24 lw Exp $
 */
public class SnowflakeSequence {

    private final Snowflake snowflake;


    public SnowflakeSequence(long workerId, long datacenterId) {
        this.snowflake = IdUtil.getSnowflake(workerId, datacenterId);
    }


    public long nextSequenceNumber() {
        return snowflake.nextId();
    }


    public String nextSequenceNo() {
        return String.valueOf(nextSequenceNumber());
    }


    public String nextSequenceString() {
        return String.valueOf(nextSequenceNumber());
    }
}
