package com.yhs.knife4j.core.pojo;

import cn.hutool.crypto.digest.MD5;
import lombok.Data;

/**
 * @author 07664-linwei
 * @version Id: CommonRoute.java, v 0.1 2023/7/25 8:23 lw Exp $
 */
@Data
public abstract class CommonRoute {

    /**
     * 服务名称
     */
    private String name;
    /**
     * openapi 本地文件路径
     */
    private String location;
    /**
     * openAPI 版本 2.0 or 3.0
     */
    private String swaggerVersion = "3.0";
    /**
     * 微服务路径,主要是针对在网关使用时，追加的basePath，主要是为了和在网关转发时路径在文档上展示一致的问题
     */
    private String servicePath;
    /**
     * 增加聚合显示顺序,参考issues：https://gitee.com/xiaoym/knife4j/issues/I27ST2
     */
    private Integer order = 1;

    /**
     * 当前Route主键唯一id
     *
     * @return
     */
    public String pkId() {
        return MD5.create().digestHex(this.toString());
    }

    @Override
    public String toString() {
        String sb = "CommonRoute{" + "name='" + name + '\'' +
                ",location='" + location + '\'' +
                ", swaggerVersion='" + swaggerVersion + '\'' +
                ", servicePath='" + servicePath + '\'' +
                ", order=" + order +
                "}";
        return sb;
    }

}
