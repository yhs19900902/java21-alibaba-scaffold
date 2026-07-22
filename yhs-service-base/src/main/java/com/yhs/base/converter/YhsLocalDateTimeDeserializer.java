package com.yhs.base.converter;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author 07664-linwei
 * @version Id: YhsLocalDateTimeDeserializer.java, v 0.1 2022/4/15 16:15 lw Exp $
 */
public class YhsLocalDateTimeDeserializer extends JSR310DateTimeDeserializerBase<LocalDateTime> {
    public static final YhsLocalDateTimeDeserializer INSTANCE = new YhsLocalDateTimeDeserializer();
    @Deprecated
    public static final String DEFAULT_DATE_FORMAT_MATCHES = "^\\d{4}-\\d{1,2}-\\d{1,2}$";
    @Deprecated
    public static final String DEFAULT_DATE_TIME_FORMAT_MATCHES = "^\\d{4}-\\d{1,2}-\\d{1,2}\\s{1}\\d{1,2}:\\d{1,2}:\\d{1,2}$";
    @Deprecated
    public static final String DEFAULT_DATE_FORMAT_EN_MATCHES = "^\\d{4}年\\d{1,2}月\\d{1,2}日$";
    @Deprecated
    public static final String DEFAULT_DATE_TIME_FORMAT_EN_MATCHES = "^\\d{4}年\\d{1,2}月\\d{1,2}日\\d{1,2}时\\d{1,2}分\\d{1,2}秒$";
    @Deprecated
    public static final String SLASH_DATE_FORMAT_MATCHES = "^\\d{4}/\\d{1,2}/\\d{1,2}$";
    @Deprecated
    public static final String SLASH_DATE_TIME_FORMAT_MATCHES = "^\\d{4}/\\d{1,2}/\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$";
    public static final String DEFAULT_DATE_FORMAT_TIMESTAMP = "^[0-9]*$";
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    /**
     * 以下是支持的6种参数格式
     */
    @Deprecated
    private static final DateTimeFormatter DEFAULT_DATE_FORMAT_DTF = DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN);
    @Deprecated
    private static final DateTimeFormatter DEFAULT_DATE_FORMAT_EN_DTF = DateTimeFormatter.ofPattern(DatePattern.CHINESE_DATE_PATTERN);
    @Deprecated
    private static final DateTimeFormatter SLASH_DATE_FORMAT_DTF = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    @Deprecated
    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMAT_DTF = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm:ss");
    @Deprecated
    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMAT_EN_DTF = DateTimeFormatter.ofPattern(DatePattern.CHINESE_DATE_TIME_PATTERN);
    @Deprecated
    private static final DateTimeFormatter SLASH_DATE_TIME_FORMAT_DTF = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public YhsLocalDateTimeDeserializer() {
        this(DEFAULT_FORMATTER);
    }

    public YhsLocalDateTimeDeserializer(DateTimeFormatter formatter) {
        super(LocalDateTime.class, formatter);
    }

    protected YhsLocalDateTimeDeserializer(YhsLocalDateTimeDeserializer base, Boolean leniency) {
        super(base, leniency);
    }

    @Override
    protected JSR310DateTimeDeserializerBase<LocalDateTime> withDateFormat(DateTimeFormatter dateTimeFormatter) {
        return new YhsLocalDateTimeDeserializer(dateTimeFormatter);
    }

    @Override
    protected JSR310DateTimeDeserializerBase<LocalDateTime> withLeniency(Boolean aBoolean) {
        return new YhsLocalDateTimeDeserializer(this, aBoolean);
    }

    @Override
    protected JSR310DateTimeDeserializerBase<LocalDateTime> withShape(JsonFormat.Shape shape) {
        return this;
    }


    private LocalDateTime convert(String source) {

        if (source.matches(DEFAULT_DATE_FORMAT_TIMESTAMP)) {
            long timestamp = Long.parseLong(source);
            if (source.length() > 10) {
                return DateUtil.toLocalDateTime(DateUtil.date(timestamp));
            }
            return LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.ofHours(8));
        }
        return DateUtil.toLocalDateTime(DateUtil.parse(source));
    }

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {

        if (parser.hasTokenId(JsonTokenId.ID_STRING) || parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            return deserializeDefaultFormatter(parser, context);
        }
        if (parser.isExpectedStartArrayToken()) {
            JsonToken t = parser.nextToken();
            if (t == JsonToken.END_ARRAY) {
                return null;
            }
            if ((t == JsonToken.VALUE_STRING || t == JsonToken.VALUE_EMBEDDED_OBJECT)
                    && context.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                final LocalDateTime parsed = deserialize(parser, context);
                if (parser.nextToken() != JsonToken.END_ARRAY) {
                    handleMissingEndArrayForSingle(parser, context);
                }
                return parsed;
            }
            if (t == JsonToken.VALUE_NUMBER_INT) {
                LocalDateTime result;

                int year = parser.getIntValue();
                int month = parser.nextIntValue(-1);
                int day = parser.nextIntValue(-1);
                int hour = parser.nextIntValue(-1);
                int minute = parser.nextIntValue(-1);

                t = parser.nextToken();
                if (t == JsonToken.END_ARRAY) {
                    result = LocalDateTime.of(year, month, day, hour, minute);
                } else {
                    int second = parser.getIntValue();
                    t = parser.nextToken();
                    if (t == JsonToken.END_ARRAY) {
                        result = LocalDateTime.of(year, month, day, hour, minute, second);
                    } else {
                        int partialSecond = parser.getIntValue();
                        if (partialSecond < 1_000 &&
                                !context.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
                            // value is milliseconds, convert it to nanoseconds
                            partialSecond *= 1_000_000;
                        }
                        if (parser.nextToken() != JsonToken.END_ARRAY) {
                            throw context.wrongTokenException(parser, handledType(), JsonToken.END_ARRAY,
                                    "Expected array to end");
                        }
                        result = LocalDateTime.of(year, month, day, hour, minute, second, partialSecond);
                    }
                }
                return result;
            }
            context.reportInputMismatch(handledType(),
                    "Unexpected token (%s) within Array, expected VALUE_NUMBER_INT",
                    t);
        }
        if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
            return (LocalDateTime) parser.getEmbeddedObject();
        }

        return _handleUnexpectedToken(context, parser, "当前参数需要数组、字符串、时间戳。");
    }


    private LocalDateTime deserializeDefaultFormatter(JsonParser parser, DeserializationContext context) throws IOException {
        String string = parser.getText().trim();
        if (string.length() == 0) {
            return null;
        }
        try {
            if (_formatter == null) {
                return convert(string);
            }
            return LocalDateTime.parse(string, this._formatter);
        } catch (DateTimeException e) {
            return _handleDateTimeException(context, e, string);
        }
    }


}
