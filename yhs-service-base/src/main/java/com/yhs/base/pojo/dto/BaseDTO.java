package com.yhs.base.pojo.dto;

import com.yhs.base.pojo.BaseObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @author 03952-yehuasheng
 * @version Id: BaseDTO.java, v0.1 2023/9/12 10:51 yehuasheng Exp $
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseDTO extends BaseObject {
    @Serial
    private static final long serialVersionUID = 4926246431415506174L;
}
