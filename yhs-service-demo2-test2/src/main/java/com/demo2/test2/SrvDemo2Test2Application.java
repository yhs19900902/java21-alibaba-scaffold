package com.demo2.test2;

import com.yhs.feign.annotate.EnableYHSFeignClients;
import com.yhs.springdoc.EnableSpringDoc;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author 03952-yehuasheng
 * @version Id: SrvDemo2Test2Application.java, v0.1 2023/9/14 10:36 yehuasheng Exp $
 */
@EnableSpringDoc
@EnableYHSFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan(basePackages = "com.demo2.test2.dao")
public class SrvDemo2Test2Application {
    public static void main(String[] args) {
        SpringApplication.run(SrvDemo2Test2Application.class, args);
    }
}
