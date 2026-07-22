package com.demo.test.pojo.vo.response;

import com.yhs.base.pojo.vo.BaseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 03952-yehuasheng
 * @version Id: YHDBusinessInfoResponse.java, v0.1 2023/9/21 08:32 yehuasheng Exp $
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class YHDBusinessInfoResponse<T> extends BaseVO {
    private int rt_code;
    private String rt_msg;
    private T data;
}
