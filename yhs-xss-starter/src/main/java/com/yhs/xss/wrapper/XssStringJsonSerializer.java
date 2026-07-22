package com.yhs.xss.wrapper;

import cn.hutool.core.text.CharSequenceUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.yhs.xss.utils.XssUtils;

import java.io.IOException;

/**
 * @author 07664-linwei
 * @version Id: XssStringJsonSerializer.java, v 0.1 2022/6/27 20:55 lw Exp $
 */
public class XssStringJsonSerializer extends JsonDeserializer<String> {

    @Override
    public Class<String> handledType() {
        return String.class;
    }

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getValueAsString();
        if (CharSequenceUtil.isNotBlank(text)) {
            return XssUtils.xssClean(text);
        }
        return text;
    }
}
