package com.yhs.knife4j.cloud;

import com.yhs.knife4j.core.pojo.CommonAuthRoute;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 07664-linwei
 * @version Id: CloudRoute.java, v 0.1 2023/7/25 8:21 lw Exp $
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CloudRoute extends CommonAuthRoute {

    private String uri;

    private String version;
}
