package com.yhs;

import com.yhs.job.config.annotate.EnableJob;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author yehuasheng
 * @version Id: Application.java, v 0.1 2022/5/11 14:52 lw Exp $
 */

@EnableJob
@EnableDiscoveryClient
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
