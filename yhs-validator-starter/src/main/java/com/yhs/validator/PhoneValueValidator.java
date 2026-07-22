package com.yhs.validator;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.PhoneUtil;
import com.yhs.validator.annotate.PhoneValue;
import com.yhs.validator.enums.PhoneRegion;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @author 07664-linwei
 * @version Id: PhoneValueValidator.java, v 0.1 2022/4/23 9:17 lw Exp $
 */
public class PhoneValueValidator implements ConstraintValidator<PhoneValue, String> {

    private Boolean required;

    private PhoneRegion region;

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if (CharSequenceUtil.isBlank(phone)) {
            return !required;
        }
        if (region.equals(PhoneRegion.ALL)) {
            return PhoneUtil.isPhone(phone);
        } else if (region.equals(PhoneRegion.HK)) {
            return PhoneUtil.isMobileHk(phone);
        } else if (region.equals(PhoneRegion.MO)) {
            return PhoneUtil.isMobileMo(phone);
        } else if (region.equals(PhoneRegion.TW)) {
            return PhoneUtil.isMobileTw(phone);
        } else {
            return PhoneUtil.isMobile(phone);
        }
    }

    @Override
    public void initialize(PhoneValue constraintAnnotation) {
        this.required = constraintAnnotation.required();
        this.region = constraintAnnotation.region();
    }
}

