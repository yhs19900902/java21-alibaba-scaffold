package com.yhs.xss.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.apache.commons.text.translate.LookupTranslator;
import org.owasp.validator.html.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author 07664-linwei
 * @version Id: XssUtils.java, v 0.1 2022/4/24 10:09 lw Exp $
 */
@Slf4j
@UtilityClass
public class XssUtils {

    private static final String ANTISAMY_SLASHDOT_XML = "antisamy-slashdot.xml";
    private static final AntiSamy ANTI_SAMY = new AntiSamy();
    public static CharSequenceTranslator ESCAPE_XSI;
    private static Policy policy = null;

    static {
        try (InputStream inputStream = XssUtils.class.getClassLoader().getResourceAsStream(ANTISAMY_SLASHDOT_XML)) {
            if (inputStream == null) {
                throw new PolicyException("");
            }
            policy = Policy.getInstance(inputStream);
            ESCAPE_XSI = new LookupTranslator(
                    Map.of("\r\n", StringUtils.EMPTY, "\n", StringUtils.EMPTY)
            );
        } catch (PolicyException e) {
            log.error("read XSS config file [" + ANTISAMY_SLASHDOT_XML + "] fail ,reason:", e);
        } catch (IOException e) {
            log.error("close XSS config file [" + ANTISAMY_SLASHDOT_XML + "] fail , reason:", e);
        }

    }

    /**
     * 跨站攻击语句过滤 方法
     *
     * @param paramValue 待过滤的参数
     * @return 清理后的字符串
     */
    public static String xssClean(String paramValue) {

        try {
            final CleanResults cr = ANTI_SAMY.scan(paramValue, policy);

            return ESCAPE_XSI.translate(StringEscapeUtils.unescapeHtml4(cr.getCleanHTML()));
        } catch (ScanException e) {
            log.error("scan failed  is [" + paramValue + "]", e);
        } catch (PolicyException e) {
            log.error("antisamy convert failed  is [" + paramValue + "]", e);
        }
        return paramValue;
    }


}
