package com.yhs.knife4j.core.executor;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.yhs.knife4j.core.RouteResponse;
import com.yhs.knife4j.core.pojo.HeaderWrapper;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 07664-linwei
 * @version Id: ApacheClientResponse.java, v 0.1 2023/7/25 14:11 lw Exp $
 */
public class ApacheClientResponse implements RouteResponse {

    private final HttpResponse httpResponse;
    Logger logger = LoggerFactory.getLogger(ApacheClientResponse.class);
    private HttpEntity httpEntity;

    public ApacheClientResponse(HttpResponse httpResponse) {
        if (httpResponse == null) {
            throw new IllegalArgumentException("响应请求体不能为空");
        }
        this.httpResponse = httpResponse;
        if (httpResponse != null) {
            httpEntity = httpResponse.getEntity();
        }
    }

    @Override
    public List<HeaderWrapper> getHeaders() {
        Header[] headers = this.httpResponse.getAllHeaders();
        List<HeaderWrapper> headerWrappers = new ArrayList<>();
        if (ArrayUtil.isNotEmpty(headers)) {
            for (Header header : headers) {
                if (header != null) {
                    headerWrappers.add(new HeaderWrapper(header.getName(), header.getValue()));
                }
            }
        }
        return headerWrappers;
    }

    @Override
    public boolean success() {
        return true;
    }

    @Override
    public int getStatusCode() {
        return httpResponse.getStatusLine().getStatusCode();
    }

    @Override
    public String getContentType() {
        if (httpEntity != null) {
            Header header = httpEntity.getContentType();
            if (header != null) {
                return header.getValue();
            }
        }
        return "application/json";
    }

    @Override
    public Long getContentLength() {
        if (httpEntity != null) {
            return httpEntity.getContentLength();
        }
        return Long.valueOf(-1);
    }

    @Override
    public Charset getCharsetEncoding() {
        if (httpEntity != null) {
            Header header = httpEntity.getContentEncoding();
            if (header != null && StrUtil.isNotBlank(header.getValue())) {
                return Charset.forName(header.getValue());
            }
        }
        return StandardCharsets.UTF_8;
    }

    @Override
    public InputStream getBody() {
        try {
            return httpEntity != null ? httpEntity.getContent() : null;
        } catch (IOException e) {
        }
        return null;
    }

    @Override
    public String text() {
        try {
            return EntityUtils.toString(httpEntity, getCharsetEncoding());
        } catch (IOException e) {
            logger.error("transform text error:" + e.getMessage(), e);
        }
        return null;
    }
}
