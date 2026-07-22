package com.demo.test.service.sao;

import com.demo.test.pojo.vo.request.ProductBusinessInfoModelVO;
import com.demo.test.pojo.vo.response.ProductBusinessInfoResponseVO;
import com.demo.test.pojo.vo.response.YHDBusinessInfoResponse;

import java.util.List;

/**
 * @author 03952-yehuasheng
 * @version Id: ProductBusinessInfoService.java, v0.1 2023/9/21 08:30 yehuasheng Exp $
 */
public interface ProductBusinessInfoService {
    YHDBusinessInfoResponse<List<ProductBusinessInfoResponseVO>> getPrice(List<ProductBusinessInfoModelVO> model);
}
