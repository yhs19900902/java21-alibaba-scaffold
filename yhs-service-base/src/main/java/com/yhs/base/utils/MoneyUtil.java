package com.yhs.base.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author 04628-duanchengjun
 * @version Id: MoneyUtil.java, v 0.1 2019/4/25 10:01 duanchengjun Exp $
 */
@UtilityClass
public class MoneyUtil {

    /**
     * 元转换分
     *
     * @param yuan
     * @return
     */
    public static String yuan2Fen(String yuan) {
        if (!StringUtils.isBlank(yuan)) {
            BigDecimal a = new BigDecimal(yuan);
            return a.multiply(new BigDecimal(100)).toBigInteger().toString();
        }
        return null;
    }

    /**
     * 元转换分
     *
     * @param yuan
     * @return
     */
    public static String yuan2Fen(BigDecimal yuan) {
        if (yuan != null) {
            return yuan.multiply(new BigDecimal(100)).toBigInteger().toString();
        }
        return null;
    }

    /**
     * 分转元
     *
     * @param fen
     * @return
     */
    public static String fen2Yuan(String fen) {
        if (!StringUtils.isBlank(fen)) {
            BigDecimal a = new BigDecimal(fen);
            return a.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).toString();
        }
        return null;
    }

    /**
     * 分转元
     *
     * @param fen
     * @return
     */
    public static String fen2Yuan(BigDecimal fen) {
        if (fen != null) {
            return fen.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).toString();
        }
        return null;
    }

}
