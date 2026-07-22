package com.yhs.cms.log.pojo.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yhs.base.pojo.po.BasePO;
import lombok.*;

import java.time.LocalDate;

/**
 * @author 03952-yehuasheng
 * @version Id: CmsLogPO.java, v0.1 2024/11/18 16:51 yehuasheng Exp $
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("ylt_cms_log")
public class CmsLogPO extends BasePO {
    /**
     * 日志类型，add新增 update更新 delete删除
     */
    private String logType;

    /**
     * 操作模块
     */
    private String operationModule;

    /**
     * 操作的表
     */
    private String operationTable;

    /**
     * 操作的数据
     */
    private String operationData;

    /**
     * 原数据
     */
    private String originalData;

    /**
     * 操作的结果
     */
    private String resultData;

    /**
     * 结果
     */
    private String result;

    /**
     * 操作的ip
     */
    private String ip;

    /**
     * 操作的uri
     */
    private String uri;

    /**
     * 浏览器信息
     */
    private String ua;

    /**
     * 创建日
     */
    private LocalDate createdDay;
}
