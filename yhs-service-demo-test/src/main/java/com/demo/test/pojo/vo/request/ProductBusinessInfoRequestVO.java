package com.demo.test.pojo.vo.request;

import com.yhs.base.pojo.vo.BaseVO;
import lombok.*;

import java.util.List;

/**
 * @author 03952-yehuasheng
 * @version Id: ProductBusinessInfoRequestVO.java, v0.1 2023/9/21 08:20 yehuasheng Exp $
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductBusinessInfoRequestVO extends BaseVO {
    private String companyCode;
    private int userRole;
    private String source;
    private List<ProductBusinessInfoModelVO> list;
}
