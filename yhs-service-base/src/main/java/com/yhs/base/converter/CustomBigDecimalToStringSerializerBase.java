package com.yhs.base.converter;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializerBase;

import java.math.BigDecimal;

/**
 * @author 07664-linwei
 * @version Id: CustomBigDecimalToStringSerializerBase.java, v 0.1 2022/12/8 17:21 lw Exp $
 */
public class CustomBigDecimalToStringSerializerBase extends ToStringSerializerBase {

    public static final CustomBigDecimalToStringSerializerBase instance = new CustomBigDecimalToStringSerializerBase();

    public CustomBigDecimalToStringSerializerBase() {
        super(BigDecimal.class);
    }


    public CustomBigDecimalToStringSerializerBase(Class<?> handledType) {
        super(handledType);
    }

    @Override
    public String valueToString(Object o) {
        return new BigDecimal(o.toString()).toPlainString();
    }
}
