package com.yhs.validator;

import cn.hutool.core.text.CharSequenceUtil;
import com.yhs.validator.annotate.InOfEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author S006538-zhuxiaoxiao
 * @version Id: InOfEnumValidator.java, v 0.1 2021/8/24 14:17 zhuxiaoxiao Exp $
 */

public class InOfEnumValidator implements ConstraintValidator<InOfEnum, String> {

    private Boolean required;

    private List<String> targetEnumValue;

    @SneakyThrows
    @Override
    public void initialize(InOfEnum constraintAnnotation) {
        this.required = constraintAnnotation.required();
        this.targetEnumValue = getEnumValues(constraintAnnotation);
    }

    private List<String> getEnumValues(InOfEnum constraintAnnotation) {
        Class<? extends Enum> enumClass = constraintAnnotation.sourceEnum();
        Field[] fields = enumClass.getFields();
        List<String> enumValues = new ArrayList<>();
        for (int i = 0; i < fields.length; i++) {
            enumValues.add(fields[i].getName());
        }
        return enumValues;
    }


    /**
     * 校验属性的值是否与枚举相同
     *
     * @param value
     * @param context
     * @return
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (CharSequenceUtil.isBlank(value)) {
            return !required;
        }
        return targetEnumValue.contains(value);
    }
}
