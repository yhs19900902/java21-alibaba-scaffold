package com.yhs.easyexcel.listener;

import cn.idev.excel.event.AnalysisEventListener;
import com.yhs.easyexcel.vo.ErrorMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 列表读取监听
 *
 * @author 07664-linwei
 * @version Id: ListAnalysisEventListener.java, v 0.1 2022/6/23 10:34 lw Exp $
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class ListAnalysisEventListener<T> extends AnalysisEventListener<T> {


    private long maxRows = -1L;

    /**
     * 获取 excel 解析的对象列表
     *
     * @return 列表
     */
    public abstract List<T> getList();

    /**
     * 获取异常校验结果
     *
     * @return 集合
     */
    public abstract List<ErrorMessage> getErrors();
}
