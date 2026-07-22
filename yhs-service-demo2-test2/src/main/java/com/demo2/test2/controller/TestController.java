package com.demo2.test2.controller;

import com.alibaba.nacos.common.http.param.MediaType;
import com.demo2.test2.mapper.CartMapper;
import com.demo2.test2.pojo.po.UserInfoPO;
import com.demo2.test2.pojo.vo.request.TestRequestVO;
import com.demo2.test2.pojo.vo.response.TestResponseVO;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.yhs.base.pojo.vo.BusinessResponse;
import com.yhs.base.pojo.vo.PageRequestVO;
import com.yhs.base.utils.LogUtil;
import com.yhs.redis.service.RedisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//import com.yhs.log.annotate.SysLog;

/**
 * @author 03952-yehuasheng
 * @version Id: TestController.java, v0.1 2023/9/14 10:38 yehuasheng Exp $
 */
@RestController
@Tag(name = "测试接口2", description = "包含一些测试内容")
@RequestMapping(value = "/test2", produces = MediaType.APPLICATION_JSON)
public class TestController {
    /**
     * 日志
     */
    private static final Logger logger = LogUtil.getLogger(TestController.class.getName());

    @Resource
    private CartMapper cartMapper;

    @Resource
    private RedisService redisService;

    @Operation(summary = "demo2案例")
    @PostMapping("/demo2")
//    @SysLog("demo2")
    public BusinessResponse<TestResponseVO> demo2(@RequestBody @Validated TestRequestVO testRequestVO) {
        logger.info("request demo1 api. parameter:{}", testRequestVO);

        return BusinessResponse.ok(TestResponseVO.builder().desc("您的名字是：" + testRequestVO.getName() + "，您的年龄是：" + testRequestVO.getAge()).build());
    }

    @Operation(summary = "分页")
    @PostMapping("/page")
//    @SysLog("demo2")
    public BusinessResponse<PageInfo<UserInfoPO>> demo3(@RequestBody PageRequestVO pageRequestVO) {
        logger.info("request demo3 api. pageRequestVO:{}", pageRequestVO);

        PageMethod.startPage(pageRequestVO.getPageNum(), pageRequestVO.getPageSize());

        List<UserInfoPO> list = cartMapper.list();
        PageInfo<UserInfoPO> pageInfo = new PageInfo<>(list);
        logger.info("get ok.");

        logger.info("是否使用虚拟线程：{}", Thread.currentThread().isVirtual());

        return BusinessResponse.ok(pageInfo);
    }
}
