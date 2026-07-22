package com.yhs.base.pojo.vo;

import com.yhs.base.pojo.BaseObject;
import com.yhs.base.utils.LogUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.Serial;

/**
 * @author 03952-yehuasheng
 * @version Id: BaseVO.java, v0.1 2023/9/11 17:36 yehuasheng Exp $
 */
public class BaseVO extends BaseObject {
    @Serial
    private static final long serialVersionUID = -785293771287573538L;

    private final transient Logger log = LogUtil.getLogger();

    public boolean isBlank(String value, String key) {
        if (StringUtils.isBlank(value)) {
            log.info("Param is null:[{}]", key);
            return true;
        } else {
            return false;
        }
    }
}
