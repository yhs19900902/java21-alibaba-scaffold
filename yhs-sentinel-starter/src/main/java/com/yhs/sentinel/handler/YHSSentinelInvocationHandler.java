package com.yhs.sentinel.handler;

import com.alibaba.cloud.sentinel.feign.SentinelContractHolder;
import com.alibaba.cloud.sentinel.feign.SentinelInvocationHandler;
import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.yhs.base.pojo.vo.BusinessResponse;
import feign.*;
import feign.InvocationHandlerFactory.MethodHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 重写{@link SentinelInvocationHandler} 支持自动降级
 *
 * @author 07664-linwei
 * @version Id: YHSSentinelInvocationHandler.java, v 0.1 2022/4/28 18:46 lw Exp $
 */
@Slf4j
public class YHSSentinelInvocationHandler implements InvocationHandler {

    private final Target<?> target;
    private final Map<Method, InvocationHandlerFactory.MethodHandler> dispatch;
    private FallbackFactory fallbackFactory;
    private Map<Method, Method> fallbackMethodMap;

    public YHSSentinelInvocationHandler(Target<?> target, Map<Method, InvocationHandlerFactory.MethodHandler> dispatch, FallbackFactory fallbackFactory) {
        this.target = Util.checkNotNull(target, "target");
        this.dispatch = Util.checkNotNull(dispatch, "dispatch");
        this.fallbackFactory = fallbackFactory;
        this.fallbackMethodMap = toFallbackMethod(dispatch);
    }

    public YHSSentinelInvocationHandler(Target<?> target, Map<Method, InvocationHandlerFactory.MethodHandler> dispatch) {
        this.target = Util.checkNotNull(target, "target");
        this.dispatch = Util.checkNotNull(dispatch, "dispatch");
    }

    static Map<Method, Method> toFallbackMethod(Map<Method, InvocationHandlerFactory.MethodHandler> dispatch) {
        Map<Method, Method> result = new LinkedHashMap<>();
        for (Method method : dispatch.keySet()) {
            method.setAccessible(true);
            result.put(method, method);
        }
        return result;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        if ("equals".equals(method.getName())) {
            try {
                Object otherHandler = args.length > 0 && args[0] != null
                        ? Proxy.getInvocationHandler(args[0])
                        : null;
                return equals(otherHandler);
            } catch (IllegalArgumentException e) {
                return false;
            }
        } else if ("hashCode".equals(method.getName())) {
            return this.hashCode();
        } else if ("toString".equals(method.getName())) {
            return this.toString();
        }

        Object result;
        MethodHandler methodHandler = this.dispatch.get(method);
        // only handle by HardCodedTarget
        if (target instanceof Target.HardCodedTarget hardCodedTarget) {
            MethodMetadata methodMetadata = SentinelContractHolder.METADATA_MAP
                    .get(hardCodedTarget.type().getName()
                            + Feign.configKey(hardCodedTarget.type(), method));
            // resource default is HttpMethod:protocol://url
            if (methodMetadata == null) {
                result = methodHandler.invoke(args);
            } else {
                String resourceName = methodMetadata.template().method().toUpperCase()
                        + ":" + hardCodedTarget.url() + methodMetadata.template().path();
                Entry entry = null;
                try {
                    ContextUtil.enter(resourceName);
                    entry = SphU.entry(resourceName, EntryType.OUT, 1, args);
                    result = methodHandler.invoke(args);
                } catch (Throwable ex) {
                    // fallback handle
                    if (!BlockException.isBlockException(ex)) {
                        Tracer.traceEntry(ex, entry);
                    }
                    if (fallbackFactory != null) {
                        if (BusinessResponse.class == method.getReturnType()) {
                            log.error("feign调用异常，异常信息：{}", ex.getMessage());
                            result = BusinessResponse.fail(ex.getMessage());
                        } else {
                            try {
                                Object fallbackResult = fallbackMethodMap.get(method)
                                        .invoke(fallbackFactory.create(ex), args);
                                return fallbackResult;
                            } catch (IllegalAccessException e) {
                                // shouldn't happen as method is public due to being an
                                // interface
                                throw new AssertionError(e);
                            } catch (InvocationTargetException e) {
                                throw new AssertionError(e.getCause());
                            }
                        }

                    } else {
                        // throw exception if fallbackFactory is null
                        throw ex;
                    }
                } finally {
                    if (entry != null) {
                        entry.exit(1, args);
                    }
                    ContextUtil.exit();
                }
            }
        } else {
            // other target type using default strategy
            result = methodHandler.invoke(args);
        }

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof YHSSentinelInvocationHandler YHSSentinelInvocationHandler) {
            return this.target.equals(YHSSentinelInvocationHandler.target);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.target.hashCode();
    }

    @Override
    public String toString() {
        return this.target.toString();
    }
}
