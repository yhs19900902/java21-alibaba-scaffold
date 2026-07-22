package com.yhs.service.imple;

import cn.hutool.core.util.StrUtil;
import com.yhs.base.pojo.vo.BusinessResponse;
import com.yhs.knife4j.cloud.CloudRoute;
import com.yhs.knife4j.repository.CloudRepository;
import com.yhs.service.RegistryService;
import com.yhs.vo.RegistryParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author 07664-linwei
 * @version Id: RegistryServiceImpl.java, v 0.1 2023/7/26 15:05 lw Exp $
 */
@Service
@RequiredArgsConstructor
public class RegistryServiceImpl implements RegistryService {

    private final CloudRepository cloudRepository;

    @Override
    public BusinessResponse<String> register(RegistryParam registryParam) {
        CloudRoute cloudRoute = new CloudRoute();
        cloudRoute.setUri(registryParam.getUri());
        cloudRoute.setName(registryParam.getGroupName());
        cloudRoute.setLocation(registryParam.getLocation());
        cloudRoute.setVersion(registryParam.getVersion());
        return cloudRepository.addCheckRoute(cloudRoute) ? BusinessResponse.ok(StrUtil.format
                ("注册成功，路由信息：{}", cloudRoute))
                : BusinessResponse.fail("注册失败");
    }
}
