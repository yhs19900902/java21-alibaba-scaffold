package com.yhs.zipkin.config;

import brave.baggage.BaggageFields;
import brave.baggage.CorrelationScopeConfig;
import brave.context.slf4j.MDCScopeDecorator;
import brave.propagation.CurrentTraceContext;
import com.yhs.zipkin.constant.SingleCorrelationNameConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 03952-yehuasheng
 * @version Id: ZipkinConfig.java, v0.1 2023/9/15 08:56 yehuasheng Exp $
 */
@Configuration
public class ZipkinConfig {
    @Bean
    CurrentTraceContext.ScopeDecorator legacyIds() {
        return MDCScopeDecorator.newBuilder()
                .clear()
                .add(CorrelationScopeConfig
                        .SingleCorrelationField
                        .newBuilder(BaggageFields.TRACE_ID)
                        .name(SingleCorrelationNameConstant.X_B3_TRACE_ID)
                        .build())
                .add(CorrelationScopeConfig
                        .SingleCorrelationField
                        .newBuilder(BaggageFields.PARENT_ID)
                        .name(SingleCorrelationNameConstant.X_B3_PARENT_SPAN_ID)
                        .build())
                .add(CorrelationScopeConfig
                        .SingleCorrelationField
                        .newBuilder(BaggageFields.SPAN_ID)
                        .name(SingleCorrelationNameConstant.X_B3_SPAN_ID)
                        .build())
                .add(CorrelationScopeConfig
                        .SingleCorrelationField
                        .newBuilder(BaggageFields.SAMPLED)
                        .name(SingleCorrelationNameConstant.X_SPAN_EXPORT)
                        .build())
                .build();
    }
}
