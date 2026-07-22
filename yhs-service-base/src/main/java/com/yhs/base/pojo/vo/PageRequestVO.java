package com.yhs.base.pojo.vo;

import com.yhs.base.pojo.BaseObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 03952-yehuasheng
 * @version Id: PageRequestVO.java, v0.1 2023/9/12 10:42 yehuasheng Exp $
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "分页请求参数")
public class PageRequestVO extends BaseObject {
    /**
     * 第几页
     */
    @Schema(name = "pageNum", description = "页码", example = "1")
    private int pageNum = 1;
    /**
     * 每页的记录数
     */
    @Schema(name = "pageSize", description = "每页的记录数", example = "20")
    private int pageSize = 20;
    /**
     * 排序字段
     */
    @Schema(name = "orderByField", description = "排序字段", example = "createdDate")
    private String orderByField;
    /**
     * 排序方式
     */
    @Schema(name = "orderByType", description = "排序方式", example = "ASC")
    private String orderByType;
}
