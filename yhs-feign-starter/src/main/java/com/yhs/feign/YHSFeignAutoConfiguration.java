package com.yhs.feign;

import feign.Feign;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * feign 自动化配置
 *
 * @author 03952-yehuasheng
 * @version Id: YHSFeignAutoConfiguration.java, v0.1 2023/9/11 16:45 yehuasheng Exp $
 */
@Configuration
@ConditionalOnClass(Feign.class)
@EnableFeignClients
public class YHSFeignAutoConfiguration {
}
