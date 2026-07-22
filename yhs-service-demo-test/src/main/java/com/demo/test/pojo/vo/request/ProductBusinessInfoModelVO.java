package com.demo.test.pojo.vo.request;

import com.yhs.base.pojo.vo.BaseVO;
import lombok.*;

/**
 * @author 03952-yehuasheng
 * @version Id: ProductBusinessInfoModelVO.java, v0.1 2023/9/21 08:21 yehuasheng Exp $
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductBusinessInfoModelVO extends BaseVO {
    private String model;
    private long quantity;
}
