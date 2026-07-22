package com.yhs;

import com.yhs.base.factory.YamlPropertySourceFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author 07664-linwei
 * @version Id: RocketMqConfiguration.java, v 0.1 2022/8/31 14:45 lw Exp $
 */
@Configuration
@PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:yhs-rocketmq.yml")
public class RocketMqConfiguration {

}
