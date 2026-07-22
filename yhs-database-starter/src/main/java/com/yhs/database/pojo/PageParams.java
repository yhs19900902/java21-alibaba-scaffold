package com.yhs.database.pojo;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhs.database.utils.AntiSqlFilterUtil;
import jakarta.validation.Valid;
import lombok.Data;


/**
 * @author 07664-linwei
 * @version Id: PageParams.java, v 0.1 2022/4/21 9:34 lw Exp $
 */
@Data
public class PageParams<T> {

    /**
     * 查询参数
     */
    @Valid
    private T model;

    /**
     *
     */
    private long size = 10;

    /**
     * 单前页
     */
    private long current = 1;
    /**
     * 排序,默认createdDate(多个逗号隔开)
     */
    private String asc;
    /**
     * 排序规则, 默认desc(对应sort 字段个数)
     */
    private String desc;

    public <E> Page<E> buildPage() {

        //没有排序参数 返回默认
        if (CharSequenceUtil.isBlank(this.getAsc()) &&
                CharSequenceUtil.isBlank(this.getDesc())) {
            return new Page<>(this.current, this.size);
        }
        Page<E> page = new Page(this.getCurrent(), this.getSize());
        String[] ascs = CharSequenceUtil.splitToArray(this.getAsc(), StrPool.COMMA);
        String[] descs = CharSequenceUtil.splitToArray(this.getDesc(), StrPool.COMMA);
        page.setOrders(AntiSqlFilterUtil.getOrderItem(ascs, descs));
        return page;
    }
}
