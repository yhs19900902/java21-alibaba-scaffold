package com.yhs.sequence.range;

/**
 * 区间管理器
 *
 * @author 07664-linwei
 * @version Id: SequenceManagement.java, v 0.1 2022/4/29 15:43 lw Exp $
 */
public interface SequenceManagement {

    /**
     * 获取指定Sequence的下一个区间
     *
     * @param name
     * @return
     */
    SequenceRange nextRange(String name, Long dynamicStepStart);

    /**
     * 初始化
     */
    void init();
}
