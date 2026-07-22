package com.yhs.cms.log.kafka.producer;

import com.yhs.base.utils.LogUtil;
import com.yhs.cms.log.constant.LogConstant;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author 03952-yehuasheng
 * @version Id: SystemCmsLogProducer.java, v0.1 2024/11/18 15:38 yehuasheng Exp $
 */
@Component
public class SystemCmsLogProducer {
    /**
     * 日志
     */
    public static final Logger logger = LogUtil.getLogger("kafka");

    /**
     * kafka模板
     */
    @Resource
    private KafkaTemplate<byte[], byte[]> kafkaTemplate;

    /**
     * 发生日志给kafka生产者
     *
     * @param message 消息
     */
    public void sendMessage(String key, String message) {
        logger.info("system cms log producer send message to kafka topic:{}, log message:{}", LogConstant.SYSTEM_CMS_LOG_KAFKA_TOPIC, message);
        kafkaTemplate.send(LogConstant.SYSTEM_CMS_LOG_KAFKA_TOPIC, key.getBytes(), message.getBytes());
    }
}
