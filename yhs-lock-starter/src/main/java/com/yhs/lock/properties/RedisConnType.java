package com.yhs.lock.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 07664-linwei
 * @version Id: RedisConnType.java, v 0.1 2022/4/24 17:13 lw Exp $
 */
@Getter
@AllArgsConstructor
public enum RedisConnType {
    /**
     * 单机
     */
    SINGLE("single", "单节点"),

    /**
     *
     */
    SENTINEL("sentinel", "哨兵部署方式"),

    /**
     *
     */
    MASTER_SLAVE("masterSlave", "主从部署方式"),
    /**
     * 集群
     */
    CLUSTER("cluster", "集群");


    /**
     * 连接类型
     */
    private String connType;
    /**
     * 描述
     */
    private String describe;
}
