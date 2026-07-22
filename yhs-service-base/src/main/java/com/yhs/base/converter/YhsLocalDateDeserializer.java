package com.yhs.base.converter;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


/**
 * @author 07664-linwei
 * @version Id: YhsLocalDateDeserializer.java, v 0.1 2022/8/15 10:23 lw Exp $
 */
@Slf4j
public class YhsLocalDateDeserializer extends JSR310DateTimeDeserializerBase<LocalDate> {
    public static final YhsLocalDateDeserializer INSTANCE = new YhsLocalDateDeserializer();
    public static final String DEFAULT_DATE_FORMAT_MATCHES = "^\\d{4}-\\d{1,2}-\\d{1,2}$";
    public static final String DEFAULT_DATE_TIME_FORMAT_MATCHES = "^\\d{4}-\\d{1,2}-\\d{1,2}\\s{1}\\d{1,2}:\\d{1,2}:\\d{1,2}$";
    public static final String DEFAULT_DATE_FORMAT_EN_MATCHES = "^\\d{4}年\\d{1,2}月\\d{1,2}日$";
    public static final String DEFAULT_DATE_TIME_FORMAT_EN_MATCHES = "^\\d{4}年\\d{1,2}月\\d{1,2}日\\d{1,2}时\\d{1,2}分\\d{1,2}秒$";
    public static final String SLASH_DATE_FORMAT_MATCHES = "^\\d{4}/\\d{1,2}/\\d{1,2}$";
    public static final String SLASH_DATE_TIME_FORMAT_MATCHES = "^\\d{4}/\\d{1,2}/\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$";
    public static final String DEFAULT_DATE_FORMAT_TIMESTAMP = "^\\d*$";
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DEFAULT_DATE_FORMAT_DTF = DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN);
    private static final DateTimeFormatter DEFAULT_DATE_FORMAT_EN_DTF = DateTimeFormatter.ofPattern(DatePattern.CHINESE_DATE_PATTERN);
    private static final DateTimeFormatter SLASH_DATE_FORMAT_DTF = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMAT_DTF = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm:ss");
    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMAT_EN_DTF = DateTimeFormatter.ofPattern(DatePattern.CHINESE_DATE_TIME_PATTERN);
    private static final DateTimeFormatter SLASH_DATE_TIME_FORMAT_DTF = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public YhsLocalDateDeserializer() {
        this(DEFAULT_FORMATTER);
    }

    public YhsLocalDateDeserializer(DateTimeFormatter formatter) {
        super(LocalDate.class, formatter);
    }

    protected YhsLocalDateDeserializer(YhsLocalDateDeserializer base, Boolean leniency) {
        super(base, leniency);
    }


    protected YhsLocalDateDeserializer(JSR310DateTimeDeserializerBase<LocalDate> base, JsonFormat.Shape shape) {
        super(base, shape);
    }

    @Override
    protected JSR310DateTimeDeserializerBase<LocalDate> withDateFormat(DateTimeFormatter dateTimeFormatter) {
        return new YhsLocalDateDeserializer(dateTimeFormatter);
    }

    @Override
    protected JSR310DateTimeDeserializerBase<LocalDate> withLeniency(Boolean aBoolean) {
        return new YhsLocalDateDeserializer(this, aBoolean);
    }

    @Override
    protected JSR310DateTimeDeserializerBase<LocalDate> withShape(JsonFormat.Shape shape) {
        return new YhsLocalDateDeserializer(this, shape);
    }

    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String source = parser.getText().trim();
        if (parser.hasToken(JsonToken.VALUE_STRING)) {
            return deserializeDefaultFormatter(parser, context, source);
        } else if (parser.isExpectedStartObjectToken()) {
            return this.deserializeDefaultFormatter(parser, context, context.extractScalarFromObject(parser, this, this.handledType()));
        } else {
            if (parser.isExpectedStartArrayToken()) {
                JsonToken t = parser.nextToken();
                if (t == JsonToken.END_ARRAY) {
                    return null;
                }

                if (context.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS) && (t == JsonToken.VALUE_STRING || t == JsonToken.VALUE_EMBEDDED_OBJECT)) {
                    LocalDate parsed = this.deserialize(parser, context);
                    if (parser.nextToken() != JsonToken.END_ARRAY) {
                        this.handleMissingEndArrayForSingle(parser, context);
                    }

                    return parsed;
                }

                if (t == JsonToken.VALUE_NUMBER_INT) {
                    int year = parser.getIntValue();
                    int month = parser.nextIntValue(-1);
                    int day = parser.nextIntValue(-1);
                    if (parser.nextToken() != JsonToken.END_ARRAY) {
                        throw context.wrongTokenException(parser, this.handledType(), JsonToken.END_ARRAY, "Expected array to end");
                    }

                    return LocalDate.of(year, month, day);
                }

                context.reportInputMismatch(this.handledType(), "Unexpected token (%s) within Array, expected VALUE_NUMBER_INT", new Object[]{t});
            }

            if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
                return (LocalDate) parser.getEmbeddedObject();
            } else if (parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
                long timestamp = Long.valueOf(source);
                if (source.length() < 11) {
                    timestamp = timestamp * 1000;
                }
                return LocalDate.from(LocalDateTimeUtil.of(timestamp, ZoneId.systemDefault()));
            } else {
                return this._handleUnexpectedToken(context, parser, "Expected array or string.", new Object[0]);
            }
        }
    }


    private LocalDate deserializeDefaultFormatter(JsonParser parser, DeserializationContext context, String source) throws IOException {
        try {
            if (source.length() == 0) {
                return this._fromEmptyString(parser, context, source);
            }
            if (this._formatter == DEFAULT_FORMATTER && source.length() > 10 && source.charAt(10) == 'T') {
                if (this.isLenient()) {
                    return source.endsWith("Z") ? LocalDate.parse(source.substring(0, source.length() - 1), DateTimeFormatter.ISO_LOCAL_DATE_TIME) : LocalDate.parse(source, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                } else {
                    JavaType t = this.getValueType(context);
                    return (LocalDate) context.handleWeirdStringValue(t.getRawClass(), source, "Should not contain time component when 'strict' mode set for property or type (enable 'lenient' handling to allow)", new Object[0]);
                }
            }
            return convert(source);
        } catch (DateTimeException e) {
            log.error("deserialize LocalDate error :{0}", e);
            return this._handleDateTimeException(context, e, source);
        }
    }

    private LocalDate convert(String source) {

        if (source.matches(DEFAULT_DATE_FORMAT_MATCHES)) {
            return LocalDate.parse(source, DEFAULT_DATE_FORMAT_DTF);
        }
        if (source.matches(DEFAULT_DATE_FORMAT_EN_MATCHES)) {
            return LocalDate.parse(source, DEFAULT_DATE_FORMAT_EN_DTF);
        }
        if (source.matches(DEFAULT_DATE_TIME_FORMAT_MATCHES)) {
            return LocalDate.parse(source, DEFAULT_DATE_TIME_FORMAT_DTF);
        }
        if (source.matches(SLASH_DATE_FORMAT_MATCHES)) {
            return LocalDate.parse(source, SLASH_DATE_FORMAT_DTF);
        }
        if (source.matches(DEFAULT_DATE_TIME_FORMAT_EN_MATCHES)) {
            return LocalDate.parse(source, DEFAULT_DATE_TIME_FORMAT_EN_DTF);
        }
        if (source.matches(SLASH_DATE_TIME_FORMAT_MATCHES)) {
            return LocalDate.parse(source, SLASH_DATE_TIME_FORMAT_DTF);
        }
        if (source.matches(DEFAULT_DATE_FORMAT_TIMESTAMP)) {
            long timestamp = Long.valueOf(source);
            if (source.length() < 11) {
                timestamp = timestamp * 1000;
            }
            return LocalDate.from(LocalDateTimeUtil.of(timestamp, ZoneId.systemDefault()));
        }
        log.error("formatter not matching, data {0}", source);
        return null;
    }

}
