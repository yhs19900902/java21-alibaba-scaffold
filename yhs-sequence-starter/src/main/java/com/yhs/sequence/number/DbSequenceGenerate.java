package com.yhs.sequence.number;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.text.CharSequenceUtil;
import com.yhs.base.exception.BizException;
import com.yhs.base.pojo.vo.BusinessResponse;
import com.yhs.sequence.range.SequenceManagement;
import com.yhs.sequence.range.SequenceRange;
import io.lettuce.core.api.sync.RedisStringCommands;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author 07664-linwei
 * @version Id: DefaultRangeSequenceGenerate.java, v 0.1 2022/4/29 16:12 lw Exp $
 */
@Slf4j
@Data
public class DbSequenceGenerate {


    private static Map<String, SequenceRange> seqRangeMap = new ConcurrentHashMap<>(8);
    /**
     * 获取区间是加一把独占锁防止资源冲突
     */
    private final Lock lock = new ReentrantLock();
    ExecutorService threadPool = new ThreadPoolExecutor(1, 5, 300, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(128), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardOldestPolicy());
    /**
     * 序列号区间管理器
     */
    private SequenceManagement seqRangeManagement;
    private RedisStringCommands<String, String> redisStringCommands;
    /**
     * 当前序列号区间
     */
    private volatile SequenceRange currentRange;
    private boolean localStorage;
    private Integer step;

    public long nextSequenceNumber(String sequenceName) {
        return getSequenceNumber(sequenceName, null);
    }

    private long getSequenceNumber(String sequenceName, Long stepStart) {
        //本地内存获取区间
        currentRange = seqRangeMap.get(sequenceName);
        //
        if (Objects.isNull(currentRange)) {
            // 加锁获取
            lock.lock();
            try {
                if (currentRange == null) {
                    currentRange = seqRangeManagement.nextRange(sequenceName, stepStart);
                    seqRangeMap.put(sequenceName, currentRange);
                    if (!localStorage) {
                        redisStringCommands.setnx(sequenceName, String.valueOf(currentRange.getStartValue()));
                    }
                }
            } finally {
                lock.unlock();
            }
        }
        return localStorage ? getDbIncrSequence(sequenceName) : getRedisIncrSequence(sequenceName);
    }


    /**
     * 基于本地内存序号生成
     *
     * @param sequenceName 序号名称
     * @return 序号
     */
    private long getDbIncrSequence(String sequenceName) {
        // 当value值为-1时，表明区间的序列号已经分配完，需要重新获取区间
        long value = currentRange.getAndIncrement();
        if (value == -1) {
            try {
                lock.lock();
                do {
                    if (currentRange.isOver()) {
                        currentRange = seqRangeManagement.nextRange(sequenceName, null);
                        seqRangeMap.put(sequenceName, currentRange);
                    }
                    value = currentRange.getAndIncrement();
                } while (value == -1);
            } finally {
                lock.unlock();
            }
        }
        if (value < 0) {
            throw new BizException(BusinessResponse.RESPONSE_ERROR,
                    "Sequence value overflow, value = " + value);
        }
        return value;
    }

    /**
     * 基于redis 自增序号生成
     *
     * @param sequenceName 序号名称
     * @return 序号
     */
    private long getRedisIncrSequence(String sequenceName) {
        long value = redisStringCommands.incr(sequenceName);

        if (value % currentRange.getEndValue() == 0) {
            //重新获取区间
            threadPool.execute(() -> {
                currentRange = seqRangeManagement.nextRange(sequenceName, null);
                seqRangeMap.put(sequenceName, currentRange);
            });
        }
        return value;
    }


    public long nextDynamicStepStartSequenceNumber(String sequenceName, long stepStart) {
        return this.getSequenceNumber(sequenceName, stepStart);
    }

    /**
     * 支持格式化序號生產
     * default 格式 prefix+yyyyMMdd+序號
     *
     * @param prefix       前缀
     * @param sequenceName 序号名称
     * @param digits       格式化序號位数
     * @return 格式化序号
     */
    public String nextFormatString(String prefix, String sequenceName, int digits) {
        if (digits == 0) {
            digits = 4;
        }
        return String.join("", prefix, String.format(CharSequenceUtil.format("%s%0{}d", digits), DateUtil.format(new Date(), DatePattern.PURE_DATE_FORMAT), nextSequenceNumber(sequenceName)));
    }

    /**
     * 支持自定义格式化序號生產
     * default 格式 prefix+yyyyMMdd+序號
     *
     * @param prefix       前缀
     * @param sequenceName 序号名称
     * @param digits       格式化序號位数
     * @param format       日期格式
     * @return 格式化序号
     */
    public String nextFormatString(String prefix, String sequenceName, int digits, FastDateFormat format) {
        if (digits == 0) {
            digits = 4;
        }
        return String.join("", prefix, String.format(CharSequenceUtil.format("%s%0{}d", digits), DateUtil.format(new Date(), format), nextSequenceNumber(sequenceName)));
    }

    public String nextSequenceNo(String sequenceName) {
        return String.valueOf(nextSequenceNumber(sequenceName));
    }
}
