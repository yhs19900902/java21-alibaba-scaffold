package com.yhs.sequence.builder;

import com.yhs.sequence.number.SnowflakeSequence;
import lombok.Getter;

/**
 * 雪花算法
 *
 * @author 07664-linwei
 * @version Id: SnowflakeBuilder.java, v 0.1 2022/4/29 16:20 lw Exp $
 */
@Getter
public class SnowflakeBuilder {

    private long workerId;

    private long datacenterId;

    public static SnowflakeBuilder create() {
        return new SnowflakeBuilder();
    }


    public SnowflakeSequence build() {
        return new SnowflakeSequence(workerId, datacenterId);
    }

    public SnowflakeBuilder workerId(long workerId) {
        this.workerId = workerId;
        return this;
    }

    public SnowflakeBuilder datacenterId(long datacenterId) {
        this.datacenterId = datacenterId;
        return this;
    }
}
