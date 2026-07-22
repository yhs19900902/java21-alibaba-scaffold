package com.yhs.easyexcel;

import com.yhs.easyexcel.handler.fill.ComplexFillSheetHandler;
import com.yhs.easyexcel.handler.fill.SimpleFillSheetHandler;
import com.yhs.easyexcel.handler.write.YHSManySheetWriteHandler;
import com.yhs.easyexcel.handler.write.YHSSingleSheetWriteHandler;
import com.yhs.easyexcel.properties.ExcelConfigProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 07664-linwei
 * @version Id: WriteHandlerConfiguration.java, v 0.1 2022/6/24 16:42 lw Exp $
 */
@Configuration
@RequiredArgsConstructor
public class WriteHandlerConfiguration {

    private final ExcelConfigProperties configProperties;

    /**
     * 单sheet 写入处理
     *
     * @return YHSSingleSheetWriteHandler
     */
    @Bean
    @ConditionalOnMissingBean
    public YHSSingleSheetWriteHandler singleSheetWriteHandler() {
        return new YHSSingleSheetWriteHandler(configProperties);
    }

    /**
     * 多sheet 写入处理
     *
     * @return YHSManySheetWriteHandler
     */
    @Bean
    @ConditionalOnMissingBean
    public YHSManySheetWriteHandler manySheetWriteHandler() {
        return new YHSManySheetWriteHandler(configProperties);
    }


    /**
     * 简单填充处理
     *
     * @return 简单填充对象
     */
    @Bean
    @ConditionalOnMissingBean
    public SimpleFillSheetHandler simpleFillSheetHandler() {
        return new SimpleFillSheetHandler(configProperties);
    }

    /**
     * 复杂填充处理
     *
     * @return 复杂填充对象
     */
    @Bean
    @ConditionalOnMissingBean
    public ComplexFillSheetHandler complexFillSheetHandler() {
        return new ComplexFillSheetHandler(configProperties);
    }
}
