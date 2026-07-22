package com.yhs.base.jsckson;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.yhs.base.converter.CustomBigDecimalToStringSerializerBase;
import com.yhs.base.converter.YhsLocalDateDeserializer;
import com.yhs.base.converter.YhsLocalDateTimeDeserializer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间默认序列化
 *
 * @author 07664-linwei
 * @version Id: YhsJavaTimeModule.java, v 0.1 2022/4/15 16:16 lw Exp $
 */
public class YhsJavaTimeModule extends SimpleModule {

    /**
     * 指定序列化规则
     */
    public YhsJavaTimeModule() {
        super(PackageVersion.VERSION);
        //------- 时间反序化
        // LocalDateTime
        this.addDeserializer(LocalDateTime.class, YhsLocalDateTimeDeserializer.INSTANCE);
        // LocalDate
        this.addDeserializer(LocalDate.class, YhsLocalDateDeserializer.INSTANCE);
        // HH:mm:ss
        this.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN)));
        //------ 时间序列化
        //  LocalDateTime yyyy-MM-dd HH:mm:ss
        this.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
        this.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        this.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN)));
        this.addSerializer(Long.class, ToStringSerializer.instance);
        this.addSerializer(Long.TYPE, ToStringSerializer.instance);
        this.addSerializer(BigInteger.class, ToStringSerializer.instance);
        this.addSerializer(BigDecimal.class, CustomBigDecimalToStringSerializerBase.instance);
    }
}
