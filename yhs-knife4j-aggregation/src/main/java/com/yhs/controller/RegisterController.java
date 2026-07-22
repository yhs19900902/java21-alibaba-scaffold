package com.yhs.controller;

import cn.hutool.extra.servlet.JakartaServletUtil;
import com.yhs.base.pojo.vo.BusinessResponse;
import com.yhs.knife4j.core.pojo.SwaggerRoute;
import com.yhs.knife4j.repository.CloudRepository;
import com.yhs.log.annotate.SysLog;
import com.yhs.redis.service.RedisService;
import com.yhs.service.RegistryService;
import com.yhs.vo.RegistryParam;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 07664-linwei
 * @version Id: RegisterController.java, v 0.1 2023/7/26 14:08 lw Exp $
 */
@Slf4j
@RequestMapping("/api/doc")
@RestController
@RequiredArgsConstructor
public class RegisterController {


    private final RegistryService registryService;

    private final CloudRepository cloudRepository;
    @Resource
    private RedisService redisService;

    @SysLog("aggregation register")
    @PostMapping("/register")
    public BusinessResponse<String> register(HttpServletRequest request, @RequestBody @Validated RegistryParam registryParam) {
        if (registryParam.getUri().contains("127.0.0.1")) {
            log.error("The component has not obtained the server ip address, please check the configuration{}",
                    registryParam.getUri());
            String ip = JakartaServletUtil.getClientIP(request);
            registryParam.setUri(registryParam.getUri().replaceAll("127.0.0.1", ip));
        }
        return registryService.register(registryParam);
    }

    @GetMapping("/getRoutes")
    public BusinessResponse<List<SwaggerRoute>> getRoutes() {
        return BusinessResponse.ok(cloudRepository.getRoutesAll());
    }

    @GetMapping("/deleteRoutes/{header}")
    public void deleteRoutes(@PathVariable("header") String header) {
        cloudRepository.removeRouteMap(header);
    }

    @GetMapping("/checkRoute")
    public BusinessResponse<Void> checkRoute() {
        cloudRepository.checkRoute();
        return BusinessResponse.ok(null);
    }

    @GetMapping("/cloudHeartbeat")
    public BusinessResponse<Void> cloudHeartbeat() {
        cloudRepository.cloudHeartbeat();
        return BusinessResponse.ok(null);
    }
}
