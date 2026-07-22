package com.yhs.log.properties;

/**
 * @author 07664-linwei
 * @version Id: OptLogType.java, v 0.1 2022/6/1 14:39 lw Exp $
 */
public enum OptLogType {

    /**
     * 通过logger记录日志到本地
     */
    LOGGER,
    /**
     * 记录日志到数据库
     */
    DB,
    /**
     * 记录日志到远程接口 ip调用
     */
    REMOTE_IP,
    /**
     * 记录日志到远程接口 服务名调用
     */
    REMOTE_SERVICE
}
