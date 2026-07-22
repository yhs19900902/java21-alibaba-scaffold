package com.yhs.log;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.yhs.base.utils.LogUtil;
import com.yhs.log.aspect.SysLogAspect;
import com.yhs.log.event.SysLogListener;
import com.yhs.log.properties.LogProperties;
import com.yhs.log.properties.OptLogType;
import com.yhs.log.remote.RemoteSaveLogInfoService;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author 07664-linwei
 * @version Id: LogAutoConfiguration.java, v 0.1 2022/4/22 8:38 lw Exp $
 */
@EnableAsync
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(LogProperties.class)
@ConditionalOnProperty(prefix = LogProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class LogAutoConfiguration {


    Logger logger = LogUtil.getLogger();

    @Bean
    @ConditionalOnMissingBean
    public SysLogAspect sysLogAspect(ApplicationEventPublisher publisher) {
        return new SysLogAspect(publisher);
    }


    /**
     * 保存到日志文件
     */
    @Bean
    @ConditionalOnMissingBean
    public SysLogListener sysLogListener(LogProperties logProperties, RestTemplate restTemplate) {
        if (logProperties != null) {
            if (logProperties.getType().equals(OptLogType.DB)) {
                logger.info("Enable the DB to store logs");
                RemoteSaveLogInfoService logInfoService = SpringUtil.getBean(RemoteSaveLogInfoService.class);
                return new SysLogListener(logInfoService::saveLog);
            } else if (logProperties.getType().equals(OptLogType.REMOTE_IP)) {
                if (CharSequenceUtil.isNotBlank(logProperties.getRemoteUrl())) {
                    logger.info("Enable the remote ip request to store logs");
                    RestTemplate ipRestTemplate = new RestTemplate();
                    return new SysLogListener(log -> {
                        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(log);
                        ipRestTemplate.exchange(logProperties.getRemoteUrl(), HttpMethod.POST, new HttpEntity(stringObjectMap), String.class);
                    });
                }
            } else if (logProperties.getType().equals(OptLogType.REMOTE_SERVICE)
                    && CharSequenceUtil.isNotBlank(logProperties.getRemoteUrl())) {
                logger.info("Enable the remote service name to request the storage of logs");
                return new SysLogListener(log -> restTemplate.exchange(logProperties.getRemoteUrl(), HttpMethod.POST, new HttpEntity(log), String.class));
            }
        }
        logger.info("Enable the log file to store logs");
        return new SysLogListener(log -> logger.info("{}", log));
    }



 /*   @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(24);
        executor.setQueueCapacity(1024);
        executor.setKeepAliveSeconds(300);
        executor.setThreadNamePrefix("logExecutor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return  new MyAsyncUncaughtExceptionHandler();
    }*/
}
