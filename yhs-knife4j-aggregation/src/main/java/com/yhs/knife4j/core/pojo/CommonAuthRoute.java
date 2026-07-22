package com.yhs.knife4j.core.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 07664-linwei
 * @version Id: CommonAuthRoute.java, v 0.1 2023/7/25 8:22 lw Exp $
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class CommonAuthRoute extends CommonRoute {

    private BasicAuth routeAuth;

}
