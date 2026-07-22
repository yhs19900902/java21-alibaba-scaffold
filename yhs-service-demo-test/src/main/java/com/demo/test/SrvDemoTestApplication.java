package com.demo.test;

import com.yhs.feign.annotate.EnableYHSFeignClients;
import com.yhs.springdoc.EnableSpringDoc;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author 03952-yehuasheng
 * @version Id: SrvDemoTestApplication.java, v0.1 2023/9/14 10:36 yehuasheng Exp $
 */
@EnableSpringDoc
@EnableYHSFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@EnableAsync
@MapperScan(basePackages = "com.demo.test.dao")
public class SrvDemoTestApplication {
    public static void main(String[] args) {
        System.setProperty("rocketmq.client.logRoot", "/data/tomcat/logs/yhd_service");
        SpringApplication.run(SrvDemoTestApplication.class, args);
    }
}
