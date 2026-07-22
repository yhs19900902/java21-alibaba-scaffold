package com.yhs.base.utils;

import com.yhs.base.enums.ClientEnum;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

/**
 * @author 04628-duanchengjun
 * @version Id: ClientValidator.java, v 0.1 2019/4/25 9:44 duanchengjun Exp $
 */
@UtilityClass
public class ClientValidatorUtil {

    public static final String MEN_CODE = "0";

    public static final String WOMEN_CODE = "1";

    public static String transferSexToCommonChinese(String sexCode) {
        if (StringUtils.equals(sexCode, ClientEnum.MEN.getCode()))
            return ClientEnum.MEN.getDesc();
        else if (StringUtils.equals(sexCode, ClientEnum.WOMEN.getCode()))
            return ClientEnum.WOMEN.getDesc();
        return sexCode;
    }

    public static String transferSexToGentleChinese(String sexCode) {
        if (StringUtils.equals(sexCode, ClientEnum.GENTLEMAN.getCode()))
            return ClientEnum.GENTLEMAN.getDesc();
        else if (StringUtils.equals(sexCode, ClientEnum.LADIES.getCode()))
            return ClientEnum.LADIES.getDesc();
        return sexCode;
    }

    public static boolean isValidSexCode(String sexCode) {
        return StringUtils.equals(sexCode, MEN_CODE) || StringUtils.equals(sexCode, WOMEN_CODE);
    }

}
