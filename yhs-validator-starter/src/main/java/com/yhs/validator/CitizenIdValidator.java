package com.yhs.validator;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReUtil;
import com.yhs.validator.annotate.CitizenId;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


/**
 * @author 07664-linwei
 * @version Id: CitizenIdValidator.java, v 0.1 2022/4/23 9:29 lw Exp $
 */
public class CitizenIdValidator implements ConstraintValidator<CitizenId, String> {

    private Boolean required;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (CharSequenceUtil.isBlank(value)) {
            return !required;
        }
        return ReUtil.isMatch(Validator.CITIZEN_ID, value);
    }

    @Override
    public void initialize(CitizenId constraintAnnotation) {
        this.required = constraintAnnotation.required();
    }
}
