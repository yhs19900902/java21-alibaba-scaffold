package com.yhs.easyexcel.heand;


import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @author 07664-linwei
 * @version Id: HeadMeta.java, v 0.1 2022/6/24 15:43 lw Exp $
 */
@Data
public class HeadMeta {

    /**
     * <p>
     * 自定义头部信息
     * </p>
     * 实现类根据数据的class信息，定制Excel头<br/>
     */
    private List<List<String>> head;

    /**
     * 忽略头对应字段名称
     */
    private Set<String> ignoreHeadFields;
}
