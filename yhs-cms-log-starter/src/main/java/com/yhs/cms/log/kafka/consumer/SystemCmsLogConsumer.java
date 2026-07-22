package com.yhs.cms.log.kafka.consumer;

import com.alibaba.fastjson2.JSON;
import com.yhs.base.utils.LogUtil;
import com.yhs.cms.log.constant.LogConstant;
import com.yhs.cms.log.dao.CmsLogDAO;
import com.yhs.cms.log.pojo.po.CmsLogPO;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author 03952-yehuasheng
 * @version Id: SystemCmsLogConsumer.java, v0.1 2024/11/18 16:32 yehuasheng Exp $
 */
@Component
public class SystemCmsLogConsumer {
    /**
     * 日志
     */
    public static final Logger logger = LogUtil.getLogger("kafka");

    @Value("${system.cms.log.enabled:false}")
    private boolean enabled;

    @Resource
    private CmsLogDAO cmsLogDAO;

    @KafkaListener(topics = {LogConstant.SYSTEM_CMS_LOG_KAFKA_TOPIC}, groupId = LogConstant.SYSTEM_CMS_LOG_KAFKA_GROUP)
    public void listen(byte[] key, byte[] message) {
        String keyString = new String(key);
        String messageJson = new String(message);
        logger.info("system cms log consumer . key:{} message:{}", keyString, messageJson);

        if (enabled) {
            CmsLogPO cmsLogPO = JSON.parseObject(messageJson, CmsLogPO.class);
            int insertResult = cmsLogDAO.insert(cmsLogPO);
            logger.info("insert system cms log success result:{}", insertResult);
        }
    }
}
