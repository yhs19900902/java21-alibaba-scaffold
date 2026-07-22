package com.yhs.validator;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReUtil;
import com.yhs.validator.annotate.ZipCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


/**
 * @author 07664-linwei
 * @version Id: ZipCodeValidator.java, v 0.1 2022/4/23 9:37 lw Exp $
 */

public class ZipCodeValidator implements ConstraintValidator<ZipCode, String> {

    private Boolean required;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (CharSequenceUtil.isBlank(value)) {
            return !required;
        }
        return ReUtil.isMatch(Validator.ZIP_CODE, value);
    }

    @Override
    public void initialize(ZipCode constraintAnnotation) {
        this.required = constraintAnnotation.required();
    }
}
