package com.yhs.validator;

import cn.hutool.core.text.CharSequenceUtil;
import com.yhs.validator.annotate.InOfValue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.SneakyThrows;

import java.util.Arrays;

/**
 * @author 03952-yehuasheng
 * @version Id: InOfValueValidator.java, v0.1 2024/9/12 14:50 yehuasheng Exp $
 */
public class InOfValueValidator implements ConstraintValidator<InOfValue, String> {

    private Boolean required;

    private String[] matchTarget;

    @SneakyThrows
    @Override
    public void initialize(InOfValue constraintAnnotation) {
        this.required = constraintAnnotation.required();
        this.matchTarget = constraintAnnotation.matchTarget();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (CharSequenceUtil.isBlank(value)) {
            return !required;
        }

        return Arrays.asList(matchTarget).contains(value);
    }
}
