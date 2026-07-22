package com.demo.test.service.sao.impl;

import com.demo.test.pojo.vo.request.TestRequestVO;
import com.demo.test.pojo.vo.response.TestResponseVO;
import com.demo.test.sao.Demo2SAO;
import com.demo.test.service.sao.DemoService;
import com.google.common.base.Stopwatch;
import com.yhs.base.pojo.vo.BusinessResponse;
import com.yhs.base.utils.LogUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author 03952-yehuasheng
 * @version Id: DemoServiceImpl.java, v0.1 2023/9/16 09:50 yehuasheng Exp $
 */
@Service
public class DemoServiceImpl implements DemoService {
    private static final Logger logger = LogUtil.getLogger(DemoServiceImpl.class.getName());

    @Resource
    private Demo2SAO demo2SAO;

    @Override
    public BusinessResponse<TestResponseVO> getDemo(TestRequestVO testRequestVO) {
        logger.info("request demo2 api.");

        logger.info("request api parameter testRequest:{}", testRequestVO);
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            BusinessResponse<TestResponseVO> businessResponse = demo2SAO.demo(testRequestVO);
            logger.info("response cost:{}ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
            return businessResponse;
        } catch (Exception e) {
            return BusinessResponse.fail("fail:" + e.getMessage());
        }
    }
}
