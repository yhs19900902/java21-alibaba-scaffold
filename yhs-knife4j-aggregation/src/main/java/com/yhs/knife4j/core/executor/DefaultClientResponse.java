package com.yhs.knife4j.core.executor;

import com.yhs.knife4j.core.RouteResponse;
import com.yhs.knife4j.core.pojo.HeaderWrapper;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author 07664-linwei
 * @version Id: DefaultClientResponse.java, v 0.1 2023/7/25 14:12 lw Exp $
 */
public class DefaultClientResponse implements RouteResponse {


    private final String uri;
    private final String error;
    private int httpCode = 500;

    public DefaultClientResponse(String uri, String error) {
        this.uri = uri;
        this.error = error;
    }

    public DefaultClientResponse(String uri, String error, int httpCode) {
        this.uri = uri;
        this.error = error;
        this.httpCode = httpCode;
    }


    @Override
    public List<HeaderWrapper> getHeaders() {
        return null;
    }

    @Override
    public boolean success() {
        return false;
    }

    @Override
    public int getStatusCode() {
        return httpCode;
    }

    @Override
    public String getContentType() {
        return "application/json";
    }

    @Override
    public Long getContentLength() {
        return 0L;
    }

    @Override
    public Charset getCharsetEncoding() {
        return StandardCharsets.UTF_8;
    }

    @Override
    public InputStream getBody() {
        return null;
    }

    @Override
    public String text() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return "{\n" +
                "    \"timestamp\": \"" + timestamp + "\",\n" +
                "    \"status\": " + getStatusCode() + ",\n" +
                "    \"message\": \"" + error + "\",\n" +
                "    \"path\": \"" + uri + "\"\n" +
                "}";
    }
}
