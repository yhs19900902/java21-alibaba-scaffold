package com.demo.test.controller;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.nacos.common.http.param.MediaType;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.demo.test.mapper.CartMapper;
import com.demo.test.mq.producer.TestProducer;
import com.demo.test.pojo.po.UserInfoPO;
import com.demo.test.pojo.vo.request.ProductBusinessInfoModelVO;
import com.demo.test.pojo.vo.request.TestRequestVO;
import com.demo.test.pojo.vo.response.ProductBusinessInfoResponseVO;
import com.demo.test.pojo.vo.response.TestResponseVO;
import com.demo.test.pojo.vo.response.YHDBusinessInfoResponse;
import com.demo.test.service.sao.DemoService;
import com.demo.test.service.sao.ProductBusinessInfoService;
import com.yhs.base.constant.CommonConstant;
import com.yhs.base.pojo.vo.BusinessResponse;
import com.yhs.base.utils.LogUtil;
//import com.yhs.log.annotate.SysLog;
import io.seata.spring.annotation.GlobalTransactional;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * @author 03952-yehuasheng
 * @version Id: TestController.java, v0.1 2023/9/14 10:38 yehuasheng Exp $
 */
@RestController
@Tag(name = "测试接口", description = "包含一些测试内容")
@RequestMapping(value = "/test", produces = MediaType.APPLICATION_JSON)
public class TestController {
    /**
     * 日志
     */
    private static final Logger logger = LogUtil.getLogger(TestController.class.getName());
    @Resource
    CartMapper cartMapper;
    @Resource
    private DemoService demoService;
    @Resource
    private ProductBusinessInfoService productBusinessInfoService;

    @Operation(summary = "demo1案例")
    @GetMapping("/demo1")
//    @SysLog("demo1")
    public BusinessResponse<TestResponseVO> demo1() {
        logger.info("request demo1 api. parameter");

        List<UserInfoPO> list = cartMapper.list();

        return demoService.getDemo(TestRequestVO.builder().name("次奥单位").age(34).build());
    }

    @Operation(summary = "获取价格", parameters = {@Parameter(name = "model", description = "型号", required = true, example = "SAD01-D3-L100"), @Parameter(name = "quantity", description = "数量", required = true, example = "1")})
    @GetMapping("/demo2")
//    @SysLog("demo2")
    public BusinessResponse<List<ProductBusinessInfoResponseVO>> demo2(@RequestParam String model, @RequestParam long quantity) {
        YHDBusinessInfoResponse<List<ProductBusinessInfoResponseVO>> price = productBusinessInfoService.getPrice(Collections.singletonList(ProductBusinessInfoModelVO.builder().model(model).quantity(quantity).build()));
        if (price.getRt_code() == CommonConstant.ZERO) {
            return BusinessResponse.ok(price.getData());
        } else {
            return BusinessResponse.fail(price.getRt_code(), price.getRt_msg());
        }
    }

    @GlobalTransactional
    @Operation(summary = "检查seata", parameters = {@Parameter(name = "id", description = "购物车id")})
    @GetMapping("/demo3")
//    @SysLog("demo3")
    public BusinessResponse<Void> update(@RequestParam String id) {
        LambdaUpdateWrapper<UserInfoPO> updateWrapper = new LambdaUpdateWrapper<UserInfoPO>()
                .set(UserInfoPO::getUpdatedDateTime, LocalDateTime.now())
                .set(UserInfoPO::getUpdatedBy, "nicai")
                .eq(UserInfoPO::getId, id);

        cartMapper.update(null, updateWrapper);

//        int a=1/0;

        return BusinessResponse.ok(null);
    }

    @Operation(summary = "检查mq", parameters = {@Parameter(name = "id", description = "购物车id")})
    @GetMapping("/demo4")
    public BusinessResponse<Void> mq(@RequestParam String id) {
        SpringUtil.getBean(TestProducer.class).testMq(id);
        return BusinessResponse.ok(null);
    }
}
