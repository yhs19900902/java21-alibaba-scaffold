package com.yhs.sequence.range;

import lombok.Data;

import java.beans.Transient;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 序列号对象
 *
 * @author 07664-linwei
 * @version Id: SequenceRange.java, v 0.1 2022/4/29 15:42 lw Exp $
 */
@Data
public class SequenceRange {


    /**
     * 区间结束
     */
    private long endValue;
    /**
     * 区间开始
     */
    private long startValue;

    private AtomicLong value;

    /**
     * 区间的序列号是否分配完毕，每次分配完毕就会去重新获取一个新的区间
     */
    private volatile boolean over = false;

    public SequenceRange() {
    }


    public SequenceRange(long startValue, long endValue) {
        this.endValue = endValue;
        this.startValue = startValue;
        this.value = new AtomicLong(startValue);
    }

    /**
     * 返回并递增下一个序列号
     *
     * @return 下一个序列号，如果返回-1表示序列号分配完毕
     */
    @Transient
    public long getAndIncrement() {
        long currentValue = value.getAndIncrement();
        if (currentValue > endValue) {
            over = true;
            return -1;
        }
        return currentValue;
    }
}
