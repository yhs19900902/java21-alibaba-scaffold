package com.yhs.service;

import com.yhs.base.pojo.vo.BusinessResponse;
import com.yhs.vo.RegistryParam;

/**
 * @author 07664-linwei
 * @version Id: RegistryService.java, v 0.1 2023/7/26 15:04 lw Exp $
 */
public interface RegistryService {

    BusinessResponse<String> register(RegistryParam registryParam);
}
