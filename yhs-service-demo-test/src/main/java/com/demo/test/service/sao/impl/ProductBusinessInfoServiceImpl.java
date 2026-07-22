package com.demo.test.service.sao.impl;

import com.demo.test.pojo.vo.request.ProductBusinessInfoModelVO;
import com.demo.test.pojo.vo.request.ProductBusinessInfoRequestVO;
import com.demo.test.pojo.vo.response.ProductBusinessInfoResponseVO;
import com.demo.test.pojo.vo.response.YHDBusinessInfoResponse;
import com.demo.test.sao.ProductBusinessInfoSAO;
import com.demo.test.service.sao.ProductBusinessInfoService;
import com.google.common.base.Stopwatch;
import com.yhs.base.utils.LogUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 03952-yehuasheng
 * @version Id: ProductBusinessInfoServiceImpl.java, v0.1 2023/9/21 08:35 yehuasheng Exp $
 */
@Service
public class ProductBusinessInfoServiceImpl implements ProductBusinessInfoService {
    private final static Logger logger = LogUtil.getLogger(ProductBusinessInfoServiceImpl.class.getName());
    @Resource
    private ProductBusinessInfoSAO productBusinessInfoSAO;

    @Override
    public YHDBusinessInfoResponse<List<ProductBusinessInfoResponseVO>> getPrice(List<ProductBusinessInfoModelVO> model) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        ProductBusinessInfoRequestVO productBusinessInfoRequestVO = ProductBusinessInfoRequestVO.builder().companyCode("A001").userRole(2).source("offline").list(model).build();
        YHDBusinessInfoResponse<List<ProductBusinessInfoResponseVO>> result = productBusinessInfoSAO.getPrice(productBusinessInfoRequestVO);
        logger.info("response product business result cost:{}ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        return result;
    }
}
