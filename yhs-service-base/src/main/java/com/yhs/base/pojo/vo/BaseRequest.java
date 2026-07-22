package com.yhs.base.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 03952-yehuasheng
 * @version Id: BaseRequest.java, v0.1 2023/9/11 17:50 yehuasheng Exp $
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "请求实体")
public class BaseRequest extends BaseVO {
    /**
     * 当前系统appId
     */
    @Schema(description = "当前系统ID", example = "202300000001")
    private String appId;

    /**
     * 时间戳
     */
    @Schema(description = "时间戳", example = "162222311454")
    private long timestamp;

    /**
     * 签名
     */
    @Schema(description = "签名", example = "1111111111111111")
    private String sign;

    /**
     * 请求业务数据
     */
    @Schema(description = "请求业务数据", example = "{\"data\":\"data\"}")
    private String data;
}
