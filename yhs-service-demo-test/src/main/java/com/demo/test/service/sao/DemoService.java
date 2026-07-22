package com.demo.test.service.sao;

import com.demo.test.pojo.vo.request.TestRequestVO;
import com.demo.test.pojo.vo.response.TestResponseVO;
import com.yhs.base.pojo.vo.BusinessResponse;

/**
 * @author 03952-yehuasheng
 * @version Id: DemoService.java, v0.1 2023/9/16 09:49 yehuasheng Exp $
 */
public interface DemoService {
    BusinessResponse<TestResponseVO> getDemo(TestRequestVO testRequestVO);
}
