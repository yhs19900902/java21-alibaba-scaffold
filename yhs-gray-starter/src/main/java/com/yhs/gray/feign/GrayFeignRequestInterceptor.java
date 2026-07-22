package com.yhs.gray.feign;

import cn.hutool.core.util.StrUtil;
import com.yhs.base.constant.CommonConstant;
import com.yhs.base.utils.WebUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;


/**
 * feign 请求版本号传递
 *
 * @author 07664-linwei
 * @version Id: GrayFeignRequestInterceptor.java, v 0.1 2022/5/6 19:34 lw Exp $
 */
@Slf4j
public class GrayFeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String yhsVersion = WebUtil.getRequest() != null ? WebUtil.getRequest().getHeader(CommonConstant.VERSION)
                : null;
        if (StrUtil.isNotBlank(yhsVersion)) {
            log.debug("feign gray add header version :{}", yhsVersion);
            requestTemplate.header(CommonConstant.VERSION, yhsVersion);
        }
    }
}
