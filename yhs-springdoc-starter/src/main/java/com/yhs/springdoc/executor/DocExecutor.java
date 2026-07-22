package com.yhs.springdoc.executor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;
import com.yhs.base.pojo.vo.BusinessResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author 07664-linwei
 * @version Id: DocExecutor.java, v 0.1 2023/7/27 10:15 lw Exp $
 */
@Data
@Slf4j
public class DocExecutor {

    private String groupName;

    private String uri;

    private String location;

    private String address;

    private String version;


    private boolean isStop = false;

    public void start() {
        new Thread(this::register).start();
    }

    private void register() {
        try {
            Map<String, Object> paramMap = Map.of("groupName", groupName, "uri", uri, "location", location);
            JSONObject from = JSONObject.from(paramMap);
            if (StrUtil.isNotBlank(version)) {
                from.put("version", version);
            }
            String result = HttpUtil.post(address + "/api/doc/register", from.toJSONString(), 10000);
            BusinessResponse businessResponse = JSONObject.parseObject(result, BusinessResponse.class);
            if (businessResponse.success()) {
                log.info("aggregation doc register success");
            } else {
                log.error("aggregation doc register error {}", businessResponse.getMessage());
            }
        } catch (Exception ex) {
            log.error("aggregation doc register error {}", ex.getMessage());
        }

    }


}
