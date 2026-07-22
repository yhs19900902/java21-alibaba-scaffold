package com.demo.test.sao;

import com.demo.test.pojo.vo.request.TestRequestVO;
import com.demo.test.pojo.vo.response.TestResponseVO;
import com.yhs.base.pojo.vo.BusinessResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 03952-yehuasheng
 * @version Id: Demo2.java, v0.1 2023/9/16 09:46 yehuasheng Exp $
 */
@FeignClient("yhs-service-demo2-test2")
public interface Demo2SAO {
    @PostMapping(value = "/test2/demo2")
    BusinessResponse<TestResponseVO> demo(@RequestBody TestRequestVO testRequestVO);
}
