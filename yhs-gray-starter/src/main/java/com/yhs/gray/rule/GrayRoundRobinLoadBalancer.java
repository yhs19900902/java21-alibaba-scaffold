package com.yhs.gray.rule;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.nacos.NacosServiceInstance;
import com.yhs.base.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.RoundRobinLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 07664-linwei
 * @version Id: GrayRoundRobinLoadBalancer.java, v 0.1 2022/5/6 19:39 lw Exp $
 */
@Slf4j
public class GrayRoundRobinLoadBalancer extends RoundRobinLoadBalancer {


    private final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;

    private final String serviceId;

    public GrayRoundRobinLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider, String serviceId) {
        super(serviceInstanceListSupplierProvider, serviceId);
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
        this.serviceId = serviceId;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider
                .getIfAvailable(NoopServiceInstanceListSupplier::new);
        return supplier.get(request).next().map(serviceInstances -> getInstanceResponse(serviceInstances, request));
    }


    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances, Request request) {
        // 注册中心无可用实例
        if (instances.isEmpty()) {
            if (log.isWarnEnabled()) {
                log.warn("No servers available for service: " + this.serviceId);
            }
            return new EmptyResponse();
        }

        if (null == request || null == request.getContext()) {
            return super.choose(request).block();
        }

        DefaultRequestContext requestContext = (DefaultRequestContext) request.getContext();
        //判断是否可以安全读取
        if (!(requestContext.getClientRequest() instanceof RequestData)) {
            return super.choose(request).block();
        }

        RequestData clientRequest = (RequestData) requestContext.getClientRequest();
        HttpHeaders headers = clientRequest.getHeaders();

        String reqVersion = headers.getFirst(CommonConstant.VERSION);
        // 版本号不存在 走默认父类选去实例
        if (StrUtil.isBlank(reqVersion)) {
            return super.choose(request).block();
        }

        // 遍历可以实例元数据，若匹配则返回此实例
        List<ServiceInstance> serviceInstanceList = instances.stream().filter(instance -> {
            NacosServiceInstance nacosInstance = (NacosServiceInstance) instance;
            Map<String, String> metadata = nacosInstance.getMetadata();
            String targetVersion = MapUtil.getStr(metadata, CommonConstant.VERSION);
            return reqVersion.equalsIgnoreCase(targetVersion);
        }).collect(Collectors.toList());

        // 存在 随机返回
        if (CollUtil.isNotEmpty(serviceInstanceList)) {
            ServiceInstance instance = RandomUtil.randomEle(serviceInstanceList);

            log.debug("gray instance available serviceId: {} , instanceId: {}", serviceId, instance.getInstanceId());
            return new DefaultResponse(instance);
        } else {
            return super.choose(request).block();
        }
    }

}
