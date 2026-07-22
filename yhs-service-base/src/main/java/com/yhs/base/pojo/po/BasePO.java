package com.yhs.base.pojo.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.yhs.base.pojo.BaseObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * @author 03952-yehuasheng
 * @version Id: BasePO.java, v0.1 2023/9/12 10:46 yehuasheng Exp $
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BasePO extends BaseObject {
    @Serial
    private static final long serialVersionUID = 4926246431415506174L;

    /**
     * id
     */
    @Schema(description = "id", name = "id", example = "20472c3ff28d4538b43ce11df9387587")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 创建人
     * 注意：数据库中使用下划线分割字段名：如created_by
     */
    @Schema(description = "创建人", name = "createdBy", example = "admin")
    private String createdBy;

    /**
     * 创建时间
     */
    @Schema(name = "createdDateDateTime", description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdDateTime;

    /**
     * 更新人
     */
    @Schema(name = "updatedBy", description = "更新人")
    private String updatedBy;

    /**
     * 更新时间
     */
    @Schema(name = "updatedDateDateTime", description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedDateTime;
}
