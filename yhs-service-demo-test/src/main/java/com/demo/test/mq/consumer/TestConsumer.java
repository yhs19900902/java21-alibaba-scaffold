package com.demo.test.mq.consumer;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.demo.test.mapper.CartMapper;
import com.demo.test.pojo.po.UserInfoPO;
import com.yhs.base.utils.LogUtil;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author 03952-yehuasheng
 * @version Id: TestConsumer.java, v0.1 2023/9/21 14:58 yehuasheng Exp $
 */
@Component
@RocketMQMessageListener(topic = "TEST-CART-TOPIC", consumerGroup = "TEST-CART-GROUP")
public class TestConsumer implements RocketMQListener<String> {
    private static final Logger logger = LogUtil.getLogger(TestConsumer.class.getName());

    private static final Logger mqLogger = LogUtil.getLogger("mq");

    @Resource
    private CartMapper cartMapper;

    @Override
    public void onMessage(String s) {
        logger.info("test consumer.");
        mqLogger.info("test consumer.");

        UserInfoPO userInfoPO = JSON.parseObject(s, UserInfoPO.class);
        cartMapper.update(null, new LambdaUpdateWrapper<UserInfoPO>().eq(UserInfoPO::getId, userInfoPO.getId()).set(UserInfoPO::getUpdatedDateTime, LocalDateTime.now()).set(UserInfoPO::getUpdatedBy, "consumer"));
        mqLogger.info("consumer success.");
    }
}
