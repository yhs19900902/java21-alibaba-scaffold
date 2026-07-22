package com.yhs.cms.log.config;

import com.yhs.cms.log.aspect.SystemCmsLogAspect;
import com.yhs.cms.log.dao.CmsLogDAO;
import com.yhs.cms.log.kafka.consumer.SystemCmsLogConsumer;
import com.yhs.cms.log.kafka.producer.SystemCmsLogProducer;
import com.yhs.cms.log.properties.SecurityClassProperties;
import lombok.AllArgsConstructor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author 03952-yehuasheng
 * @version Id: SystemCmsLogAutoConfiguration.java, v0.1 2024/11/20 09:49 yehuasheng Exp $
 */
@Configuration
@AllArgsConstructor
@ConditionalOnWebApplication
@Import({
        SystemCmsLogAspect.class,
        SystemCmsLogProducer.class,
        SystemCmsLogConsumer.class,
        SecurityClassProperties.class
})
@MapperScan(basePackageClasses = CmsLogDAO.class)
public class SystemCmsLogAutoConfiguration {
}
