package com.yhs.easyexcel.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.experimental.UtilityClass;

import java.util.Set;

/**
 * 参数校验
 *
 * @author 07664-linwei
 * @version Id: Validators.java, v 0.1 2022/6/23 10:38 lw Exp $
 */
@UtilityClass
public class Validators {

    private static final Validator validator = Validation.buildDefaultValidatorFactory()
            .getValidator();


    /**
     * 验证
     *
     * @param o 验证对象
     * @return 验证结果
     */
    public static Set<ConstraintViolation<Object>> validate(Object o) {
        return validator.validate(o);
    }
}
