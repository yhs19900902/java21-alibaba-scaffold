package com.yhs.springdoc;

import cn.hutool.core.util.StrUtil;
import com.yhs.springdoc.executor.DocSpringExecutor;
import com.yhs.springdoc.properties.SpringDocProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

/**
 * @author 07664-linwei
 * @version Id: YHSApiDocAutoConfiguration.java, v 0.1 2023/7/27 8:40 lw Exp $
 */
@Slf4j
public class YHSApiDocAutoConfiguration {


    @Bean
    public DocSpringExecutor docSpringExecutor(SpringDocProperties springDocProperties, Environment environment) {
        DocSpringExecutor docSpringExecutor = new DocSpringExecutor();
        if (Objects.nonNull(springDocProperties.getAggregation())) {
            String groupName = springDocProperties.getAggregation().getGroupName();
            if (StrUtil.isBlank(groupName)) {
                groupName = environment.getProperty("spring.application.name");
            }
            String address = springDocProperties.getAggregation().getAddress();
            if (StrUtil.isBlank(address)) {
                log.error("The aggregate document server address cannot be empty");
                return null;
            }
            docSpringExecutor.setAddress(address);
            docSpringExecutor.setGroupName(groupName);
            docSpringExecutor.setLocation(springDocProperties.getAggregation().getLocation());
            String uri = springDocProperties.getAggregation().getUri();
            if (StrUtil.isBlank(uri)) {
                InetAddress localHost = null;
                try {
                    localHost = InetAddress.getLocalHost();
                    String portStr = environment.getProperty("server.port");
                    uri = localHost.getHostAddress() + ":" + portStr;
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }

            }
            String version = environment.getProperty("spring.cloud.nacos.discovery.metadata.yhs_version");
            if (StrUtil.isNotBlank(version)) {
                docSpringExecutor.setVersion(version);
            }
            docSpringExecutor.setUri(uri);
        }
        return docSpringExecutor;
    }

}
