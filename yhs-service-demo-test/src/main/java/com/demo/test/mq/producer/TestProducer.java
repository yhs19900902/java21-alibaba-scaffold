package com.demo.test.mq.producer;

import com.alibaba.fastjson2.JSON;
import com.demo.test.mapper.CartMapper;
import com.demo.test.pojo.po.UserInfoPO;
import com.yhs.base.utils.LogUtil;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author 03952-yehuasheng
 * @version Id: TestProducer.java, v0.1 2023/9/21 14:51 yehuasheng Exp $
 */
@Component
public class TestProducer {
    private static final Logger logger = LogUtil.getLogger(TestProducer.class.getName());

    private static final Logger mqLogger = LogUtil.getLogger("mq");

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private CartMapper cartMapper;

    @Async
    public void testMq(String id) {
        logger.info("send cart id for producer:{}", id);
        mqLogger.info("send cart id for producer:{}", id);

        UserInfoPO userInfoPO = cartMapper.getById(id);

        String json = JSON.toJSONString(userInfoPO);
        mqLogger.info("send producer str:{}", json);
        rocketMQTemplate.convertAndSend("TEST-CART-TOPIC", json);
    }
}
