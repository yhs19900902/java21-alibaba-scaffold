package com.yhs.encrypt;

import com.yhs.encrypt.handler.encrypt.EncryptHandler;
import com.yhs.encrypt.handler.sign.SignReturnValueHandler;
import com.yhs.encrypt.properties.EncryptProperties;
import com.yhs.encrypt.properties.SignProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 07664-linwei
 * @version Id: EncryptAutoConfiguration.java, v 0.1 2022/7/5 14:45 lw Exp $
 */
@RequiredArgsConstructor
@ComponentScan(value = "com.yhs.encrypt")
@EnableConfigurationProperties({EncryptProperties.class, SignProperties.class})
public class EncryptAutoConfiguration {


    private final RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    private final SignProperties signProperties;

    private final EncryptProperties encryptProperties;

    private final List<EncryptHandler> encryptHandlers;

    /**
     * 追加 Excel返回值处理器 到 springmvc 中
     */
    @PostConstruct
    public void setReturnValueHandlers() {
        List<HandlerMethodReturnValueHandler> returnValueHandlers = requestMappingHandlerAdapter
                .getReturnValueHandlers();
        List<HandlerMethodReturnValueHandler> newHandlers = new ArrayList<>();
        newHandlers.add(new SignReturnValueHandler(signProperties, encryptProperties, encryptHandlers));
        assert returnValueHandlers != null;
        newHandlers.addAll(returnValueHandlers);
        requestMappingHandlerAdapter.setReturnValueHandlers(newHandlers);
    }
}
