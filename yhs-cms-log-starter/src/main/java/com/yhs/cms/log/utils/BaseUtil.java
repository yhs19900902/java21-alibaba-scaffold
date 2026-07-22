package com.yhs.cms.log.utils;


import com.yhs.base.constant.CommonConstant;
import com.yhs.cms.log.pojo.po.CmsLogPO;
import org.apache.commons.lang3.StringUtils;

/**
 * @author 07558-fenggang
 * @version Id: BaseUtil.java, v 0.1 2021/7/22 16:07 fenggang Exp $
 */
public class BaseUtil {

    public static final char UNDERLINE = '_';

    /**
     * 驼峰格式字符串转换为下划线格式字符串
     *
     * @param param
     * @return
     */
    public static String camelToUnderline(String param) {
        if (StringUtils.isBlank(param)) {
            return CommonConstant.EMPTY;
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(UNDERLINE);
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * kafkakey
     *
     * @param cmsLogPO cmsLogPO
     * @return String
     */
    public static String kafkaKey(CmsLogPO cmsLogPO) {
        if (StringUtils.isNotBlank(cmsLogPO.getUri())) {
            return cmsLogPO.getLogType() + cmsLogPO.getUri();
        } else {
            return "SystemCmsLog";
        }
    }
}
